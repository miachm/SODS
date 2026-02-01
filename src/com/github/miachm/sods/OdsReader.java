package com.github.miachm.sods;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

class OdsReader {
    private static final String CORRECT_MIMETYPE = "application/vnd.oasis.opendocument.spreadsheet";
    private static final Locale defaultLocal = Locale.US;
    private final Uncompressor uncompressor;
    private final XmlReader reader = new XmlReaderEventImpl();
    private final ChartParser chartParser;
    private final SpreadSheet spread;
    private final StylesParser stylesParser;
    private final SpreadsheetParser spreadsheetParser;
    private final OdsOptionParameters options;
    private final ChartObjectRegistry chartObjectRegistry;
    private final ImageObjectRegistry imageObjectRegistry;
    private final String sourceDescription;

    private OdsReader(InputStream in, SpreadSheet spread, OdsOptionParameters options) {
        this.spread = spread;
        this.uncompressor = new Uncompressor(in);
        this.options = options;
        this.chartObjectRegistry = new ChartObjectRegistry();
        this.imageObjectRegistry = new ImageObjectRegistry();
        this.stylesParser = new StylesParser(options);
        this.spreadsheetParser = new SpreadsheetParser(stylesParser, spread, options, chartObjectRegistry,
                imageObjectRegistry);
        this.chartParser = new ChartParser(stylesParser, spread, options, chartObjectRegistry);
        this.sourceDescription = "InputStream(" + in.getClass().getName() + ")";
    }

    private OdsReader(File file, SpreadSheet spread, OdsOptionParameters options) throws IOException {
        this.spread = spread;
        this.uncompressor = new Uncompressor(file);
        this.options = options;
        this.chartObjectRegistry = new ChartObjectRegistry();
        this.imageObjectRegistry = new ImageObjectRegistry();
        this.stylesParser = new StylesParser(options);
        this.spreadsheetParser = new SpreadsheetParser(stylesParser, spread, options, chartObjectRegistry,
                imageObjectRegistry);
        this.chartParser = new ChartParser(stylesParser, spread, options, chartObjectRegistry);
        this.sourceDescription = "File(" + file.getPath() + ")";
    }

    static void load(InputStream in, SpreadSheet spread) throws IOException {
        OdsReader reader = new OdsReader(in, spread, new OdsOptionParameters());
        reader.load();
    }

    static void load(InputStream in, SpreadSheet spread, OdsOptionParameters options) throws IOException {
        OdsReader reader = new OdsReader(in, spread, options);
        reader.load();
    }

    static void load(File file, SpreadSheet spread) throws IOException {
        OdsReader reader = new OdsReader(file, spread, new OdsOptionParameters());
        reader.load();
    }

    static void load(File file, SpreadSheet spread, OdsOptionParameters options) throws IOException {
        OdsReader reader = new OdsReader(file, spread, options);
        reader.load();
    }

    private void load() throws IOException {
        long startNs = System.nanoTime();
        options.getLogger().fine("Loading spreadsheet from " + sourceDescription);
        options.getLogger().fine("Load options - styles=" + options.isLoadStyles()
                + ", images=" + options.isLoadImages()
                + ", graphs=" + options.isLoadGraphs()
                + ", sheetNumbers=" + options.getSheetNumbers());
        boolean mimetypeChecked = false;
        String entry = uncompressor.nextFile();
        while (entry != null) {
            options.getLogger().config("Parsing entry: " + entry);
            if (entry.endsWith(".xml")) {
                options.getLogger().info("Parsing XML entry: " + entry);
                processContent(entry);
            } else if (entry.equals("mimetype")) {
                options.getLogger().finer("Loading mimetype...");
                checkMimeType();
                mimetypeChecked = true;
                options.getLogger().fine("Mimetype verified");
            } else if (entry.startsWith("Pictures/")) {
                if (options.isLoadImages()) {
                    byte[] data = readEntryData(uncompressor.getInputStream());
                    if (data != null) {
                        imageObjectRegistry.registerFile(entry, guessImageMimeType(entry), data);
                    } else {
                        options.getLogger().warning("Skipping image entry with no data: " + entry);
                    }
                } else {
                    options.getLogger().warning("Skipping image entry (load images disabled): " + entry);
                }
            }
            entry = uncompressor.nextFile();
        }
        uncompressor.close();
        spread.trimSheets();
        options.getLogger().info("Spreadsheet loaded, " + spread.getNumSheets() + " sheet(s)");
        long elapsedMs = (System.nanoTime() - startNs) / 1000000;
        options.getLogger().fine("Spreadsheet load completed in " + elapsedMs + " ms");

        if (!mimetypeChecked) {
            options.getLogger().severe("This file doesn't contain a mimetype. It's invalid according to OpenDocument Specification");
            throw new NotAnOdsException("This file doesn't contain a mimetype");
        }
    }

    private void checkMimeType() throws IOException {
        byte[] buff = new byte[CORRECT_MIMETYPE.getBytes().length];
        uncompressor.getInputStream().read(buff);
        String mimetype = new String(buff);
        options.getLogger().finer("Mimetype read: " + mimetype);
        if (!mimetype.equals(CORRECT_MIMETYPE)) {
            options.getLogger().severe("Invalid mimetype: " + mimetype);
            throw new NotAnOdsException("This file doesn't look like an ODS file. Mimetype: " + mimetype);
        }
    }

    private void processContent(String entryName) throws IOException {
        options.getLogger().info("Processing content entry: " + entryName);
        InputStream in = uncompressor.getInputStream();
        XmlReaderInstance instance = reader.load(in);
        if (instance == null) {
            options.getLogger().warning("Skipping empty XML entry: " + entryName);
            return;
        }

        if (options.isLoadStyles()) {
            options.getLogger().finer("Loading styles from: " + entryName);
            XmlReaderInstance stylesInstance = instance.nextElement("office:automatic-styles", "office:styles");
            stylesParser.parseStyles(stylesInstance);
            options.getLogger().fine("Styles loaded");
        } else {
            options.getLogger().warning("Skipping styles (load styles disabled): " + entryName);
        }

        XmlReaderInstance contentInstance = instance.nextElement("office:body");

        if (contentInstance != null) {
            options.getLogger().finer("Parsing office body: " + entryName);
            XmlReaderInstance spreadsheetInstance = contentInstance.nextElement("office:spreadsheet", "office:chart");
            if (spreadsheetInstance != null) {
                if (spreadsheetInstance.getTag().equals("office:chart")) {
                    if (options.isLoadGraphs()) {
                        options.getLogger().finer("Parsing chart content from: " + entryName);
                        chartParser.parseContent(spreadsheetInstance, entryName);
                    } else {
                        options.getLogger().warning("Skipping charts (load graphs disabled): " + entryName);
                    }
                } else {
                    options.getLogger().finer("Parsing spreadsheet content from: " + entryName);
                    spreadsheetParser.parseContent(contentInstance);
                }
            } else {
                options.getLogger().warning("No spreadsheet content found in: " + entryName);
            }
        } else {
            options.getLogger().warning("No office body found in: " + entryName);
        }

        reader.close();
    }

    private byte[] readEntryData(InputStream in) throws IOException {
        if (in == null) return null;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = in.read(buffer)) >= 0) {
            if (read == 0) continue;
            output.write(buffer, 0, read);
        }
        return output.toByteArray();
    }

    private String guessImageMimeType(String path) {
        if (path == null) return null;
        String lower = path.toLowerCase(Locale.US);
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".bmp")) return "image/bmp";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        return null;
    }
}

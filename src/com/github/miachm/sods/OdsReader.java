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
    private final StylesParser stylesParser = new StylesParser();
    private final SpreadsheetParser spreadsheetParser;
    private final OdsOptionParameters options;

    private OdsReader(InputStream in, SpreadSheet spread, OdsOptionParameters options) {
        this.spread = spread;
        this.uncompressor = new Uncompressor(in);
        this.options = options;
        this.spreadsheetParser = new SpreadsheetParser(stylesParser, spread, options);
        this.chartParser = new ChartParser(stylesParser, spread, options);
    }

    private OdsReader(File file, SpreadSheet spread, OdsOptionParameters options) throws IOException {
        this.spread = spread;
        this.uncompressor = new Uncompressor(file);
        this.options = options;
        this.spreadsheetParser = new SpreadsheetParser(stylesParser, spread, options);
        this.chartParser = new ChartParser(stylesParser, spread, options);
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
        options.getLogger().fine("Loading spreadsheet...");
        boolean mimetypeChecked = false;
        String entry = uncompressor.nextFile();
        while (entry != null) {
            options.getLogger().config("Parsing entry: " + entry);
            if (entry.endsWith(".xml")) {
                processContent(entry);
            } else if (entry.equals("mimetype")) {
                options.getLogger().finer("Loading mimetype...");
                checkMimeType();
                mimetypeChecked = true;
                options.getLogger().fine("Mimetype verified");
            } else if (entry.startsWith("Pictures/")) {
                byte[] data = readEntryData(uncompressor.getInputStream());
                if (data != null) {
                    spread.registerFile(entry, guessImageMimeType(entry), data);
                }
            }
            entry = uncompressor.nextFile();
        }
        uncompressor.close();
        spread.trimSheets();
        chartParser.resolveChartData();
        options.getLogger().info("Spreadsheet loaded, " + spread.getNumSheets() + " sheet(s)");

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
        options.getLogger().fine("Processing content");
        InputStream in = uncompressor.getInputStream();
        XmlReaderInstance instance = reader.load(in);
        if (instance == null) return;

        if (options.isLoadStyles()) {
            XmlReaderInstance stylesInstance = instance.nextElement("office:automatic-styles", "office:styles");
            stylesParser.parseStyles(stylesInstance);
            options.getLogger().fine("Styles loaded");
        }

        XmlReaderInstance contentInstance = instance.nextElement("office:body");

        if (contentInstance != null) {
            XmlReaderInstance spreadsheetInstance = contentInstance.nextElement("office:spreadsheet", "office:chart");
            if (spreadsheetInstance != null) {
                if (spreadsheetInstance.getTag().equals("office:chart")) {
                    chartParser.parseContent(spreadsheetInstance, entryName);
                } else {
                    spreadsheetParser.parseContent(contentInstance);
                }
            }
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

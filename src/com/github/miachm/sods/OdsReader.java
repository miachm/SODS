package com.github.miachm.sods;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

class OdsReader {
    private static final String CORRECT_MIMETYPE = "application/vnd.oasis.opendocument.spreadsheet";
    private static final Locale defaultLocal = Locale.US;
    private final XmlReader reader = new XmlReaderEventImpl();
    private final ChartParser chartParser;
    private final SpreadSheet spread;
    private final StylesParser stylesParser;
    private final SpreadsheetParser spreadsheetParser;
    private final OdsOptionParameters options;
    private final ChartObjectRegistry chartObjectRegistry;
    private final ImageObjectRegistry imageObjectRegistry;
    private final String sourceDescription;
    private final File sourceFile;
    private final InputStream sourceStream;

    private OdsReader(InputStream in, SpreadSheet spread, OdsOptionParameters options) {
        this.spread = spread;
        this.options = options;
        this.chartObjectRegistry = new ChartObjectRegistry();
        this.imageObjectRegistry = new ImageObjectRegistry();
        this.stylesParser = new StylesParser(options);
        this.spreadsheetParser = new SpreadsheetParser(stylesParser, spread, options, chartObjectRegistry,
                imageObjectRegistry);
        this.chartParser = new ChartParser(stylesParser, spread, options, chartObjectRegistry);
        this.sourceDescription = "InputStream(" + in.getClass().getName() + ")";
        this.sourceFile = null;
        this.sourceStream = in;
    }

    private OdsReader(File file, SpreadSheet spread, OdsOptionParameters options) throws IOException {
        this.spread = spread;
        this.options = options;
        this.chartObjectRegistry = new ChartObjectRegistry();
        this.imageObjectRegistry = new ImageObjectRegistry();
        this.stylesParser = new StylesParser(options);
        this.spreadsheetParser = new SpreadsheetParser(stylesParser, spread, options, chartObjectRegistry,
                imageObjectRegistry);
        this.chartParser = new ChartParser(stylesParser, spread, options, chartObjectRegistry);
        this.sourceDescription = "File(" + file.getPath() + ")";
        this.sourceFile = file;
        this.sourceStream = null;
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
                + ", macros=" + options.isLoadMacros()
                + ", sheetNumbers=" + options.getSheetNumbers()
                + ", passwordPassed=" + (options.getPassword() != null ));

        Map<String, byte[]> entries = sourceFile != null
                ? ZipPackageReader.readAll(sourceFile)
                : ZipPackageReader.readAll(sourceStream);

        byte[] manifestBytes = entries.get("META-INF/manifest.xml");
        ManifestParser manifest = ManifestParser.parse(manifestBytes);
        if (manifest.hasEncryptedEntries()) {
            options.getLogger().fine("Encrypted entries");
            String password = options.getPassword();
            if (password == null || password.isEmpty()) {
                options.getLogger().severe("No password provided");
                throw new WrongPasswordException("Document is password protected");
            }
            if (manifest.hasEncryptedPackage()) {
                options.getLogger().fine("Encrypted Package");
                byte[] encryptedPackage = entries.get("encrypted-package");
                if (encryptedPackage == null) {
                    options.getLogger().severe("Encrypted package not found");
                    throw new NotAnOdsException("Encrypted package entry not found");
                }
                entries = ZipPackageReader.readAll(new ByteArrayInputStream(
                        OdfEncryption.decryptEncryptedPackage(encryptedPackage,
                                manifest.getEncryptedPackageMetadata(), password)));
                manifestBytes = entries.get("META-INF/manifest.xml");
                manifest = ManifestParser.parse(manifestBytes);
            }
        }

        // Validate mimetype before parsing any XML to avoid mutating spread with invalid data
        options.getLogger().finer("Loading mimetype...");
        checkMimeType(entries.get("mimetype"));
        options.getLogger().fine("Mimetype verified");

        for (Map.Entry<String, byte[]> entry : entries.entrySet()) {
            String path = entry.getKey();
            byte[] data = entry.getValue();
            options.getLogger().config("Parsing entry: " + path);

            if (path.equals("mimetype")) continue;

            if (manifest.isEncrypted(path)) {
                OdfEncryptionMetadata meta = manifest.get(path);
                data = OdfEncryption.decrypt(data, meta, options.getPassword());
            }

            if (path.startsWith("Basic/")) {
                if (options.isLoadMacros()) {
                    options.getLogger().finer("Loading macro entry: " + path);
                    spread.getMacroRegistry().readEntry(path, new ByteArrayInputStream(data));
                }
            } else if (path.endsWith(".xml")) {
                options.getLogger().info("Parsing XML entry: " + path);
                processContent(path, data);
            } else if (path.startsWith("Pictures/")) {
                if (options.isLoadImages() && data != null && data.length > 0) {
                    imageObjectRegistry.registerFile(path, guessImageMimeType(path), data);
                } else if (!options.isLoadImages()) {
                    options.getLogger().warning("Skipping image entry (load images disabled): " + path);
                }
            }
        }

        spread.trimSheets();
        options.getLogger().info("Spreadsheet loaded, " + spread.getNumSheets() + " sheet(s)");
        long elapsedMs = (System.nanoTime() - startNs) / 1000000;
        options.getLogger().fine("Spreadsheet load completed in " + elapsedMs + " ms");
    }

    private void checkMimeType(byte[] buff) throws IOException {
        if (buff == null || buff.length < CORRECT_MIMETYPE.getBytes().length) {
            throw new NotAnOdsException("This file doesn't look like an ODS file");
        }
        String mimetype = new String(buff, 0, CORRECT_MIMETYPE.length());
        options.getLogger().finer("Mimetype read: " + mimetype);
        if (!mimetype.equals(CORRECT_MIMETYPE)) {
            options.getLogger().severe("Invalid mimetype: " + mimetype);
            throw new NotAnOdsException("This file doesn't look like an ODS file. Mimetype: " + mimetype);
        }
    }

    private void processContent(String entryName, byte[] data) throws IOException {
        options.getLogger().info("Processing content entry: " + entryName);
        XmlReaderInstance instance = reader.load(new ByteArrayInputStream(data));
        if (instance == null) {
            options.getLogger().warning("Skipping empty XML entry: " + entryName);
            return;
        }

        if ("settings.xml".equals(entryName)) {
            new SettingsParser(spread).parseSettings(instance);
            reader.close();
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

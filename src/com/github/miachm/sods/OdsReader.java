package com.github.miachm.sods;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

class OdsReader {
    private static final String CORRECT_MIMETYPE = "application/vnd.oasis.opendocument.spreadsheet";
    private static final Locale defaultLocal = Locale.US;
    private final Uncompressor uncompressor;
    private final XmlReader reader = new XmlReaderEventImpl();
    private final SpreadSheet spread;
    private final StylesParser stylesParser = new StylesParser();
    private final SpreadsheetParser spreadsheetParser;
    private final OdsOptionParameters options;

    private OdsReader(InputStream in, SpreadSheet spread, OdsOptionParameters options) {
        this.spread = spread;
        this.uncompressor = new Uncompressor(in);
        this.options = options;
        this.spreadsheetParser = new SpreadsheetParser(stylesParser, spread);
    }

    static void load(InputStream in, SpreadSheet spread) throws IOException {
        OdsReader reader = new OdsReader(in, spread, new OdsOptionParameters());
        reader.load();
    }

    static void load(InputStream in, SpreadSheet spread, OdsOptionParameters options) throws IOException {
        OdsReader reader = new OdsReader(in, spread, options);
        reader.load();
    }

    private void load() throws IOException {
        boolean mimetypeChecked = false;
        String entry = uncompressor.nextFile();
        while (entry != null) {
            if (entry.endsWith(".xml")) {
                processContent();
            } else if (entry.equals("mimetype")) {
                checkMimeType();
                mimetypeChecked = true;
            }
            entry = uncompressor.nextFile();
        }
        uncompressor.close();
        spread.trimSheets();

        if (!mimetypeChecked) {
            throw new NotAnOdsException("This file doesn't contain a mimetype");
        }
    }

    private void checkMimeType() throws IOException {
        byte[] buff = new byte[CORRECT_MIMETYPE.getBytes().length];
        uncompressor.getInputStream().read(buff);
        String mimetype = new String(buff);
        if (!mimetype.equals(CORRECT_MIMETYPE)) {
            throw new NotAnOdsException("This file doesn't look like an ODS file. Mimetype: " + mimetype);
        }
    }

    private void processContent() throws IOException {
        InputStream in = uncompressor.getInputStream();
        XmlReaderInstance instance = reader.load(in);
        if (instance == null) return;

        if (options.isLoadStyles()) {
            XmlReaderInstance stylesInstance = instance.nextElement("office:automatic-styles", "office:styles");
            stylesParser.parseStyles(stylesInstance);
        }

        XmlReaderInstance contentInstance = instance.nextElement("office:body");
        spreadsheetParser.parseContent(contentInstance);

        reader.close();
    }
}
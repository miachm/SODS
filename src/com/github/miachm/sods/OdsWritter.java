package com.github.miachm.sods;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import static com.github.miachm.sods.OpenDocumentNamespaces.*;

/**
 * Internal class for generate ODS files.
 */
class OdsWritter {

    private final SpreadSheet spread;
    private final Compressor out;
    private final StyleWriter styleWriter;
    private final ChartWriter chartWriter;
    private final SheetWriter sheetWriter;
    private final String MIMETYPE= "application/vnd.oasis.opendocument.spreadsheet";

    private OdsWritter(OutputStream o, SpreadSheet spread) {
        this.spread = spread;
        this.out = new Compressor(o);
        spread.trimSheets();
        this.styleWriter = new StyleWriter(spread);
        this.chartWriter = new ChartWriter(spread);
        this.sheetWriter = new SheetWriter(spread, styleWriter, chartWriter);
    }

    public static void save(OutputStream out, SpreadSheet spread) throws IOException {
        new OdsWritter(out, spread).save();
    }

    private void save() throws IOException {
        writeMymeType();
        writeManifest();
        try {
            writeSpreadsheet();
            styleWriter.writeSettingsStyleFile(out);
            chartWriter.writeCharts(out);
            writeExtraFiles();
        } catch (XMLStreamException e) {
            throw new GenerateOdsException(e);
        }
        out.flush();
        out.close();
    }

    private void writeManifest() {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
            XMLStreamWriter out = XMLOutputFactory.newInstance().createXMLStreamWriter(
                    new OutputStreamWriter(output, "utf-8"));

            out.writeStartDocument("UTF-8", "1.0");
            out.setPrefix("manifest", MANIFEST);
            out.writeStartElement(MANIFEST, "manifest");
            out.writeNamespace("manifest", MANIFEST);
            out.writeAttribute(MANIFEST, "version", "1.2");

            out.writeStartElement(MANIFEST, "file-entry");
            out.writeAttribute(MANIFEST, "full-path", "/");
            out.writeAttribute(MANIFEST, "version", "1.2");
            out.writeAttribute(MANIFEST, "media-type", MIMETYPE);
            out.writeEndElement();

            out.writeStartElement(MANIFEST, "file-entry");
            out.writeAttribute(MANIFEST, "full-path", "content.xml");
            out.writeAttribute(MANIFEST, "media-type", "text/xml");
            out.writeEndElement();

            out.writeStartElement(MANIFEST, "file-entry");
            out.writeAttribute(MANIFEST, "full-path", "styles.xml");
            out.writeAttribute(MANIFEST, "media-type", "text/xml");
            out.writeEndElement();

            chartWriter.appendManifestEntries(out);

            out.writeEndElement();
            out.writeEndDocument();
            out.close();

            byte[] bytes = output.toByteArray();
            this.out.addEntry(bytes, "META-INF/manifest.xml");

        } catch (XMLStreamException | IOException pce) {
            throw new GenerateOdsException(pce);
        }
    }

    private void writeMymeType() throws IOException {
        out.addEntry(MIMETYPE.getBytes(),"mimetype",true);
    }

    private void writeSpreadsheet() throws UnsupportedEncodingException, XMLStreamException {
        ByteArrayOutputStream output = new ByteArrayOutputStream(10*1024);
        XMLStreamWriter out = XMLOutputFactory.newInstance().createXMLStreamWriter(
                new OutputStreamWriter(output, "utf-8"));

        out.writeStartDocument("UTF-8", "1.0");
        out.setPrefix("office", OFFICE);
        out.writeStartElement(OFFICE, "document-content");
        out.writeNamespace("office", OFFICE);
        out.writeNamespace("table", TABLE);
        out.writeNamespace("text", TEXT);
        out.writeNamespace("fo", FONT);
        out.writeNamespace("style", STYLE);
        out.writeNamespace("dc", METADATA);
        out.writeNamespace("number", DATATYPE);
        out.writeNamespace("draw", DRAW);
        out.writeNamespace("svg", SVG);
        out.writeNamespace("xlink", XLINK);
        out.writeNamespace("chart", CHART);

        out.writeAttribute(OFFICE, "version", "1.2");

        styleWriter.writeStyles(out);
        sheetWriter.writeContent(out);

        out.writeEndElement();
        out.writeEndDocument();
        out.close();

        try {
            this.out.addEntry(output.toByteArray(),"content.xml");
        } catch (IOException e) {
            throw new GenerateOdsException(e);
        }
    }

    private void writeExtraFiles() throws IOException {
        for (FileEntry entry : spread.getExtraFiles())
            this.out.addEntry(entry.data, entry.path);
    }
}

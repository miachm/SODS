package com.github.miachm.sods;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final ImageObjectRegistry imageObjectRegistry;
    private final String MIMETYPE= "application/vnd.oasis.opendocument.spreadsheet";
    private final String documentPassword;
    private final Map<String, OdfEncryptionMetadata> encryptionByPath = new HashMap<>();
    private List<FileEntry> macroEntries;

    private OdsWritter(OutputStream o, SpreadSheet spread, String documentPassword) {
        this.spread = spread;
        this.out = new Compressor(o);
        this.documentPassword = documentPassword;
        spread.trimSheets();
        this.styleWriter = new StyleWriter(spread);
        this.chartWriter = new ChartWriter(spread);
        this.sheetWriter = new SheetWriter(spread, styleWriter, chartWriter);
        this.imageObjectRegistry = new ImageObjectRegistry();
    }

    public static void save(OutputStream out, SpreadSheet spread) throws IOException {
        save(out, spread, spread.getDocumentPassword());
    }

    public static void save(OutputStream out, SpreadSheet spread, String documentPassword) throws IOException {
        new OdsWritter(out, spread, documentPassword).save();
    }

    private boolean wroteSettings = false;

    private void save() throws IOException {
        prepareImages();
        writeMymeType();
        try {
            writeSpreadsheet();
            styleWriter.writeSettingsStyleFile(this::addPackageEntry);
            chartWriter.writeCharts(this::addPackageEntry);
            writeExtraFiles();
            writeMacros();
            if (SettingsWriter.hasSettings(spread)) {
                SettingsWriter.writeSettings(spread, this::addPackageEntry);
                wroteSettings = true;
            }
        } catch (XMLStreamException e) {
            throw new GenerateOdsException(e);
        }
        writeManifest();
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

            writeManifestFileEntry(out, "content.xml", "text/xml");
            writeManifestFileEntry(out, "styles.xml", "text/xml");
            if (wroteSettings) writeManifestFileEntry(out, "settings.xml", "text/xml");

            chartWriter.appendManifestEntries(out, this::writeManifestFileEntry);
            appendExtraFileEntries(out);
            appendMacroManifestEntries(out);

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

    private void writeSpreadsheet() throws IOException, XMLStreamException {
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

        addPackageEntry(output.toByteArray(), "content.xml");
    }

    private void writeExtraFiles() throws IOException {
        for (FileEntry entry : imageObjectRegistry.getFiles())
            addPackageEntry(entry.data, entry.path);
    }

    private void writeMacros() throws IOException {
        if (!spread.getMacroRegistry().hasMacros()) return;
        macroEntries = spread.getMacroRegistry().buildZipEntries();
        for (FileEntry entry : macroEntries) {
            addPackageEntry(entry.data, entry.path);
        }
    }

    private void addPackageEntry(byte[] data, String path) throws IOException {
        if (OdfEncryption.shouldEncryptEntry(path, documentPassword)) {
            OdfEncryption.EncryptResult result = OdfEncryption.encrypt(data, documentPassword);
            encryptionByPath.put(path, result.metadata);
            out.addStoredEntry(result.ciphertext, path);
        } else {
            out.addEntry(data, path);
        }
    }

    private void writeManifestFileEntry(XMLStreamWriter out, String path, String mediaType)
            throws XMLStreamException {
        OdfEncryptionMetadata meta = encryptionByPath.get(path);
        out.writeStartElement(MANIFEST, "file-entry");
        out.writeAttribute(MANIFEST, "full-path", path);
        out.writeAttribute(MANIFEST, "media-type", mediaType);
        if (meta != null) {
            out.writeAttribute(MANIFEST, "size", String.valueOf(meta.originalSize));
            ManifestWriter.writeEncryptionData(out, meta);
        }
        out.writeEndElement();
    }

    private void appendMacroManifestEntries(XMLStreamWriter out) throws XMLStreamException {
        if (macroEntries == null) return;
        for (FileEntry entry : macroEntries) {
            if (entry == null || entry.path == null) continue;
            writeManifestFileEntry(out, entry.path,
                    entry.mimetype != null ? entry.mimetype : "");
        }
    }

    private void prepareImages() {
        for (Sheet sheet : spread.getSheets()) {
            for (SheetImage image : sheet.getImages()) {
                imageObjectRegistry.registerImage(image);
            }
        }
    }

    private void appendExtraFileEntries(XMLStreamWriter out) throws XMLStreamException {
        for (FileEntry entry : imageObjectRegistry.getFiles()) {
            if (entry == null || entry.path == null) continue;
            writeManifestFileEntry(out, entry.path,
                    entry.mimetype != null ? entry.mimetype : "");
        }
    }
}

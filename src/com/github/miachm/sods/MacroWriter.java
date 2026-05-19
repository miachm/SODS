package com.github.miachm.sods;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Serialises macros to the {@code Basic/} subtree of an ODS ZIP archive.
 */
class MacroWriter {

    private static final String LIBRARY_NAME = MacroParser.LIBRARY_NAME;
    private static final String LIBRARY_NS = "http://openoffice.org/2000/library";
    private static final String XLINK_NS = "http://www.w3.org/1999/xlink";
    private static final String SCRIPT_NS = "http://openoffice.org/2000/script";
    private static final String SCRIPT_PREFIX = "script";
    private static final String MODULE_TYPE = "normal";
    private static final String BASIC_MARKER = "REM ***** BASIC *****";

    private MacroWriter() {
    }

    /**
     * Builds the list of {@link FileEntry} objects for the full {@code Basic/} subtree.
     *
     * <ul>
     *   <li>{@code Basic/script-lc.xml} — library container index</li>
     *   <li>{@code Basic/Standard/script-lb.xml} — library index</li>
     *   <li>One {@code Basic/Standard/&lt;Name&gt;.xml} per macro</li>
     * </ul>
     */
    static List<FileEntry> buildZipEntries(List<Macro> macros) throws IOException {
        List<FileEntry> entries = new ArrayList<FileEntry>();

        entries.add(new FileEntry(
                "Basic/script-lc.xml",
                "text/xml",
                buildTopLevelIndex()));

        entries.add(new FileEntry(
                "Basic/" + LIBRARY_NAME + "/script-lb.xml",
                "text/xml",
                buildLibraryIndex(macros)));

        for (Macro macro : macros) {
            entries.add(new FileEntry(
                    "Basic/" + LIBRARY_NAME + "/" + macro.getName() + ".xml",
                    "text/xml",
                    buildModuleXml(macro)));
        }

        return entries;
    }

    private static byte[] buildTopLevelIndex() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            XMLStreamWriter w = XMLOutputFactory.newInstance().createXMLStreamWriter(
                    new OutputStreamWriter(baos, "UTF-8"));
            w.writeStartDocument("UTF-8", "1.0");
            w.setPrefix("library", LIBRARY_NS);
            w.writeStartElement(LIBRARY_NS, "libraries");
            w.writeNamespace("library", LIBRARY_NS);
            w.writeNamespace("xlink", XLINK_NS);

            w.writeStartElement(LIBRARY_NS, "library");
            w.writeAttribute(LIBRARY_NS, "name", LIBRARY_NAME);
            w.writeAttribute(XLINK_NS, "href", "Standard/");
            w.writeAttribute(XLINK_NS, "type", "simple");
            w.writeAttribute(LIBRARY_NS, "link", "false");
            w.writeEndElement();

            w.writeEndElement();
            w.writeEndDocument();
            w.close();
        } catch (XMLStreamException e) {
            throw new GenerateOdsException(e);
        }
        return baos.toByteArray();
    }

    private static byte[] buildLibraryIndex(List<Macro> macros) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            XMLStreamWriter w = XMLOutputFactory.newInstance().createXMLStreamWriter(
                    new OutputStreamWriter(baos, "UTF-8"));
            w.writeStartDocument("UTF-8", "1.0");
            w.setPrefix("library", LIBRARY_NS);
            w.writeStartElement(LIBRARY_NS, "library");
            w.writeNamespace("library", LIBRARY_NS);
            w.writeAttribute(LIBRARY_NS, "name", LIBRARY_NAME);
            w.writeAttribute(LIBRARY_NS, "isPasswordProtected", "false");

            for (Macro macro : macros) {
                w.writeStartElement(LIBRARY_NS, "element");
                w.writeAttribute(LIBRARY_NS, "name", macro.getName());
                w.writeAttribute(LIBRARY_NS, "language", macro.getLanguage());
                w.writeEndElement();
            }

            w.writeEndElement();
            w.writeEndDocument();
            w.close();
        } catch (XMLStreamException e) {
            throw new GenerateOdsException(e);
        }
        return baos.toByteArray();
    }

    private static byte[] buildModuleXml(Macro macro) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            XMLStreamWriter w = XMLOutputFactory.newInstance().createXMLStreamWriter(
                    new OutputStreamWriter(baos, "UTF-8"));
            w.writeStartDocument("UTF-8", "1.0");
            w.setPrefix(SCRIPT_PREFIX, SCRIPT_NS);
            w.writeStartElement(SCRIPT_NS, "module");
            w.writeNamespace(SCRIPT_PREFIX, SCRIPT_NS);
            w.writeAttribute(SCRIPT_NS, "name", macro.getName());
            w.writeAttribute(SCRIPT_NS, "language", macro.getLanguage());
            w.writeAttribute(SCRIPT_NS, "moduleType", MODULE_TYPE);
            w.writeCharacters(formatModuleSource(macro));
            w.writeEndElement();
            w.writeEndDocument();
            w.close();
        } catch (XMLStreamException e) {
            throw new GenerateOdsException(e);
        }
        return baos.toByteArray();
    }

    /**
     * Formats StarBasic source the way LibreOffice stores it in module XML
     * (inline text, optional {@code REM ***** BASIC *****} header).
     */
    private static String formatModuleSource(Macro macro) {
        String code = macro.getCode();
        if (!"StarBasic".equals(macro.getLanguage())) {
            return code;
        }
        if (code.contains(BASIC_MARKER)) {
            return code;
        }
        return BASIC_MARKER + "\n" + code;
    }
}

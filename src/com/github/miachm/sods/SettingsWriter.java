package com.github.miachm.sods;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static com.github.miachm.sods.OpenDocumentNamespaces.*;

class SettingsWriter {

    static boolean hasSettings(SpreadSheet spread) {
        for (Sheet sheet : spread.getSheets()) {
            if (sheet.getFrozenRows() > 0 || sheet.getFrozenColumns() > 0) return true;
        }
        return false;
    }

    static void writeSettings(SpreadSheet spread, ChartWriter.PackageEntryWriter entryWriter)
            throws IOException, XMLStreamException {
        ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
        XMLStreamWriter out = javax.xml.stream.XMLOutputFactory.newInstance().createXMLStreamWriter(
                new OutputStreamWriter(output, "utf-8"));

        out.writeStartDocument("UTF-8", "1.0");
        out.setPrefix("office", OFFICE);
        out.writeStartElement(OFFICE, "document-settings");
        out.writeNamespace("office", OFFICE);
        out.writeNamespace("config", CONFIG);
        out.writeAttribute(OFFICE, "version", "1.2");

        out.writeStartElement(OFFICE, "settings");
        out.writeStartElement(CONFIG, "config-item-set");
        out.writeAttribute(CONFIG, "name", "ooo:view-settings");
        out.writeStartElement(CONFIG, "config-item-map-indexed");
        out.writeAttribute(CONFIG, "name", "Views");
        out.writeStartElement(CONFIG, "config-item-map-entry");
        out.writeStartElement(CONFIG, "config-item-map-named");
        out.writeAttribute(CONFIG, "name", "Tables");

        for (Sheet sheet : spread.getSheets()) {
            int frozenRows = sheet.getFrozenRows();
            int frozenCols = sheet.getFrozenColumns();
            if (frozenRows == 0 && frozenCols == 0) continue;

            out.writeStartElement(CONFIG, "config-item-map-entry");
            out.writeAttribute(CONFIG, "name", sheet.getName());

            writeItem(out, "HorizontalSplitMode",     "short", frozenCols > 0 ? "2" : "0");
            writeItem(out, "VerticalSplitMode",       "short", frozenRows > 0 ? "2" : "0");
            writeItem(out, "HorizontalSplitPosition", "int",   String.valueOf(frozenCols));
            writeItem(out, "VerticalSplitPosition",   "int",   String.valueOf(frozenRows));
            writeItem(out, "PositionRight",           "int",   String.valueOf(frozenCols));
            writeItem(out, "PositionBottom",          "int",   String.valueOf(frozenRows));

            out.writeEndElement(); // config-item-map-entry (sheet)
        }

        out.writeEndElement(); // config-item-map-named (Tables)
        out.writeEndElement(); // config-item-map-entry (view)
        out.writeEndElement(); // config-item-map-indexed (Views)
        out.writeEndElement(); // config-item-set
        out.writeEndElement(); // settings
        out.writeEndElement(); // document-settings
        out.writeEndDocument();
        out.close();

        entryWriter.write(output.toByteArray(), "settings.xml");
    }

    private static void writeItem(XMLStreamWriter out, String name, String type, String value)
            throws XMLStreamException {
        out.writeStartElement(CONFIG, "config-item");
        out.writeAttribute(CONFIG, "name", name);
        out.writeAttribute(CONFIG, "type", type);
        out.writeCharacters(value);
        out.writeEndElement();
    }
}

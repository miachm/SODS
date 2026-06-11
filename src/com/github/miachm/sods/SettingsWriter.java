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
        out.writeNamespace("ooo", OOO);
        out.writeNamespace("config", CONFIG);
        out.writeAttribute(OFFICE, "version", "1.2");

        out.writeStartElement(OFFICE, "settings");

        // --- ooo:view-settings ---
        out.writeStartElement(CONFIG, "config-item-set");
        out.writeAttribute(CONFIG, "name", "ooo:view-settings");

        // LibreOffice anchors the Views block with a visible area.
        writeItem(out, "VisibleAreaTop",    "int", "0");
        writeItem(out, "VisibleAreaLeft",   "int", "0");
        writeItem(out, "VisibleAreaWidth",  "int", "9031");
        writeItem(out, "VisibleAreaHeight", "int", "2258");

        out.writeStartElement(CONFIG, "config-item-map-indexed");
        out.writeAttribute(CONFIG, "name", "Views");
        out.writeStartElement(CONFIG, "config-item-map-entry");
        writeItem(out, "ViewId", "string", "view1");
        out.writeStartElement(CONFIG, "config-item-map-named");
        out.writeAttribute(CONFIG, "name", "Tables");

        // LibreOffice needs a Tables entry for every sheet (not only frozen ones),
        // and each entry must carry the full set of view items it expects, otherwise
        // it discards the entry and the freeze is not applied.
        for (Sheet sheet : spread.getSheets()) {
            writeSheetView(out, sheet);
        }

        out.writeEndElement(); // config-item-map-named (Tables)

        // View-level display settings. ActiveTable selects the sheet shown on open.
        writeItem(out, "ActiveTable",             "string",  spread.getSheets().get(0).getName());
        writeItem(out, "HorizontalScrollbarWidth", "int",    "3754");
        writeItem(out, "ZoomType",                "short",   "0");
        writeItem(out, "ZoomValue",               "int",     "100");
        writeItem(out, "PageViewZoomValue",       "int",     "60");
        writeItem(out, "ShowZeroValues",          "boolean", "true");
        writeItem(out, "ShowNotes",               "boolean", "true");
        writeItem(out, "ShowGrid",                "boolean", "true");
        writeItem(out, "HasColumnRowHeaders",     "boolean", "true");
        writeItem(out, "HasSheetTabs",            "boolean", "true");
        writeItem(out, "IsOutlineSymbolsSet",     "boolean", "true");

        out.writeEndElement(); // config-item-map-entry (view)
        out.writeEndElement(); // config-item-map-indexed (Views)
        out.writeEndElement(); // config-item-set (view-settings)

        // --- ooo:configuration-settings ---
        out.writeStartElement(CONFIG, "config-item-set");
        out.writeAttribute(CONFIG, "name", "ooo:configuration-settings");
        writeItem(out, "HasSheetTabs",        "boolean", "true");
        writeItem(out, "HasColumnRowHeaders", "boolean", "true");
        writeItem(out, "ShowGrid",            "boolean", "true");
        writeItem(out, "ShowZeroValues",      "boolean", "true");
        out.writeEndElement(); // config-item-set (configuration-settings)

        out.writeEndElement(); // settings
        out.writeEndElement(); // document-settings
        out.writeEndDocument();
        out.close();

        entryWriter.write(output.toByteArray(), "settings.xml");
    }

    private static void writeSheetView(XMLStreamWriter out, Sheet sheet) throws XMLStreamException {
        int frozenRows = sheet.getFrozenRows();
        int frozenCols = sheet.getFrozenColumns();

        // ActiveSplitRange: 3 = right pane active (column freeze, or both),
        // 0 = lower-left pane active (row-only freeze), 2 = no split.
        int activeSplitRange = frozenCols > 0 ? 3 : (frozenRows > 0 ? 0 : 2);

        out.writeStartElement(CONFIG, "config-item-map-entry");
        out.writeAttribute(CONFIG, "name", sheet.getName());

        writeItem(out, "CursorPositionX",         "int",   String.valueOf(frozenCols));
        writeItem(out, "CursorPositionY",         "int",   String.valueOf(frozenRows));
        writeItem(out, "HorizontalSplitMode",     "short", frozenCols > 0 ? "2" : "0");
        writeItem(out, "VerticalSplitMode",       "short", frozenRows > 0 ? "2" : "0");
        writeItem(out, "HorizontalSplitPosition", "int",   String.valueOf(frozenCols));
        writeItem(out, "VerticalSplitPosition",   "int",   String.valueOf(frozenRows));
        writeItem(out, "ActiveSplitRange",        "short", String.valueOf(activeSplitRange));
        writeItem(out, "PositionLeft",            "int",   "0");
        writeItem(out, "PositionRight",           "int",   String.valueOf(frozenCols));
        writeItem(out, "PositionTop",             "int",   "0");
        writeItem(out, "PositionBottom",          "int",   String.valueOf(frozenRows));
        writeItem(out, "ZoomType",                "short", "0");
        writeItem(out, "ZoomValue",               "int",   "100");
        writeItem(out, "PageViewZoomValue",       "int",   "60");
        writeItem(out, "ShowGrid",                "boolean", "true");

        out.writeEndElement(); // config-item-map-entry (sheet)
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

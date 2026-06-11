package com.github.miachm.sods;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.github.miachm.sods.OpenDocumentNamespaces.*;

class StyleWriter {
    private final SpreadSheet spread;
    private final Map<Style, String> stylesUsed = new HashMap<>();
    private final Map<Double, String> columnStyleStringMap = new HashMap<>();
    private final Map<Double, String> rowStyleStringMap = new HashMap<>();
    private final Map<TableStyle, String> tableStyleStringMap = new HashMap<>();

    StyleWriter(SpreadSheet spread) {
        this.spread = spread;
    }

    void writeSettingsStyleFile(ChartWriter.PackageEntryWriter entryWriter)
            throws UnsupportedEncodingException, XMLStreamException {
        ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
        XMLStreamWriter xmlWriter = javax.xml.stream.XMLOutputFactory.newInstance().createXMLStreamWriter(
                new OutputStreamWriter(output, "utf-8"));
        xmlWriter.writeStartDocument("UTF-8", "1.0");
        xmlWriter.setPrefix("office", OFFICE);
        xmlWriter.writeStartElement(OFFICE, "document-styles");
        xmlWriter.writeNamespace("office", OFFICE);
        xmlWriter.writeNamespace("style", STYLE);
        xmlWriter.writeNamespace("fo", FONT);
        xmlWriter.writeAttribute(OFFICE, "version", "1.2");
        xmlWriter.writeStartElement(OFFICE, "styles");
        // Write conditional formatting target styles as common styles
        for (Style style : stylesUsed.keySet()) {
            if (!style.getConditions().isEmpty()) {
                for (ConditionalFormat format : style.getConditions()) {
                    Style targetStyle = format.getStyleApplied();
                    String targetKey = stylesUsed.get(targetStyle);
                    if (targetKey != null) {
                        writeCellStyle(xmlWriter, targetStyle, targetKey);
                    }
                }
            }
        }
        xmlWriter.writeEndElement();
        xmlWriter.writeEndElement();
        xmlWriter.writeEndDocument();
        xmlWriter.close();

        try {
            entryWriter.write(output.toByteArray(), "styles.xml");
        } catch (IOException e) {
            throw new GenerateOdsException(e);
        }
    }

    void writeFontFaceDecls(XMLStreamWriter out) throws XMLStreamException {
        java.util.Set<String> fonts = new java.util.LinkedHashSet<>();
        for (Sheet sheet : spread.getSheets()) {
            for (Row row : sheet.getRowsInternal()) {
                for (Cell cell : row.cells) {
                    String ff = cell.getStyle().getFontFamily();
                    if (ff != null) fonts.add(ff);
                }
            }
        }
        if (fonts.isEmpty()) return;
        out.writeStartElement(OFFICE, "font-face-decls");
        for (String font : fonts) {
            out.writeEmptyElement(STYLE, "font-face");
            out.writeAttribute(STYLE, "name", font);
            out.writeAttribute(SVG, "font-family", font);
        }
        out.writeEndElement();
    }

    void writeStyles(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(OFFICE, "automatic-styles");

        writeDataFormatStyles(out);

        for (Sheet sheet : spread.getSheets()) {
            for (Row row : sheet.getRowsInternal()) {
                for (Cell cell : row.cells) {
                    Style style = cell.getStyle();

                    if (!style.isDefault()) {
                        writeCellStyle(out, style);
                    }
                }

                Double height = row.row_style.getHeight();
                if (height != null) {
                    writeRowStyle(out, height);
                }
            }

            for (Column column : sheet.getColumnsInternal()) {
                Double width = column.column_style.getWidth();
                if (width != null) {
                    writeColumnStyle(out, width);
                }

                Style defaultCellStyle = column.column_style.getDefaultCellStyleDangerous();
                if (!defaultCellStyle.isDefault()) {
                    writeCellStyle(out, defaultCellStyle);
                }
            }

            if (sheet.isHidden() || sheet.getTabColor() != null) {
                writeTableStyle(out, sheet);
            }
        }
        out.writeEndElement();
    }

    void setCellStyle(XMLStreamWriter out, Style style) throws XMLStreamException {
        if (!style.isDefault()) {
            String key = stylesUsed.computeIfAbsent(style, k -> "cel" + stylesUsed.size());
            out.writeAttribute(TABLE, "style-name", key);
        }
    }

    String getColumnStyleName(Double width) {
        return columnStyleStringMap.get(width);
    }

    String getRowStyleName(Double height) {
        return rowStyleStringMap.get(height);
    }

    String getTableStyleName(TableStyle tableStyle) {
        return tableStyleStringMap.get(tableStyle);
    }

    String getCellStyleName(Style style) {
        return stylesUsed.get(style);
    }

    private void writeDataFormatStyles(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement("number:text-style");
        out.writeAttribute(STYLE, "name", "textstyle");
        out.writeEmptyElement("number:text-content");
        out.writeEndElement();

        out.writeStartElement("number:date-style");
        out.writeAttribute(STYLE, "name", "datestyle");
        out.writeStartElement("number:year");
        out.writeAttribute("number:style", "long");
        out.writeEndElement();
        out.writeStartElement("number:text");
        out.writeCharacters("-");
        out.writeEndElement();
        out.writeStartElement("number:month");
        out.writeAttribute("number:style", "long");
        out.writeEndElement();
        out.writeStartElement("number:text");
        out.writeCharacters("-");
        out.writeEndElement();
        out.writeEmptyElement("number:day");
        out.writeEndElement();
    }

    private void writeCellStyle(XMLStreamWriter out, Style style) throws XMLStreamException {
        String key = stylesUsed.get(style);
        if (key == null)
        {
            key = "cel" + stylesUsed.size();
            stylesUsed.put(style, key);
            writeCellStyle(out, style, key);
        }
    }

    private void writeCellStyle(XMLStreamWriter out, Style style, String key) throws XMLStreamException {
        for (ConditionalFormat conditionalFormat : style.getConditions()) {
            writeCellStyle(out, conditionalFormat.getStyleApplied());
        }
        out.writeStartElement(STYLE, "style");
        out.writeAttribute(STYLE, "family", "table-cell");
        out.writeAttribute(STYLE, "name", key);

        String dataStyle = style.getDataStyle();
        if (Style.PLAIN_DATA_STYLE.equals(dataStyle))
            out.writeAttribute(STYLE, "data-style-name", "textstyle");
        else if (Style.ISO_DATE_DATA_STYLE.equals(dataStyle))
            out.writeAttribute(STYLE, "data-style-name", "datestyle");

        if (style.hasTableCellProperties()) {
            out.writeStartElement(STYLE, "table-cell-properties");

            if (style.getBackgroundColor() != null) {
                out.writeAttribute(FONT, "background-color", style.getBackgroundColor().toString());
            }

            if (style.isWrap()) {
                out.writeAttribute(FONT, "wrap-option", "wrap");
            }

            if (style.getVerticalTextAligment() != null) {
                out.writeAttribute(STYLE, "vertical-align", style.getVerticalTextAligment().toString().toLowerCase());
            }

            if(style.hasBorders()) {
                writeBorderStyle(out, style);
            }

            out.writeEndElement();
        }

        for (ConditionalFormat format : style.getConditions()) {
            String targetStyleName = getConditionalFormatName(format.getStyleApplied());
            if (targetStyleName != null) {
                out.writeStartElement(STYLE, "map");
                out.writeAttribute(STYLE, "condition", format.getRawCondition());
                out.writeAttribute(STYLE, "apply-style-name", targetStyleName);
                out.writeEndElement();
            }
        }

        out.writeStartElement(STYLE, "text-properties");
        if (style.isItalic())
            out.writeAttribute(FONT, "font-style", "italic");

        if (style.isBold())
            out.writeAttribute(FONT, "font-weight", "bold");

        if (style.isUnderline()) {
            out.writeAttribute(STYLE, "text-underline-style", "solid");
            out.writeAttribute(STYLE, "text-underline-type", "single");
            out.writeAttribute(STYLE, "text-underline-width", "auto");
            out.writeAttribute(STYLE, "text-underline-color", "font-color");
        }

        if (style.isLineThrough()) {
            out.writeAttribute(STYLE, "text-line-through-style", "solid");
            out.writeAttribute(STYLE, "text-line-through-type", "single");
            out.writeAttribute(STYLE, "text-line-through-width", "auto");
            out.writeAttribute(STYLE, "text-line-through-color", "font-color");
        }

        if (style.getFontSize() != -1)
            out.writeAttribute(FONT, "font-size", "" + style.getFontSize() + "pt");

        if (style.getFontColor() != null)
            out.writeAttribute(FONT, "color", style.getFontColor().toString());

        if (style.getFontFamily() != null)
            out.writeAttribute(STYLE, "font-name", style.getFontFamily());

        out.writeEndElement();

        if(style.getTextAligment() != null) {
            out.writeStartElement(STYLE, "paragraph-properties");
            out.writeAttribute(FONT, "text-align", toValue(style.getTextAligment()));
            out.writeEndElement();
        }

        out.writeEndElement();
    }

    private String getConditionalFormatName(Style style) {
        return stylesUsed.get(style);
    }

    private String toValue(Style.TEXT_ALIGMENT textAligment) {
        switch (textAligment)
        {
            case Left:
                return "start";
            case Center:
                return "center";
            case Right:
                return "end";
        }
        return null;
    }

    private void writeColumnStyle(XMLStreamWriter out, Double width) throws XMLStreamException {
        if (!columnStyleStringMap.containsKey(width)) {
            String key = "co" + columnStyleStringMap.size();

            out.writeStartElement(STYLE, "style");
            out.writeAttribute(STYLE, "family", "table-column");
            out.writeAttribute(STYLE, "name", key);
            out.writeStartElement(STYLE, "table-column-properties");
            out.writeAttribute(STYLE, "column-width", width.toString() + "mm");
            out.writeEndElement();
            out.writeEndElement();

            columnStyleStringMap.put(width, key);
        }
    }

    private void writeRowStyle(XMLStreamWriter out, Double height) throws XMLStreamException {
        if (!rowStyleStringMap.containsKey(height)) {
            String key = "ro" + rowStyleStringMap.size();
            out.writeStartElement(STYLE, "style");
            out.writeAttribute(STYLE, "family", "table-row");
            out.writeAttribute(STYLE, "name", key);
            out.writeStartElement(STYLE, "table-row-properties");
            out.writeAttribute(STYLE, "row-height", height.toString() + "mm");
            out.writeEndElement();
            out.writeEndElement();

            rowStyleStringMap.put(height, key);
        }
    }

    private void writeTableStyle(XMLStreamWriter out, Sheet sheet) throws XMLStreamException {
        TableStyle tableStyle = new TableStyle();
        tableStyle.setHidden(sheet.isHidden());
        tableStyle.setTabColor(sheet.getTabColor());
        if (!tableStyleStringMap.containsKey(tableStyle)) {
            String key = "tb" + tableStyleStringMap.size();
            out.writeStartElement(STYLE, "style");
            out.writeAttribute(STYLE, "family", "table");
            out.writeAttribute(STYLE, "name", key);
            out.writeStartElement(STYLE, "table-properties");
            out.writeAttribute(TABLE, "display", tableStyle.isHidden() ? "false" : "true");
            if (tableStyle.getTabColor() != null)
                out.writeAttribute(TABLE, "tab-color", tableStyle.getTabColor().toString());
            out.writeEndElement();
            out.writeEndElement();

            tableStyleStringMap.put(tableStyle, key);
        }
    }

    private void writeBorderStyle(XMLStreamWriter out, Style style) throws XMLStreamException {

        Borders borders = style.getBorders();
        if (borders.isBorder()) {
            out.writeAttribute(FONT, "border", borders.getBorderProperties());
        }

        if (borders.isBorderTop()) {
            out.writeAttribute(FONT, "border-top", borders.getBorderTopProperties());
        }

        if (borders.isBorderBottom()) {
            out.writeAttribute(FONT, "border-bottom", borders.getBorderBottomProperties());
        }

        if (borders.isBorderLeft()) {
            out.writeAttribute(FONT, "border-left", borders.getBorderLeftProperties());
        }

        if (borders.isBorderRight()) {
            out.writeAttribute(FONT, "border-right", borders.getBorderRightProperties());
        }
    }
}

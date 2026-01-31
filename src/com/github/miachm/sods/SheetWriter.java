package com.github.miachm.sods;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static com.github.miachm.sods.OpenDocumentNamespaces.*;

class SheetWriter {
    private final SpreadSheet spread;
    private final StyleWriter styleWriter;
    private final ChartWriter chartWriter;

    SheetWriter(SpreadSheet spread, StyleWriter styleWriter, ChartWriter chartWriter) {
        this.spread = spread;
        this.styleWriter = styleWriter;
        this.chartWriter = chartWriter;
    }

    void writeContent(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(OFFICE, "body");
        out.writeStartElement(OFFICE, "spreadsheet");

        for (Sheet sheet : spread.getSheets()) {
            out.writeStartElement(TABLE, "table");
            out.writeAttribute(TABLE, "name", sheet.getName());
            if (sheet.isHidden()) {
                TableStyle tableStyle = new TableStyle();
                tableStyle.setHidden(true);
                String name = styleWriter.getTableStyleName(tableStyle);
                if (name != null)
                    out.writeAttribute(TABLE, "style-name", name);
            }
            if (sheet.isProtected()) {
                out.writeAttribute(TABLE, "protected", "true");
                out.writeAttribute(TABLE, "protection-key", sheet.getHashedPassword());
                out.writeAttribute(TABLE, "protection-key-digest-algorithm", sheet.getHashedAlgorithm());
            }

            chartWriter.writeDrawFrames(out, sheet);
            writeColumnsStyles(out, sheet);
            writeRows(out, sheet);

            out.writeEndElement();
        }

        out.writeEndElement();
        out.writeEndElement();
    }

    private void writeColumnsStyles(XMLStreamWriter out, Sheet sheet) throws XMLStreamException {
        for (Column column : sheet.columns){
            out.writeStartElement(TABLE, "table-column");
            if (column.num_repeated > 1)
                out.writeAttribute(TABLE, "number-columns-repeated", "" + column.num_repeated);

            Double width = column.column_style.getWidth();
            if (width != null) {
                String name = styleWriter.getColumnStyleName(width);
                if (name != null)
                    out.writeAttribute(TABLE, "style-name", name);
            }

            if (column.column_style.isHidden())
                out.writeAttribute(TABLE, "visibility", "collapse");

            Style defaultCellStyle = column.column_style.getDefaultCellStyleDangerous();
            if (!defaultCellStyle.isDefault()) {
                String name = styleWriter.getCellStyleName(defaultCellStyle);
                if (name != null)
                    out.writeAttribute(TABLE, "default-cell-style-name", name);
            }

            out.writeEndElement();
        }
    }

    private void writeRows(XMLStreamWriter out, Sheet sheet) throws XMLStreamException {
        for (Row row : sheet.rows) {
            out.writeStartElement(TABLE, "table-row");
            if (row.num_repeated > 1)
                out.writeAttribute(TABLE, "number-rows-repeated", ""+row.num_repeated);
            writeRowStyles(out, row);

            for (Cell cell :  row.cells) {
                writeCell(out, cell);
            }

            out.writeEndElement();
        }
    }

    private void writeRowStyles(XMLStreamWriter out, Row row) throws XMLStreamException {
        if (row.row_style.isHidden())
            out.writeAttribute(TABLE, "visibility", "collapse");

        writeRowHeight(out, row);
    }

    private void writeCell(XMLStreamWriter out, Cell cell) throws XMLStreamException {
        String formula = cell.getFormula();
        Style style = cell.getStyle();

        GroupCell group = cell.getGroup();
        if (group != null) {
            if (group.getCell() != cell) {
                out.writeStartElement(TABLE, "covered-table-cell");
                out.writeEndElement();
                return;
            }
        }
        out.writeStartElement(TABLE, "table-cell");
        if (cell.num_repeated > 1)
            out.writeAttribute(TABLE, "number-columns-repeated", ""+ cell.num_repeated);
        if (group != null) {
            if (group.getLength().getY() > 1)
                out.writeAttribute(TABLE, "number-columns-spanned", "" + group.getLength().getY());
            if (group.getLength().getX() > 1)
                out.writeAttribute(TABLE, "number-rows-spanned", "" + group.getLength().getX());
        }

        if (formula != null)
            out.writeAttribute(TABLE, "formula", formula);

        styleWriter.setCellStyle(out, style);
        writeValue(out, cell);
        out.writeEndElement();
    }

    private void writeValue(XMLStreamWriter out, Cell cell) throws XMLStreamException {
        Object v = cell.getValue();
        if (v != null) {
            OfficeValueType valueType = OfficeValueType.ofJavaType(v.getClass());
            /*
                This fixes issue #65.
                LibreOffice only writes the "string-value" attribute for formulaic cells. Writing it for non-formulaic
                cells makes LibreOffice discard newlines when opening the sheet.
             */
            if (valueType != OfficeValueType.STRING || cell.getFormula() != null) {
                valueType.write(v, out);
            }
            else if (valueType == OfficeValueType.STRING) {
                out.writeAttribute(OFFICE, "value-type", "string");
            }

            out.writeStartElement(TEXT, "p");
            String text = v.toString();

            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) == ' ') {
                    out.writeStartElement(TEXT, "s");
                    int cnt = 0;
                    while (i+cnt < text.length() && text.charAt(i + cnt) == ' ') {
                        cnt++;
                    }
                    if (cnt > 1)
                        out.writeAttribute(TEXT, "c", "" + cnt);
                    i += cnt - 1 ;
                    out.writeEndElement();
                }
                else if (text.charAt(i) == '\t') {
                    out.writeEmptyElement(TEXT, "tab");
                }
                else if (text.charAt(i) == '\n') {
                    out.writeEmptyElement(TEXT, "line-break");
                }
                else if (Character.isHighSurrogate(text.charAt(i)) && i + 1 < text.length() && Character.isLowSurrogate(text.charAt(i + 1))) {
                    // write surrogate pair
                    out.writeCharacters("" + text.charAt(i) + text.charAt(i + 1));
                    i++;
                }
                else
                    out.writeCharacters("" + text.charAt(i));
            }

            out.writeEndElement();
        }
        OfficeAnnotation annotation = cell.getAnnotation();
        if (annotation != null) {
            out.writeStartElement(OFFICE, "annotation");
            if (annotation.getLastModified() != null) {
                out.writeStartElement(METADATA,  "date");
                out.writeCharacters(annotation.getLastModified().toString());
                out.writeEndElement();
            }
            if (annotation.getMsg() != null) {
                out.writeStartElement(TEXT, "p");
                out.writeCharacters(annotation.getMsg());
                out.writeEndElement();
            }
            out.writeEndElement();
        }
    }

    private void writeRowHeight(XMLStreamWriter out, Row row) throws XMLStreamException {
        Double height = row.row_style.getHeight();
        if (height != null) {
            String name = styleWriter.getRowStyleName(height);
            if (name != null)
                out.writeAttribute(TABLE, "style-name", name);
        }
    }
}

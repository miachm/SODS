package com.github.miachm.sods;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            if (sheet.isHidden() || sheet.getTabColor() != null) {
                TableStyle tableStyle = new TableStyle();
                tableStyle.setHidden(sheet.isHidden());
                tableStyle.setTabColor(sheet.getTabColor());
                String name = styleWriter.getTableStyleName(tableStyle);
                if (name != null)
                    out.writeAttribute(TABLE, "style-name", name);
            }
            if (sheet.isProtected()) {
                out.writeAttribute(TABLE, "protected", "true");
                out.writeAttribute(TABLE, "protection-key", sheet.getHashedPassword());
                out.writeAttribute(TABLE, "protection-key-digest-algorithm", sheet.getHashedAlgorithm());
            }

            writeShapes(out, sheet);
            writeColumnsStyles(out, sheet);
            writeRows(out, sheet);

            out.writeEndElement();
        }

        out.writeEndElement();
        out.writeEndElement();
    }

    private void writeColumnsStyles(XMLStreamWriter out, Sheet sheet) throws XMLStreamException {
        for (Column column : sheet.getColumnsInternal()){
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
        Map<Long, List<SheetImage>> anchoredImages = buildAnchoredImageMap(sheet);
        int rowIndex = 0;
        for (Row row : sheet.getRowsInternal()) {
            int repeatRows = Math.max(1, row.num_repeated);
            if (repeatRows > 1 && hasImagesInRowSpan(anchoredImages, rowIndex, repeatRows)) {
                for (int i = 0; i < repeatRows; i++) {
                    writeRow(out, row, 1, rowIndex + i, anchoredImages);
                }
            } else {
                writeRow(out, row, repeatRows, rowIndex, anchoredImages);
            }
            rowIndex += repeatRows;
        }
    }

    private void writeRow(XMLStreamWriter out, Row row, int repeatRows, int rowIndex,
                          Map<Long, List<SheetImage>> anchoredImages) throws XMLStreamException {
        out.writeStartElement(TABLE, "table-row");
        if (repeatRows > 1) {
            out.writeAttribute(TABLE, "number-rows-repeated", "" + repeatRows);
        }
        writeRowStyles(out, row);

        int columnIndex = 0;
        for (Cell cell : row.cells) {
            int repeatColumns = Math.max(1, cell.num_repeated);
            if (repeatColumns > 1 && hasImagesInColumnSpan(anchoredImages, rowIndex, columnIndex, repeatColumns)) {
                for (int i = 0; i < repeatColumns; i++) {
                    writeCell(out, cell, anchoredImages, rowIndex, columnIndex + i, 1);
                }
            } else {
                writeCell(out, cell, anchoredImages, rowIndex, columnIndex, repeatColumns);
            }
            columnIndex += repeatColumns;
        }

        out.writeEndElement();
    }

    private void writeShapes(XMLStreamWriter out, Sheet sheet) throws XMLStreamException {
        List<SheetImage> images = sheet.getImages();
        List<SheetImage> unanchoredImages = filterUnanchoredImages(images);
        boolean hasImages = !unanchoredImages.isEmpty();
        boolean hasCharts = chartWriter.hasCharts(sheet);
        if (!hasImages && !hasCharts) {
            return;
        }
        out.writeStartElement(TABLE, "shapes");
        int zIndex = 0;
        if (hasCharts) {
            zIndex = chartWriter.writeDrawFramesContent(out, sheet, zIndex);
        }
        if (hasImages) {
            writeImageFrames(out, unanchoredImages, zIndex);
        }
        out.writeEndElement();
    }

    private void writeImageFrames(XMLStreamWriter out, List<SheetImage> images, int startZIndex) throws XMLStreamException {
        int zIndex = startZIndex;
        for (SheetImage image : images) {
            if (image == null) continue;
            String path = image.getPath();
            if (path == null) continue;
            out.writeStartElement(DRAW, "frame");
            out.writeAttribute(DRAW, "z-index", String.valueOf(zIndex++));
            if (image.getName() != null) {
                out.writeAttribute(DRAW, "name", image.getName());
            }
            out.writeAttribute(SVG, "width", normalizeSize(image.getWidth(), "5cm"));
            out.writeAttribute(SVG, "height", normalizeSize(image.getHeight(), "5cm"));
            out.writeAttribute(SVG, "x", normalizeSize(image.getX(), "0cm"));
            out.writeAttribute(SVG, "y", normalizeSize(image.getY(), "0cm"));

            out.writeStartElement(DRAW, "image");
            out.writeAttribute(XLINK, "href", path);
            out.writeAttribute(XLINK, "type", "simple");
            out.writeAttribute(XLINK, "show", "embed");
            out.writeAttribute(XLINK, "actuate", "onLoad");
            if (image.getMimeType() != null) {
                out.writeAttribute(DRAW, "mime-type", image.getMimeType());
            }
            out.writeEndElement();

            out.writeEndElement();
        }
    }

    private String normalizeSize(String size, String fallback) {
        if (size == null || size.trim().isEmpty()) {
            return fallback;
        }
        String trimmed = size.trim();
        if (trimmed.endsWith("cm") || trimmed.endsWith("mm") || trimmed.endsWith("in") || trimmed.endsWith("pt")) {
            return trimmed;
        }
        return trimmed + "cm";
    }

    private void writeRowStyles(XMLStreamWriter out, Row row) throws XMLStreamException {
        if (row.row_style.isHidden())
            out.writeAttribute(TABLE, "visibility", "collapse");

        writeRowHeight(out, row);
    }

    private void writeCell(XMLStreamWriter out, Cell cell, Map<Long, List<SheetImage>> anchoredImages,
                           int rowIndex, int columnIndex, int repeatColumns) throws XMLStreamException {
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
        if (repeatColumns > 1)
            out.writeAttribute(TABLE, "number-columns-repeated", ""+ repeatColumns);
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
        writeCellImages(out, anchoredImages, rowIndex, columnIndex);
        out.writeEndElement();
    }

    private void writeValue(XMLStreamWriter out, Cell cell) throws XMLStreamException {
        if (!cell.hasLinkedValues()) {
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
        }
        else {
            List<LinkedValue> links = cell.getLinkedValues();
            if (links != null && !links.isEmpty()) {
                OfficeValueType.ofJavaType(String.class).write("", out);

                for (LinkedValue link : links) {
                    out.writeStartElement(TEXT, "p");
                    out.writeStartElement(TEXT, "a");
                    out.writeAttribute(XLINK, "href", link.getHref());
                    out.writeAttribute(XLINK, "type", "simple");
                    out.writeCharacters(link.getText());
                    out.writeEndElement();
                    out.writeEndElement();
                }
            }
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
                String[] lines = annotation.getMsg().split("\n", -1);
                for (String line : lines) {
                    out.writeStartElement(TEXT, "p");
                    if (!line.isEmpty())
                        out.writeCharacters(line);
                    out.writeEndElement();
                }
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

    private Map<Long, List<SheetImage>> buildAnchoredImageMap(Sheet sheet) {
        Map<Long, List<SheetImage>> map = new HashMap<>();
        List<SheetImage> images = sheet.getImages();
        if (images == null) {
            return map;
        }
        for (SheetImage image : images) {
            if (image == null) continue;
            Integer row = image.getAnchorRow();
            Integer column = image.getAnchorColumn();
            if (row == null || column == null) {
                continue;
            }
            long key = anchorKey(row, column);
            List<SheetImage> list = map.computeIfAbsent(key, k -> new ArrayList<>());
            list.add(image);
        }
        return map;
    }

    private void writeCellImages(XMLStreamWriter out, Map<Long, List<SheetImage>> anchoredImages,
                                 int rowIndex, int columnIndex) throws XMLStreamException {
        if (anchoredImages == null || anchoredImages.isEmpty()) {
            return;
        }
        List<SheetImage> images = anchoredImages.get(anchorKey(rowIndex, columnIndex));
        if (images == null || images.isEmpty()) {
            return;
        }
        for (SheetImage image : images) {
            if (image == null) continue;
            String path = image.getPath();
            if (path == null) continue;
            out.writeStartElement(DRAW, "frame");
            if (image.getName() != null) {
                out.writeAttribute(DRAW, "name", image.getName());
            }
            out.writeAttribute(SVG, "width", normalizeSize(image.getWidth(), "5cm"));
            out.writeAttribute(SVG, "height", normalizeSize(image.getHeight(), "5cm"));
            out.writeAttribute(SVG, "x", normalizeSize(image.getX(), "0cm"));
            out.writeAttribute(SVG, "y", normalizeSize(image.getY(), "0cm"));

            out.writeStartElement(DRAW, "image");
            out.writeAttribute(XLINK, "href", path);
            out.writeAttribute(XLINK, "type", "simple");
            out.writeAttribute(XLINK, "show", "embed");
            out.writeAttribute(XLINK, "actuate", "onLoad");
            if (image.getMimeType() != null) {
                out.writeAttribute(DRAW, "mime-type", image.getMimeType());
            }
            out.writeEndElement();

            out.writeEndElement();
        }
    }

    private long anchorKey(int row, int column) {
        return (((long) row) << 32) | (column & 0xffffffffL);
    }

    private boolean hasImagesInColumnSpan(Map<Long, List<SheetImage>> anchoredImages, int row,
                                          int columnStart, int columns) {
        if (anchoredImages == null || anchoredImages.isEmpty()) {
            return false;
        }
        for (int i = 0; i < columns; i++) {
            if (anchoredImages.containsKey(anchorKey(row, columnStart + i))) {
                return true;
            }
        }
        return false;
    }

    private boolean hasImagesInRowSpan(Map<Long, List<SheetImage>> anchoredImages, int rowStart, int rows) {
        if (anchoredImages == null || anchoredImages.isEmpty()) {
            return false;
        }
        for (int i = 0; i < rows; i++) {
            int row = rowStart + i;
            for (Long key : anchoredImages.keySet()) {
                if ((key >> 32) == row) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<SheetImage> filterUnanchoredImages(List<SheetImage> images) {
        if (images == null || images.isEmpty()) {
            return new ArrayList<>();
        }
        List<SheetImage> unanchored = new ArrayList<>();
        for (SheetImage image : images) {
            if (image == null) continue;
            if (image.getAnchorRow() == null || image.getAnchorColumn() == null) {
                unanchored.add(image);
            }
        }
        return unanchored;
    }
}

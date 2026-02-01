package com.github.miachm.sods;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

class SheetParser {
    private static final int BUGGED_COUNT = 10 * 1000;
    private final Sheet sheet;
    private final StylesParser stylesParser;
    private final SpreadSheet spread;
    private final OdsOptionParameters options;
    private final Map<Integer, Style> columnDefaultStyles = new HashMap<>();
    private final Map<Integer, Style> rowDefaultStyles = new HashMap<>();
    private final Set<Pair<Vector, Vector>> groupCells = new HashSet<>();

    public SheetParser(Sheet sheet, StylesParser stylesParser, SpreadSheet spread, OdsOptionParameters options) {
        this.sheet = sheet;
        this.stylesParser = stylesParser;
        this.spread = spread;
        this.options = options;
    }

    public void parseSheet(XmlReaderInstance reader) {
        String tableStyleName = reader.getAttribValue("table:style-name");
        if (tableStyleName != null) setTableStyles(tableStyleName);

        String protectedSheet = reader.getAttribValue("table:protected");
        if (protectedSheet != null) {
            String algorithm = reader.getAttribValue("table:protection-key-digest-algorithm");
            if (algorithm == null) algorithm = "http://www.w3.org/2000/09/xmldsig#sha1";
            String protectedKey = reader.getAttribValue("table:protection-key");
            sheet.setRawPassword(protectedKey, algorithm);
        }

        int rowCount = 0;
        groupCells.clear();

        while (reader.hasNext()) {
            XmlReaderInstance instance = reader.nextElement("table:table-column", "table:table-row", "draw:frame");
            if (instance == null) break;

            String styleName = instance.getAttribValue("table:default-cell-style-name");
            Style style = styleName != null ? stylesParser.getCellStyle(styleName) : null;

            if (instance.getTag().equals("table:table-column")) {
                parseColumnProperties(instance, style);
            } else if (instance.getTag().equals("draw:frame")) {
                parseDrawFrame(instance, null, null);
            } else if (instance.getTag().equals("table:table-row")) {
                if (style != null) rowDefaultStyles.put(rowCount, style);

                int numRows = 1;
                String numRowsStr = instance.getAttribValue("table:number-rows-repeated");
                if (numRowsStr != null) {
                    try {
                        numRows = Integer.parseInt(numRowsStr);
                        if (numRows > BUGGED_COUNT) continue;
                    } catch (NumberFormatException ignored) {}
                }

                sheet.appendRows(numRows);

                String visibility = instance.getAttribValue("table:visibility");
                if ("collapse".equals(visibility)) sheet.hideRows(sheet.getMaxRows() - numRows, numRows);

                String rowStyleName = instance.getAttribValue("table:style-name");
                if (rowStyleName != null) {
                    RowStyle rowStyle = stylesParser.getRowStyle(rowStyleName);
                    if (rowStyle != null) sheet.setRowHeights(sheet.getMaxRows() - numRows, numRows, rowStyle.getHeight());
                }

                processCells(instance, numRows);
                rowCount += numRows;
            }
        }

        for (Pair<Vector, Vector> pair : groupCells) {
            Vector cord = pair.first;
            Vector length = pair.second;
            Range range = sheet.getRange(cord.getX(), cord.getY(), length.getX(), length.getY());
            range.merge();
        }
    }

    private void setTableStyles(String tableStyleName) {
        TableStyle style = stylesParser.getTableStyle(tableStyleName);
        if (style != null && style.isHidden()) sheet.hideSheet();
    }

    private void parseColumnProperties(XmlReaderInstance instance, Style style) {
        boolean areHidden = "collapse".equals(instance.getAttribValue("table:visibility"));
        int numColumns = 1;
        String columnsRepeated = instance.getAttribValue("table:number-columns-repeated");
        if (columnsRepeated != null) {
            numColumns = Integer.parseInt(columnsRepeated);
            if (numColumns > BUGGED_COUNT) return;
        }

        int index = sheet.getMaxColumns();
        sheet.appendColumns(numColumns);

        if (style != null && !style.isDefault()) {
            for (int j = index; j < index + numColumns; j++) {
                sheet.setDefaultColumnCellStyle(j, style);
                columnDefaultStyles.put(j, style);
            }
        }

        if (areHidden) sheet.hideColumns(index, numColumns);

        String columnStyleName = instance.getAttribValue("table:style-name");
        if (columnStyleName != null) {
            ColumnStyle columnStyle = stylesParser.getColumnStyle(columnStyleName);
            if (columnStyle != null) sheet.setColumnWidths(sheet.getMaxColumns() - numColumns, numColumns, columnStyle.getWidth());
        }
    }

    private void parseDrawFrame(XmlReaderInstance frameInstance, Integer anchorRow, Integer anchorColumn) {
        if (frameInstance == null || spread == null) return;
        SpreadSheet.ChartFrame frame = buildChartFrame(frameInstance);
        String frameName = frameInstance.getAttribValue("draw:name");
        while (frameInstance.hasNext()) {
            XmlReaderInstance child = frameInstance.nextElement("draw:object", "draw:object-ole", "draw:image");
            if (child == null) break;
            if ("draw:image".equals(child.getTag())) {
                if (options != null && !options.isLoadImages()) {
                    continue;
                }
                String href = child.getAttribValue("xlink:href");
                String mimeType = child.getAttribValue("draw:mime-type");
                String imagePath = normalizeObjectPath(href);
                if (imagePath == null) {
                    options.getLogger().warning("ImagePath is null. Checking if the image is inline");
                    if (hasInlineBinaryData(child)) {
                        options.getLogger().warning("Skipping inline image with office:binary-data (unsupported).");
                    }
                    continue;
                }
                if (imagePath != null) {
                    if (anchorRow != null && anchorColumn != null) {
                        sheet.getCell(anchorRow, anchorColumn);
                    }
                    SheetImage image = new SheetImage(imagePath, mimeType, null);
                    image.setName(frameName);
                    if (frame != null) {
                        image.setX(frame.x);
                        image.setY(frame.y);
                        image.setWidth(frame.width);
                        image.setHeight(frame.height);
                    }
                    image.setAnchorRow(anchorRow);
                    image.setAnchorColumn(anchorColumn);
                    sheet.addImage(image);
                }
            } else {
                if (options != null && !options.isLoadGraphs()) {
                    continue;
                }
                String href = child.getAttribValue("xlink:href");
                if (href != null) {
                    String objectPath = normalizeObjectPath(href);
                    if (objectPath != null) {
                        spread.registerChartObject(objectPath, sheet, frame);
                    }
                }
            }
        }
    }

    private SpreadSheet.ChartFrame buildChartFrame(XmlReaderInstance frameInstance) {
        if (frameInstance == null) return null;
        String x = frameInstance.getAttribValue("svg:x");
        String y = frameInstance.getAttribValue("svg:y");
        String width = frameInstance.getAttribValue("svg:width");
        String height = frameInstance.getAttribValue("svg:height");
        if (isBlank(x) && isBlank(y) && isBlank(width) && isBlank(height)) {
            return null;
        }
        return new SpreadSheet.ChartFrame(x, y, width, height);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalizeObjectPath(String href) {
        if (href == null) return null;
        String trimmed = href.trim();
        if (trimmed.isEmpty()) return null;
        if (trimmed.startsWith("./")) {
            trimmed = trimmed.substring(2);
        }
        if (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean hasInlineBinaryData(XmlReaderInstance imageInstance) {
        if (imageInstance == null) return false;
        while (imageInstance.hasNext()) {
            XmlReaderInstance binary = imageInstance.nextElement("office:binary-data");
            if (binary == null) break;
            return true;
        }
        return false;
    }

    private void processCells(XmlReaderInstance reader, int numberRowsRepeated) {
        int column = 0;
        while (reader.hasNext()) {
            int numberColumnsRepeated = 1;
            Object lastCellValue = null;
            Style lastStyle = null;

            XmlReaderInstance instance = reader.nextElement("table:table-cell", "table:covered-table-cell");
            if (instance == null) break;

            if (instance.getTag().equals("table:covered-table-cell")) {
                String numColumnsRepeated = instance.getAttribValue("table:number-columns-repeated");
                column += numColumnsRepeated == null ? 1 : Integer.parseInt(numColumnsRepeated);
                continue;
            }

            int rows = 1, columns = 1;
            String rowsSpanned = instance.getAttribValue("table:number-rows-spanned");
            if (rowsSpanned != null) rows = Integer.parseInt(rowsSpanned);
            String columnsSpanned = instance.getAttribValue("table:number-columns-spanned");
            if (columnsSpanned != null) columns = Integer.parseInt(columnsSpanned);

            if (numberRowsRepeated == 1 && (rows != 1 || columns != 1)) {
                Pair<Vector, Vector> pair = new Pair<>();
                pair.first = new Vector(sheet.getMaxRows() - 1, column);
                pair.second = new Vector(rows, columns);
                groupCells.add(pair);
            }

            int positionX = sheet.getMaxRows() - numberRowsRepeated;
            int positionY = column;

            OfficeValueType valueType = OfficeValueType.ofReader(instance);
            Object value = valueType.read(instance);

            String raw = instance.getAttribValue("table:number-columns-repeated");
            if (raw != null) {
                numberColumnsRepeated = Integer.parseInt(raw);
                if (numberColumnsRepeated > BUGGED_COUNT) continue;
            }

            if (positionY + numberColumnsRepeated > sheet.getMaxColumns()) {
                sheet.appendColumns(positionY + numberColumnsRepeated - sheet.getMaxColumns());
            }

            Range range = sheet.getRange(positionX, positionY, numberRowsRepeated, numberColumnsRepeated);

            String formula = instance.getAttribValue("table:formula");
            if (formula != null) range.setFormula(formula);
            range.setValue(value);

            Style style = stylesParser.getCellStyle(instance.getAttribValue("table:style-name"));
            if (style == null) style = columnDefaultStyles.get(column);
            if (style == null) style = rowDefaultStyles.get(sheet.getMaxRows() - 1);
            if (style != null && !style.isDefault()) range.setStyle(style);

            readCellContent(instance, range, positionX, positionY);
            column += numberColumnsRepeated;
        }
    }

    private void readCellContent(XmlReaderInstance cellReader, Range range, int row, int column) {
        StringBuffer s = new StringBuffer();
        boolean firstTextElement = true;

        XmlReaderInstance textElement;
        while ((textElement = cellReader.nextElement("text:p", "text:h", "office:annotation", "draw:frame")) != null) {
            if (textElement.getTag().equals("office:annotation")) {
                range.setAnnotation(getOfficeAnnotation(textElement));
                continue;
            }
            if (textElement.getTag().equals("draw:frame")) {
                parseDrawFrame(textElement, row, column);
                continue;
            }

            if (firstTextElement) firstTextElement = false;
            else s.append("\n");

            XmlReaderInstance spanElement;
            while ((spanElement = textElement.nextElement("text:s", "text:tab", "text:line-break", XmlReaderInstance.CHARACTERS)) != null) {
                if (spanElement.getTag().equals("text:s")) {
                    int num = tryParseTextCAttribute(spanElement);
                    while (num-- > 0) s.append(" ");
                } else if (spanElement.getTag().equals("text:tab")) {
                    int num = tryParseTextCAttribute(spanElement);
                    while (num-- > 0) s.append("\t");
                } else if (spanElement.getTag().equals("text:line-break")) {
                    s.append("\n");
                }

                String spanContent = spanElement.getContent();
                if (spanContent != null) s.append(spanContent);
            }
        }

        Object value = range.getValue();
        if (s.length() > 0 && (value == null || value instanceof String)) {
            range.setValue(s.toString());
        }
    }

    private OfficeAnnotation getOfficeAnnotation(XmlReaderInstance reader) {
        OfficeAnnotationBuilder annotation = new OfficeAnnotationBuilder();
        StringBuilder msg = new StringBuilder();

        while (reader.hasNext()) {
            XmlReaderInstance instance = reader.nextElement("dc:date", "text:p");
            if (instance == null) break;

            if (instance.getTag().equals("dc:date")) {
                instance = instance.nextElement(XmlReaderInstance.CHARACTERS);
                if (instance != null) {
                    String content = instance.getContent();
                    try {
                        if (content != null) annotation.setLastModified(LocalDateTime.parse(content));
                    } catch (DateTimeParseException e) {
                        logger().warning("Invalid date in office annotation.");
                    }
                }
            } else if (instance.getTag().equals("text:p")) {
                instance = instance.nextElement(XmlReaderInstance.CHARACTERS);
                if (msg.length() > 0) msg.append("\n");
                if (instance != null) msg.append(instance.getContent());
            }
        }

        annotation.setMsg(msg.toString());
        return annotation.build();
    }

    private int tryParseTextCAttribute(XmlReaderInstance spanElement) {
        int num = 1;
        String attrib = spanElement.getAttribValue("text:c");
        if (attrib != null && !attrib.isEmpty()) {
            try {
                num = Integer.parseInt(attrib);
            } catch (NumberFormatException e) {
                logger().warning("Invalid number of characters: " + attrib);
            }
        }
        return num;
    }

    private java.util.logging.Logger logger() {
        return options == null ? java.util.logging.Logger.getLogger("ODS_Reader") : options.getLogger();
    }
}

package com.github.miachm.sods;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Internal class for read ODS files
 */
@SuppressWarnings("unused")
class OdsReader {
    private static final String CORRECT_MIMETYPE = "application/vnd.oasis.opendocument.spreadsheet";
    private static final String MANIFEST_PATH = "META-INF/manifest.xml";
    private static final Locale defaultLocal = Locale.US;
    private static final int BUGGED_COUNT = 10 * 1000;
    private Uncompressor uncompressor;
    private XmlReader reader = new XmlReaderEventImpl();
    private SpreadSheet spread;
    private Map<String,Style> styles = new HashMap<>();
    private Map<Integer,Style> rows_styles = new HashMap<>();
    private Map<Integer,Style> columns_styles = new HashMap<>();
    private Map<String,ColumnStyle> styleColumn = new HashMap<>();
    private Map<String,RowStyle> styleRow = new HashMap<>();
    private Map<String,TableStyle> styleTable = new HashMap<>();
    private Set<Pair<Vector, Vector>> groupCells = new HashSet<>();

    private OdsReader(InputStream in,SpreadSheet spread) {
        /* TODO This code if for ods files in zip. But we could have XML-ONLY FILES */
        this.spread = spread;
        styles.put("Default", new Style());
        uncompressor = new Uncompressor(in);
    }

    static void load(InputStream in,SpreadSheet spread) throws IOException {
        OdsReader reader = new OdsReader(in, spread);
        reader.load();
    }

    private void load() throws IOException {
        boolean mimetypeChecked = false;
        String entry = uncompressor.nextFile();
        while (entry != null) {
            if (entry.endsWith(".xml"))
                processContent();
            else if (entry.equals("mimetype")) {
                checkMimeType();
                mimetypeChecked = true;
            }
            entry = uncompressor.nextFile();
        }
        uncompressor.close();
        spread.trimSheets();

        if (!mimetypeChecked)
            throw new NotAnOdsException("This file doesn't contain a mimetype");
    }

    private void checkMimeType() throws IOException {
        byte[] buff = new byte[CORRECT_MIMETYPE.getBytes().length];
        uncompressor.getInputStream().read(buff);

        String mimetype = new String(buff);
        if (!mimetype.equals(CORRECT_MIMETYPE))
            throw new NotAnOdsException("This file doesn't look like an ODS file. Mimetype: " + mimetype);
    }

    private void processContent() throws IOException
    {
        InputStream in = uncompressor.getInputStream();

        XmlReaderInstance instance = reader.load(in);
        if (instance == null)
            return;

        XmlReaderInstance styles = instance.nextElement("office:automatic-styles", "office:styles");
        iterateStyleEntries(styles);

        XmlReaderInstance content = instance.nextElement("office:body");
        iterateFilesEntries(content);

        reader.close();
    }

    private void iterateStyleEntries(XmlReaderInstance reader) {
        if (reader == null)
            return;

        while (reader.hasNext()) {
            XmlReaderInstance instance = reader.nextElement("style:style");
            if (instance == null)
                return;

            String name = instance.getAttribValue("style:name");
            String family = instance.getAttribValue("style:family");
            if (name != null && family != null) {
                if (family.equals("table-cell")) {
                    Style style = readCellStyleEntry(instance, name);
                    styles.put(name, style);
                }
                else if (family.equals("table-column")) {
                    ColumnStyle style = readColumnStyleEntry(instance);
                    styleColumn.put(name, style);
                }
                else if (family.equals("table-row")) {
                    RowStyle style = readRowStyleEntry(instance);
                    styleRow.put(name, style);
                }
                else if (family.equals("table")) {
                    TableStyle style = readTableStyleEntry(instance);
                    styleTable.put(name, style);
                }
            }
        }
    }

    private Style readCellStyleEntry(XmlReaderInstance reader, String name) {
        Style style = styles.get(name);
        if (style == null)
            style = new Style();
        while (reader.hasNext()) {
            XmlReaderInstance instance = reader.nextElement("style:text-properties",
                    "style:table-cell-properties",
                    "style:paragraph-properties",
                    "style:map");

            if (instance == null)
                return style;

            if (instance.getTag().equals("style:text-properties")) {
                String bold = instance.getAttribValue("fo:font-weight");
                if (bold != null)
                    style.setBold(bold.equals("bold"));

                String italic = instance.getAttribValue("fo:font-style");
                if (italic != null)
                    style.setItalic(italic.equals("italic"));

                String underline = instance.getAttribValue("style:text-underline-style");
                if (underline != null)
                    style.setUnderline(underline.equals("solid"));

                String fontcolor = instance.getAttribValue("fo:color");
                if (fontcolor != null && !fontcolor.equals("transparent"))
                    try {
                        style.setFontColor(new Color(fontcolor));
                    }
                    catch (IllegalArgumentException e) { System.err.println(e.getMessage());}

                String fontsize = instance.getAttribValue("fo:font-size");
                if (fontsize != null) {
                    if (fontsize.endsWith("pt")) {
                        try {
                            int index = fontsize.lastIndexOf("pt");
                            int fontSize = (int) Math.round(Double.parseDouble(fontsize.substring(0, index)));
                            style.setFontSize(fontSize);
                        }
                        catch (NumberFormatException e)
                        {
                            System.err.println("Error, invalid font size " + fontsize);
                        }
                    }
                    else
                        throw new OperationNotSupportedException("Error, font size is not measured in PT. Skipping...");
                }
            }

            if (instance.getTag().equals("style:table-cell-properties")) {
                String backgroundColor = instance.getAttribValue("fo:background-color");
                if (backgroundColor != null && !backgroundColor.equals("transparent"))
                    try {
                        style.setBackgroundColor(new Color(backgroundColor));
                    }
                    catch (IllegalArgumentException e) { System.err.println(e.getMessage());}

                String verticalAlign = instance.getAttribValue("style:vertical-align");
                if (verticalAlign != null) {
                    Style.VERTICAL_TEXT_ALIGMENT pos = null;
                    if (verticalAlign.equalsIgnoreCase("middle")) {
                        pos = Style.VERTICAL_TEXT_ALIGMENT.Middle;
                    } else if (verticalAlign.equalsIgnoreCase("top")) {
                        pos = Style.VERTICAL_TEXT_ALIGMENT.Top;
                    } else if (verticalAlign.equalsIgnoreCase("bottom")) {
                        pos = Style.VERTICAL_TEXT_ALIGMENT.Bottom;
                    }
                    style.setVerticalTextAligment(pos);
                }
            }

            if(instance.getTag().equals("style:paragraph-properties")) {
                String align = instance.getAttribValue("fo:text-align");
                if(align != null) {
                    Style.TEXT_ALIGMENT pos = null;
                    if(align.equals("center")) {
                        pos = Style.TEXT_ALIGMENT.Center;
                    }
                    else if(align.equals("end")) {
                        pos = Style.TEXT_ALIGMENT.Right;
                    }
                    else if(align.equals("start")) {
                        pos = Style.TEXT_ALIGMENT.Left;
                    }
                    style.setTextAligment(pos);
                }
            }

            if (instance.getTag().equals("style:map")) {
                String key = instance.getAttribValue("style:apply-style-name");
                String condition = instance.getAttribValue("style:condition");
                if (key != null && condition != null) {
                    Style other = styles.get(key);
                    if (other == null) {
                        other = new Style();
                        styles.put(key, other);
                    }
                    ConditionalFormat conditionalFormat = new ConditionalFormat(other, condition);
                    style.addCondition(conditionalFormat);
                }
            }
        }
        return style;
    }

    private ColumnStyle readColumnStyleEntry(XmlReaderInstance reader) {
        ColumnStyle style = new ColumnStyle();
        while (reader.hasNext()) {
            XmlReaderInstance instance = reader.nextElement("style:table-column-properties");
            if (instance == null)
                return style;

            String columnWidth = instance.getAttribValue("style:column-width");
            if (columnWidth != null)
                style.setWidth(columnWidth);
        }
        return style;
    }

    private RowStyle readRowStyleEntry(XmlReaderInstance reader) {
        RowStyle style = new RowStyle();
        while (reader.hasNext()) {
            XmlReaderInstance instance = reader.nextElement("style:table-row-properties");
            if (instance == null)
                return style;

            String rowHeight = instance.getAttribValue("style:row-height");
            if (rowHeight != null)
                style.setHeight(rowHeight);
        }
        return style;
    }

    private TableStyle readTableStyleEntry(XmlReaderInstance reader) {
        TableStyle style = new TableStyle();
        while (reader.hasNext()) {
            XmlReaderInstance instance = reader.nextElement("style:table-properties");
            if (instance == null)
                return style;

            String display = instance.getAttribValue("table:display");
            if (display != null)
                style.setHidden(display.equals("false"));
        }
        return style;
    }

    private void iterateFilesEntries(XmlReaderInstance reader) {
        if (reader == null)
            return;

        XmlReaderInstance instance = reader.nextElement("office:spreadsheet");
        if (instance != null)
            processSpreadsheet(instance);
    }

    private void processSpreadsheet(XmlReaderInstance reader) {
        while (reader.hasNext()) {
            XmlReaderInstance instance = reader.nextElement("table:table");
            if (instance != null)
                processTable(instance);
        }
    }

    private void processTable(XmlReaderInstance reader) {
        String name = reader.getAttribValue("table:name");

        Sheet sheet = new Sheet(name, 0, 0);

        String tableStyleName = reader.getAttribValue("table:style-name");
        if (tableStyleName != null) {
            setTableStyles(sheet, tableStyleName);
        }

        String protectedSheet = reader.getAttribValue("table:protected");
        if (protectedSheet != null) {
            String algorithm = reader.getAttribValue("table:protection-key-digest-algorithm");
            if (algorithm == null)
                algorithm = "http://www.w3.org/2000/09/xmldsig#sha1";

            String protectedKey = reader.getAttribValue("table:protection-key");
            sheet.setRawPassword(protectedKey, algorithm);
        }

        int rowCount = 0;
        groupCells.clear();

        while (reader.hasNext()) {
            XmlReaderInstance instance = reader.nextElement("table:table-column",
                    "table:table-row");

            if (instance != null) {
                String styleName = instance.getAttribValue("table:default-cell-style-name");
                Style style = null;
                if (styleName != null)
                    style = styles.get(styleName);

                if (instance.getTag().equals("table:table-column")) {
                    parseColumnProperties(instance, sheet, style);
                }
                else if (instance.getTag().equals("table:table-row")) {
                    if (style != null)
                        rows_styles.put(rowCount, style);

                    int numRows = 1;
                    String numRowsStr = instance.getAttribValue("table:number-rows-repeated");
                    if (numRowsStr != null) {
                        try {
                            numRows = Integer.parseInt(numRowsStr);
                            if (numRows > BUGGED_COUNT)
                                continue;
                        }
                        catch (NumberFormatException e) {}
                    }

                    sheet.appendRows(numRows);

                    String visibility = instance.getAttribValue("table:visibility");
                    if ("collapse".equals(visibility))
                        sheet.hideRows(sheet.getMaxRows()-numRows, numRows);

                    String rowStyleName = instance.getAttribValue("table:style-name");
                    if (rowStyleName != null) {
                        RowStyle rowStyle = styleRow.get(rowStyleName);
                        if (rowStyle != null)
                            sheet.setRowHeights(sheet.getMaxRows()-numRows, numRows, rowStyle.getHeight());
                    }
                    processCells(instance, sheet);
                }
            }
        }

        for (Pair<Vector, Vector> pair : groupCells) {
            Vector cord = pair.first;
            Vector length = pair.second;
            Range range = sheet.getRange(cord.getX(), cord.getY(), length.getX(), length.getY());
            range.merge();
        }

        spread.appendSheet(sheet);
    }

    private void setTableStyles(Sheet sheet, String tableStyleName) {
        TableStyle style = styleTable.get(tableStyleName);
        if (style != null) {
            if (style.isHidden())
                sheet.hideSheet();
        }
    }

    private void parseColumnProperties(XmlReaderInstance instance, Sheet sheet, Style style) {
        boolean areHidden = false;
        String visibility = instance.getAttribValue("table:visibility");
        if ("collapse".equals(visibility))
            areHidden = true;

        int numColumns = 1;
        String columnsRepeated = instance.getAttribValue("table:number-columns-repeated");
        if (columnsRepeated != null) {
            numColumns = Integer.parseInt(columnsRepeated);
            if (numColumns > BUGGED_COUNT)
                return;
        }

        int index = sheet.getMaxColumns();
        sheet.appendColumns(numColumns);

        if (style != null && !style.isDefault()) {
            for (int j = index; j < index + numColumns; j++) {
                sheet.setDefaultColumnCellStyle(j, style);
                columns_styles.put(j, style);
            }
        }

        if (areHidden) {
            sheet.hideColumns(index, numColumns);
        }

        String columnStyleName = instance.getAttribValue("table:style-name");
        if (columnStyleName != null) {
            ColumnStyle columnStyle = styleColumn.get(columnStyleName);
            if (columnStyle != null) {
                sheet.setColumnWidths(sheet.getMaxColumns() - numColumns, numColumns, columnStyle.getWidth());
            }
        }
    }

    private void processCells(XmlReaderInstance reader, Sheet sheet) {
        int column = 0;
        while (reader.hasNext()) {
            // number of columns repeated
            int number_columns_repeated = 1;
            // value and style to be copied
            Object last_cell_value = null;
            Style last_style = null;

            XmlReaderInstance instance = reader.nextElement("table:table-cell", "table:covered-table-cell");
            if (instance != null) {
                if (instance.getTag().equals("table:covered-table-cell")) {
                    String numColumnsRepeated = instance.getAttribValue("table:number-columns-repeated");
                    if (numColumnsRepeated == null)
                        column++;
                    else {
                        column += Integer.parseInt(numColumnsRepeated);
                    }
                    continue;
                }
                int rows = 1;
                int columns = 1;
                String rowsSpanned = instance.getAttribValue("table:number-rows-spanned");
                if (rowsSpanned != null) {
                    rows = Integer.parseInt(rowsSpanned);
                }

                String columnsSpanned = instance.getAttribValue("table:number-columns-spanned");
                if (columnsSpanned != null) {
                    columns = Integer.parseInt(columnsSpanned);
                }

                int positionX = sheet.getMaxRows()-1;
                int positionY = column;
                if (rows != 1 || columns != 1) {
                    Pair<Vector, Vector> pair =  new Pair<>();
                    pair.first = new Vector(positionX, positionY);
                    pair.second = new Vector(rows, columns);
                    groupCells.add(pair);
                }

                OfficeValueType valueType = OfficeValueType.ofReader(instance);
                Object value = valueType.read(instance);

                String raw = instance.getAttribValue("table:number-columns-repeated");
                if (raw != null) {
                    number_columns_repeated = Integer.parseInt(raw);
                    if (number_columns_repeated > BUGGED_COUNT)
                        continue;
                }

                if (positionY+number_columns_repeated >= sheet.getMaxColumns()) {
                    sheet.appendColumns(positionY + number_columns_repeated - sheet.getMaxColumns());
                }

                Range range = sheet.getRange(positionX, positionY, 1, number_columns_repeated);

                String formula = instance.getAttribValue("table:formula");
                if (formula != null)
                    range.setFormula(formula);
                range.setValue(value);

                Style style = styles.get(instance.getAttribValue("table:style-name"));

                if (style == null)
                    style = columns_styles.get(column);

                if (style == null)
                    style = rows_styles.get(sheet.getMaxRows() - 1);

                if (style != null && !style.isDefault())
                    range.setStyle(style);

                readCellText(instance, range);

                column += number_columns_repeated;
            }
        }
    }

    private void readCellText(XmlReaderInstance cellReader, Range range) {
        // A cell can contain zero(?) or more text:p tags,
        // that each can contain zero or more text:span tags.
        // Concatenate all text in them.

        StringBuffer s = new StringBuffer();

        XmlReaderInstance textElement = null;
        boolean firstTextElement = true;
        while ((textElement = cellReader.nextElement("text:p",
                "text:h", "office:annotation")) != null) {
            if (textElement.getTag().equals("office:annotation")) {
                range.setAnnotation(getOfficeAnnotation(textElement));
                continue;
            }
            // Each text:p tag seems to represent a separate row.  Separate them with newlines.
            if (firstTextElement) {
                firstTextElement = false;
            } else {
                s.append("\n");
            }
            // Add content of any contained text:span tags
            XmlReaderInstance spanElement = textElement.nextElement("text:s",
                    XmlReaderInstance.CHARACTERS);

            while (spanElement != null) {

                if (spanElement.getTag().equals("text:s")) {
                    int num = 1;
                    String atrib = spanElement.getAttribValue("text:c");
                    if (atrib != null && !atrib.isEmpty()) {
                        try {
                            num = Integer.parseInt(atrib);
                        }
                        catch (NumberFormatException e){
                            System.err.println("Invalid number of characters: " + atrib);
                        }
                    }
                    while (num > 0) {
                        s.append(" ");
                        num--;
                    }
                }

                String spanContent = spanElement.getContent();
                if (spanContent != null) s.append(spanContent);


                spanElement = textElement.nextElement("text:s",
                        XmlReaderInstance.CHARACTERS);
            }

            // Add direct content of text:p tag (we do it here, as
            // textElement.nextElement() will not work after textElement.getContent()).

        }

        Object value = range.getValue();
        // Empty cells are supposed to be represented by null, so return that if we got no content.
        if (s.length() > 0 && (value == null || value instanceof String)) {
            range.setValue(s.toString());
        }
    }

    private OfficeAnnotation getOfficeAnnotation(XmlReaderInstance reader) {
        OfficeAnnotationBuilder annotation = new OfficeAnnotationBuilder();
        StringBuilder msg = new StringBuilder();

        while (reader.hasNext()) {
            XmlReaderInstance instance = reader.nextElement("dc:date", "text:p");
            if (instance == null) {
                annotation.setMsg(msg.toString());
                return annotation.build();
            }

            if (instance.getTag().equals("dc:date")) {
                instance = instance.nextElement(XmlReaderInstance.CHARACTERS);
                if (instance != null) {
                    String content = instance.getContent();
                    try {
                        if (content != null)
                            annotation.setLastModified(LocalDateTime.parse(content));
                    } catch (DateTimeParseException e) {
                        System.err.println("DATE INVALID IN OFFICE ANNOTATION");
                    }
                }
            }
            else if (instance.getTag().equals("text:p")) {
                instance = instance.nextElement(XmlReaderInstance.CHARACTERS);
                if (msg.length() > 0)
                    msg.append("\n");

                if (instance != null) {
                    String content = instance.getContent();
                    msg.append(content);
                }
            }
        }

        annotation.setMsg(msg.toString());
        return annotation.build();
    }
}

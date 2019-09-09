package com.github.miachm.sods;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Internal class for read ODS files
 */
class OdsReader {
    private static final String CORRECT_MIMETYPE = "application/vnd.oasis.opendocument.spreadsheet";
    private static final String MANIFEST_PATH = "META-INF/manifest.xml";
    private static final Locale defaultLocal = Locale.US;
    private Uncompressor uncompressor;
    private XmlReader reader = new XmlReaderEventImpl();
    private SpreadSheet spread;
    private Map<String,Style> styles = new HashMap<>();
    private Map<Integer,Style> rows_styles = new HashMap<>();
    private Map<Integer,Style> columns_styles = new HashMap<>();
    private Map<String,ColumnStyle> styleColumn = new HashMap<>();
    private Map<String,RowStyle> styleRow = new HashMap<>();
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

        XmlReaderInstance styles = instance.nextElement("office:automatic-styles");
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
                    Style style = readCellStyleEntry(instance);
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
            }
        }
    }

    private Style readCellStyleEntry(XmlReaderInstance reader) {
        Style style = new Style();
        while (reader.hasNext()) {
            XmlReaderInstance instance = reader.nextElement("style:text-properties",
                                                            "style:table-cell-properties");

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
                        int index = fontsize.lastIndexOf("pt");
                        int fontSize = Integer.parseInt(fontsize.substring(0,index));
                        style.setFontSize(fontSize);
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

        Sheet sheet = new Sheet(name);
        sheet.deleteRow(0);
        sheet.deleteColumn(0);

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
                    int numColumns = 1;
                    String columnsRepeated = instance.getAttribValue("table:number-columns-repeated");
                    if (columnsRepeated != null)
                        numColumns = Integer.parseInt(columnsRepeated);

                    if (style != null) {
                        for (int j = sheet.getMaxColumns(); j < sheet.getMaxColumns() + numColumns; j++)
                            columns_styles.put(j, style);
                    }

                    sheet.appendColumns(numColumns);

                    String columnStyleName = instance.getAttribValue("table:style-name");
                    if (columnStyleName != null) {
                        ColumnStyle columnStyle = styleColumn.get(columnStyleName);
                        if (columnStyle != null) {
                            for (int i = 0; i < numColumns; i++) {
                                sheet.setColumnWidth(sheet.getMaxColumns() - i - 1, columnStyle.getWidth());
                            }
                        }
                    }
                }
                else if (instance.getTag().equals("table:table-row")) {
                    if (style != null)
                        rows_styles.put(rowCount, style);

                    sheet.appendRow();

                    String rowStyleName = instance.getAttribValue("table:style-name");
                    if (rowStyleName != null) {
                        RowStyle rowStyle = styleRow.get(rowStyleName);
                        if (rowStyle != null)
                            sheet.setRowHeight(sheet.getMaxRows() - 1, rowStyle.getHeight());
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

    private void processCells(XmlReaderInstance reader, Sheet sheet) {
        int column = 0;
        while (reader.hasNext()) {
            // number of columns repeated
            long number_columns_repeated = 0;
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

                Range range = sheet.getRange(positionX, positionY);

                String formula = instance.getAttribValue("table:formula");
                range.setFormula(formula);

                OfficeValueType valueType = OfficeValueType.ofReader(instance);
                Object value = valueType.read(instance);

                String raw = instance.getAttribValue("table:number-columns-repeated");
                if (raw != null)
                    number_columns_repeated = Long.parseLong(raw);

                Style style = styles.get(instance.getAttribValue("table:style-name"));

                if (style == null)
                    style = columns_styles.get(column);

                if (style == null)
                    style = rows_styles.get(sheet.getMaxRows() - 1);

                if (style != null)
                    range.setStyle(style);

                last_style = style;

                if (value == null) {
                    value = readCellText(instance);
                }

                last_cell_value = value;
                range.setValue(value);

                column++;
            }

            if (number_columns_repeated > 0) {
                for (int j = 0; j < number_columns_repeated - 1; j++) {
                    Range range = sheet.getRange(sheet.getMaxRows() - 1, column);
                    if (last_style != null) {
                        range.setStyle(last_style);
                    }

                    range.setValue(last_cell_value);
                    column++;
                }
            }
        }
    }

    private String readCellText(XmlReaderInstance cellReader) {
        // A cell can contain zero(?) or more text:p tags,
        // that each can contain zero or more text:span tags.
        // Concatenate all text in them.

        StringBuffer s = new StringBuffer();

        XmlReaderInstance textElement = cellReader.nextElement( "text:p",
                                                                "text:h");
        boolean firstTextElement = true;
        while (textElement != null) {
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

            textElement = cellReader.nextElement("text:p",
                    "text:h");
        }

        // Empty cells are supposed to be represented by null, so return that if we got no content.
        if (s.length() <= 0) {
            return null;
        }
        else {
            return s.toString();
        }
    }

}

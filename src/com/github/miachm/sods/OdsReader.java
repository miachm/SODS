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
    private List<OdsReaderExtension> extensions = new ArrayList<>();
    private Style last_style = new Style();
    private ColumnStyle last_column_style = new ColumnStyle();
    private RowStyle last_row_style = new RowStyle();
    private TableStyle last_table_style = new TableStyle();
    private OfficeAnnotationBuilder currentOfficeAnnotation;

    private OdsReader(InputStream in,SpreadSheet spread) {
        /* TODO This code if for ods files in zip. But we could have XML-ONLY FILES */
        this.spread = spread;
        styles.put("Default", new Style());
        uncompressor = new Uncompressor(in);

        extensions.add(new OdsReaderSheets(this));
        extensions.add(new OdsReaderRows(this));
        extensions.add(new OdsReaderColumns(this));
        extensions.add(new OdsReaderStyle(this));
        extensions.add(new OdsReaderValues(this));
    }

    static void load(InputStream in,SpreadSheet spread, LoadOptions options) throws IOException {
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

        Map<String, OdsReaderExtension> extensionHandler = new HashMap<>();
        Set<String> tags = new HashSet<>();
        for (OdsReaderExtension e : this.extensions) {
            extensionHandler.put(e.tag(), e);
            tags.add(e.tag());
        }
        while (instance.hasNext()) {
            XmlReaderInstance reader = instance.nextElement(tags);
            if (reader != null) {
                OdsReaderExtension extension = extensionHandler.get(reader.getTag());
                if (extension != null) {
                    extension.processTag(reader);
                }
            }

        }

        reader.close();
    }

    /*

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
                                numRows = 100;
                        }
                        catch (NumberFormatException e) {}
                    }

                    sheet.appendRows(numRows);

                    String visibility = instance.getAttribValue("table:visibility");
                    if ("collapse".equals(visibility))
                        for (int i = 0; i < numRows; i++)
                            sheet.hideRow(sheet.getMaxRows() - i - 1);

                    String rowStyleName = instance.getAttribValue("table:style-name");
                    if (rowStyleName != null && numRows < 1000) {
                        RowStyle rowStyle = styleRow.get(rowStyleName);
                        if (rowStyle != null)
                            for (int i = 0; i < numRows; i++)
                                sheet.setRowHeight(sheet.getMaxRows() - i - 1, rowStyle.getHeight());
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

        // Workaround for issue #12
        // Excel for some rason generates sheets with 100.000 rows
        // although it's only using a few ones
        // This can be a problem if the users tries to save back
        if (sheet.getMaxRows() > BUGGED_COUNT)
            sheet.trim();

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
        }

        if (style != null && !style.isDefault()) {
            for (int j = sheet.getMaxColumns(); j < sheet.getMaxColumns() + numColumns; j++)
                columns_styles.put(j, style);
        }

        int index = sheet.getMaxColumns();
        sheet.appendColumns(numColumns);

        if (areHidden) {
            for (int j = 0; j < numColumns; j++)
                sheet.hideColumn(index + j);
        }

        String columnStyleName = instance.getAttribValue("table:style-name");
        if (columnStyleName != null && numColumns < 1000) {
            ColumnStyle columnStyle = styleColumn.get(columnStyleName);
            if (columnStyle != null) {
                for (int i = 0; i < numColumns; i++) {
                    sheet.setColumnWidth(sheet.getMaxColumns() - i - 1, columnStyle.getWidth());
                }
            }
        }
    }

    private void processCells(XmlReaderInstance reader, Sheet sheet) {
        int column = 0;
        while (reader.hasNext()) {
            // number of columns repeated
            int number_columns_repeated = 0;
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
                if (positionY >= sheet.getMaxColumns()) {
                    sheet.appendColumns(positionY - sheet.getMaxColumns() + 1);
                }
                Range range = sheet.getRange(positionX, positionY);

                String formula = instance.getAttribValue("table:formula");
                if (formula != null)
                    range.setFormula(formula);

                OfficeValueType valueType = OfficeValueType.ofReader(instance);
                Object value = valueType.read(instance);

                String raw = instance.getAttribValue("table:number-columns-repeated");
                if (raw != null) {
                    number_columns_repeated = Integer.parseInt(raw);

                    // Issue #12, check function trimColumns()
                    if (number_columns_repeated > 1000)
                        number_columns_repeated = 1000;
                }

                if (value != null)
                    range.setValue(value);

                Style style = styles.get(instance.getAttribValue("table:style-name"));

                if (style == null)
                    style = columns_styles.get(column);

                if (style == null)
                    style = rows_styles.get(sheet.getMaxRows() - 1);

                if (style != null && !style.isDefault())
                    range.setStyle(style);

                last_style = style;

                readCellText(instance, range);
                last_cell_value = range.getValue();

                column++;
            }

            if (number_columns_repeated > 0 && (last_cell_value != null || last_style != null)) {

                for (int j = 0; j < number_columns_repeated - 1; j++) {
                    Range range = sheet.getRange(sheet.getMaxRows() - 1, column);
                    if (last_style != null) {
                        range.setStyle(last_style);
                    }

                    range.setValue(last_cell_value);
                    column++;
                }
            }
            else if (number_columns_repeated > 0)
                column += number_columns_repeated - 1;
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

        // Empty cells are supposed to be represented by null, so return that if we got no content.
        if (s.length() > 0 && (range.getValue() == null || range.getValue() instanceof String)) {
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
*/
    public Range getCurrentRange() {
        Sheet sheet = getCurrentSheet();
        return sheet.getRange(sheet.getMaxRows()-1, sheet.getMaxColumns() - 1);
    }

    public Sheet getCurrentSheet() {
        return this.spread.getSheet(this.spread.getNumSheets()-1);
    }

    public void addSheet(Sheet sheet) {
        this.spread.appendSheet(sheet);
    }

    public ColumnStyle getCurrentColumnStyle() {
        return this.last_column_style;
    }

    public RowStyle getCurrentRowStyle() {
        return this.last_row_style;
    }

    public TableStyle getCurrentTableStyle() {
        return this.last_table_style;
    }

    public Style getStyle(String name)
    {
        Style style = styles.get(name);

        /*
        if (style == null)
            style = columns_styles.get(column);

        if (style == null)
            style = rows_styles.get(sheet.getMaxRows() - 1);
        */
        return style;
    }

    public void defineAnnotation(OfficeAnnotationBuilder officeAnnotationBuilder) {
        this.currentOfficeAnnotation = officeAnnotationBuilder;
    }

    public OfficeAnnotationBuilder getCurrentAnnotation() {
        return this.currentOfficeAnnotation;
    }

    public void defineStyle(Style style, String name) {
        this.styles.put(name, style);
    }

    public void defineStyle(ColumnStyle style, String name) {
        this.styleColumn.put(name, style);
    }

    public void defineStyle(RowStyle style, String name) {
        this.styleRow.put(name, style);
    }

    public void defineStyle(TableStyle style, String name) {
        this.styleTable.put(name, style);
    }
}

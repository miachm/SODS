package com.github.miachm.sods;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * A range represents a subset of a Sheet.
 * You use ranges for write/read content in a Sheet.
 */
public class Range {
    private final int column_init,row_init;
    private final int numrows,numcolumns;
    private final Sheet sheet;

    Range(Sheet sheet,int row_init,int column_init,int numrows,int numcolumns){
        if (row_init < 0)
            throw new IndexOutOfBoundsException("Starting row is negative");

        if (column_init < 0)
            throw new IndexOutOfBoundsException("Starting column is negative");

        if (row_init + numrows > sheet.getMaxRows())
            throw new IndexOutOfBoundsException("Range goes out of bounds: "+
                                    "(end_of_range: " + (row_init+numrows) + ", maxrows in sheet: " + sheet.getMaxRows());

        if (column_init + numcolumns > sheet.getMaxColumns())
            throw new IndexOutOfBoundsException("Range goes out of bounds: "+
                    "(end_of_range: " + (column_init+numcolumns) + ", maxcolumns in sheet: " + sheet.getMaxColumns());

        this.sheet = sheet;
        this.row_init = row_init;
        this.column_init = column_init;
        this.numrows = numrows;
        this.numcolumns = numcolumns;
    }

    /**
     * Clear all the content/styles of the cells in this range
     */

    public void clear(){
        iterateRange((cell,row,column) -> cell.clear());
    }

    /**
     * Copy the content of this range to another,
     * this include values and formatting
     *
     * @param dest The destination range, it must have the same size of the orig range.
     * @throws IllegalArgumentException if the range hasn't the same size
     */
    public void copyTo(Range dest){
        if (dest.getNumValues() != getNumValues())
            throw new IllegalArgumentException("Error in copyTo, the range is of different size ("
                    + dest.getNumValues() + " against " + getNumValues() + ")");

        dest.setValues(getValues());
        dest.setStyles(getStyles());
    }

    /**
     * Get a specific cell of this Range using relative coords
     * for example, (0,0) would be the first cell of this range
     *
     * @param row The X Cord
     * @param column The Y Cord
     * @return A range which contains that specific cell
     * @throws IndexOutOfBoundsException if the coordinates are invalid
     */
    public Range getCell(int row,int column)
    {
        if (row >= getNumRows())
            throw new IndexOutOfBoundsException("Row is greater than range size");
        if (row < 0)
            throw new IndexOutOfBoundsException("Row is negative");
        if (column >= getNumColumns())
            throw new IndexOutOfBoundsException("Column is greater than range size");
        if (column < 0)
            throw  new IndexOutOfBoundsException("Column is negative");

        return new Range(sheet,row_init+row,column_init+column,1,1);
    }

    /**
     * Get the starting column index of this range.
     * For example a value of 2, indicates what this range starts at the third column of the sheet.
     *
     * @return An integer with the column index
     */
    public int getColumn(){
        return column_init;
    }

    private Cell getFirstCell()
    {
        Cell cell = sheet.getCell(row_init,column_init);
        if (cell.getGroup() != null)
            cell = cell.getGroup().getCell();
        return cell;
    }

    /**
     * Get the formula String of the first cell of the range
     *
     * @return the formula text representation, it can be null
     */
    public String getFormula(){
        return getFirstCell().getFormula();
    }

    /**
     * Returns the formulas (A1 notation) for the cells in the range.
     * Entries in the 2D array are null for cells with no formula.
     *
     * @return The formulas of the range
     */
    public String[][] getFormulas(){
        String[][] formulas = new String[getNumRows()][getNumColumns()];

        iterateRange((cell,row,column) -> formulas[row][column] = cell.getFormula());

        return formulas;
    }

    /**
     * Returns the end column position.
     *
     * @return The coordinate of the end column position in the sheet.
     */
    public int getLastColumn(){
        return column_init + getNumColumns()-1;
    }

    /**
     * Returns the end row position.
     *
     * @return The coordinate of the end row position in the sheet.
     */
    public int getLastRow(){
        return row_init + getNumRows()-1;
    }

    /**
     * Returns an array of Range objects representing merged cells that either
     * are fully within the current range, or contain at least one cell in the current range.
     *
     * @return An array with all the Ranges[]
     */
    public Range[] getMergedCells() {
        Set<GroupCell> groupCells = new TreeSet<>();
        iterateRange((cell, row, column) -> {
            if (cell.getGroup() != null)
                groupCells.add(cell.getGroup());
        });

        List<Range> result = new ArrayList<>();
        for (GroupCell groupCell : groupCells) {
            Vector cord = groupCell.getCord();
            Vector length = groupCell.getLength();
            Range range = new Range(sheet, cord.getX(), cord.getY(), length.getX(), length.getY());
            result.add(range);
        }

        return result.toArray(new Range[result.size()]);
    }

    /**
     * Number of columns which contains this range
     *
     * @return the number of columns
     */
    public int getNumColumns(){
        return numcolumns;
    }

    /**
     * Number of rows which contains this range
     *
     * @return the number of columns
     */
    public int getNumRows(){
        return numrows;
    }

    /**
     * Get the starting row index of this range.
     * For example a value of 2, indicates what this range starts at the third row of the sheet.
     *
     * @return An integer with the row index
     */
    public int getRow(){
        return row_init;
    }

    /**
     * Returns the sheet where this range is contained
     *
     * @return A not-null sheet object
     */
    public Sheet getSheet(){
        return sheet;
    }

    /**
     * Returns the value of the top-left cell in the range.
     * The values could be String, Float, Integer, OfficeCurrency, OfficePercentage or a Date
     * Empty cells returns a null object
     *
     * @see OfficeCurrency
     * @see OfficePercentage
     * @return the value in this cell
     */
    public Object getValue()
    {
        return getFirstCell().getValue();
    }

    /**
     * Returns the rectangular grid of values for this range.
     * The values could be String, Float, Integer, OfficeCurrency, OfficePercentage or a Date
     * Empty cells returns a null object
     *
     * @see OfficeCurrency
     * @see OfficePercentage
     * @return A two-dimensional array of values.
     */
    public Object[][] getValues(){
        Object[][] values = new Object[getNumRows()][getNumColumns()];
        iterateRange((cell,row,column) -> values[row][column] = cell.getValue());
        return values;
    }

    /**
     * Returns the formating style of the top-left cell in the range.
     * It's safe to manipulate the Style object since is a copy of the original one
     *
     * @return the style in this cell. It can not be null.
     */
    public Style getStyle()
    {
        return getFirstCell().getStyleCopy();
    }

    /**
     * Returns the rectangular grid of formating styles for this range.
     * It's safe to manipulate the Styles objects since they are copies of the original ones
     * @return A two-dimensional array of values. It can not be null
     */
    public Style[][] getStyles() {
        Style[][] arr = new Style[getNumRows()][getNumColumns()];
        iterateRange((cell, row, column) ->  arr[row][column] = cell.getStyleCopy());
        return arr;
    }

    /**
     * Return the number of cells which contains this range
     *
     * @return The number of cells in this range
     */
    public int getNumValues(){
        return getNumColumns()*getNumRows();
    }

    /**
     * Set a value to the entire range
     *
     * @param o The value object, it can be Integer, Float, Date, OfficeCurrency, OfficePercentage. Differents values types
     *          will be considered as Strings and toString() will be invoked
     *
     * @see OfficeCurrency
     * @see OfficePercentage
     *
     */
    public void setValue(Object o){
        iterateRange((cell,row,column) -> cell.setValue(o));
    }

    /**
     * Set a set of values to the range. The values must have the same size of the entire range itself
     *
     * <pre>
     *     Range range = sheet.getRange(0, 0, 2, 2); // 2x2 Range
     *     range.setValues(1, 2, 3, 4); // Set the values for the range
     * </pre>
     *
     * @param o The values array, it must the same size of the range itself
     * @throws IllegalArgumentException if the number of values is not equals to the size of range
     */
    public void setValues(Object... o){
        if (o.length != getNumValues())
            throw new IllegalArgumentException("Error in setValues, the number of the arguments doesn't fit ("
                    + o.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.setValue(o[row*getNumColumns()+column]));
    }

    /**
     * Set a set of values to the range. The values array must have the same size of the entire range itself
     *
     * @param o The values 2D-array, it must have the same size of the range itself
     * @throws IllegalArgumentException if the number of values is not equals to the size of range
     */
    public void setValues(Object[][] o){
        if (o.length == 0)
            throw new IllegalArgumentException("Error in setValues, the array is empty");
        if (o.length != getNumRows())
            throw new IllegalArgumentException("Error in setValues, the number of rows doesn't fit ("
                    + o.length + " against " + getNumRows() + ")");
        if (o[0].length != getNumColumns())
            throw new IllegalArgumentException("Error in setValues, the number of columns doesn't fit ("
                    + o.length + " against " + getNumColumns() + ")");

        iterateRange((cell,row,column) -> cell.setValue(o[row][column]));
    }

    /**
     * Set a font bold style to the entire range
     *
     * @param bold Boolean which indicates if the font has bold style or not
     */
    public void setFontBold(boolean bold) {
        iterateRange((cell,row,column) -> cell.getStyle().setBold(bold));
    }

    /**
     * Set a set of font bolds styles to the range. The array must have the same size of the entire range itself
     *
     * <pre>
     *     Range range = sheet.getRange(0, 0, 2, 2); // 2x2 Range
     *     range.setFontBolds(true, false, true, false); // Set the font bold formatting for the range
     * </pre>
     *
     * @param bold The font bolds array, it must the same size of the range itself
     * @throws IllegalArgumentException if the number of values is not equals to the size of range
     */
    public void setFontBolds(boolean... bold) {
        if (bold.length != getNumValues())
            throw new IllegalArgumentException("Error in setFontBold, the number of the arguments doesn't fit ("
                    + bold.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setBold(bold[row*getNumColumns()+column]));
    }

    /**
     * Set a set of font bolds formatting to the range. The format array must have the same size of the entire range itself
     *
     * @param bold The format 2D-array, it must have the same size of the range itself
     * @throws IllegalArgumentException if the number of values is not equals to the size of range
     */
    public void setFontBolds(boolean[][] bold) {
        if (bold.length == 0)
            throw new IllegalArgumentException("Error in setFontBold, the array is empty");
        if (bold.length != getNumRows())
            throw new IllegalArgumentException("Error in setFontBolds, the number of rows doesn't fit ("
                    + bold.length + " against " + getNumRows() + ")");
        if (bold[0].length != getNumColumns())
            throw new IllegalArgumentException("Error in setFontBolds, the number of columns doesn't fit ("
                    + bold.length + " against " + getNumColumns() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setBold(bold[row][column]));
    }

    /**
     * Set a font italic style to the entire range
     *
     * @param italic Boolean which indicates if the font has italic style or not
     */
    public void setFontItalic(boolean italic)
    {
        iterateRange((cell, row, column) -> cell.getStyle().setItalic(italic));
    }

    /**
     * Set a set of font italics styles to the range. The array must have the same size of the entire range itself
     *
     * <pre>
     *     Range range = sheet.getRange(0, 0, 2, 2); // 2x2 Range
     *     range.setFontItalics(true, false, true, false); // Set the font italic formatting for the range
     * </pre>
     *
     * @param italic The font italics array, it must the same size of the range itself
     * @throws IllegalArgumentException if the number of values is not equals to the size of range
     */
    public void setFontItalics(boolean... italic) {
        if (italic.length != getNumValues())
            throw new IllegalArgumentException("Error in setFontItalic, the number of the arguments doesn't fit ("
                    + italic.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setItalic(italic[row*getNumColumns()+column]));
    }

    /**
     * Set a set of font italics formatting to the range. The format array must have the same size of the entire range itself
     *
     * @param italic The format 2D-array, it must have the same size of the range itself
     * @throws IllegalArgumentException if the number of values is not equals to the size of range
     */
    public void setFontItalics(boolean[][] italic)
    {
        if (italic.length == 0)
            throw new IllegalArgumentException("Error in setFontItalics, the array is empty");
        if (italic.length != getNumRows())
            throw new IllegalArgumentException("Error in setFontItalics, the number of rows doesn't fit ("
                    + italic.length + " against " + getNumRows() + ")");
        if (italic[0].length != getNumColumns())
            throw new IllegalArgumentException("Error in setFontItalics, the number of columns doesn't fit ("
                    + italic.length + " against " + getNumColumns() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setItalic(italic[row][column]));
    }

    /**
     * Set a font underline style to the entire range
     *
     * @param underline Boolean which indicates if the font has underline style or not
     */
    public void setFontUnderline(boolean underline)
    {
        iterateRange((cell, row, column) -> cell.getStyle().setUnderline(underline));
    }

    /**
     * Set a set of font underlines styles to the range. The array must have the same size of the entire range itself
     *
     * <pre>
     *     Range range = sheet.getRange(0, 0, 2, 2); // 2x2 Range
     *     range.setFontUnderlines(true, false, true, false); // Set the font underline formatting for the range
     * </pre>
     *
     * @param underline The font underline array, it must the same size of the range itself
     * @throws IllegalArgumentException if the number of values is not equals to the size of range
     */
    public void setFontUnderlines(boolean... underline)
    {
        if (underline.length != getNumValues())
            throw new IllegalArgumentException("Error in setFontUnderlines, the number of the arguments doesn't fit ("
                    + underline.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setUnderline(underline[row*getNumColumns()+column]));
    }

    /**
     * Set a set of font underlines formatting to the range. The format array must have the same size of the entire range itself
     *
     * @param underline The format 2D-array, it must have the same size of the range itself
     * @throws IllegalArgumentException if the number of values is not equals to the size of range
     */
    public void setFontUnderlines(boolean[][] underline)
    {
        if (underline.length == 0)
            throw new IllegalArgumentException("Error in setFontUnderlines, the array is empty");
        if (underline.length != getNumRows())
            throw new IllegalArgumentException("Error in setFontUnderlines, the number of rows doesn't fit ("
                    + underline.length + " against " + getNumRows() + ")");
        if (underline[0].length != getNumColumns())
            throw new IllegalArgumentException("Error in setFontUnderlines, the number of columns doesn't fit ("
                    + underline.length + " against " + getNumColumns() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setUnderline(underline[row][column]));
    }

    /**
     * Set a font color to the entire range
     *
     * @param color The color to aplicate. A null value indicates no color
     */
    public void setFontColor(Color color)
    {
        iterateRange((cell, row, column) -> cell.getStyle().setFontColor(color));
    }

    /**
     * Set a set of font colors styles to the range. The array must have the same size of the entire range itself
     *
     * <pre>
     *     Color red = new Color(255, 0, 0);
     *     Color green = new Color(0, 255, 0);
     *     Color blue = new Color(0, 0, 255);
     *     Range range = sheet.getRange(0, 0, 2, 2); // 2x2 Range
     *     range.setFontColors(red, green, blue, null); // Set the font colors for the range
     * </pre>
     *
     * @param color The font colors array, it must the same size of the range itself. Null values indicates no color
     * @throws IllegalArgumentException if the number of values is not equals to the size of range
     */
    public void setFontColors(Color... color)
    {
        if (color.length != getNumValues())
            throw new IllegalArgumentException("Error in setFontColors, the number of the arguments doesn't fit ("
                    + color.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setFontColor(color[row*getNumColumns()+column]));
    }

    /**
     * Set a set of font colors formatting to the range. The format array must have the same size of the entire range itself
     *
     * @param color The format 2D-array, it must have the same size of the range itself. A null value in the array indicates no color
     * @throws IllegalArgumentException if the number of values is not equals to the size of range
     */
    public void setFontColors(Color[][] color)
    {
        if (color.length == 0)
            throw new IllegalArgumentException("Error in setFontColors, the array is empty");
        if (color.length != getNumRows())
            throw new IllegalArgumentException("Error in setFontColors, the number of rows doesn't fit ("
                    + color.length + " against " + getNumRows() + ")");
        if (color[0].length != getNumColumns())
            throw new IllegalArgumentException("Error in setFontColors, the number of columns doesn't fit ("
                    + color.length + " against " + getNumColumns() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setFontColor(color[row][column]));
    }

    /**
     * Set a cell background color to the entire range
     *
     * @param color The color to aplicate. A null value indicates "no color"
     */
    public void setBackgroundColor(Color color)
    {
        iterateRange((cell, row, column) -> cell.getStyle().setBackgroundColor(color));
    }

    /**
     * Set a set of cell background colors to the range. The array must have the same size of the entire range itself
     *
     * <pre>
     *     Color red = new Color(255, 0, 0);
     *     Color green = new Color(0, 255, 0);
     *     Color blue = new Color(0, 0, 255);
     *     Range range = sheet.getRange(0, 0, 2, 2); // 2x2 Range
     *     range.setBackgroundColors(red, green, blue, null); // Set the background colors for the range
     * </pre>
     *
     * @param color The background colors array, it must the same size of the range itself. Null values indicates no color
     * @throws IllegalArgumentException if the number of values is not equals to the size of range
     */
    public void setBackgroundColors(Color... color)
    {
        if (color.length != getNumValues())
            throw new IllegalArgumentException("Error in setBackgroundColors, the number of the arguments doesn't fit ("
                    + color.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setBackgroundColor(color[row*getNumColumns()+column]));
    }

    /**
     * Set a set of cell background colors formatting to the range. The format array must have the same size of the entire range itself
     *
     * @param color The format 2D-array, it must have the same size of the range itself
     * @throws IllegalArgumentException if the number of values is not equals to the size of range
     */
    public void setBackgroundColors(Color[][] color)
    {
        if (color.length == 0)
            throw new IllegalArgumentException("Error in setBackgroundColors, the array is empty");
        if (color.length != getNumRows())
            throw new IllegalArgumentException("Error in setBackgroundColors, the number of rows doesn't fit ("
                    + color.length + " against " + getNumRows() + ")");
        if (color[0].length != getNumColumns())
            throw new IllegalArgumentException("Error in setBackgroundColors, the number of columns doesn't fit ("
                    + color.length + " against " + getNumColumns() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setBackgroundColor(color[row][column]));
    }

    /**
     * Set the font size to the entire range
     *
     * @param fontSize The fontsize to aplicate. A value of -1 unsets the font size
     * @throws IllegalArgumentException if the font size is less of -1
     */
    public void setFontSize(int fontSize)
    {
        iterateRange((cell, row, column) -> cell.getStyle().setFontSize(fontSize));
    }

    /**
     * Set a set of font sizes to the range. The array must have the same size of the entire range itself
     *
     * <pre>
     *     Range range = sheet.getRange(0, 0, 2, 2); // 2x2 Range
     *     range.setBackgroundColors(20, 15, -1, 12); // Set the font sizes for the range
     * </pre>
     *
     * @param fontSizes The background colors array, it must the same size of the range itself. -1 values unsets the font size
     * @throws IllegalArgumentException if the number of values is not equals to the size of range or a font size is less of -1
     */
    public void setFontSizes(int... fontSizes)
    {
        if (fontSizes.length != getNumValues())
            throw new IllegalArgumentException("Error in setFontSizes, the number of the arguments doesn't fit ("
                    + fontSizes.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setFontSize(fontSizes[row*getNumColumns()+column]));
    }

    /**
     * Set a set of font sizes to the range. The format array must have the same size of the entire range itself
     *
     * @param fontSizes The format 2D-array, it must have the same size of the range itself
     * @throws IllegalArgumentException if the number of values is not equals to the size of range or a font size is less of -1
     */
    public void setFontSizes(int[][] fontSizes)
    {
        if (fontSizes.length == 0)
            throw new IllegalArgumentException("Error in setFontSizes, the array is empty");
        if (fontSizes.length != getNumRows())
            throw new IllegalArgumentException("Error in setFontSizes, the number of rows doesn't fit ("
                    + fontSizes.length + " against " + getNumRows() + ")");
        if (fontSizes[0].length != getNumColumns())
            throw new IllegalArgumentException("Error in setFontSizes, the number of columns doesn't fit ("
                    + fontSizes.length + " against " + getNumColumns() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setFontSize(fontSizes[row][column]));
    }

    private void iterateRange(RangeIterator e){
        for (int i = 0;i < numrows;i++){
            for (int j = 0;j < numcolumns;j++) {
                Cell cell = sheet.getCell(row_init+i,column_init+j);
                GroupCell groupCell = cell.getGroup();
                if (groupCell != null)
                    cell = groupCell.getCell();
                e.call(cell,i,j);
            }
        }
    }

    private String valuesToString(){
        StringBuilder builder = new StringBuilder();

        MutableInteger lastRow = new MutableInteger();
        iterateRange((cell, i, j) -> {
            if (lastRow.number != i) {
                builder.append("\n");
                lastRow.number = i;
            }
            if (j > 0) {
                builder.append(" , ");
            }
            builder.append(cell.getValue());
        });
        return builder.toString();
    }

    /**
     * Set a formula for every cell in the range. Note that this method doesn't evaluate the formula. It just
     * sets the formula value for when you decide to save it to a file.
     *
     * @param formula The formula string representation in the same format of a regular spreadsheet, a example would be: "A1+A2+A3"
     */
    public void setFormula(String formula)
    {
        iterateRange((cell,row,column) -> cell.setFormula(formula));
    }

    /**
     * Set a set of formulas to the range. The array must have the same size of the entire range itself.
     * Note that this method doesn't evaluate the formulas. It jus sets the formula value for when you decide to save it to a file.
     *
     * @param formula The formulas array, it must the same size of the range itself. The format of each formula
     *                should be same used in a regular spreadsheet, for example: "A1+A2+A3"
     * @throws IllegalArgumentException if the number of values is not equals to the size of range
     */
    public void setFormulas(String... formula){
        if (formula.length != getNumValues())
            throw new IllegalArgumentException("Error in setFormulas, the number of the arguments doesn't fit ("
                    + formula.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.setFormula(formula[row*getNumColumns()+column]));
    }

    /**
     * Set a set of formulas to the range. The formula array must have the same size of the entire range itself
     * Note that this method doesn't evaluate the formulas. It jus sets the formula value for when you decide to save it to a file.
     *
     * @param formula The formula 2D-array, it must have the same size of the range itself
     * @throws IllegalArgumentException if the number of values is not equals to the size of range
     */
    public void setFormulas(String formula[][])
    {
        if (formula.length == 0)
            throw new IllegalArgumentException("Error in setFormulas, the array is empty");
        if (formula.length != getNumRows())
            throw new IllegalArgumentException("Error in setFormulas, the number of rows doesn't fit ("
                    + formula.length + " against " + getNumRows() + ")");
        if (formula[0].length != getNumColumns())
            throw new IllegalArgumentException("Error in setFormulas, the number of columns doesn't fit ("
                    + formula.length + " against " + getNumColumns() + ")");

        iterateRange((cell,row,column) -> cell.setFormula(formula[row][column]));
    }

    @Override
    public String toString()
    {
        return "Range{" +
                "\ncolumn_init=" + column_init +
                "\nrow_init=" + row_init +
                "\nnumrows=" + numrows +
                "\nnumcolumns=" + numcolumns +
                "\nvalues =\n\n" + valuesToString() +
                "\n}";
    }

    /**
     * Set a format style for all the cells of the range
     *
     * @param style The style to apply. Cells will receive a clone of this object.
     * @throws IllegalArgumentException if the style is null
     */
    public void setStyle(Style style)
    {
        iterateRange((cell, row, column) -> cell.setStyle(style));
    }

    /**
     * Set a set of format styles to the range. The array must have the same size of the entire range itself.
     *
     * @param style The style array, it must the same size of the range itself.
     * @throws IllegalArgumentException if the number of values is not equals to the size of range or a style is null
     */
    public void setStyles(Style... style){
        if (style.length != getNumValues())
            throw new IllegalArgumentException("Error in setStyles, the number of the arguments doesn't fit ("
                    + style.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.setStyle(style[row*getNumColumns()+column]));
    }

    /**
     * Set a set of styles to the range. The array must have the same size of the entire range itself
     *
     * @param style The formula 2D-array, it must have the same size of the range itself
     * @throws IllegalArgumentException if the number of values is not equals to the size of range or a style is null
     */
    public void setStyles(Style style[][])
    {
        if (style.length == 0)
            throw new IllegalArgumentException("Error in setStyles, the array is empty");
        if (style.length != getNumRows())
            throw new IllegalArgumentException("Error in setStyles, the number of rows doesn't fit ("
                    + style.length + " against " + getNumRows() + ")");
        if (style[0].length != getNumColumns())
            throw new IllegalArgumentException("Error in setStyles, the number of columns doesn't fit ("
                    + style.length + " against " + getNumColumns() + ")");

        iterateRange((cell,row,column) -> cell.setStyle(style[row][column]));
    }

    /**
     * Merges the cells in the range together into a single block. If the range only contains a cell, no actions will be taken
     *
     * @throws AssertionError if a cell is already in a group. No changes will be done
     */
    public void merge()
    {
        if (getNumValues() <= 1)
            return;

        if (isPartOfMerge())
            throw new AssertionError("Error, one of the cells is already on a group");

        Vector cord = new Vector(getRow(), getColumn());
        Vector length = new Vector(getNumRows(), getNumColumns());
        Cell firstCell = sheet.getCell(row_init,column_init);
        GroupCell groupCell = new GroupCell(cord, length, firstCell);
        iterateRange((cell,row,column) -> cell.setGroup(groupCell));
    }

    private boolean rowInRange(int row)
    {
        return row >= getRow() && row <= getLastRow();
    }

    private boolean columnsInrange(int column)
    {
        return column >= getColumn() && column <= getLastColumn();
    }

    /**
     * Breaks apart any combined cells on the range.
     * The full group must be inside of the range or a IllegalArgumentException would be thrown.
     *
     * @throws IllegalArgumentException If a group is not fully included on the range, no changes will be done
     */

    public void split()
    {
        Range[] groupRange = getMergedCells();

        for (Range range : groupRange) {
            if (!rowInRange(range.getRow()) || !rowInRange(range.getLastRow())) {
                throw new IllegalArgumentException("All the combined cells must be inside the range. The row interval (" + range.getRow() + " - " + range.getLastRow() + ") is not fully included");
            }
            if (!columnsInrange(range.getColumn()) || !columnsInrange(range.getLastColumn())) {
                throw new IllegalArgumentException("All the combined cells must be inside the range. The column interval (" + range.getColumn() + " , " + range.getLastColumn() + ") is not fully included");
            }
        }

        for (Range range : groupRange) {
            for (int i = 0; i < range.getNumRows(); i++){
                for (int j = 0; j < range.getNumColumns(); j++) {
                    Cell cell = sheet.getCell(range.getRow()+i,range.getColumn()+j);
                    if (i > 0 || j > 0)
                        cell.clear();
                    cell.setGroup(null);
                }
            }
        }
    }

    /**
     * Determines if the range is part of an existing cell's block
     *
     * @return True if the range overlap any merged cells
     */
    public boolean isPartOfMerge()
    {
        return getMergedCells().length > 0;
    }
}
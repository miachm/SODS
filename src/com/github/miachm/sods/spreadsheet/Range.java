package com.github.miachm.sods.spreadsheet;

/**
 * A range represents a subset of a Sheet
 * You use ranges for write/read content in a Sheet
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

    /**
     * Get the formula String of the first cell of the range
     *
     * @return the formula text representation, it can be null
     */
    public String getFormula(){
        return sheet.getCell(row_init,column_init).getFormula();
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
     * The value could be String, Float or Integer
     * Empty cells returns a null object
     *
     * @return the value in this cell
     */
    public Object getValue(){
        return sheet.getCell(row_init,column_init).getValue();
    }

    /**
     * Returns the rectangular grid of values for this range.
     * The values could be String, Float or Integer
     * Empty cells returns a null object
     *
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
        return sheet.getCell(row_init,column_init).getStyleCopy();
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
     * @param o The value object, it can be Integer, Float, String or null. Differents values types
     *          will be considered as Strings and toString() will be invoked
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
     * @param o The values 2D-array, it must the same size of the range itself
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
     * @param o The format 2D-array, it must the same size of the range itself
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

    public void setFontItalic(boolean italic)
    {
        iterateRange((cell, row, column) -> cell.getStyle().setItalic(italic));
    }

    public void setFontItalics(boolean... italic) {
        if (italic.length != getNumValues())
            throw new IllegalArgumentException("Error in setFontItalic, the number of the arguments doesn't fit ("
                    + italic.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setItalic(italic[row*getNumColumns()+column]));
    }

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

    public void setFontUnderline(boolean underline)
    {
        iterateRange((cell, row, column) -> cell.getStyle().setUnderline(underline));
    }

    public void setFontUnderlines(boolean... underline)
    {
        if (underline.length != getNumValues())
            throw new IllegalArgumentException("Error in setFontUnderlines, the number of the arguments doesn't fit ("
                    + underline.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setUnderline(underline[row*getNumColumns()+column]));
    }

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

    public void setFontColor(Color color)
    {
        iterateRange((cell, row, column) -> cell.getStyle().setFontColor(color));
    }

    public void setFontColors(Color... color)
    {
        if (color.length != getNumValues())
            throw new IllegalArgumentException("Error in setFontColors, the number of the arguments doesn't fit ("
                    + color.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setFontColor(color[row*getNumColumns()+column]));
    }

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


    public void setBackgroundColor(Color color)
    {
        iterateRange((cell, row, column) -> cell.getStyle().setBackgroundColor(color));
    }

    public void setBackgroundColors(Color... color)
    {
        if (color.length != getNumValues())
            throw new IllegalArgumentException("Error in setBackgroundColors, the number of the arguments doesn't fit ("
                    + color.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setBackgroundColor(color[row*getNumColumns()+column]));
    }

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

    public void setFontSize(int fontSize)
    {
        iterateRange((cell, row, column) -> cell.getStyle().setFontSize(fontSize));
    }

    public void setFontSizes(int... fontSizes)
    {
        if (fontSizes.length != getNumValues())
            throw new IllegalArgumentException("Error in setFontSizes, the number of the arguments doesn't fit ("
                    + fontSizes.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setFontSize(fontSizes[row*getNumColumns()+column]));
    }

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
            for (int j = 0;j < numcolumns;j++){
                Cell cell = sheet.getCell(row_init+i,column_init+j);
                e.call(cell,i,j);
            }
        }
    }

    private String valuesToString(){
        StringBuilder builder = new StringBuilder();
        Object[][] values = getValues();

        for (int i = 0;i < values.length;i++){
            builder.append(values[i][0]);
            for (int j = 1;j < values[i].length;j++){
                builder.append(" , " + values[i][j]);
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public void setFormula(String formula)
    {
        iterateRange((cell,row,column) -> cell.setFormula(formula));
    }

    public void setFormulas(String... formula){
        if (formula.length != getNumValues())
            throw new IllegalArgumentException("Error in setValues, the number of the arguments doesn't fit ("
                    + formula.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.setFormula(formula[row*getNumColumns()+column]));
    }

    public void setFormulas(String formula[][])
    {
        if (formula.length == 0)
            throw new IllegalArgumentException("Error in setValues, the array is empty");
        if (formula.length != getNumRows())
            throw new IllegalArgumentException("Error in setValues, the number of rows doesn't fit ("
                    + formula.length + " against " + getNumRows() + ")");
        if (formula[0].length != getNumColumns())
            throw new IllegalArgumentException("Error in setValues, the number of columns doesn't fit ("
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

    public void setStyle(Style style)
    {
        iterateRange((cell, row, column) -> cell.setStyle(style));
    }

    public void setStyles(Style style[][])
    {
        if (style.length == 0)
            throw new IllegalArgumentException("Error in setFontItalics, the array is empty");
        if (style.length != getNumRows())
            throw new IllegalArgumentException("Error in setFontItalics, the number of rows doesn't fit ("
                    + style.length + " against " + getNumRows() + ")");
        if (style[0].length != getNumColumns())
            throw new IllegalArgumentException("Error in setFontItalics, the number of columns doesn't fit ("
                    + style.length + " against " + getNumColumns() + ")");

        iterateRange((cell,row,column) -> cell.setStyle(style[row][column]));
    }
}

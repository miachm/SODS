package com.github.miachm.SODS.spreadsheet;

public class Range {
    private final int column_init,row_init;
    private final int numrows,numcolumns;
    private final Sheet sheet;

    Range(Sheet sheet,int row_init,int column_init,int numrows,int numcolumns){
        if (row_init + numrows > sheet.getMaxRows())
            throw new AssertionError("Range goes out of bounds: "+
                                    "(end_of_range: " + (row_init+numrows) + ", maxrows in sheet: " + sheet.getMaxRows());

        if (column_init + numcolumns > sheet.getMaxColumns())
            throw new AssertionError("Range goes out of bounds: "+
                    "(end_of_range: " + (column_init+numcolumns) + ", maxcolumns in sheet: " + sheet.getMaxColumns());

        this.sheet = sheet;
        this.row_init = row_init;
        this.column_init = column_init;
        this.numrows = numrows;
        this.numcolumns = numcolumns;
    }

    public void clear(){
        iterateRange((cell,row,column) -> cell.clear());
    }

    public void copyTo(Range dest){
        if (dest.getNumValues() != getNumValues())
            throw new AssertionError("Error in copyTo, the range is of different size ("
                    + dest.getNumValues() + " against " + getNumValues() + ")");

        dest.setValues(getValues());
    }

    public Range getCell(int row,int column){
        return new Range(sheet,row_init+row,column_init+column,1,1);
    }

    public int getColumn(){
        return column_init;
    }

    public String getFormula(){
        return sheet.getCell(row_init,column_init).getFormula();
    }

    public String[][] getFormulas(){
        String[][] formulas = new String[getNumRows()][getNumColumns()];

        iterateRange((cell,row,column) -> formulas[row][column] = cell.getFormula());

        return formulas;
    }

    public int getLastColumn(){
        return column_init + getNumColumns()-1;
    }

    public int getLastRow(){
        return row_init + getNumRows()-1;
    }

    public int getNumColumns(){
        return numcolumns;
    }

    public int getNumRows(){
        return numrows;
    }

    public int getRow(){
        return row_init;
    }

    public Sheet getSheet(){
        return sheet;
    }

    public Object getValue(){
        return sheet.getCell(row_init,column_init).getValue();
    }

    public Object[][] getValues(){
        Object[][] values = new Object[getNumRows()][getNumColumns()];
        iterateRange((cell,row,column) -> values[row][column] = cell.getValue());
        return values;
    }

    public Style getStyle()
    {
        return sheet.getCell(row_init,column_init).getStyle();
    }

    public Style[][] getStyles() {
        Style[][] arr = new Style[getNumRows()][getNumColumns()];
        iterateRange((cell, row, column) ->  arr[row][column] = cell.getStyle());
        return arr;
    }

    public int getNumValues(){
        return getNumColumns()*getNumRows();
    }

    public void setValue(Object o){
        iterateRange((cell,row,column) -> cell.setValue(o));
    }

    public void setValues(Object... o){
        if (o.length != getNumValues())
            throw new AssertionError("Error in setValues, the number of the arguments doesn't fit ("
                    + o.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.setValue(o[row*getNumColumns()+column]));
    }

    public void setValues(Object[][] o){
        if (o.length == 0)
            throw new AssertionError("Error in setValues, the array is empty");
        if (o.length != getNumRows())
            throw new AssertionError("Error in setValues, the number of rows doesn't fit ("
                    + o.length + " against " + getNumRows() + ")");
        if (o[0].length != getNumColumns())
            throw new AssertionError("Error in setValues, the number of columns doesn't fit ("
                    + o.length + " against " + getNumColumns() + ")");

        iterateRange((cell,row,column) -> cell.setValue(o[row][column]));
    }

    public void setFontBold(boolean bold) {
        iterateRange((cell,row,column) -> cell.getStyle().setBold(bold));
    }

    public void setFontBolds(boolean... bold) {
        if (bold.length != getNumValues())
            throw new AssertionError("Error in setFontBold, the number of the arguments doesn't fit ("
                    + bold.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setBold(bold[row*getNumColumns()+column]));
    }

    public void setFontBolds(boolean[][] bold) {
        if (bold.length == 0)
            throw new AssertionError("Error in setFontBold, the array is empty");
        if (bold.length != getNumRows())
            throw new AssertionError("Error in setFontBolds, the number of rows doesn't fit ("
                    + bold.length + " against " + getNumRows() + ")");
        if (bold[0].length != getNumColumns())
            throw new AssertionError("Error in setFontBolds, the number of columns doesn't fit ("
                    + bold.length + " against " + getNumColumns() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setBold(bold[row][column]));
    }

    public void setFontItalic(boolean italic)
    {
        iterateRange((cell, row, column) -> cell.getStyle().setItalic(italic));
    }

    public void setFontItalics(boolean... italic) {
        if (italic.length != getNumValues())
            throw new AssertionError("Error in setFontItalic, the number of the arguments doesn't fit ("
                    + italic.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setItalic(italic[row*getNumColumns()+column]));
    }

    public void setFontItalics(boolean[][] italic)
    {
        if (italic.length == 0)
            throw new AssertionError("Error in setFontItalics, the array is empty");
        if (italic.length != getNumRows())
            throw new AssertionError("Error in setFontItalics, the number of rows doesn't fit ("
                    + italic.length + " against " + getNumRows() + ")");
        if (italic[0].length != getNumColumns())
            throw new AssertionError("Error in setFontItalics, the number of columns doesn't fit ("
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
            throw new AssertionError("Error in setFontUnderlines, the number of the arguments doesn't fit ("
                    + underline.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setUnderline(underline[row*getNumColumns()+column]));
    }

    public void setFontUnderlines(boolean[][] underline)
    {
        if (underline.length == 0)
            throw new AssertionError("Error in setFontUnderlines, the array is empty");
        if (underline.length != getNumRows())
            throw new AssertionError("Error in setFontUnderlines, the number of rows doesn't fit ("
                    + underline.length + " against " + getNumRows() + ")");
        if (underline[0].length != getNumColumns())
            throw new AssertionError("Error in setFontUnderlines, the number of columns doesn't fit ("
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
            throw new AssertionError("Error in setFontColors, the number of the arguments doesn't fit ("
                    + color.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setFontColor(color[row*getNumColumns()+column]));
    }

    public void setFontColors(Color[][] color)
    {
        if (color.length == 0)
            throw new AssertionError("Error in setFontColors, the array is empty");
        if (color.length != getNumRows())
            throw new AssertionError("Error in setFontColors, the number of rows doesn't fit ("
                    + color.length + " against " + getNumRows() + ")");
        if (color[0].length != getNumColumns())
            throw new AssertionError("Error in setFontColors, the number of columns doesn't fit ("
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
            throw new AssertionError("Error in setBackgroundColors, the number of the arguments doesn't fit ("
                    + color.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setBackgroundColor(color[row*getNumColumns()+column]));
    }

    public void setBackgroundColors(Color[][] color)
    {
        if (color.length == 0)
            throw new AssertionError("Error in setBackgroundColors, the array is empty");
        if (color.length != getNumRows())
            throw new AssertionError("Error in setBackgroundColors, the number of rows doesn't fit ("
                    + color.length + " against " + getNumRows() + ")");
        if (color[0].length != getNumColumns())
            throw new AssertionError("Error in setBackgroundColors, the number of columns doesn't fit ("
                    + color.length + " against " + getNumColumns() + ")");

        iterateRange((cell,row,column) -> cell.getStyle().setBackgroundColor(color[row][column]));
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
            throw new AssertionError("Error in setValues, the number of the arguments doesn't fit ("
                    + formula.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.setFormula(formula[row*getNumColumns()+column]));
    }

    public void setFormulas(String formula[][])
    {
        if (formula.length == 0)
            throw new AssertionError("Error in setValues, the array is empty");
        if (formula.length != getNumRows())
            throw new AssertionError("Error in setValues, the number of rows doesn't fit ("
                    + formula.length + " against " + getNumRows() + ")");
        if (formula[0].length != getNumColumns())
            throw new AssertionError("Error in setValues, the number of columns doesn't fit ("
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
            throw new AssertionError("Error in setFontItalics, the array is empty");
        if (style.length != getNumRows())
            throw new AssertionError("Error in setFontItalics, the number of rows doesn't fit ("
                    + style.length + " against " + getNumRows() + ")");
        if (style[0].length != getNumColumns())
            throw new AssertionError("Error in setFontItalics, the number of columns doesn't fit ("
                    + style.length + " against " + getNumColumns() + ")");

        iterateRange((cell,row,column) -> cell.setStyle(style[row][column]));
    }
}

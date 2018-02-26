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

    public boolean getFontBold() {
        return sheet.getCell(row_init,column_init).isBold();
    }

    public boolean[][] getFontBolds() {
        boolean[][] arr = new boolean[getNumRows()][getNumColumns()];
        iterateRange((cell,row,column) -> arr[row][column] = cell.isBold());
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
        iterateRange((cell,row,column) -> cell.setBold(bold));
    }

    public void setFontBolds(boolean... bold) {
        if (bold.length != getNumValues())
            throw new AssertionError("Error in setFontBold, the number of the arguments doesn't fit ("
                    + bold.length + " against " + getNumValues() + ")");

        iterateRange((cell,row,column) -> cell.setBold(bold[row*getNumColumns()+column]));
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

        iterateRange((cell,row,column) -> cell.setBold(bold[row][column]));
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
    public String toString() {
        return "Range{" +
                "\ncolumn_init=" + column_init +
                "\nrow_init=" + row_init +
                "\nnumrows=" + numrows +
                "\nnumcolumns=" + numcolumns +
                "\nvalues =\n\n" + valuesToString() +
                "\n}";
    }
}

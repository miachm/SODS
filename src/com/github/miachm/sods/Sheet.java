package com.github.miachm.sods;

/**
 * Represents a sheet in a Spreadsheet.
 *
 * You can create empty sheets and add to an existing Spreadsheet
 */
public class Sheet implements Cloneable,Comparable<Sheet> {
    private RowCollection rows = new RowCollection();
    private ColumnCollection columns = new ColumnCollection(rows);

    private String name;
    private int numColumns = 0;
    private int numRows = 0;
    private boolean isHidden = false;

    /**
     * Create an empty sheet with a given name.
     * The sheet will have a 1x1 GRID by default.
     *
     * @param name A name which identifies this sheet
     */
    public Sheet(String name)
    {
        this(name, 1, 1);
    }

    /**
     * Create an empty sheet with a given name and dimmensions
     *
     * @param name A name which identifies this sheet
     * @param rows Number of rows in the sheet
     * @param columns Number of columns in the sheet
     *
     * @throws IllegalArgumentException If the number of rows/columns are negative
     */
    public Sheet(String name, int rows, int columns)
    {
        if (rows < 0 || columns < 0)
            throw new IllegalArgumentException("Rows/Columns can't be negative");

        this.name = name;
        appendColumns(columns);
        appendRows(rows);
    }

    /**
     * Append a new row at the end of the heet
     *
     * @see appendRows
     */
    public void appendRow(){
        appendRows(1);
    }

    /**
     * Append many rows at the end of the Spreadsheet
     * @param howmany The number of rows to be appended
     */
    public void appendRows(int howmany)
    {
        insertRowsAfter(getMaxRows()-1,howmany);
    }

    /**
     * Append a column at the end of the Spreadsheet
     *
     * @see appendColumns
     */
    public void appendColumn(){
        appendColumns(1);
    }

    /**
     * Append many columns at the end of the Spreadsheet.
     * @param howmany The number of columns to be appended
      */
    public void appendColumns(int howmany){
        insertColumnsAfter(getMaxColumns()-1,howmany);
    }

    /**
     * Clear all the content of the sheet. This doesn't change the number of rows/columns
     */
    public void clear() {
        getDataRange().clear();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Delete a specific column on the sheet
     *
     * @param column The column index to be deleted
     * @throws IndexOutOfBoundsException if the index is invalid
     * @see deleteColumns
     */

    public void deleteColumn(int column) {
        deleteColumns(column,1);
    }

    /**
     * Delete a number of columns starting in a specific index
     *
     * @param column The column index to start
     * @param howmany The number of columns to be deleted
     * @throws IndexOutOfBoundsException If columns + howmany is out bounds of the sheet. No changes will be done to the sheet
     */

    public void deleteColumns(int column, int howmany) {
        if (column + howmany > getMaxColumns())
            throw new IndexOutOfBoundsException("Column " + column + " plus " + howmany + " is out of bounds (" + getMaxColumns()+")");

        columns.remove(column, howmany);
        numColumns -= howmany;
    }

    /**
     * Delete a specific row in the sheet
     *
     * @param row The row index to be deleted
     * @throws  IndexOutOfBoundsException if the index is out of bounds
     * @see deleteRows
     */
    public void deleteRow(int row) {
        deleteRows(row,1);
    }

    /**
     * Delete a number of rows starting in a specific index
     *
     * @param row The row index where start
     * @param howmany How many rows will be deleted
     * @throws  IndexOutOfBoundsException if row + howmany is out of bounds, no changes will be done to the sheet
     * @see deleteRows
     */
    public void deleteRows(int row, int howmany) {
        if (row > getMaxRows())
            throw new IndexOutOfBoundsException("Row " + row + " is out of bounds (" + getMaxRows()+")");

        rows.remove(row, howmany);
        numRows -= howmany;
    }

    /**
     * Get the width of a column
     * @param column The column index where start
     * @throws IndexOutOfBoundsException if the column index is invalid
     * @return The width of the column, null if not specified
     */

    public Double getColumnWidth(int column)
    {
        checkColumnRange(column);
        return getColum(column).getStyle().getWidth();
    }

    private Column getColum(int column)
    {
        return columns.getItem(column);
    }

    /**
     * Get the height of a row
     * @param row The row index where start
     * @return The height of the row, null if not specified
     */

    public Double getRowHeight(int row) {
        checkRowRange(row);
        return getRow(row).getStyle().getHeight();
    }

    private Row getRow(int row)
    {
        return rows.getItem(row);
    }

    /**
     * Get a @Range which contains the whole Sheet content. Its useful
     * if you want look at the entire sheet content
     *
     * @return The range which contains the whole sheet's content
     */
    public Range getDataRange() {
        return getRange(0,0,getMaxRows(),getMaxColumns());
    }

    /**
     * The number of columns created in this sheet
     *
     * @return An integer which represents the number of columns in this sheet
     */
    public int getMaxColumns() {
        return numColumns;
    }

    /**
     * The number of rows created in this sheet
     *
     * @return An integer which represents the number of rows in this sheet
     */
    public int getMaxRows() {
        return numRows;
    }

    /**
     * Obtains the name of this sheet
     *
     * @return The name of the sheet
     */
    public String getName() {
        return name;
    }

    /**
     * Obtains a @Range which contains a specific cell of the sheet
     *
     * @param row X Coordinate of the cell
     * @param column Y Coordinate of the cell
     * @return A range which represents the cell
     * @throws IndexOutOfBoundsException if it represents a invalid range
     * */
    public Range getRange(int row, int column) {
        return getRange(row, column, 1, 1);
    }

    /**
     * Obtains a @Range which represents a number of rows starting
     * in a specific Cell. Note: This function only take the first column of every row
     *
     * @param row X Coordinate of the starting cell
     * @param column Y Coordinate of the starting cell
     * @param numRows How many rows to take
     * @return A range which represents the cell
     * @throws IndexOutOfBoundsException if it represents a invalid range
     * @see #getRange(int, int, int, int)
     **/
    public Range getRange(int row, int column, int numRows) {
        return getRange(row, column, numRows, 1);
    }

    /**
     * Obtains a @Range which represents a subset of the Sheet.
     *
     * @param row X Coordinate of the starting cell
     * @param column Y Coordinate of the starting cell
     * @param numRows How many rows to take
     * @param numColumns How many columns to take
     * @throws IndexOutOfBoundsException if it represents a invalid range
     * @return A range which represents the cell
     **/
    public Range getRange(int row, int column, int numRows, int numColumns) {
        return new Range(this, row, column, numRows, numColumns);
    }

    /**
     * Obtains a @Range using the A1 Notation format.
     * A1 notation is a string representation of a subset in a Spreadsheet where the column
     * is represented by a capital letter (starting in A) and the row by a row number (starting in 1).
     * For example, you would write: "B3" to obtain the Cell of column B and row 3.
     * You would write "A2:D2" to obtain the cells between A2 and D2 (included).
     *
     * Inconsistent ranges ("C2:A3") are reinterpreted when it's possible. In the example
     * would be ("A3:C2")
     *
     * @param a1Notation The string representation of the range in A1Notation
     * @throws NullPointerException If the argument is null
     * @throws IllegalArgumentException If the argument is not a valid A1Notation
     * @throws IndexOutOfBoundsException If it represents a invalid range
     * @return The range requested
     */

    public Range getRange(String a1Notation)
    {
        A1NotationCord cord = new A1NotationCord(a1Notation);
        int row = cord.getInitRow();
        int column = cord.getInitColumn();
        int numRows = cord.getLastRow() - cord.getInitRow() + 1;
        int numColumns = cord.getLastColumn() - cord.getInitColumn() + 1;
        return getRange(row, column, numRows, numColumns);
    }

    Cell getCell(int row, int column){
        Row rowEntry = rows.getItemForEdit(row);
        CellCollection cells = rowEntry.getCells();
        return cells.getItemForEdit(column);
    }

    Cell getCellReadOnly(int row, int column) {
        Row rowEntry = rows.getItem(row);
        CellCollection cells = rowEntry.getCells();
        return cells.getItem(column);
    }

    /**
     * Hides a row specified by his index
     * @param row The index of the row
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public void hideRow(int row) {
        checkRowRange(row);
        Row rowObj = getRowForEdit(row);
        rowObj.getStyle().setHidden(true);
    }

    private Column getColumnForEdit(int column) {
        return columns.getItemForEdit(column);
    }

    private Row getRowForEdit(int row) {
        return rows.getItemForEdit(row);
    }

    /**
     * Hides a column specified by his index
     * @param column The index of the row
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public void hideColumn(int column)
    {
        checkColumnRange(column);
        getColumnForEdit(column).getStyle().setHidden(true);
    }

    /**
     * Insert a column after a specific position
     * @param afterPosition The index where insert
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public void insertColumnAfter(int afterPosition) {
        insertColumnsAfter(afterPosition, 1);
    }

    /**
     * Insert a column before a specific position
     * @param beforePosition The index where insert
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public void insertColumnBefore(int beforePosition) {
        insertColumnsBefore(beforePosition, 1);
    }

    /**
     * Insert a number of columns after a specific position
     * @param columnIndex The index where insert
     * @param howmany How many columns to insert
     * @throws IndexOutOfBoundsException if the columnIndex is out of bounds, no changes will be done
     */
    public void insertColumnsAfter(int columnIndex, int howmany) {
        insertColumnsBefore(columnIndex+1,howmany);
    }

    /**
     * Insert a number of columns before a specific position
     * @param columnIndex The index where insert
     * @param howmany How many columns to insert
     * @throws IndexOutOfBoundsException if the columnIndex is out of bounds, no changes will be done
     */
    public void insertColumnsBefore(int columnIndex, int howmany) {
        if (columnIndex-1 > getMaxColumns())
            throw new IndexOutOfBoundsException("Column " + columnIndex + " is out of bounds (" + getMaxColumns()+")");

        if (howmany < 0)
            throw new IllegalArgumentException("Illegal argument in howmany");

        if (howmany == 0)
            return;

        columns.addItems(columnIndex, howmany);
        numColumns += howmany;
    }

    /**
     * Insert a row after a specific position
     * @param afterPosition The index where insert
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public void insertRowAfter(int afterPosition) {
        insertRowsAfter(afterPosition, 1);
    }

    /**
     * Insert a row before a specific position
     * @param beforePosition The index where insert
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public void insertRowBefore(int beforePosition) {
        insertRowsBefore(beforePosition, 1);
    }

    /**
     * Insert a row before a specific position
     * @param rowIndex The index where insert
     * @param howmany How many rows to insert
     * @throws IndexOutOfBoundsException if the index is out bounds, no changes will be done to sheet
     */
    public void insertRowsBefore(int rowIndex, int howmany) {
        if (rowIndex < 0 || (rowIndex - 1) > getMaxRows())
            throw new IndexOutOfBoundsException("Row " + rowIndex + " is out of bounds (" + getMaxRows()+")");

        if (howmany < 0)
            throw new IllegalArgumentException("Illegal argument in howmany");

        if (howmany == 0)
            return;

        rows.addItems(rowIndex, howmany);
        numRows += howmany;
    }

    /**
     * Insert a row after a specific position
     * @param rowIndex The index where insert
     * @param howmany How many rows to insert
     * @throws IndexOutOfBoundsException if the index is out bounds, no changes will be done to sheet
     */
    public void insertRowsAfter(int rowIndex, int howmany) {
        insertRowsBefore(rowIndex+1,howmany);
    }

    /**
     * Set a specific column width to a specific column
     * @param column The index of the column
     * @param width The width of the column. It can be a null if you want to "unset" the width
     * @throws IndexOutOfBoundsException if the column is negative or &gt;= numColumns
     * @throws IllegalArgumentException Width has to be positive
     */

    public void setColumnWidth(int column, Double width)
    {
        checkColumnRange(column);

        if (width != null) {
            if (width < 0.0)
                throw new IllegalArgumentException("Width can't be negative!");
        }
        getColumnForEdit(column).getStyle().setWidth(width);
    }

    /**
     * Set a column width to a specific set of columns
     * @param column The index of the column
     * @param numColumns The number of columns to be modified, starting on index.
     * @param width The width of the column. It can be a null if you want to "unset" the width
     * @throws IndexOutOfBoundsException if the column is negative or &gt;= numColumns
     * @throws IllegalArgumentException Width has to be positive
     */

    public void setColumnWidths(int column, int numColumns, Double width)
    {
        for (int i = 0; i < numColumns; i++)
            setColumnWidth(column +i, width);
    }

    /**
     * Set a specific row height to a specific row
     * @param row The index of the row
     * @param height The height of the row. It can be a null if you want to "unset" the height
     * @throws IndexOutOfBoundsException if the row is negative or &gt;= numRows
     * @throws IllegalArgumentException Height has to be positive
     */
    public void setRowHeight(int row, Double height)
    {
        checkRowRange(row);

        if (height != null) {
            if (height < 0.0)
                throw new IllegalArgumentException("Height can't be negative!");
        }
        getRowForEdit(row).getStyle().setHeight(height);
    }

    /**
     * Set a row height to a specific set of rows
     * @param row The index of the row
     * @param numRows The number of rows to be modified, starting on index.
     * @param height The height of the row. It can be a null if you want to "unset" the row
     * @throws IndexOutOfBoundsException if the row is negative or &gt;= numRows
     * @throws IllegalArgumentException Height has to be positive
     */
    public void setRowHeights(int row, int numRows, Double height)
    {
        for (int i = 0; i < numRows; i++)
            setRowHeight(row +i, height);
    }

    /**
     * Unhides the row at the given index. If the row wasn't hidden, this method would not have any effect
     * @param row The index of the row
     * @throws IndexOutOfBoundsException  if the row is negative or &gt;= numRows
     *
     */

    public void showRow(int row)
    {
        checkRowRange(row);
        getRowForEdit(row).getStyle().setHidden(false);
    }

    /**
     * Unhides the columns at the given index. If the column wasn't hidden, this method would not have any effect
     * @param column The index of the row
     * @throws IndexOutOfBoundsException  if the column is negative or &gt;= numColumns
     *
     */
    public void showColumn(int column)
    {
        checkColumnRange(column);
        getColumnForEdit(column).getStyle().setHidden(false);
    }

    /**
     * Determinies if a specific row is hidden or not
     * @param row The index of the row
     * @throws IndexOutOfBoundsException  if the row is negative or &gt;= numRows
     * @return True if the row is hidden
     */
    public boolean rowIsHidden(int row)
    {
        checkRowRange(row);
        return getRow(row).getStyle().isHidden();
    }

    private void checkRowRange(int row) {
        if (row < 0 || row >= getMaxRows())
            throw new IndexOutOfBoundsException("Row is not a valid position: " + row);
    }

    /**
     * Determinies if a specific column is hidden or not
     * @param column The index of the row
     * @throws IndexOutOfBoundsException  if the row is negative or &gt;= numRows
     * @return True if the row is hidden
     */
    public boolean columnIsHidden(int column)
    {
        checkColumnRange(column);
        return getColumnForEdit(column).getStyle().isHidden();
    }

    private void checkColumnRange(int column) {
        if (column < 0 || column >= getMaxColumns())
            throw new IndexOutOfBoundsException("Column is not a valid position: " + column);
    }

    /**
     * Determinies if the sheet is marked as hidden or not
     * @return True if the sheet is hidden
     */

    public boolean isHidden() {
        return isHidden;
    }

    /**
     * Mark this sheet as hidden
     */

    public void hideSheet() {
        isHidden = true;
    }

    /**
     * Mark this sheet as visible
    */
    public void showSheet() {
        isHidden = false;
    }

    /**
     * Return the last row of the sheet which contains useful data
     */
    public int getLastRow()
    {
        return rows.getLastUsefulItemIndex();
    }

    /**
     * Return the last column of the sheet which contains useful data
     */
    public int getLastColumn()
    {
        return columns.getLastUsefulItemIndex();
    }

    /**
     * Trim the sheet removing unused rows/columns at the end
    */
    public void trim()
    {
        rows.trim();
        columns.trim();

        numRows = rows.getNumItems();
        numColumns = columns.getNumItems();
    }

    void compact()
    {
        rows.compact();
        columns.compact();;
    }

    /**
     * Equals method, two sheets are considered the same if have the same name and the same content (include formatting)
     * @param o The object to compare
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sheet sheet = (Sheet) o;

        if (isHidden != sheet.isHidden) return false;
        if (!getDataRange().equals(sheet.getDataRange())) return false;
        return name.equals(sheet.name);
    }

    @Override
    public int hashCode() {
        int result = columns.hashCode();
        result = 31 * result + rows.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (isHidden ? 1 : 0);
        return result;
    }

    /**
     * compareTo method, this comparator only compare the names
     * Note: this class has a natural ordering that is inconsistent with equals.
     * @param o The object to compare
     */
    @Override
    public int compareTo(Sheet o) {
        return name.compareTo(o.getName());
    }

    @Override
    public String toString() {
        return "Sheet{" +
               // "\ndimensions=" + getLastRow() + "x" + getLastColumn() +
                ",\nname='" + name + '\'' +
                ",\nishidden=" + isHidden +
                '}';
    }
}
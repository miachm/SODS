package com.github.miachm.sods;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Represents a sheet in a Spreadsheet.
 *
 * You can create empty sheets and add to an existing Spreadsheet
 */
public class Sheet implements Cloneable,Comparable<Sheet> {
    private List<List<Cell>> cells = new ArrayList<List<Cell>>();
    private List<RowStyle> rowStyles = new ArrayList<>();
    private List<ColumnStyle> columnStyles = new ArrayList<>();
    private String name;
    private int numColumns = 0;
    private int numRows = 0;
    private boolean isHidden = false;
    private String hashed_password = null;
    private String hash_algorithm = null;

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
     * @throws NullPointerException If the number of rows/columns are negative
     */
    public Sheet(String name, int rows, int columns)
    {
        if (rows < 0 || columns < 0)
            throw new IllegalArgumentException("Rows/Columns can't be negative");
        if (name == null)
            throw new NullPointerException("The name of the sheet can't be null");

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
    public void appendRows(int howmany){
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

        numColumns -= howmany;

        int lastColumn = getLastColumn();
        howmany = Math.min(howmany, lastColumn - column);
        for (List<Cell> row : cells) {
            for (int i = 0; i < howmany; i++)
                row.remove(column);
        }

        for (int i = 0; i < howmany; i++)
            columnStyles.remove(column);
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

        for (int i = 0; i < howmany && row < getLastRow(); i++) {
            cells.remove(row);
            rowStyles.remove(row);
        }
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
        if (!isColumnLoaded(column))
            return null;
        return columnStyles.get(column).getWidth();
    }

    /**
     * Get the height of a row
     * @param row The row index where start
     * @return The height of the row, null if not specified
     */

    public Double getRowHeight(int row) {
        checkRowRange(row);
        if (!isRowLoaded(row))
            return null;
        return rowStyles.get(row).getHeight();
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

    Cell getCell(int row,int column){
        checkLazyRowLoading(row);
        checkLazyColumnLoading(column);
        Cell cell = cells.get(row).get(column);
        if (cell == Cell.default_cell) {
            cell = new Cell();
            cells.get(row).set(column, cell);
        }
        return cell;
    }

    private void checkLazyRowLoading(int row) {
        while (row >= getLastRow()) {
            List<Cell> rowItem = new ArrayList<>();

            for (int i = 0; i < getLastColumn(); i++)
                rowItem.add(Cell.default_cell);

            cells.add(rowItem);
            rowStyles.add(new RowStyle());
        }
    }

    private void checkLazyColumnLoading(int column) {
        int diff = column - getLastColumn() + 1;
        if (diff > 0) {
            for (List<Cell> rowItem : cells) {
                for (int i = 0; i < diff; i++) {
                    rowItem.add(Cell.default_cell);
                }
            }
            for (int i = 0; i < diff; i++)
                columnStyles.add(new ColumnStyle());
        }
    }

    /**
     * Hides a row specified by his index
     * @param row The index of the row
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public void hideRow(int row)
    {
        checkRowRange(row);
        checkLazyRowLoading(row);
        rowStyles.get(row).setHidden(true);
    }

    /**
     * Hides a column specified by his index
     * @param column The index of the row
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public void hideColumn(int column)
    {
        checkColumnRange(column);
        checkLazyColumnLoading(column);
        columnStyles.get(column).setHidden(true);
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

        if (columnIndex <= getLastColumn() - 1) {
            for (List<Cell> row : cells) {
                for (int i = 0; i < howmany; i++) {
                    row.add(columnIndex, Cell.default_cell);
                }
            }

            for (int i = 0; i < howmany; i++)
                columnStyles.add(new ColumnStyle());
        }
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
        if (rowIndex-1 > getMaxRows())
            throw new IndexOutOfBoundsException("Row " + rowIndex + " is out of bounds (" + getMaxRows()+")");


        if (rowIndex <= getLastRow() - 1) {
            for (int i = 0; i < howmany; i++) {
                ArrayList<Cell> row = new ArrayList<>();
                for (int j = 0; j < numColumns; j++)
                    row.add(Cell.default_cell);
                cells.add(rowIndex, row);
                rowStyles.add(new RowStyle());
            }
        }
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
        checkLazyColumnLoading(column);
        columnStyles.get(column).setWidth(width);
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
        checkLazyRowLoading(row);
        rowStyles.get(row).setHeight(height);
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
        checkLazyRowLoading(row);
        rowStyles.get(row).setHidden(false);
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
        checkLazyColumnLoading(column);
        columnStyles.get(column).setHidden(false);
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
        if (!isRowLoaded(row))
            return false;
        return rowStyles.get(row).isHidden();
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
        if (!isColumnLoaded(column)) {
            return false;
        }
        return columnStyles.get(column).isHidden();
    }

    private boolean isRowLoaded(int row)
    {
        return row < getLastRow();
    }

    private boolean isColumnLoaded(int column)
    {
        return column < getLastColumn();
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
     * @return The index of the last row
     */
    public int getLastRow()
    {
        return cells.size();
    }

    /**
     * Return the last column of the sheet which contains useful data
     * @return The index of the last column
     */
    public int getLastColumn()
    {
        return cells.isEmpty() ? columnStyles.size() : cells.get(0).size();
    }

    /**
     * Trim the sheet removing unused rows/columns at the end
    */
    public void trim()
    {
        numRows = getLastRow();
        numColumns = getLastColumn();
    }

    public boolean isProtected()
    {
        return this.hashed_password != null;
    }

    public void setPassword(String key) throws NoSuchAlgorithmException {
        if (key.isEmpty())
            throw new IllegalArgumentException("Key is empty");

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        this.hashed_password = Base64.getEncoder().encodeToString(digest.digest(
                key.getBytes(StandardCharsets.UTF_8)));

        this.hash_algorithm = "http://www.w3.org/2000/09/xmldsig#sha256";
    }

    void setRawPassword(String hashed_password, String algorithm) {
        if (algorithm == null)
            algorithm = "http://www.w3.org/2000/09/xmldsig#sha1";

        this.hashed_password = hashed_password;
        this.hash_algorithm = algorithm;
    }

    String getHashedPassword()
    {
        return this.hashed_password;
    }

    String getHashedAlgorithm()
    {
        return this.hash_algorithm;
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

        if (numColumns != sheet.numColumns) return false;
        if (numRows != sheet.numRows) return false;
        if (isHidden != sheet.isHidden) return false;

        int minRowSize = Math.min(getLastRow(), sheet.getLastRow());
        int minColumnSize = Math.min(getLastColumn(), sheet.getLastColumn());
        for (int i = 0; i < minRowSize; i++) {
            if (!rowStyles.get(i).equals(sheet.rowStyles.get(i))) {
                return false;
            }
            for (int j = 0; j < minColumnSize; j++) {
                if (!cells.get(i).get(j).equals(sheet.cells.get(i).get(j))) {
                    return false;
                }
                if (!columnStyles.get(j).equals(sheet.columnStyles.get(j))) {
                    return false;
                }
            }
        }

        if (!hasRemainingEmptyCells(minRowSize, minColumnSize, this)) return false;
        if (!hasRemainingEmptyCells(minRowSize, minColumnSize, sheet)) return false;

        return name.equals(sheet.name);
    }

    private boolean hasRemainingEmptyCells(int minRowSize, int minColumnSize, Sheet sheet) {
        Cell emptyCell = new Cell();
        for (int i = minRowSize; i < sheet.getLastRow(); i++)
            for (int j = 0; j < sheet.getLastColumn(); j++)
                if (!sheet.cells.get(i).get(j).equals(emptyCell)) {
                    return false;
                }


        for (int i = 0; i < sheet.getLastRow(); i++)
            for (int j = minColumnSize; j < sheet.getLastColumn(); j++)
                if (!sheet.cells.get(i).get(j).equals(emptyCell)) {
                    return false;
                }
        return true;
    }

    @Override
    public int hashCode() {
        int result = cells.hashCode();
        result = 31 * result + rowStyles.hashCode();
        result = 31 * result + columnStyles.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + numColumns;
        result = 31 * result + numRows;
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
                "\ndimensions=" + getLastRow() + "x" + getLastColumn() +
                ",\nname='" + name + '\'' +
                ",\nishidden=" + isHidden +
                '}';
    }
}
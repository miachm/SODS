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

    List<Column> columns = new ArrayList<>();
    List<Row> rows = new ArrayList<>();
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
     * @throws IllegalArgumentException if howmany is negative, no changes will be done to the sheet
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
     * @throws IllegalArgumentException if howmany is negative, no changes will be done to the sheet
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

    private <T extends TableField> void deleteFields(List<T> fields, int index, int howmany)
    {
        Pair<Integer,Integer> pair = getIndexDelete(fields, index);
        if (pair.first == fields.size())
            return;

        if (pair.second > 0) {
            T item = fields.get(pair.first);
            T other = (T) item.clone();

            int aux = item.num_repeated;
            item.num_repeated = pair.second;
            other.num_repeated = aux - pair.second;
            fields.add(pair.first + 1, other);
            pair.first++;
            pair.second = 0;
        }

        while (howmany > 0) {
            T item = fields.get(pair.first);
            if (howmany < item.num_repeated) {
                item.num_repeated -= howmany;
                howmany = 0;
            }
            else {
                howmany -= item.num_repeated;
                fields.remove((int)pair.first);
            }
        }
    }

    public <T extends TableField> T createInstanceOfT(Class<T> aClass) {
        try {
            return aClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private <T extends TableField> T getFieldForEditing(List<T> fields, Class<T> aClass, int index)
    {
        List<T> list = getFieldForEditingRange(fields, aClass, index, 1);
        return list.get(0);
    }

    private <T extends TableField> List<T> getFieldForEditingRange(List<T> fields, Class<T> aClass, int index, int howmany)
    {
        Pair<Integer,Integer> pair = getIndexDelete(fields, index);

        if (pair.second > 0) {
            if (pair.first == fields.size()) {
                fields.add(createInstanceOfT(aClass));
                T item = fields.get(pair.first);
                item.num_repeated = pair.second;
                pair.first++;
            }
            else {
                T item = fields.get(pair.first);
                T other = (T) item.clone();

                int aux = item.num_repeated;
                item.num_repeated = pair.second;
                other.num_repeated = aux - pair.second;
                fields.add(pair.first + 1, other);
                pair.first++;
            }
        }

        List<T> list = new ArrayList<>();
        while (howmany > 0 && pair.first < fields.size()) {
            T item = fields.get(pair.first);
            if (item.num_repeated == howmany) {
                list.add(item);
                return list;
            }
            else if (item.num_repeated < howmany){
                howmany -= item.num_repeated;
                list.add(item);
                pair.first++;
            }
            else {
                int aux = item.num_repeated;
                item.num_repeated -= howmany;
                T other = (T) item.clone();
                other.num_repeated = aux - item.num_repeated;
                list.add(other);
                fields.add(pair.first, other);
                pair.first++;
                howmany = 0;
            }
        }
        if (howmany > 0) {
            T item = createInstanceOfT(aClass);
            item.num_repeated = howmany;
            fields.add(item);
            list.add(item);
        }
        return list;
    }

    private <T extends TableField> void insertField(List<T> fields, T value, int index)
    {
        for (int i = 0; i < fields.size(); i++) {
            T item = fields.get(i);
            if (index >= item.num_repeated) {
                index -= item.num_repeated;
            }
            else {
                int cnt = item.num_repeated;
                item.num_repeated = item.num_repeated - index;
                fields.add(i, value);

                T other = (T) item.clone();
                other.num_repeated = cnt - item.num_repeated;
                if (other.num_repeated > 0)
                    fields.add(i+1, other);
                return;
            }
        }
        fields.add(value);
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
     * @throws IllegalArgumentException if howmany is negative, no changes will be done to the sheet
     */

    public void deleteColumns(int column, int howmany) {
        if (column < 0)
            throw new IndexOutOfBoundsException("Column " + column + " is negative");
        if (column + howmany > getMaxColumns())
            throw new IndexOutOfBoundsException("Column " + column + " plus " + howmany + " is out of bounds (" + getMaxColumns()+")");
        if (howmany < 0)
            throw new IllegalArgumentException("The number of columns can't be negative");
        if (howmany == 0)
            return;

        deleteFields(columns, column, howmany);
        for (Row row : rows)
            deleteFields(row.cells, column, howmany);

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
        if (row + howmany > getMaxRows())
            throw new IndexOutOfBoundsException("Row " + row + " is out of bounds (" + getMaxRows()+")");
        if (row < 0)
            throw new IndexOutOfBoundsException("Row index can't be negative");
        if (howmany < 0)
            throw new IllegalArgumentException("The number of rows can't be negative");
        if (howmany == 0)
            return;

        deleteFields(rows, row, howmany);
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
        int index = getIndex(columns, column);
        if (index == columns.size())
            return ColumnStyle.default_style.getWidth();
        else
            return columns.get(index).column_style.getWidth();
    }

    /**
     * Get the height of a row
     * @param row The row index where start
     * @return The height of the row, null if not specified
     */

    public Double getRowHeight(int row) {
        checkRowRange(row);
        int index = getIndex(rows, row);
        if (index == rows.size())
            return RowStyle.default_style.getHeight();
        else
            return rows.get(index).row_style.getHeight();
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

    private Pair<Integer,Integer> getIndexDelete(List<? extends TableField> fields, int index)
    {
        for (int i = 0; i < fields.size(); i++) {
            TableField item = fields.get(i);
            if (index >= item.num_repeated) {
                index -= item.num_repeated;
            }
            else {
                return new Pair<>(i, index);
            }
        }
        return new Pair<>(fields.size(), index);
    }

    private int getIndex(List<? extends TableField> fields, int index)
    {
        return getIndexDelete(fields, index).first;
    }

    Cell getCell(int row,int column){
        Row item = getFieldForEditing(rows, Row.class, row);
        Cell cell = getFieldForEditing(item.cells, Cell.class, column);
        return cell;
    }

    /**
     * Hides a row specified by his index
     * @param row The index of the row
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public void hideRow(int row)
    {
        checkRowRange(row);
        Row item = getFieldForEditing(rows, Row.class, row);
        item.row_style.setHidden(true);
    }

    /**
     * Hides a row specified by a range
     * @param row The index of the row
     * @param howmany The number of different rows to hide
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public void hideRows(int row, int howmany)
    {
        toggleRows(row, howmany, true);
    }

    private void toggleRows(int row, int howmany, boolean hidden)
    {
        if (howmany < 0)
            throw new IllegalArgumentException("howmany needs to be positive");
        if (howmany == 0)
            return;

        checkRowRange(row);
        checkRowRange(row + howmany - 1);

        List<Row> list = getFieldForEditingRange(rows, Row.class, row, howmany);
        for (Row item : list)
            item.row_style.setHidden(hidden);
    }

    /**
     * Hides a column specified by his index
     * @param column The index of the row
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public void hideColumn(int column)
    {
        checkColumnRange(column);
        Column item = getFieldForEditing(columns, Column.class, column);
        item.column_style.setHidden(true);
    }

    /**
     * Hides a column specified by a range
     * @param column The index of the row
     * @param howmany The number of columns
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public void hideColumns(int column, int howmany)
    {
        toggleColumns(column, howmany, true);
    }

    private void toggleColumns(int column, int howmany, boolean hidden)
    {
        if (howmany < 0)
            throw new IllegalArgumentException("howmany needs to be positive");
        if (howmany == 0)
            return;

        checkColumnRange(column);
        checkColumnRange(column + howmany - 1);

        List<Column> list = getFieldForEditingRange(columns, Column.class, column, howmany);
        for (Column item : list)
            item.column_style.setHidden(hidden);
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
     * @throws IllegalArgumentException if howmany is negative, no changes will be done
     */
    public void insertColumnsAfter(int columnIndex, int howmany) {
        insertColumnsBefore(columnIndex+1, howmany);
    }

    /**
     * Insert a number of columns before a specific position
     * @param columnIndex The index where insert
     * @param howmany How many columns to insert
     * @throws IndexOutOfBoundsException if the columnIndex is out of bounds, no changes will be done
     * @throws IllegalArgumentException if howmany is negative, no changes will be done
     */
    public void insertColumnsBefore(int columnIndex, int howmany) {
        if (columnIndex > getMaxColumns())
            throw new IndexOutOfBoundsException("Column " + columnIndex + " is out of bounds (" + getMaxColumns()+")");
        if (howmany < 0)
            throw new IllegalArgumentException("Number of columns can't be negative");
        if (howmany == 0)
            return;

        Column column = new Column();
        column.num_repeated = howmany;
        insertField(columns, column, columnIndex);
        numColumns += howmany;

        for (Row row : rows) {
            Cell cell = new Cell();
            cell.num_repeated = howmany;
            insertField(row.cells, cell, columnIndex);
        }
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
     * @throws IndexOutOfBoundsException if the index is out bounds, no changes will be done to the sheet
     * @throws IllegalArgumentException if howmany is negative, no changes will be done to the sheet
     */
    public void insertRowsBefore(int rowIndex, int howmany) {
        if (rowIndex > getMaxRows())
            throw new IndexOutOfBoundsException("Row " + rowIndex + " is out of bounds (" + getMaxRows()+")");
        if (howmany < 0)
            throw new IllegalArgumentException("Number of rows can't be negative");
        if (howmany == 0)
            return;

        Row row = new Row();
        row.num_repeated = howmany;
        insertField(rows, row, rowIndex);
        numRows += howmany;
    }

    /**
     * Insert a row after a specific position
     * @param rowIndex The index where insert
     * @param howmany How many rows to insert
     * @throws IndexOutOfBoundsException if the index is out bounds, no changes will be done to sheet
     * @throws IllegalArgumentException if howmany is negative, no changes will be done to the sheet
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
        Column item = getFieldForEditing(columns, Column.class, column);
        item.column_style.setWidth(width);
    }

    /**
     * Set a column width to a specific set of columns
     * @param column The index of the column
     * @param numColumns The number of columns to be modified, starting on index.
     * @param width The width of the column. It can be a null if you want to "unset" the width
     * @throws IndexOutOfBoundsException if the column is negative or &gt;= numColumns
     * @throws IllegalArgumentException Width and numColumns has to be positive
     */

    public void setColumnWidths(int column, int numColumns, Double width)
    {
        if (numColumns < 0)
            throw new IllegalArgumentException("Numcolumns needs to be positive");
        if (numColumns == 0)
            return;
        checkColumnRange(column);
        checkColumnRange(column + numColumns - 1);

        List<Column> list = getFieldForEditingRange(columns, Column.class, column, numColumns);
        for (Column item : list)
            item.column_style.setWidth(width);
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
        Row item = getFieldForEditing(rows, Row.class, row);
        item.row_style.setHeight(height);
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
        if (numRows < 0)
            throw new IllegalArgumentException("numRows needs to be positive");
        if (numRows == 0)
            return;
        checkRowRange(row);
        checkRowRange(row + numRows - 1);
        List<Row> list = getFieldForEditingRange(rows, Row.class, row, numRows);
        for (Row item : list)
            item.row_style.setHeight(height);
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
        Row item = getFieldForEditing(rows, Row.class, row);
        item.row_style.setHidden(false);
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
        Column item = getFieldForEditing(columns, Column.class, column);
        item.column_style.setHidden(false);
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
        int index = getIndex(rows, row);
        if (index == rows.size())
            return RowStyle.default_style.isHidden();
        else {
            return rows.get(index).row_style.isHidden();
        }
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
        int index = getIndex(columns, column);
        if (index == columns.size())
            return ColumnStyle.default_style.isHidden();
        else {
            return columns.get(index).column_style.isHidden();
        }
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
     * Gets the default cell style of a specific column.
     * The default style is used as a fallback whenever a cell in the column doesn't specify its own style.
     * It's safe to manipulate the returned style object since it is a copy of the original one.
     *
     * @param column The index of the column
     * @throws IndexOutOfBoundsException if the column index is invalid
     * @return The default cell style of the column. It cannot be null.
     */
    public Style getDefaultColumnCellStyle(int column) {
        checkColumnRange(column);
        int index = getIndex(columns, column);
        if (index == columns.size())
            return ColumnStyle.default_style.getDefaultCellStyleCopy();
        else {
            return columns.get(index).column_style.getDefaultCellStyleCopy();
        }
    }

    /**
     * Sets the default cell style of a specific column.
     * The default style is used as a fallback whenever a cell in the column doesn't specify its own style.
     *
     * @param column The index of the column
     * @param defaultColumnCellStyle The default cell style of the column. A clone of this object will be stored.
     * @throws IndexOutOfBoundsException if the column is negative or &gt;= numColumns
     * @throws IllegalArgumentException if the style is null
     */
    public void setDefaultColumnCellStyle(int column, Style defaultColumnCellStyle) {
        checkColumnRange(column);
        Column item = getFieldForEditing(columns, Column.class, column);
        item.column_style.setDefaultCellStyle(defaultColumnCellStyle);
    }

    /**
     * @deprecated Use getMaxRow() instead
     * Return the last row of the sheet which contains useful data
     * @return The index of the last row
     */
    public int getLastRow()
    {
        return getMaxRows();
    }

    /**
     * @deprecated Use getMaxColumn() instead
     * Return the last column of the sheet which contains useful data
     * @return The index of the last column
     */
    public int getLastColumn()
    {
        return getMaxColumns();
    }

    /**
     * Trim the sheet removing unused rows/columns
     * @deprecated This function should not be called since it won't make any visible difference to the user. This is going to be moved to private visibility
     *
    */
    public void trim()
    {
        trim(columns);
        for (Row row : rows)
            trim(row.cells);
        trim(rows);
    }

    private <T extends TableField> void trim(List<T> fields)
    {
        T last_item = null;
        for (int i = 0; i < fields.size(); i++) {
            T item = fields.get(i);
            if (last_item != null) {
                if (item.equals(last_item)) {
                    last_item.num_repeated += item.num_repeated;
                    fields.remove(i);
                    i--;
                }
                else
                    last_item = item;
            }
            else
                last_item = item;
        }
    }

    /**
     * Determines if this sheet is protected by a password or not
     * @return True if it's protected
     */
    public boolean isProtected()
    {
        return this.hashed_password != null;
    }

    /**
     * Sets a password for a sheet. SODS will ignore this setting but other consumers will ask for a password to the user
     * in order to access this sheet
     * @param key the password. Sets the password to null to disable the password protection
     * @throws IllegalArgumentException if the password parameter is empty
     * @throws NoSuchAlgorithmException if your java installation doesn't have SHA-256 hash encryption
     */
    public void setPassword(String key) throws NoSuchAlgorithmException {
        if (key == null) {
            this.hashed_password = null;
            this.hash_algorithm = null;
            return;
        }
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
        if (!name.equals(sheet.name)) return false;
        if (numColumns != sheet.numColumns) return false;
        if (numRows != sheet.numRows) return false;
        if (isHidden != sheet.isHidden) return false;

        trim();
        sheet.trim();

        if (!rows.equals(sheet.rows)) return false;
        return columns.equals(sheet.columns);
    }

    @Override
    public int hashCode() {
        int result = rows.hashCode();
        result = 31 * result + columns.hashCode();
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
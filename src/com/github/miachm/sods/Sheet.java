package com.github.miachm.sods;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sheet in a Spreadsheet.
 *
 * You can create empty sheets and add to an existing Spreadsheet
 */
public class Sheet implements Cloneable,Comparable<Sheet> {
    private List<List<Cell>> cells = new ArrayList<List<Cell>>();
    private String name;
    private int numColumns = 1;

    /**
     * Create an empty sheet with a given name.
     * The sheet will have a 1x1 GRID by default.
     *
     * @param name A name which identifies this sheet
     */
    public Sheet(String name)
    {
        this.name = name;
        cells.add(new ArrayList<>());

        Cell cell = new Cell();
        cell.setValue("");
        cells.get(0).add(cell);
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

        for (List<Cell> row : cells){
           for (int i = 0;i < howmany;i++)
           {
               row.remove(column);
           }
        }
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

        for (int i = 0;i < howmany;i++)
            cells.remove(row);
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
        return cells.isEmpty() ? numColumns : cells.get(0).size();
    }

    /**
     * The number of rows created in this sheet
     *
     * @return An integer which represents the number of rows in this sheet
     */
    public int getMaxRows() {
        return cells.size();
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
        return new Range(this,row,column,numRows,numColumns);
    }

    Cell getCell(int row,int column){
        return cells.get(row).get(column);
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

        for (List<Cell> row : cells){
            for (int i = 0;i < howmany;i++){
                row.add(columnIndex,new Cell());
            }
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

        for (int i = 0;i < howmany;i++){
            ArrayList<Cell> row = new ArrayList<>();
            for (int j = 0;j < numColumns;j++)
                row.add(new Cell());
            cells.add(rowIndex,row);
        }
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
     * Equals method, two sheets are considered the same if have the same name and the same content
     * @param o The object to compare
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sheet sheet = (Sheet) o;

        if (!cells.equals(sheet.cells)) return false;
        return name.equals(sheet.name);
    }

    @Override
    public int hashCode() {
        int result = cells.hashCode();
        result = 31 * result + name.hashCode();
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
                " name='" + name + '\'' + " ," +
                "cells=" + getDataRange().toString() +
                '}';
    }
}
package com.github.miachm.SODS.spreadsheet;

import java.util.ArrayList;
import java.util.List;

public class Sheet implements Cloneable,Comparable<Sheet> {
    private List<List<Cell>> cells = new ArrayList<List<Cell>>();
    private String name;

    public Sheet(String name) {
        this.name = name;
        cells.add(new ArrayList<>());
        cells.get(0).add(new Cell());
    }

    public void appendRow(){
        appendRows(1);
    }

    public void appendRows(int howmany){
        insertRowsAfter(getMaxRows()-1,howmany);
    }

    public void appendColumn(){
        appendColumns(1);
    }

    public void appendColumns(int howmany){
        insertColumnsAfter(getMaxColumns()-1,howmany);
    }

    public void clear() {
        getDataRange().clear();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void deleteColumn(int column) {
        deleteColumns(column,1);
    }

    public void deleteColumns(int column, int howmany) {
        for (List<Cell> row : cells){
           for (int i = 0;i < howmany;i++)
           {
               row.remove(column);
           }
        }
    }

    public void deleteRow(int row) {
        deleteRows(row,1);
    }

    public void deleteRows(int row, int howmany) {
        for (int i = 0;i < howmany;i++)
            cells.remove(row);
    }

    public Range getDataRange() {
        return getRange(0,0,getMaxRows(),getMaxColumns());
    }

    public int getMaxColumns() {
        return cells.isEmpty() ? 0 : cells.get(0).size();
    }

    public int getMaxRows() {
        return cells.size();
    }

    public String getName() {
        return name;
    }

    public Range getRange(int row, int column) {
        return getRange(row, column, 1, 1);
    }

    public Range getRange(int row, int column, int numRows) {
        return getRange(row, column, numRows, 1);
    }

    public Range getRange(int row, int column, int numRows, int numColumns) {
        return new Range(this,row,column,numRows,numColumns);
    }

    Cell getCell(int row,int column){
        return cells.get(row).get(column);
    }

    public void insertColumnAfter(int afterPosition) {
        insertColumnsAfter(afterPosition, 1);
    }

    public void insertColumnBefore(int beforePosition) {
        insertColumnsBefore(beforePosition, 1);
    }

    public void insertColumnsAfter(int columnIndex, int howmany) {
        insertColumnsBefore(columnIndex+1,howmany);
    }

    public void insertColumnsBefore(int columnIndex, int howmany) {
        for (List<Cell> row : cells){
            for (int i = 0;i < howmany;i++){
                row.add(columnIndex,new Cell());
            }
        }
    }
    public void insertRowAfter(int afterPosition) {
        insertRowsAfter(afterPosition, 1);
    }

    public void insertRowBefore(int beforePosition) {
        insertRowsBefore(beforePosition, 1);
    }

    public void insertRowsBefore(int rowIndex, int howmany) {
        for (int i = 0;i < howmany;i++){
            ArrayList<Cell> row = new ArrayList<>();
            for (int j = 0;j < getMaxColumns();j++)
                row.add(new Cell());
            cells.add(rowIndex,row);
        }
    }

    public void insertRowsAfter(int rowIndex, int howmany) {
        insertRowsBefore(rowIndex+1,howmany);
    }

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

    @Override
    public int compareTo(Sheet o) {
        return name.compareTo(o.getName());
    }
}
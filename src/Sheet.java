package com.github.miachm.SODS;

import java.util.ArrayList;
import java.util.List;

public class Sheet implements Cloneable {
    private List<List<Cell>> cells = new ArrayList<List<Cell>>();
    private String name;

    public Sheet(String name) {
        this.name = name;
    }

    public void clear() {
        getDataRange().clear();
    }

    public void clearContents() {
        getDataRange().clearContent();
    }

    public void clearFormats() {
        getDataRange().clearFormat();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void deleteColumn(int column) {
        deleteColumn(column,1);
    }

    public void deleteColumn(int column, int howmany) {
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
        // TODO

        return null;
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
            cells.add(rowIndex,new ArrayList<Cell>(getMaxColumns()));
        }
    }

    public void insertRowsAfter(int rowIndex, int howmany) {
        insertRowsBefore(rowIndex+1,howmany);
    }


}
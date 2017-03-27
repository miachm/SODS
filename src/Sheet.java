package com.github.miachm.SODS;

import java.util.ArrayList;
import java.util.List;

public class Sheet implements Cloneable {
    private List<Cell> cells = new ArrayList<Cell>();
    private String name;

    public Sheet(String name) {
        this.name = name;
    }

    public void clear() {
        // TODO
    }

    public void clearContents() {
        // TODO
    }

    public void clearFormats() {
        // TODO
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void deleteColumn(int column) {
        // TODO
    }

    public void deleteColumn(int column, int howmany) {
        // TODO
    }

    public void deleteRow(int column) {
        // TODO
    }

    public void deleteRows(int column, int howmany) {
        // TODO
    }

    public Range getDataRange() {
        // TODO
        return null;
    }

    public int getMaxColumns() {
        // TODO
        return 0;
    }

    public int getMaxRows() {
        // TODO
        return 0;
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
        // TODO
    }

    public void insertColumnsBefore(int columnIndex, int howmany) {
        // TODO
    }
    public void insertRowAfter(int afterPosition) {
        insertRowsAfter(afterPosition, 1);
    }

    public void insertRowBefore(int beforePosition) {
        insertRowsBefore(beforePosition, 1);
    }

    public void insertRowsAfter(int rowIndex, int howmany) {
        // TODO
    }

    public void insertRowsBefore(int rowIndex, int howmany) {
        // TODO
    }


}
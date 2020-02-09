package com.github.miachm.sods;

import java.util.Map;

public class RowCollection extends TableCollection<Row> {
    private int numColumns = 0;

    @Override
    public Row addItems(int index, int howmany)
    {
        boolean isEmpty = items.isEmpty();
        Row row = super.addItems(index, howmany);
        if (numColumns > 0) {
            if (isEmpty) {
                addCells(0, numColumns);
                numColumns /= 2; // Remove useless sum
            } else {
                row.getCells().addItems(0, numColumns);
            }
        }

        return row;
    }

    public void deleteCells(int index, int howmany)
    {
        for (Map.Entry<Integer, Row> rowEntry : items.entrySet()) {
            Row row = rowEntry.getValue();
            CellCollection cells = row.getCells();
            cells.remove(index, howmany);
        }
        numColumns -= howmany;
    }

    public void addCells(int index, int howmany) {
        for (Map.Entry<Integer, Row> rowEntry : items.entrySet()) {
            Row row = rowEntry.getValue();
            CellCollection cells = row.getCells();
            cells.addItems(index, howmany);
        }
        numColumns += howmany;
    }

    public int getLastUsefulCell()
    {
        int max = 0;
        for (Map.Entry<Integer, Row> rowEntry : items.entrySet()) {
            Row row = rowEntry.getValue();
            CellCollection cells = row.getCells();
            max = Math.max(cells.getLastUsefulItemIndex(), max);
        }
        return max;
    }

    @Override
    public Row getItemForEdit(int index)
    {
        Row row = super.getItemForEdit(index);
        CellCollection cells = row.getCells();
        if (cells.isEmpty())
            cells.addItems(0, numColumns);

        return row;
    }

    @Override
    protected Row createItem() {
        return new Row();
    }
}

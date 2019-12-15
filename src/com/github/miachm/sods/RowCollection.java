package com.github.miachm.sods;

import java.util.Map;

public class RowCollection extends TableCollection<Row> {
    private int numColumns = 0;

    @Override
    public void addItems(int index, int howmany)
    {
        boolean isEmpty = items.isEmpty();
        super.addItems(index, howmany);
        if (isEmpty) {
            addCells(0, numColumns);
            numColumns /= 2; // Remove useless sum
        }
    }

    public void deleteCells(int index, int howmany)
    {
        for (Map.Entry<Integer, Row> rowEntry : items.entrySet()) {
            Row row = rowEntry.getValue();
            CellCollection cells = row.getCells();
            cells.remove(index, howmany);
        }
    }

    public void addCells(int index, int howmany) {
        for (Map.Entry<Integer, Row> rowEntry : items.entrySet()) {
            Row row = rowEntry.getValue();
            CellCollection cells = row.getCells();
            cells.addItems(index, howmany);
        }
        numColumns += howmany;
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

package com.github.miachm.sods;

class ColumnCollection extends TableCollection<Column> {
    private RowCollection rows;
    ColumnCollection(RowCollection rows)
    {
        this.rows = rows;
    }

    @Override
    public void remove(int index, int howmany)
    {
        super.remove(index, howmany);
        rows.deleteCells(index, howmany);
    }

    @Override
    public Column addItems(int index, int howmany)
    {
        Column column = super.addItems(index, howmany);
        rows.addCells(index, howmany);
        return column;
    }

    @Override
    protected Column createItem() {
        return new Column();
    }

    @Override
    public void trim()
    {
        int original = super.getNumItems();
        int numColumns = rows.getLastUsefulCell();
        super.trim(numColumns);
        int index = getNumItems();
        int howmany = original - index;
        if (howmany > 0)
            rows.deleteCells(index, howmany);
    }

    @Override
    public int getLastUsefulItemIndex() {
        return rows.getLastUsefulCell();
    }
}

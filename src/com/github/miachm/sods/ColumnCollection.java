package com.github.miachm.sods;

class ColumnCollection extends TableCollection<Column> {
    private RowCollection rows;
    public ColumnCollection(RowCollection rows)
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
    public void addItems(int index, int howmany)
    {
        super.addItems(index, howmany);
        rows.addCells(index, howmany);
    }

    @Override
    protected Column createItem() {
        return new Column();
    }
}

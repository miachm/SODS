package com.github.miachm.sods;

class CellCollection extends TableCollection<Cell> {

    public boolean isBlank()
    {
        for (Cell cell : items.values()) {
            if (!cell.isBlank())
                return false;
        }

        return true;
    }

    @Override
    protected Cell createItem() {
        return new Cell();
    }
}

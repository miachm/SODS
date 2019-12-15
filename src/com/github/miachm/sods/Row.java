package com.github.miachm.sods;

class Row extends TableItem {
    private RowStyle style;
    private CellCollection cells = new CellCollection();

    public RowStyle getStyle() {
        return style;
    }

    public void setStyle(RowStyle style) {
        this.style = style;
    }

    public CellCollection getCells() {
        return cells;
    }

    @Override
    public Row clone() throws CloneNotSupportedException {
        return (Row) super.clone();
    }

    @Override
    public boolean isBlank() {
        return style.isDefault() && cells.isEmpty();
    }
}

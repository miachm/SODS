package com.github.miachm.sods;

class Row extends TableItem {
    private RowStyle style = new RowStyle();
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
        return style.isDefault() && cells.isBlank();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Row row = (Row) o;

        if (style != null ? !style.equals(row.style) : row.style != null) return false;
        return cells != null ? cells.equals(row.cells) : row.cells == null;
    }

    @Override
    public int hashCode() {
        int result = style != null ? style.hashCode() : 0;
        result = 31 * result + (cells != null ? cells.hashCode() : 0);
        return result;
    }
}

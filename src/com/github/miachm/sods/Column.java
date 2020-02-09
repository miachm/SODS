package com.github.miachm.sods;

class Column extends TableItem {
    private ColumnStyle style = new ColumnStyle();

    @Override
    public Column clone() throws CloneNotSupportedException {
        return (Column) super.clone();
    }

    @Override
    public boolean isBlank() {
        return style.isDefault();
    }

    public ColumnStyle getStyle() {
        return style;
    }

    public void setStyle(ColumnStyle style) {
        this.style = style;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Column column = (Column) o;

        return style != null ? style.equals(column.style) : column.style == null;
    }

    @Override
    public int hashCode() {
        return style != null ? style.hashCode() : 0;
    }
}

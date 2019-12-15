package com.github.miachm.sods;

class Column extends TableItem {
    private ColumnStyle style;

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
}

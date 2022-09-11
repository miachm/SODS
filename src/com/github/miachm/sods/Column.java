package com.github.miachm.sods;

import java.util.Objects;

class Column extends TableField {
    ColumnStyle column_style = new ColumnStyle();

    @Override
    public Object clone()
    {
        Column column = (Column) super.clone();
        column.column_style = (ColumnStyle) column_style.clone();
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Column column = (Column) o;
        return column_style.equals(column.column_style);
    }

    @Override
    public int hashCode() {
        return Objects.hash(column_style);
    }
}

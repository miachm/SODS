package com.github.miachm.sods;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class Row extends TableField {
    RowStyle row_style = new RowStyle();
    List<Cell> cells = new ArrayList<>();

    @Override
    public Object clone()
    {
        Row row = (Row) super.clone();
        row.row_style = (RowStyle) row_style.clone();
        // Force list clone
        row.cells = new ArrayList<>(cells.size());
        for (Cell cell : cells) row.cells.add((Cell)cell.clone());
        return row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Row row = (Row) o;
        return row_style.equals(row.row_style) && cells.equals(row.cells);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row_style, cells);
    }
}

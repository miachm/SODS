package com.github.miachm.sods;

/**
 * Abstract out a cell in a spreadsheet.
 * 
 */
public class LogicalCell {
    private final CellIterator iterator;
    private final int row;
    private final int column;

    LogicalCell(CellIterator iterator, int row, int column) {
        if (iterator == null) {
            throw new NullPointerException("Iterator can not be null");
        }
        this.iterator = iterator;
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Object getValue() {
        Cell cell = iterator.readOnlyCellAt(row, column);
        return cell == null ? null : cell.getValue();
    }

    public void setValue(Object value) {
        iterator.writableCellAtCurrent(row, column).setValue(value);
    }

    public String getFormula() {
        Cell cell = iterator.readOnlyCellAt(row, column);
        return cell == null ? null : cell.getFormula();
    }

    public void setFormula(String formula) {
        iterator.writableCellAtCurrent(row, column).setFormula(formula);
        return;
    }

    /**
     * Returns the mutable style of this cell.
     * Changes are applied directly to the cell.
     */
    public Style getStyle() {
        try {
            return (Style) iterator.readOnlyCellAt(row, column).getStyle().clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Style is not cloneable");
        }
    }

    public void setStyle(Style style) {
        iterator.writableCellAtCurrent(row, column).setStyle(style);
    }

    public OfficeAnnotation getAnnotation() {
        Cell cell = iterator.readOnlyCellAt(row, column);
        return cell == null ? null : cell.getAnnotation();
    }

    public void setAnnotation(OfficeAnnotation annotation) {
        iterator.writableCellAtCurrent(row, column).setAnnotation(annotation);
    }

    public void clear() {
        iterator.writableCellAtCurrent(row, column).clear();
    }
}

package com.github.miachm.SODS.spreadsheet;

class Cell {
    private Object value;
    private String formula;

    public void clear(){
        value = null;
    }

    public String getFormula() {
        return formula;
    }

    public Object getValue(){
        return value;
    }

    public void setValue(Object value){
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cell cell = (Cell) o;

        if (value != null ? !value.equals(cell.value) : cell.value != null) return false;
        return formula != null ? formula.equals(cell.formula) : cell.formula == null;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (formula != null ? formula.hashCode() : 0);
        return result;
    }
}
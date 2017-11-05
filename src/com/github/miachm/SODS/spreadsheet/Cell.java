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

        return value != null ? value.equals(cell.value) : cell.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "value=" + value +
                '}';
    }
}
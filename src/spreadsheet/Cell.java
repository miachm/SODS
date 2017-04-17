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
}
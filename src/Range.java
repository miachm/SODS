package com.github.miachm.SODS;

import java.util.List;

public class Range {

    private List<List<Cell>> cells;

    Range(Sheet sheet,List<List<Cell>> cells){
        this.cells = cells;
    }

    public void clear(){
        for (List<Cell> list : cells)
            list.forEach((cell) -> cell.clear());
    }

    public void copyTo(Range dest){
        dest.setValues(dest.getValues());
    }

    public Range getCell(int row,int column){
        // TODO
        return null;
    }

    public int getColumn(){
        // TODO
        return 0;
    }

    public String getFormula(){
        // TODO
        return null;
    }

    public List<String> getFormulas(){
        // TODO
        return null;
    }

    public int getLastColumn(){
        // TODO
        return 0;
    }

    public int getLastRow(){
        // TODO
        return 0;
    }

    public int getNumColumns(){
        // TODO
        return 0;
    }

    public int getNumRows(){
        // TODO
        return 0;
    }

    public int getRow(){
        // TODO
        return 0;
    }

    public Sheet getSheet(){
        // TODO
        return null;
    }

    public Object getValue(){
        // TODO
        return null;
    }

    public List<Object> getValues(){
        // TODO
        return null;
    }

    public void setValue(Object o){
        // TODO
    }

    public void setValues(List<Object> o){
        // TODO
    }

    public void sort(){
        // TODO
    }
}

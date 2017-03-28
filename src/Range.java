package com.github.miachm.SODS;

import java.util.ArrayList;
import java.util.List;

public class Range {

    private List<List<Cell>> cells;
    private int column_init,row_init;
    private Sheet sheet;

    Range(List<List<Cell>> cells,Sheet sheet,int row_init,int column_init){
        this.cells = cells;
        this.sheet = sheet;
        this.row_init = row_init;
        this.column_init = column_init;
    }

    public void clear(){
        for (List<Cell> list : cells)
            list.forEach((cell) -> cell.clear());
    }

    public void copyTo(Range dest){
        dest.setValues(dest.getValues());
    }

    public Range getCell(int row,int column){
        List<List<Cell>> list = new ArrayList<>();
        list.add(new ArrayList<>());
        list.get(0).add(cells.get(row).get(column));
        return new Range(list,sheet,row_init+row,column_init+column);
    }

    public int getColumn(){
        return column_init;
    }

    public String getFormula(){
        return cells.get(0).get(0).getFormula();
    }

    public List<String> getFormulas(){
        ArrayList<String> formulas = new ArrayList<>();
        formulas.ensureCapacity(getNumRows()*getNumColumns());

        for (List<Cell> row : cells){
            for (Cell cell : row){
                formulas.add(cell.getFormula());
            }
        }

        return formulas;
    }

    public int getLastColumn(){
        return column_init + getNumColumns();
    }

    public int getLastRow(){
        return row_init + getNumRows();
    }

    public int getNumColumns(){
        return cells.get(0).size();
    }

    public int getNumRows(){
        return cells.size();
    }

    public int getRow(){
        return row_init;
    }

    public Sheet getSheet(){
        return sheet;
    }

    public Object getValue(){
        return cells.get(0).get(0).getValue();
    }

    public List<Object> getValues(){
        ArrayList<Object> values = new ArrayList<>();
        values.ensureCapacity(getNumRows()*getNumColumns());

        for (List<Cell> row : cells){
            for (Cell cell : row){
                values.add(cell.getValue());
            }
        }

        return values;
    }

    public void setValue(Object o){
        for (List<Cell> row : cells){
            for (Cell cell : row){
                cell.setValue(o);
            }
        }
    }

    public void setValues(List<Object> o){
        int index = 0;
        for (List<Cell> row : cells){
            for (Cell cell : row){
                if (index >= o.size())
                    return;
                cell.setValue(index);
            }
        }
    }
}

package com.github.miachm.SODS;

import java.util.ArrayList;
import java.util.List;

interface RangeIterator{
    public void call(Cell cell);
}

public class Range {
    private final int column_init,row_init;
    private final int numrows,numcolumns;
    private final Sheet sheet;

    Range(Sheet sheet,int row_init,int column_init,int numrows,int numcolumns){
        this.sheet = sheet;
        this.row_init = row_init;
        this.column_init = column_init;
        this.numrows = numrows;
        this.numcolumns = numcolumns;
    }

    public void clear(){
        iterateRange((cell) -> cell.clear());
    }

    public void copyTo(Range dest){
        dest.setValues(dest.getValues());
    }

    public Range getCell(int row,int column){
        return new Range(sheet,row_init+row,column_init+column,1,1);
    }

    public int getColumn(){
        return column_init;
    }

    public String getFormula(){
        return sheet.getCell(row_init,column_init).getFormula();
    }

    public List<String> getFormulas(){
        ArrayList<String> formulas = new ArrayList<String>();
        formulas.ensureCapacity(getNumRows()*getNumColumns());

        iterateRange((cell) -> formulas.add(cell.getFormula()));

        return formulas;
    }

    public int getLastColumn(){
        return column_init + getNumColumns();
    }

    public int getLastRow(){
        return row_init + getNumRows();
    }

    public int getNumColumns(){
        return numcolumns;
    }

    public int getNumRows(){
        return numrows;
    }

    public int getRow(){
        return row_init;
    }

    public Sheet getSheet(){
        return sheet;
    }

    public Object getValue(){
        return sheet.getCell(row_init,column_init).getValue();
    }

    public List<Object> getValues(){
        ArrayList<Object> values = new ArrayList<Object>();
        values.ensureCapacity(getNumRows()*getNumColumns());
        iterateRange((cell) -> values.add(cell.getValue()));
        return values;
    }

    public void setValue(Object o){
        iterateRange((cell) -> cell.setValue(o));
    }

    public void setValues(List<Object> o){
        iterateRange(new RangeIterator() {
            int index = 0;
            @Override
            public void call(Cell cell) {
                if (index < o.size()) {
                    cell.setValue(index);
                    index++;
                }
            }
        });
    }

    private void iterateRange(RangeIterator e){
        for (int i = 0;i < numrows;i++){
            for (int j = 0;j < numcolumns;j++){
                Cell cell = sheet.getCell(row_init+i,column_init+j);
                e.call(cell);
            }
        }
    }
}

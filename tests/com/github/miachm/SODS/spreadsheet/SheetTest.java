package com.github.miachm.SODS.spreadsheet;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.testng.AssertJUnit.assertEquals;

public class SheetTest {
    private Random random = new Random();

    private Sheet generateASheet(){
        Sheet sheet = new Sheet("A");
        sheet.insertRowsAfter(0,99);
        sheet.insertColumnsAfter(0,99);

        Range range = sheet.getDataRange();

        List<Integer> integers = new ArrayList<>();

        for (int i = 0;i < range.getNumValues();i++){
            integers.add(random.nextInt());
        }

        range.setValues(integers);
        return sheet;
    }

    private Sheet generateDeterministicSheet(){
        Sheet sheet = new Sheet("A");
        sheet.insertRowAfter(0);
        sheet.insertColumnAfter(0);

        sheet.getRange(0,0).setValue(1);
        sheet.getRange(1,0).setValue(2);
        sheet.getRange(0,1).setValue(3);
        sheet.getRange(1,1).setValue(4);
        return sheet;
    }

    @Test
    public void testClear() throws Exception {
        Sheet sheet = generateASheet();
        int rows = sheet.getMaxRows();
        int columns = sheet.getMaxColumns();
        sheet.clear();
        assertEquals(sheet.getMaxRows(),rows);
        assertEquals(sheet.getMaxColumns(),columns);

        Range range = sheet.getDataRange();

        for (Object o : range.getValues()){
            if (o != null)
                assertEquals(true,false);
        }
    }

    @Test
    public void testClone() throws Exception {
        Sheet sheet = generateASheet();
        Sheet other = (Sheet) sheet.clone();

        assertEquals(sheet,other);
    }

    @Test
    public void testEquals() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        Sheet other = generateDeterministicSheet();
        assertEquals(sheet,other);
        sheet.getRange(0,0).setValue(-1);

        boolean equals = sheet.equals(other);
        assertEquals(equals,false);
    }

    @Test
    public void testDeleteColumn() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.insertColumnAfter(0);
        sheet.getDataRange().setValues(1,2);

        sheet.deleteColumn(0);

        assertEquals(sheet.getMaxColumns(),1);
        assertEquals(sheet.getRange(0,0).getValue(),2);

        sheet.insertColumnAfter(0);
        sheet.insertColumnAfter(0);
        sheet.getDataRange().setValues(1,2,3);

        sheet.deleteColumn(2);

        assertEquals(sheet.getMaxColumns(),2);
        assertEquals(sheet.getRange(0,0).getValue(),1);
    }

    @Test
    public void testDeleteColumns() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.insertColumnAfter(0);
        sheet.insertColumnAfter(0);
        sheet.getDataRange().setValues(1,2,3);

        sheet.deleteColumns(1,2);

        assertEquals(sheet.getMaxColumns(),1);
        assertEquals(sheet.getRange(0,0).getValue(),1);
    }

    @Test
    public void testDeleteRow() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.insertRowAfter(0);
        sheet.getDataRange().setValues(1,2);

        sheet.deleteRow(0);

        assertEquals(sheet.getMaxRows(),1);
        assertEquals(sheet.getRange(0,0).getValue(),2);

        sheet.insertRowAfter(0);
        sheet.insertRowAfter(0);
        sheet.getDataRange().setValues(1,2,3);

        sheet.deleteRow(2);

        assertEquals(sheet.getMaxRows(),2);
        assertEquals(sheet.getRange(0,0).getValue(),1);
    }

    @Test
    public void testDeleteRows() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.insertRowAfter(0);
        sheet.insertRowAfter(0);
        sheet.getDataRange().setValues(1,2,3);

        sheet.deleteRows(1,2);

        assertEquals(sheet.getMaxRows(),1);
        assertEquals(sheet.getRange(0,0).getValue(),1);
    }

    @Test
    public void testGetDataRange() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        Range range = sheet.getDataRange();
        List<Integer> solution = new ArrayList<>();
        solution.add(1);
        solution.add(3);
        solution.add(2);
        solution.add(4);
        assertEquals(range.getValues(),solution);
    }

    @Test
    public void testGetMaxColumns() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        assertEquals(sheet.getMaxColumns(),2);
    }

    @Test
    public void testGetMaxRows() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        assertEquals(sheet.getMaxRows(),2);
    }

    @Test
    public void testGetName() throws Exception {
        Sheet sheet = new Sheet("D");
        assertEquals(sheet.getName(),"D");
    }

    @Test
    public void testGetRangeCell() throws Exception {
        Sheet sheet = generateASheet();
        List<Object> values = sheet.getDataRange().getValues();
        for (int i = 0;i < values.size();i++){
            int row = i / sheet.getMaxColumns();
            int columns = i % sheet.getMaxColumns();
            assertEquals(values.get(i),sheet.getRange(row,columns).getValue());
        }
    }

    @Test
    public void testGetRange1() throws Exception {

    }

    @Test
    public void testGetRange2() throws Exception {

    }

    @Test
    public void testInsertColumnAfter() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        sheet.insertColumnAfter(0);
        assertEquals(sheet.getMaxColumns(),3);
        sheet.insertColumnAfter(1);
        assertEquals(sheet.getMaxColumns(),4);
        List<Object> list = sheet.getDataRange().getValues();
        assertEquals(list.get(0),1);
        assertEquals(list.get(1),null);
        assertEquals(list.get(2),null);
        assertEquals(list.get(3),3);
        assertEquals(list.get(4),2);
        assertEquals(list.get(5),null);
        assertEquals(list.get(6),null);
        assertEquals(list.get(7),4);
    }

    @Test
    public void testInsertColumnBefore() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        sheet.insertColumnBefore(1);
        assertEquals(sheet.getMaxColumns(),3);
        sheet.insertColumnBefore(0);
        assertEquals(sheet.getMaxColumns(),4);
        List<Object> list = sheet.getDataRange().getValues();
        assertEquals(list.get(0),null);
        assertEquals(list.get(1),1);
        assertEquals(list.get(2),null);
        assertEquals(list.get(3),3);
        assertEquals(list.get(4),null);
        assertEquals(list.get(5),2);
        assertEquals(list.get(6),null);
        assertEquals(list.get(7),4);
    }

    @Test
    public void testInsertColumnsAfter() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        sheet.insertColumnsAfter(1,3);
        sheet.insertColumnsAfter(0,2);
        assertEquals(sheet.getMaxColumns(),7);

        List<Object> list = sheet.getDataRange().getValues();
        assertEquals(list.get(0),1);
        assertEquals(list.get(1),null);
        assertEquals(list.get(2),null);
        assertEquals(list.get(3),3);
        assertEquals(list.get(4),null);
        assertEquals(list.get(5),null);
        assertEquals(list.get(6),null);
        assertEquals(list.get(7),2);
        assertEquals(list.get(8),null);
        assertEquals(list.get(9),null);
        assertEquals(list.get(10),4);
        assertEquals(list.get(11),null);
        assertEquals(list.get(12),null);
        assertEquals(list.get(13),null);
    }

    @Test
    public void testInsertColumnsBefore() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        sheet.insertColumnsAfter(1,3);
        sheet.insertColumnsAfter(0,2);
        assertEquals(sheet.getMaxColumns(),7);

        List<Object> list = sheet.getDataRange().getValues();
        assertEquals(list.get(0),1);
        assertEquals(list.get(1),null);
        assertEquals(list.get(2),null);
        assertEquals(list.get(3),3);
        assertEquals(list.get(4),null);
        assertEquals(list.get(5),null);
        assertEquals(list.get(6),null);
        assertEquals(list.get(7),2);
        assertEquals(list.get(8),null);
        assertEquals(list.get(9),null);
        assertEquals(list.get(10),4);
        assertEquals(list.get(11),null);
        assertEquals(list.get(12),null);
        assertEquals(list.get(13),null);
    }

    @Test
    public void testInsertRowAfter() throws Exception {

    }

    @Test
    public void testInsertRowBefore() throws Exception {

    }

    @Test
    public void testInsertRowsBefore() throws Exception {

    }

    @Test
    public void testInsertRowsAfter() throws Exception {

    }

    @Test
    public void testCompareTo() throws Exception {

    }

}
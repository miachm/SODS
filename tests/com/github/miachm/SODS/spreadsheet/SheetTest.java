package com.github.miachm.SODS.spreadsheet;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.internal.junit.ArrayAsserts.assertArrayEquals;

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

        range.setValues(integers.toArray());
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
            assertNotNull(o);
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
        Object[][] solution = new Object[2][2];
        solution[0][0] = 1;
        solution[0][1] = 3;
        solution[1][0] = 2;
        solution[1][1] = 4;
        assertArrayEquals(range.getValues(),solution);
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
        Object[][] values = sheet.getDataRange().getValues();
        for (int i = 0;i < values.length;i++){
            for (int j = 0;j < values[i].length;j++) {
                assertEquals(values[i][j], sheet.getRange(i, j).getValue());
            }
        }
    }

    @Test
    public void testGetRangeRows() throws Exception {
        Sheet sheet = generateASheet();
        Object[][] values = sheet.getDataRange().getValues();
        for (int i = 0;i < values.length-1;i++){
            for (int j = 0;j < values[i].length-1;j++) {
                Range range = sheet.getRange(i, j,2);
                Object[][] v = range.getValues();
                assertEquals(values[i][j],v[0][0]);
                assertEquals(values[i+1][j],v[1][0]);
            }
        }
    }

    @Test
    public void testGetRangeTable() throws Exception {
        Sheet sheet = generateASheet();
        Object[][] values = sheet.getDataRange().getValues();
        for (int i = 0;i < values.length-1;i++){
            for (int j = 0;j < values[i].length-1;j++) {
                Range range = sheet.getRange(i, j,2,2);
                Object[][] v = range.getValues();
                assertEquals(values[i][j],v[0][0]);
                assertEquals(values[i+1][j],v[1][0]);
                assertEquals(values[i][j+1],v[0][1]);
                assertEquals(values[i+1][j+1],v[1][1]);
            }
        }
    }

    @Test
    public void testInsertColumnAfter() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        sheet.insertColumnAfter(0);
        assertEquals(sheet.getMaxColumns(),3);
        sheet.insertColumnAfter(1);
        assertEquals(sheet.getMaxColumns(),4);
        Object[][] list = sheet.getDataRange().getValues();
        assertEquals(list[0][0],1);
        assertEquals(list[0][1],null);
        assertEquals(list[0][2],null);
        assertEquals(list[0][3],3);
        assertEquals(list[1][0],2);
        assertEquals(list[1][1],null);
        assertEquals(list[1][2],null);
        assertEquals(list[1][3],4);
    }

    @Test
    public void testInsertColumnBefore() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        sheet.insertColumnBefore(1);
        assertEquals(sheet.getMaxColumns(),3);
        sheet.insertColumnBefore(0);
        assertEquals(sheet.getMaxColumns(),4);
        Object[][] list = sheet.getDataRange().getValues();
        assertEquals(list[0][0],null);
        assertEquals(list[0][1],1);
        assertEquals(list[0][2],null);
        assertEquals(list[0][3],3);
        assertEquals(list[1][0],null);
        assertEquals(list[1][1],2);
        assertEquals(list[1][2],null);
        assertEquals(list[1][3],4);
    }

    @Test
    public void testInsertColumnsAfter() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        sheet.insertColumnsAfter(1,3);
        sheet.insertColumnsAfter(0,2);
        assertEquals(sheet.getMaxColumns(),7);

        Object[][] list = sheet.getDataRange().getValues();
        assertEquals(list[0][0],1);
        assertEquals(list[0][1],null);
        assertEquals(list[0][2],null);
        assertEquals(list[0][3],3);
        assertEquals(list[0][4],null);
        assertEquals(list[0][5],null);
        assertEquals(list[0][6],null);
        assertEquals(list[1][0],2);
        assertEquals(list[1][1],null);
        assertEquals(list[1][2],null);
        assertEquals(list[1][3],4);
        assertEquals(list[1][4],null);
        assertEquals(list[1][5],null);
        assertEquals(list[1][6],null);
    }

    @Test
    public void testInsertColumnsBefore() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        sheet.insertColumnsAfter(1,3);
        sheet.insertColumnsAfter(0,2);
        assertEquals(sheet.getMaxColumns(),7);

        Object[][] list = sheet.getDataRange().getValues();
        assertEquals(list[0][0],1);
        assertEquals(list[0][1],null);
        assertEquals(list[0][2],null);
        assertEquals(list[0][3],3);
        assertEquals(list[0][4],null);
        assertEquals(list[0][5],null);
        assertEquals(list[0][6],null);
        assertEquals(list[1][0],2);
        assertEquals(list[1][1],null);
        assertEquals(list[1][2],null);
        assertEquals(list[1][3],4);
        assertEquals(list[1][4],null);
        assertEquals(list[1][5],null);
        assertEquals(list[1][6],null);
    }

    @Test
    public void testInsertRowAfter() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        sheet.insertRowAfter(0);
        assertEquals(sheet.getMaxRows(),3);
        sheet.insertRowAfter(1);
        assertEquals(sheet.getMaxRows(),4);
        Object[][] list = sheet.getDataRange().getValues();
        assertEquals(list[0][0],1);
        assertEquals(list[1][0],null);
        assertEquals(list[2][0],null);
        assertEquals(list[3][0],2);
        assertEquals(list[0][1],3);
        assertEquals(list[1][1],null);
        assertEquals(list[2][1],null);
        assertEquals(list[3][1],4);
    }

    @Test
    public void testInsertRowBefore() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        sheet.insertRowBefore(1);
        assertEquals(sheet.getMaxRows(),3);
        sheet.insertRowBefore(0);
        assertEquals(sheet.getMaxRows(),4);
        Object[][] list = sheet.getDataRange().getValues();
        assertEquals(list[0][0],null);
        assertEquals(list[1][0],1);
        assertEquals(list[2][0],null);
        assertEquals(list[3][0],2);
        assertEquals(list[0][1],null);
        assertEquals(list[1][1],3);
        assertEquals(list[2][1],null);
        assertEquals(list[3][1],4);
    }

    @Test
    public void testInsertRowsBefore() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        sheet.insertRowsAfter(1,3);
        sheet.insertRowsAfter(0,2);
        assertEquals(sheet.getMaxRows(),7);

        Object[][] list = sheet.getDataRange().getValues();
        assertEquals(list[0][0],1);
        assertEquals(list[1][0],null);
        assertEquals(list[2][0],null);
        assertEquals(list[3][0],2);
        assertEquals(list[4][0],null);
        assertEquals(list[5][0],null);
        assertEquals(list[6][0],null);
        assertEquals(list[0][1],3);
        assertEquals(list[1][1],null);
        assertEquals(list[2][1],null);
        assertEquals(list[3][1],4);
        assertEquals(list[4][1],null);
        assertEquals(list[5][1],null);
        assertEquals(list[6][1],null);
    }

    @Test
    public void testInsertRowsAfter() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        sheet.insertRowsAfter(1,3);
        sheet.insertRowsAfter(0,2);
        assertEquals(sheet.getMaxRows(),7);

        Object[][] list = sheet.getDataRange().getValues();
        assertEquals(list[0][0],1);
        assertEquals(list[1][0],null);
        assertEquals(list[2][0],null);
        assertEquals(list[3][0],2);
        assertEquals(list[4][0],null);
        assertEquals(list[5][0],null);
        assertEquals(list[6][0],null);
        assertEquals(list[0][1],3);
        assertEquals(list[1][1],null);
        assertEquals(list[2][1],null);
        assertEquals(list[3][1],4);
        assertEquals(list[4][1],null);
        assertEquals(list[5][1],null);
        assertEquals(list[6][1],null);
    }

    @Test
    public void testCompareTo() throws Exception {

    }

}
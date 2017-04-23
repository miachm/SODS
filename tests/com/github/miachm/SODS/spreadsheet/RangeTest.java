package com.github.miachm.SODS.spreadsheet;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.fail;

public class RangeTest {
    @Test
    public void testClear() throws Exception {
        Sheet sheet = new Sheet("A");
        Range range = sheet.getDataRange();
        range.setValue(1);
        range.clear();
        assertNull(range.getValue());

        sheet.insertColumnAfter(0);
        sheet.insertRowAfter(0);

        range = sheet.getDataRange();
        range.setValues(1,2,3,4);
        range.clear();

        Object[][] values = range.getValues();

        for (Object[] row : values)
            for (Object value : row)
                assertNull(value);
    }

    @Test
    public void testCopyTo() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.insertColumnsAfter(0,3);
        sheet.insertRowAfter(0);

        Range range = sheet.getDataRange();
        range.setValues(1,2,3,4,5,6,7,8);

        Range origin = sheet.getRange(0,0,2,2);
        Range dest = sheet.getRange(0,2,2,2);
        origin.copyTo(dest);

        Object[][] values = dest.getValues();

        assertEquals(values[0][0],1);
        assertEquals(values[1][0],5);
        assertEquals(values[0][1],2);
        assertEquals(values[1][1],6);
    }

    @Test
    public void testGetCell() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.insertColumnsAfter(0,2);
        sheet.insertRowAfter(0);

        Range range = sheet.getDataRange();
        range.setValues(1,2,3,4,5,6);

        assertEquals(sheet.getCell(0,0).getValue(),1);
        assertEquals(sheet.getCell(0,1).getValue(),2);
        assertEquals(sheet.getCell(0,2).getValue(),3);
        assertEquals(sheet.getCell(1,0).getValue(),4);
        assertEquals(sheet.getCell(1,1).getValue(),5);
        assertEquals(sheet.getCell(1,2).getValue(),6);
    }

    @Test
    public void testGetColumn() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRows(10);
        sheet.appendColumns(10);

        for (int i = 0;i < sheet.getMaxRows()/2;i++) {
            for (int j = 0; j < sheet.getMaxColumns()/2; j++) {
                Range range = sheet.getRange(i,j,2,2);
                assertEquals(range.getColumn(),j);
            }
        }
    }

    @Test
    public void testGetFormula() throws Exception {
        fail("Not implemented");
    }

    @Test
    public void testGetFormulas() throws Exception {
        fail("Not implemented");
    }

    @Test
    public void testGetLastColumn() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRows(10);
        sheet.appendColumns(10);

        for (int i = 0;i < sheet.getMaxRows()/2;i++) {
            for (int j = 0; j < sheet.getMaxColumns()/2; j++) {
                Range range = sheet.getRange(i,j,2,2);
                assertEquals(range.getLastColumn(),j+1);
            }
        }
    }

    @Test
    public void testGetLastRow() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRows(10);
        sheet.appendColumns(10);

        for (int i = 0;i < sheet.getMaxRows()/2;i++) {
            for (int j = 0; j < sheet.getMaxColumns()/2; j++) {
                Range range = sheet.getRange(i,j,2,2);
                assertEquals(range.getLastRow(),i+1);
            }
        }
    }

    @Test
    public void testGetNumColumns() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRows(10);
        sheet.appendColumns(10);

        for (int i = 0;i < sheet.getMaxRows()/2;i++) {
            for (int j = 0; j < sheet.getMaxColumns()/2; j++) {
                Range range = sheet.getRange(i,j,3,2);
                assertEquals(range.getNumColumns(),2);
            }
        }
    }

    @Test
    public void testGetNumRows() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRows(10);
        sheet.appendColumns(10);

        for (int i = 0;i < sheet.getMaxRows()/2;i++) {
            for (int j = 0; j < sheet.getMaxColumns()/2; j++) {
                Range range = sheet.getRange(i,j,3,2);
                assertEquals(range.getNumRows(),3);
            }
        }
    }

    @Test
    public void testGetRow() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRows(10);
        sheet.appendColumns(10);

        for (int i = 0;i < sheet.getMaxRows()/2;i++) {
            for (int j = 0; j < sheet.getMaxColumns() / 2; j++) {
                Range range = sheet.getRange(i, j, 2, 2);
                assertEquals(range.getRow(), i);
            }
        }
    }

    @Test
    public void testGetSheet() throws Exception {
        Sheet sheet = new Sheet("A");
        Range range = sheet.getDataRange();
        Sheet parent = range.getSheet();

        assertEquals(sheet,parent);
    }

    @Test
    public void testGetValue() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Range range = sheet.getDataRange();
        range.setValues(1,2,3,4);

        assertEquals(range.getValue(),1);

        range = sheet.getRange(1,1);
        assertEquals(range.getValue(),4);
    }

    @Test
    public void testGetValues() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Range range = sheet.getDataRange();
        range.setValues(1,2,3,4);

        Object[][] arr = range.getValues();

        assertEquals(arr[0][0],1);
        assertEquals(arr[0][1],2);
        assertEquals(arr[1][0],3);
        assertEquals(arr[1][1],4);
    }

    @Test
    public void testGetNumValues() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRows(2);
        sheet.appendColumn();

        Range range = sheet.getDataRange();
        assertEquals(range.getNumValues(),6);
    }

    @Test
    public void testSetValue() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Range range = sheet.getDataRange();
        range.setValue(1);

        Object[][] arr = range.getValues();

        assertEquals(arr[0][0],1);
        assertEquals(arr[0][1],1);
        assertEquals(arr[1][0],1);
        assertEquals(arr[1][1],1);
    }

    @Test
    public void testSetValues() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Range range = sheet.getDataRange();
        range.setValues(1,2,3,4);

        Object[][] arr = range.getValues();

        assertEquals(arr[0][0],1);
        assertEquals(arr[0][1],2);
        assertEquals(arr[1][0],3);
        assertEquals(arr[1][1],4);
    }

    @Test
    public void testSetValuesMat() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumns(2);

        Range range = sheet.getDataRange();
        Object[][] arr = new Object[2][3];
        arr[0][0] = 1;
        arr[0][1] = 2;
        arr[0][2] = 3;
        arr[1][0] = 4;
        arr[1][1] = 5;
        arr[1][2] = 6;

        range.setValues(arr);

        arr = range.getValues();

        assertEquals(arr[0][0],1);
        assertEquals(arr[0][1],2);
        assertEquals(arr[0][2],3);
        assertEquals(arr[1][0],4);
        assertEquals(arr[1][1],5);
        assertEquals(arr[1][2],6);
    }
}
package com.github.miachm.SODS.spreadsheet;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertNull;

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

        for (int i = 0;i < values.length;i++)
            for (int j = 0;j < values[i].length;j++)
                assertNull(values[i][j]);
    }

    @Test
    public void testCopyTo() throws Exception {

    }

    @Test
    public void testGetCell() throws Exception {

    }

    @Test
    public void testGetColumn() throws Exception {

    }

    @Test
    public void testGetFormula() throws Exception {

    }

    @Test
    public void testGetFormulas() throws Exception {

    }

    @Test
    public void testGetLastColumn() throws Exception {

    }

    @Test
    public void testGetLastRow() throws Exception {

    }

    @Test
    public void testGetNumColumns() throws Exception {

    }

    @Test
    public void testGetNumRows() throws Exception {

    }

    @Test
    public void testGetRow() throws Exception {

    }

    @Test
    public void testGetSheet() throws Exception {

    }

    @Test
    public void testGetValue() throws Exception {

    }

    @Test
    public void testGetValues() throws Exception {

    }

    @Test
    public void testGetNumValues() throws Exception {

    }

    @Test
    public void testSetValue() throws Exception {

    }

    @Test
    public void testSetValues() throws Exception {

    }

    @Test
    public void testSetValues1() throws Exception {

    }

}
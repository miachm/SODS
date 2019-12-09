package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.internal.junit.ArrayAsserts.assertArrayEquals;

public class SheetTest {
    private Random random = new Random();

    private Sheet generateASheet() {
        Sheet sheet = new Sheet("A");
        sheet.insertRowsAfter(0, 9);
        sheet.insertColumnsAfter(0, 9);

        Range range = sheet.getDataRange();

        List<Integer> integers = new ArrayList<>();

        for (int i = 0; i < range.getNumValues(); i++) {
            integers.add(random.nextInt());
        }

        range.setValues(integers.toArray());
        return sheet;
    }

    private Sheet generateDeterministicSheet() {
        Sheet sheet = new Sheet("A");
        sheet.insertRowAfter(0);
        sheet.insertColumnAfter(0);

        sheet.getRange(0, 0).setValue(1);
        sheet.getRange(1, 0).setValue(2);
        sheet.getRange(0, 1).setValue(3);
        sheet.getRange(1, 1).setValue(4);
        return sheet;
    }

    @Test
    public void construct() throws Exception {
        Sheet sheet = new Sheet("A");
        assertEquals(sheet.getMaxRows(), 1);
        assertEquals(sheet.getMaxColumns(), 1);

        sheet = new Sheet("A", 4, 6);
        assertEquals(sheet.getMaxRows(), 4);
        assertEquals(sheet.getMaxColumns(), 6);
    }

    @Test
    public void testClear() throws Exception {
        Sheet sheet = generateASheet();
        int rows = sheet.getMaxRows();
        int columns = sheet.getMaxColumns();
        sheet.clear();
        assertEquals(sheet.getMaxRows(), rows);
        assertEquals(sheet.getMaxColumns(), columns);

        Range range = sheet.getDataRange();

        for (Object o : range.getValues()) {
            assertNotNull(o);
        }
    }

    @Test
    public void testClone() throws Exception {
        Sheet sheet = generateASheet();
        Sheet other = (Sheet) sheet.clone();

        assertEquals(sheet, other);
    }

    @Test
    public void testEquals() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        Sheet other = generateDeterministicSheet();
        assertEquals(sheet, other);
        sheet.getRange(0, 0).setValue(-1);

        boolean equals = sheet.equals(other);
        assertEquals(equals, false);
    }

    @Test
    public void testDeleteColumn() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.insertColumnAfter(0);
        sheet.getDataRange().setValues(1, 2);

        sheet.setColumnWidth(1, 6.5);
        sheet.deleteColumn(0);

        assertEquals(sheet.getMaxColumns(), 1);
        assertEquals(sheet.getRange(0, 0).getValue(), 2);
        assertEquals(sheet.getColumnWidth(0), 6.5);

        sheet.insertColumnAfter(0);
        sheet.insertColumnAfter(0);
        sheet.getDataRange().setValues(1, 2, 3);

        sheet.deleteColumn(2);

        assertEquals(sheet.getMaxColumns(), 2);
        assertEquals(sheet.getRange(0, 0).getValue(), 1);
    }

    @Test
    public void testDeleteColumns() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.insertColumnAfter(0);
        sheet.insertColumnAfter(0);
        sheet.getDataRange().setValues(1, 2, 3);

        sheet.deleteColumns(1, 2);

        assertEquals(sheet.getMaxColumns(), 1);
        assertEquals(sheet.getRange(0, 0).getValue(), 1);
    }

    @Test
    public void testDeleteRow() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.insertRowAfter(0);
        sheet.getDataRange().setValues(1, 2);

        sheet.setRowHeight(1, 4.4);
        sheet.deleteRow(0);

        assertEquals(sheet.getMaxRows(), 1);
        assertEquals(sheet.getRange(0, 0).getValue(), 2);
        assertEquals(sheet.getRowHeight(0), 4.4);

        sheet.insertRowAfter(0);
        sheet.insertRowAfter(0);
        sheet.getDataRange().setValues(1, 2, 3);

        sheet.deleteRow(2);

        assertEquals(sheet.getMaxRows(), 2);
        assertEquals(sheet.getRange(0, 0).getValue(), 1);
    }

    @Test
    public void testDeleteRows() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.insertRowAfter(0);
        sheet.insertRowAfter(0);
        sheet.getDataRange().setValues(1, 2, 3);

        sheet.deleteRows(1, 2);

        assertEquals(sheet.getMaxRows(), 1);
        assertEquals(sheet.getRange(0, 0).getValue(), 1);
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
        assertArrayEquals(range.getValues(), solution);
    }

    @Test
    public void testGetMaxColumns() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        assertEquals(sheet.getMaxColumns(), 2);
    }

    @Test
    public void testGetMaxRows() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        assertEquals(sheet.getMaxRows(), 2);
    }

    @Test
    public void testGetName() throws Exception {
        Sheet sheet = new Sheet("D");
        assertEquals(sheet.getName(), "D");
    }

    @Test
    public void testGetRangeCell() throws Exception {
        Sheet sheet = generateASheet();
        Object[][] values = sheet.getDataRange().getValues();
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                assertEquals(values[i][j], sheet.getRange(i, j).getValue());
            }
        }
    }

    @Test
    public void testGetRangeRows() throws Exception {
        Sheet sheet = generateASheet();
        Object[][] values = sheet.getDataRange().getValues();
        for (int i = 0; i < values.length - 1; i++) {
            for (int j = 0; j < values[i].length - 1; j++) {
                Range range = sheet.getRange(i, j, 2);
                Object[][] v = range.getValues();
                assertEquals(values[i][j], v[0][0]);
                assertEquals(values[i + 1][j], v[1][0]);
            }
        }
    }

    @Test
    public void testGetRangeTable() throws Exception {
        Sheet sheet = generateASheet();
        Object[][] values = sheet.getDataRange().getValues();
        for (int i = 0; i < values.length - 1; i++) {
            for (int j = 0; j < values[i].length - 1; j++) {
                Range range = sheet.getRange(i, j, 2, 2);
                Object[][] v = range.getValues();
                assertEquals(values[i][j], v[0][0]);
                assertEquals(values[i + 1][j], v[1][0]);
                assertEquals(values[i][j + 1], v[0][1]);
                assertEquals(values[i + 1][j + 1], v[1][1]);
            }
        }
    }

    @Test
    public void testInsertColumnAfter() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        sheet.insertColumnAfter(0);
        assertEquals(sheet.getMaxColumns(), 3);
        sheet.insertColumnAfter(1);
        assertEquals(sheet.getMaxColumns(), 4);

        Object[][] list = sheet.getDataRange().getValues();
        assertEquals(list[0][0], 1);
        assertEquals(list[0][1], null);
        assertEquals(list[0][2], null);
        assertEquals(list[0][3], 3);
        assertEquals(list[1][0], 2);
        assertEquals(list[1][1], null);
        assertEquals(list[1][2], null);
        assertEquals(list[1][3], 4);
    }

    @Test
    public void testInsertColumnBefore() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        sheet.insertColumnBefore(1);
        assertEquals(sheet.getMaxColumns(), 3);
        sheet.insertColumnBefore(0);
        assertEquals(sheet.getMaxColumns(), 4);
        Object[][] list = sheet.getDataRange().getValues();
        assertEquals(list[0][0], null);
        assertEquals(list[0][1], 1);
        assertEquals(list[0][2], null);
        assertEquals(list[0][3], 3);
        assertEquals(list[1][0], null);
        assertEquals(list[1][1], 2);
        assertEquals(list[1][2], null);
        assertEquals(list[1][3], 4);
    }

    @Test
    public void testInsertColumnsAfter() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        sheet.insertColumnsAfter(1, 3);
        sheet.insertColumnsAfter(0, 2);
        assertEquals(sheet.getMaxColumns(), 7);

        Object[][] list = sheet.getDataRange().getValues();
        assertEquals(list[0][0], 1);
        assertEquals(list[0][1], null);
        assertEquals(list[0][2], null);
        assertEquals(list[0][3], 3);
        assertEquals(list[0][4], null);
        assertEquals(list[0][5], null);
        assertEquals(list[0][6], null);
        assertEquals(list[1][0], 2);
        assertEquals(list[1][1], null);
        assertEquals(list[1][2], null);
        assertEquals(list[1][3], 4);
        assertEquals(list[1][4], null);
        assertEquals(list[1][5], null);
        assertEquals(list[1][6], null);
    }

    @Test
    public void testInsertColumnsBefore() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        sheet.insertColumnsAfter(1, 3);
        sheet.insertColumnsAfter(0, 2);
        assertEquals(sheet.getMaxColumns(), 7);

        Object[][] list = sheet.getDataRange().getValues();
        assertEquals(list[0][0], 1);
        assertEquals(list[0][1], null);
        assertEquals(list[0][2], null);
        assertEquals(list[0][3], 3);
        assertEquals(list[0][4], null);
        assertEquals(list[0][5], null);
        assertEquals(list[0][6], null);
        assertEquals(list[1][0], 2);
        assertEquals(list[1][1], null);
        assertEquals(list[1][2], null);
        assertEquals(list[1][3], 4);
        assertEquals(list[1][4], null);
        assertEquals(list[1][5], null);
        assertEquals(list[1][6], null);
    }

    @Test
    public void testInsertRowAfter() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        sheet.insertRowAfter(0);
        assertEquals(sheet.getMaxRows(), 3);
        sheet.insertRowAfter(1);
        assertEquals(sheet.getMaxRows(), 4);
        Object[][] list = sheet.getDataRange().getValues();
        assertEquals(list[0][0], 1);
        assertEquals(list[1][0], null);
        assertEquals(list[2][0], null);
        assertEquals(list[3][0], 2);
        assertEquals(list[0][1], 3);
        assertEquals(list[1][1], null);
        assertEquals(list[2][1], null);
        assertEquals(list[3][1], 4);
    }

    @Test
    public void testInsertRowBefore() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        sheet.insertRowBefore(1);
        assertEquals(sheet.getMaxRows(), 3);
        sheet.insertRowBefore(0);
        assertEquals(sheet.getMaxRows(), 4);
        Object[][] list = sheet.getDataRange().getValues();
        assertEquals(list[0][0], null);
        assertEquals(list[1][0], 1);
        assertEquals(list[2][0], null);
        assertEquals(list[3][0], 2);
        assertEquals(list[0][1], null);
        assertEquals(list[1][1], 3);
        assertEquals(list[2][1], null);
        assertEquals(list[3][1], 4);
    }

    @Test
    public void testInsertRowsBefore() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        sheet.insertRowsAfter(1, 3);
        sheet.insertRowsAfter(0, 2);
        assertEquals(sheet.getMaxRows(), 7);

        Object[][] list = sheet.getDataRange().getValues();
        assertEquals(list[0][0], 1);
        assertEquals(list[1][0], null);
        assertEquals(list[2][0], null);
        assertEquals(list[3][0], 2);
        assertEquals(list[4][0], null);
        assertEquals(list[5][0], null);
        assertEquals(list[6][0], null);
        assertEquals(list[0][1], 3);
        assertEquals(list[1][1], null);
        assertEquals(list[2][1], null);
        assertEquals(list[3][1], 4);
        assertEquals(list[4][1], null);
        assertEquals(list[5][1], null);
        assertEquals(list[6][1], null);
    }

    @Test
    public void testInsertRowsAfter() throws Exception {
        Sheet sheet = generateDeterministicSheet();
        sheet.insertRowsAfter(1, 3);
        sheet.insertRowsAfter(0, 2);
        assertEquals(sheet.getMaxRows(), 7);

        Object[][] list = sheet.getDataRange().getValues();
        assertEquals(list[0][0], 1);
        assertEquals(list[1][0], null);
        assertEquals(list[2][0], null);
        assertEquals(list[3][0], 2);
        assertEquals(list[4][0], null);
        assertEquals(list[5][0], null);
        assertEquals(list[6][0], null);
        assertEquals(list[0][1], 3);
        assertEquals(list[1][1], null);
        assertEquals(list[2][1], null);
        assertEquals(list[3][1], 4);
        assertEquals(list[4][1], null);
        assertEquals(list[5][1], null);
        assertEquals(list[6][1], null);
    }

    @Test
    public void testCompareTo() throws Exception {
        Sheet a = new Sheet("A");
        Sheet b = new Sheet("B");

        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
    }

    @Test
    public void testAppendRow() throws Exception {
        Sheet a = new Sheet("A");
        a.getDataRange().setValue(1);
        a.appendRow();
        a.appendRow();
        a.appendRow();

        assertEquals(a.getMaxRows(), 4);
        assertEquals(a.getRange(0, 0).getValue(), 1);
    }

    @Test
    public void testAppendRows() throws Exception {
        Sheet a = new Sheet("A");
        a.getDataRange().setValue(1);
        a.appendRows(3);

        assertEquals(a.getMaxRows(), 4);
        assertEquals(a.getRange(0, 0).getValue(), 1);
    }

    @Test
    public void testAppendColumn() throws Exception {
        Sheet a = new Sheet("A");
        a.getDataRange().setValue(1);
        a.appendColumn();
        a.appendColumn();
        a.appendColumn();

        assertEquals(a.getMaxColumns(), 4);
        assertEquals(a.getRange(0, 0).getValue(), 1);
    }

    @Test
    public void testAppendColumns() throws Exception {
        Sheet a = new Sheet("A");
        a.getDataRange().setValue(1);
        a.appendColumns(3);

        assertEquals(a.getMaxColumns(), 4);
        assertEquals(a.getRange(0, 0).getValue(), 1);
    }

    @Test
    public void testSetColumnWidth() throws Exception {
        Sheet a = new Sheet("A");
        a.appendColumns(3);

        double[] widths = {23.43, 12.412, 4.31, 9.42};
        for (int i = 0; i < 4; i++)
            a.setColumnWidth(i, widths[i]);

        for (int i = 0; i < 4; i++)
            assertEquals(widths[i], a.getColumnWidth(i));

        a.setColumnWidth(1, null);
        assertNull(a.getColumnWidth(1));

        try {
            a.setColumnWidth(2, -1.0);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            a.setColumnWidth(-2, 3.0);
            fail();
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            a.setColumnWidth(4, 3.0);
            fail();
        } catch (IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void testSetColumnWidths() throws Exception {
        Sheet a = new Sheet("A");
        a.getDataRange().setValue(1);
        a.appendColumns(3);
        a.setColumnWidths(0, 3, 23.43);

        for (int i = 0; i < 3; i++)
            assertEquals(23.43, a.getColumnWidth(i));

        try {
            a.getColumnWidth(4);
            fail();
        }
        catch (IndexOutOfBoundsException e) {}
    }


    @Test
    public void testSetRowHeight() throws Exception {
        Sheet a = new Sheet("A");
        a.getDataRange().setValue(1);
        a.appendRows(3);

        double[] heights = {23.43, 12.412, 4.31, 9.42};
        for (int i = 0; i < 4; i++)
            a.setRowHeight(i, heights[i]);

        for (int i = 0; i < 4; i++)
            assertEquals(heights[i], a.getRowHeight(i));

        a.setRowHeight(1, null);
        assertNull(a.getRowHeight(1));

        try {
            a.setRowHeight(2, -1.0);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            a.setRowHeight(-2, 3.0);
            fail();
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            a.setRowHeight(4, 3.0);
            fail();
        } catch (IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void testSetRowHeights() throws Exception {
        Sheet a = new Sheet("A");
        a.getDataRange().setValue(1);
        a.appendRows(3);
        a.setRowHeights(0, 3, 23.43);

        for (int i = 0; i < 3; i++)
            assertEquals(23.43, a.getRowHeight(i));

        try {
            a.getRowHeight(4);
            fail();
        }
        catch (IndexOutOfBoundsException e) {}
    }

    @Test
    public void testTrim()
    {
        Sheet sheet = new Sheet("A", 30, 30);
        assertEquals(sheet.getMaxRows(), 30);
        assertEquals(sheet.getMaxColumns(), 30);
        assertEquals(sheet.getLastRow(), 0);
        assertEquals(sheet.getLastColumn(), 0);

        sheet.trim();

        assertEquals(sheet.getMaxRows(), 0);
        assertEquals(sheet.getMaxColumns(), 0);
        assertEquals(sheet.getLastRow(), 0);
        assertEquals(sheet.getLastColumn(), 0);

        sheet.appendRows(3);
        sheet.appendColumns(3);
        sheet.getRange(0, 0, 2, 2).setValues(1, 2, 3, 4);

        sheet.trim();

        assertEquals(sheet.getMaxRows(), 2);
        assertEquals(sheet.getMaxColumns(), 2);
        assertEquals(sheet.getLastRow(), 2);
        assertEquals(sheet.getLastColumn(), 2);
    }
}
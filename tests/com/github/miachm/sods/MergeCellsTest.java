package com.github.miachm.sods;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class MergeCellsTest {
    @Test
    public void testGetMergedCells() {
        Sheet sheet = new Sheet("A");
        sheet.appendRows(4);
        sheet.appendColumns(4);

        Range range = sheet.getRange(1,1);
        assertEquals(range.getMergedCells().length, 0);

        range = sheet.getRange(0,0,2,2);
        range.merge();
        range = sheet.getRange(2,2,2,2);
        range.merge();

        range = sheet.getRange(1,1);
        Range[] group = range.getMergedCells();
        assertEquals(group.length, 1);

        range = sheet.getDataRange();
        checkMergedCells(range);
    }


    @Test
    public void testMerge() {
        Sheet sheet = new Sheet("A");
        sheet.appendRows(4);
        sheet.appendColumns(4);

        Range range = sheet.getRange(0,0,2,2);
        range.merge();
        range = sheet.getRange(2,2,2,2);
        range.merge();

        Range[] group = range.getMergedCells();
        assertEquals(group.length, 1);

        range = sheet.getDataRange();
        checkMergedCells(range);

        try {
            range.merge();
        }
        catch (AssertionError e) {}

        range = sheet.getDataRange();
        checkMergedCells(range);
    }

    private void checkMergedCells(Range range)
    {
        Range[] group = range.getMergedCells();
        assertEquals(group.length, 2);
        assertEquals(group[0].getRow(), 0);
        assertEquals(group[0].getColumn(), 0);
        assertEquals(group[0].getLastRow(), 1);
        assertEquals(group[0].getLastColumn(), 1);

        assertEquals(group[1].getRow(), 2);
        assertEquals(group[1].getColumn(), 2);
        assertEquals(group[1].getLastRow(), 3);
        assertEquals(group[1].getLastColumn(), 3);
    }

    @Test
    public void split()
    {
        Sheet sheet = new Sheet("A");
        sheet.appendRows(4);
        sheet.appendColumns(4);

        Range range = sheet.getRange(0,0,2,2);
        range.merge();
        range = sheet.getRange(2,2,2,2);
        range.merge();

        range = sheet.getRange(0,0,2,2);
        range.split();
        assertEquals(sheet.getDataRange().getMergedCells().length, 1);

        range = sheet.getRange(0,0,2,2);
        range.merge();
        range = sheet.getRange(0,0,4,4);
        range.split();
        assertEquals(sheet.getDataRange().getMergedCells().length, 0);
    }

    @Test
    public void isPartOfMerge()
    {
        Sheet sheet = new Sheet("A");
        sheet.appendRows(4);
        sheet.appendColumns(4);

        Range range = sheet.getRange(0,0,2,2);
        range.merge();

        assertEquals(sheet.getDataRange().isPartOfMerge(), true);
        assertEquals(sheet.getRange(2,2,2,2).isPartOfMerge(), false);

        assertEquals(sheet.getRange(1,1).isPartOfMerge(),true);
        assertEquals(sheet.getRange(1,1,2,2).isPartOfMerge(), true);
    }

}

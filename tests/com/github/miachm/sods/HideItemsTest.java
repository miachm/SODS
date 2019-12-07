package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.testng.AssertJUnit.*;

public class HideItemsTest {

    @Test
    public void testRowInterface()
    {
        Sheet sheet = new Sheet("A", 10, 10);
        assertEquals(sheet.getHiddenRows().size(), 0);

        sheet.hideRow(5);
        sheet.hideRow(8);

        Set<Integer> hiddenRows = sheet.getHiddenRows();
        assertEquals(sheet.getHiddenRows().size(), 2);
        assertTrue(hiddenRows.contains(5));
        assertTrue(hiddenRows.contains(8));
        assertTrue(sheet.rowIsHidden(5));
        assertTrue(sheet.rowIsHidden(8));
        assertFalse(sheet.rowIsHidden(0));
        assertFalse(sheet.rowIsHidden(1));
        assertFalse(sheet.rowIsHidden(2));
        assertFalse(sheet.rowIsHidden(9));

        sheet.showRow(8);
        hiddenRows = sheet.getHiddenRows();
        assertEquals(sheet.getHiddenRows().size(), 1);
        assertTrue(hiddenRows.contains(5));
        assertFalse(hiddenRows.contains(8));
        assertFalse(sheet.rowIsHidden(8));
        assertTrue(sheet.rowIsHidden(5));

        sheet.hideRow(8);
        assertTrue(sheet.rowIsHidden(8));
    }

    @Test
    public void testColumnInterface()
    {
        Sheet sheet = new Sheet("A", 10, 10);
        assertEquals(sheet.getHiddenColumns().size(), 0);

        sheet.hideColumn(5);
        sheet.hideColumn(8);

        Set<Integer> hiddenColumns = sheet.getHiddenColumns();
        assertEquals(sheet.getHiddenColumns().size(), 2);
        assertTrue(hiddenColumns.contains(5));
        assertTrue(hiddenColumns.contains(8));
        assertTrue(sheet.columnIsHidden(5));
        assertTrue(sheet.columnIsHidden(8));
        assertFalse(sheet.columnIsHidden(0));
        assertFalse(sheet.columnIsHidden(1));
        assertFalse(sheet.columnIsHidden(2));
        assertFalse(sheet.columnIsHidden(9));

        sheet.showColumn(8);
        hiddenColumns = sheet.getHiddenColumns();
        assertEquals(sheet.getHiddenColumns().size(), 1);
        assertTrue(hiddenColumns.contains(5));
        assertFalse(hiddenColumns.contains(8));
        assertFalse(sheet.columnIsHidden(8));
        assertTrue(sheet.columnIsHidden(5));

        sheet.hideColumn(8);
        assertTrue(sheet.columnIsHidden(8));
    }

    @Test
    public void loadFile() throws IOException {
        SpreadSheet spreadSheet = new SpreadSheet(new File("resources/hiddenItems.ods"));
        List<Sheet> sheets = spreadSheet.getSheets();
        Sheet visible = sheets.get(0);

        assertFalse(visible.rowIsHidden(0));
        assertTrue(visible.rowIsHidden(1));
        assertFalse(visible.rowIsHidden(2));
        assertEquals(visible.getHiddenRows().size(), 1);

        assertFalse(visible.columnIsHidden(0));
        assertFalse(visible.columnIsHidden(1));
        assertTrue(visible.columnIsHidden(2));
        assertEquals(visible.getHiddenColumns().size(), 1);
    }

    @Test
    public void saveFile() throws IOException {
        SpreadSheet spreadSheet = new SpreadSheet();
        Sheet sheet = new Sheet("A", 10, 10);
        sheet.hideRow(5);
        sheet.hideRow(3);
        sheet.hideColumn(2);
        sheet.hideColumn(9);
        spreadSheet.appendSheet(sheet);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        spreadSheet.save(output);
        spreadSheet.save(new File("example.ods"));

        ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
        SpreadSheet newSpreadsheet = new SpreadSheet(input);

        assertEquals(spreadSheet, newSpreadsheet);

        Sheet newSheet = newSpreadsheet.getSheet(0);
        assertEquals(sheet.getHiddenRows(), newSheet.getHiddenRows());
        assertEquals(sheet.getHiddenColumns(), newSheet.getHiddenColumns());
        assertTrue(newSheet.rowIsHidden(5));
        assertTrue(newSheet.columnIsHidden(2));
        assertFalse(newSheet.rowIsHidden(2));
        assertFalse(newSheet.columnIsHidden(5));
    }
}

package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.testng.AssertJUnit.*;

public class HideItemsTest {

    @Test
    public void testRowInterface()
    {
        Sheet sheet = new Sheet("A", 10, 10);

        sheet.hideRow(5);
        sheet.hideRow(8);

        assertTrue(sheet.rowIsHidden(5));
        assertTrue(sheet.rowIsHidden(8));
        assertFalse(sheet.rowIsHidden(0));
        assertFalse(sheet.rowIsHidden(1));
        assertFalse(sheet.rowIsHidden(2));
        assertFalse(sheet.rowIsHidden(9));

        sheet.showRow(8);
        assertFalse(sheet.rowIsHidden(8));
        assertTrue(sheet.rowIsHidden(5));

        sheet.hideRow(8);
        assertTrue(sheet.rowIsHidden(8));

        assertFalse(sheet.rowIsHidden(7));
        sheet.deleteRow(7);
        assertTrue(sheet.rowIsHidden(7));
        assertFalse(sheet.rowIsHidden(8));

        sheet.deleteRows(7, sheet.getMaxRows() - 7);
    }

    @Test
    public void testColumnInterface()
    {
        Sheet sheet = new Sheet("A", 10, 10);

        sheet.hideColumn(5);
        sheet.hideColumn(8);

        assertTrue(sheet.columnIsHidden(5));
        assertTrue(sheet.columnIsHidden(8));
        assertFalse(sheet.columnIsHidden(0));
        assertFalse(sheet.columnIsHidden(1));
        assertFalse(sheet.columnIsHidden(2));
        assertFalse(sheet.columnIsHidden(9));

        sheet.showColumn(8);
        assertFalse(sheet.columnIsHidden(8));
        assertTrue(sheet.columnIsHidden(5));

        sheet.hideColumn(8);
        assertTrue(sheet.columnIsHidden(8));

        assertFalse(sheet.columnIsHidden(7));
        sheet.deleteColumn(7);
        assertTrue(sheet.columnIsHidden(7));
        assertFalse(sheet.columnIsHidden(8));

        sheet.deleteColumns(7, sheet.getMaxColumns() - 7);
    }

    @Test
    public void testSheetInterface()
    {
        Sheet sheet = new Sheet("A");
        assertFalse(sheet.isHidden());
        sheet.hideSheet();
        assertTrue(sheet.isHidden());
        sheet.showSheet();
        assertFalse(sheet.isHidden());
        sheet.hideSheet();
        assertTrue(sheet.isHidden());
    }

    @Test
    public void loadFile() throws IOException {
        SpreadSheet spreadSheet = new SpreadSheet(new File("resources/hiddenItems.ods"));
        List<Sheet> sheets = spreadSheet.getSheets();
        Sheet visible = sheets.get(0);

        assertFalse(visible.isHidden());

        assertFalse(visible.rowIsHidden(0));
        assertTrue(visible.rowIsHidden(1));
        assertFalse(visible.rowIsHidden(2));

        assertFalse(visible.columnIsHidden(0));
        assertFalse(visible.columnIsHidden(1));
        assertTrue(visible.columnIsHidden(2));

        Sheet invisible = sheets.get(1);
        assertTrue(invisible.isHidden());
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

        Sheet invisibleSheet = new Sheet("B");
        invisibleSheet.hideSheet();
        spreadSheet.appendSheet(invisibleSheet);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        spreadSheet.save(output);

        ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
        SpreadSheet newSpreadsheet = new SpreadSheet(input);

        assertEquals(spreadSheet, newSpreadsheet);

        Sheet newSheet = newSpreadsheet.getSheet(0);
        assertFalse(newSheet.isHidden());
        assertTrue(newSheet.rowIsHidden(5));
        assertTrue(newSheet.columnIsHidden(2));
        assertFalse(newSheet.rowIsHidden(2));
        assertFalse(newSheet.columnIsHidden(5));

        Sheet newInvisibleSheet = newSpreadsheet.getSheet(1);
        assertTrue(newInvisibleSheet.isHidden());
    }
}

package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Test reading of cells with multiple rows and/or multiple styles.
 */
public class MultiLineCellTest {

    @Test
    public void testTextLineBreakRoundTrip() throws Exception {
        SpreadSheet spread = new SpreadSheet();
        spread.appendSheet(new Sheet("Sheet1"));
        Sheet sheet = spread.getSheet(0);
        String value = "Hello," + "\n" + "line break!";
        sheet.getRange(0, 0).setValue(value);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        spread.save(out);
        SpreadSheet loaded = new SpreadSheet(new ByteArrayInputStream(out.toByteArray()));
        assertEquals(value, loaded.getSheet(0).getRange(0, 0).getValue());
    }

    @Test
    public void testReadNormalCell() throws Exception {
        // Just to make sure the test file is up to date, and existing functionality works.
        assertValueInCellEquals(2, 0, 0, "abc");
    }

    @Test
    public void testReadMultiStyleCell() throws Exception {
        assertValueInCellEquals(2, 0, 1, "def");
    }

    @Test
    public void testReadMultiRowCell() throws Exception {
        assertValueInCellEquals(2, 1, 0, "g\nh\ni");
    }

    @Test
    public void testReadMultiRowMultiStyleCell() throws Exception {
        assertValueInCellEquals(2, 1, 1, "jk\nlm\nno");
    }

    private void assertValueInCellEquals(int sheetNumber, int row, int column, String expectedValue) throws IOException {
        // Load example spreadsheet
        File testFile = new File("resources/CAS.ods");
        SpreadSheet spread = new SpreadSheet(testFile);

        // Get correct sheet
        Sheet sheet = spread.getSheet(sheetNumber);

        // Get correct cell and compare value
        assertEquals(expectedValue, sheet.getRange(row, column).getValue());
    }

}

package com.github.miachm.SODS.spreadsheet

/**
 * Created by MiguelPC on 18/04/2017.
 */
class SpreadSheetTest extends GroovyTestCase {

    void testClone() {
        SpreadSheet spread = new SpreadSheet();
        Sheet sheet = new Sheet();
        sheet.insertColumnsAfter(0,10);
        sheet.insertRowAfter(0,10);
        sheet.getDataRange().setValues(Collections.nCopies(1,"A"));

        spread.appendSheet(sheet);
        spread.appendSheet(sheet);

        SpreadSheet copy = spread.clone();

        assertEquals(spread,copy);
    }

    void testAppendSheet() {

    }

    void testAddSheet() {

    }

    void testClear() {

    }

    void testDeleteSheet() {

    }

    void testDeleteSheet1() {

    }

    void testDeleteSheet2() {

    }

    void testGetSheets() {

    }

    void testGetNumSheets() {

    }

    void testSetSheet() {

    }

    void testSave() {

    }

    void testSave1() {

    }

    void testSortSheets() {

    }

    void testSortSheets1() {

    }
}

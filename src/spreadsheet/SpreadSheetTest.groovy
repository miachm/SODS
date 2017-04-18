package com.github.miachm.SODS.spreadsheet

/**
 * Created by MiguelPC on 18/04/2017.
 */
class SpreadSheetTest extends GroovyTestCase {

    void testClone() {
        SpreadSheet spread = new SpreadSheet();
        Sheet sheet = new Sheet("A");
        sheet.insertColumnsAfter(0,10);
        sheet.insertRowAfter(0,10);
        sheet.getDataRange().setValues(Collections.nCopies(1,"A"));

        spread.appendSheet(sheet);
        spread.appendSheet(new Sheet("B"));

        SpreadSheet copy = spread.clone();

        assertEquals(spread,copy);
    }

    void testAppendSheet() {
        SpreadSheet spread = new SpreadSheet();
        spread.appendSheet(new Sheet("A"));
        spread.appendSheet(new Sheet("B"));
        spread.appendSheet(new Sheet("C"));
        assertEquals(spread.getNumSheets(),3);
    }

    void testAddSheet() {
        SpreadSheet spread = new SpreadSheet();
        spread.appendSheet(new Sheet("A"));
        spread.appendSheet(new Sheet("C"));
        spread.appendSheet(new Sheet("E"));
        spread.addSheet(new Sheet("B"),1);
        spread.addSheet(new Sheet("D"),3);
        assertEquals(spread.getNumSheets(),5);
        List<String> list = new ArrayList<>();
        Collections.addAll(list,"A","B","C","D","E");

        List<Sheet> sheets = spread.getSheets();
        for (int i = 0;i < sheets.size();i++){
            assertEquals(list.get(i),sheets.get(i).getName());
        }
    }

    void testClear() {
        SpreadSheet spread = new SpreadSheet();
        spread.appendSheet(new Sheet("A"));
        spread.appendSheet(new Sheet("B"));
        spread.appendSheet(new Sheet("C"));
        spread.clear();

        assertEquals(spread.getNumSheets(),0);
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

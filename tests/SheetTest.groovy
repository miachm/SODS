import com.github.miachm.SODS.spreadsheet.Sheet
import com.github.miachm.SODS.spreadsheet.Range

/**
 * Created by MiguelPC on 18/04/2017.
 */
class SheetTest extends GroovyTestCase {
    private Random random = new Random();

    Sheet generateASheet(){
        Sheet sheet = new Sheet("A");
        sheet.insertRowsAfter(0,99);
        sheet.insertColumnsAfter(0,99);

        Range range = sheet.getDataRange();

        List<Integer> integers;

        for (int i = 0;i < range.getNumValues();i++){
            integers.add(random.nextInt());
        }

        range.setValues(range);
        return sheet;
    }

    Sheet generateDeterministicSheet(){
        Sheet sheet = new Sheet();
        sheet.insertRowAfter(0);
        sheet.insertColumnAfter(0);

        sheet.getRange(0,0).setValue(1);
        sheet.getRange(1,0).setValue(2);
        sheet.getRange(0,1).setValue(3);
        sheet.getRange(1,1).setValue(4);
        return sheet;
    }

    void testClear() {
        Sheet sheet = generateASheet();
        int rows = sheet.getMaxRows();
        int columns = sheet.getMaxColumns();
        sheet.clear();
        assertEquals(sheet.getMaxRows(),rows);
        assertEquals(sheet.getMaxColumns(),columns);

        Range range = sheet.getDataRange();

        for (Object o : range.getValues()){
            if (o != null)
                assertEquals(true,false);
        }
    }

    void testClone() {
        Sheet sheet = generateASheet();
        Sheet other = sheet.clone();

        assertEquals(sheet,other);
    }

    void testEquals(){
        Sheet sheet = generateDeterministicSheet();
        Sheet other = generateDeterministicSheet();
        assertEquals(sheet,other);
        sheet.getRange(0,0).setValue(-1);

        boolean equals = sheet.equals(other);
        assertEquals(equals,false);
    }

    void testDeleteColumn() {
        Sheet sheet = new Sheet();
        sheet.insertColumnsAfter(0);
        sheet.getDataRange().setValues(1,2);

        sheet.deleteColumn(0);

        assertEquals(sheet.getMaxColumns(),1);
        assertEquals(sheet.getRange(0,0).getValue(),2);

        sheet.insertColumnsAfter(0);
        sheet.insertColumnsAfter(0);
        sheet.getDataRange().setValues(1,2,3);

        sheet.deleteColumn(2);

        assertEquals(sheet.getMaxColumns(),2);
        assertEquals(sheet.getRange(0,0).getValue(),1);
    }

    void testDeleteColumn1() {

    }

    void testDeleteRow() {

    }

    void testDeleteRows() {

    }

    void testGetDataRange() {

    }

    void testGetMaxColumns() {

    }

    void testGetMaxRows() {

    }

    void testGetName() {

    }

    void testGetRange() {

    }

    void testGetRange1() {

    }

    void testGetRange2() {

    }

    void testGetCell() {

    }

    void testInsertColumnAfter() {

    }

    void testInsertColumnBefore() {

    }

    void testInsertColumnsAfter() {

    }

    void testInsertColumnsBefore() {

    }

    void testInsertRowAfter() {

    }

    void testInsertRowBefore() {

    }

    void testInsertRowsBefore() {

    }

    void testInsertRowsAfter() {

    }
}

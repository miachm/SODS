import com.github.miachm.SODS.spreadsheet.Sheet
import com.github.miachm.SODS.spreadsheet.Range

class SheetTest extends GroovyTestCase {
    private Random random = new Random();

    Sheet generateASheet(){
        Sheet sheet = new Sheet("A");
        sheet.insertRowsAfter(0,99);
        sheet.insertColumnsAfter(0,99);

        Range range = sheet.getDataRange();

        List<Integer> integers = new ArrayList<>();

        for (int i = 0;i < range.getNumValues();i++){
            integers.add(random.nextInt());
        }

        range.setValues(range);
        return sheet;
    }

    Sheet generateDeterministicSheet(){
        Sheet sheet = new Sheet("A");
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
        Sheet sheet = new Sheet("A");
        sheet.insertColumnAfter(0);
        sheet.getDataRange().setValues(1,2);

        sheet.deleteColumn(0);

        assertEquals(sheet.getMaxColumns(),1);
        assertEquals(sheet.getRange(0,0).getValue(),2);

        sheet.insertColumnAfter(0);
        sheet.insertColumnAfter(0);
        sheet.getDataRange().setValues(1,2,3);

        sheet.deleteColumn(2);

        assertEquals(sheet.getMaxColumns(),2);
        assertEquals(sheet.getRange(0,0).getValue(),1);
    }

    void testDeleteColumns() {
        Sheet sheet = new Sheet("A");
        sheet.insertColumnAfter(0);
        sheet.insertColumnAfter(0);
        sheet.getDataRange().setValues(1,2,3);

        sheet.deleteColumns(1,2);

        assertEquals(sheet.getMaxColumns(),1);
        assertEquals(sheet.getRange(0,0).getValue(),1);
    }

    void testDeleteRow() {
        Sheet sheet = new Sheet("A");
        sheet.insertRowAfter(0);
        sheet.getDataRange().setValues(1,2);

        sheet.deleteRow(0);

        assertEquals(sheet.getMaxRows(),1);
        assertEquals(sheet.getRange(0,0).getValue(),2);

        sheet.insertRowAfter(0);
        sheet.insertRowAfter(0);
        sheet.getDataRange().setValues(1,2,3);

        sheet.deleteRow(2);

        assertEquals(sheet.getMaxRows(),2);
        assertEquals(sheet.getRange(0,0).getValue(),1);
    }

    void testDeleteRows() {
        Sheet sheet = new Sheet("A");
        sheet.insertRowAfter(0);
        sheet.insertRowAfter(0);
        sheet.getDataRange().setValues(1,2,3);

        sheet.deleteRows(1,2);

        assertEquals(sheet.getMaxRows(),1);
        assertEquals(sheet.getRange(0,0).getValue(),1);
    }

    void testGetDataRange() {
        Sheet sheet = generateDeterministicSheet();
        Range range = sheet.getDataRange();
        List<Integer> solution = new ArrayList<>();
        solution.add(1);
        solution.add(3);
        solution.add(2);
        solution.add(4);
        assertEquals(range.getValues(),solution);
    }

    void testGetMaxColumns() {
        Sheet sheet = generateDeterministicSheet();
        assertEquals(sheet.getMaxColumns(),2);
    }

    void testGetMaxRows() {
        Sheet sheet = generateDeterministicSheet();
        assertEquals(sheet.getMaxRows(),2);
    }

    void testGetName() {
        Sheet sheet = new Sheet("D");
        assertEquals(sheet.getName(),"D");
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

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

    }

    void testDeleteColumn() {

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

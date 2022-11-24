package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.testng.AssertJUnit.*;

public class SpreadSheetTest {
    private static final Color COLOR0 = new Color("#FF0000");
    private static final Color COLOR1 = new Color("#ffff00");
    private static final Color COLOR2 = new Color("#ff0203");

    private SpreadSheet generateASpreadsheet(){
        SpreadSheet spread = new SpreadSheet();
        spread.appendSheet(new Sheet("A"));
        spread.appendSheet(new Sheet("B"));
        spread.appendSheet(new Sheet("C"));
        return spread;
    }

    private SpreadSheet generateAnUnsortedSpreadsheet(){
        SpreadSheet spread = new SpreadSheet();
        spread.appendSheet(new Sheet("Z"));
        spread.appendSheet(new Sheet("D"));

        final int tam_alphabet = 'Z' - 'A' + 1;
        HashSet<Character> current_letters = new HashSet<>();
        current_letters.add('Z');
        current_letters.add('D');
        Random random = new Random();
        for (int i = 0;i < 10;i++){
            char letter;
            do {
                letter = (char) ('A' + random.nextInt(tam_alphabet));
            } while(current_letters.contains(letter));

            current_letters.add(letter);
            spread.appendSheet(new Sheet(""+letter));
        }

        return spread;
    }

    static <T extends Comparable<? super T>> boolean isSorted(Iterable<T> iterable) {
        Iterator<T> iter = iterable.iterator();
        if (!iter.hasNext()) {
            return true;
        }
        T t = iter.next();
        while (iter.hasNext()) {
            T t2 = iter.next();
            if (t.compareTo(t2) > 0) {
                return false;
            }
            t = t2;
        }
        return true;
    }

    @Test
    public void testClone() throws Exception {
        SpreadSheet spread = new SpreadSheet();
        spread.appendSheet(new Sheet("A"));
        spread.appendSheet(new Sheet("B"));
        SpreadSheet copy = (SpreadSheet) spread.clone();
        assertEquals(spread,copy);
    }

    @Test
    public void testAppendSheet() throws Exception {
        SpreadSheet spread = generateASpreadsheet();
        assertEquals(spread.getNumSheets(),3);
    }

    @Test
    public void testAddSheet() throws Exception {
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

    @Test
    public void testClear() throws Exception {
        SpreadSheet spread = generateASpreadsheet();
        spread.clear();

        assertEquals(spread.getNumSheets(),0);
    }

    @Test
    public void testDeleteSheetPos() throws Exception {
        SpreadSheet spread = generateASpreadsheet();
        spread.appendSheet(new Sheet("D"));
        spread.deleteSheet(1);
        spread.deleteSheet(2);
        assertEquals(spread.getNumSheets(),2);

        List<Sheet> sheets = spread.getSheets();

        assertEquals(sheets.get(0).getName(),"A");
        assertEquals(sheets.get(1).getName(),"C");
    }

    @Test
    public void testDeleteSheetName() throws Exception {
        SpreadSheet spread = generateASpreadsheet();
        spread.appendSheet(new Sheet("D"));
        spread.deleteSheet("B");
        spread.deleteSheet("D");
        assertEquals(spread.getNumSheets(),2);

        List<Sheet> sheets = spread.getSheets();

        assertEquals(sheets.get(0).getName(),"A");
        assertEquals(sheets.get(1).getName(),"C");
    }

    @Test
    public void testDeleteSheet() throws Exception {
        SpreadSheet spread = generateASpreadsheet();
        Sheet[] sheets = new Sheet[2];
        sheets[0] = new Sheet("D");
        sheets[1] = new Sheet("E");

        for (Sheet sheet: sheets)
            spread.appendSheet(sheet);

        for (Sheet sheet: sheets)
            spread.deleteSheet(sheet);

        assertEquals(spread.getNumSheets(),3);

        List<Sheet> sheets_list = spread.getSheets();

        assertEquals(sheets_list.get(0).getName(),"A");
        assertEquals(sheets_list.get(1).getName(),"B");
        assertEquals(sheets_list.get(2).getName(),"C");
    }

    @Test
    public void testGetSheets() throws Exception {
        SpreadSheet spread = generateASpreadsheet();
        List<Sheet> sheets = spread.getSheets();
        assertEquals(sheets.size(),spread.getNumSheets());

        assertEquals(sheets.get(0).getName(),"A");
        assertEquals(sheets.get(1).getName(),"B");
        assertEquals(sheets.get(2).getName(),"C");
    }

    @Test
    public void testGetNumSheets() throws Exception {
        SpreadSheet spread = generateASpreadsheet();
        assertEquals(spread.getNumSheets(),3);
    }

    @Test
    public void  testGetSheetString() throws Exception {
        SpreadSheet spread = generateASpreadsheet();
        Sheet sheet = spread.getSheet("B");
        assertNotNull(sheet);
        assertEquals(sheet.getName(),"B");
        sheet = spread.getSheet("G");
        assertNull(sheet);
    }

    @Test
    public void  testGetSheetInt() throws Exception {
        SpreadSheet spread = generateASpreadsheet();
        Sheet sheet = spread.getSheet(1);
        assertNotNull(sheet);
        assertEquals(sheet.getName(),"B");

        try {
            sheet = spread.getSheet(5);
            fail();
        }
        catch (IndexOutOfBoundsException e)
        {
            try {
                sheet = spread.getSheet(-1);
                fail();
            }
            catch (IndexOutOfBoundsException e2) {}
        }
    }

    @Test
    public void testLoad() throws Exception {
        SpreadSheet spread = new SpreadSheet(new File("resources/CAS.ods"));
        assertEquals(spread.getNumSheets(),3);

        Sheet sheet = spread.getSheet(0);
        Range dataRange = sheet.getDataRange();

        Range group[] = dataRange.getMergedCells();
        assertEquals(group.length, 0);

        Object[][] arr = dataRange.getValues();
        assertEquals(arr[0][0],"A");
        assertEquals(arr[0][1],"B");
        assertEquals(arr[0][2],"C");
        assertEquals(arr[0][3],"D");
        assertEquals(arr[0][4],"E");

        assertEquals(arr[1][0],"F");
        assertEquals(arr[1][1],"G");
        assertEquals(arr[1][2],"  H  ASF ASG ");
        assertEquals(arr[1][3],"I");
        assertEquals(arr[1][4],"J");

        assertEquals(arr[2][0],"K");
        assertEquals(arr[2][1],"K");
        assertEquals(arr[2][2]," Fas K\n" +
                "Afsafasf\n" +
                "Safasgasg\n");
        assertEquals(arr[2][3],"L");
        assertEquals(arr[2][4],"M");

        Style[][] styles = dataRange.getStyles();
        
        assertFalse(styles[0][0].isBold());
        assertTrue(styles[0][1].isBold());
        assertFalse(styles[0][2].isBold());
        assertFalse(styles[0][3].isBold());
        assertTrue(styles[0][4].isBold());
        assertTrue(styles[1][0].isBold());
        assertFalse(styles[1][1].isBold());
        assertTrue(styles[1][2].isBold());
        assertFalse(styles[1][3].isBold());
        assertFalse(styles[1][4].isBold());
        assertFalse(styles[2][0].isBold());
        assertFalse(styles[2][1].isBold());
        assertFalse(styles[2][2].isBold());
        assertFalse(styles[2][3].isBold());
        assertFalse(styles[2][4].isBold());

        assertFalse(styles[0][0].isItalic());
        assertFalse(styles[0][1].isItalic());
        assertTrue(styles[0][2].isItalic());
        assertFalse(styles[0][3].isItalic());
        assertTrue(styles[0][4].isItalic());
        assertFalse(styles[1][0].isItalic());
        assertTrue(styles[1][1].isItalic());
        assertTrue(styles[1][2].isItalic());
        assertFalse(styles[1][3].isItalic());
        assertFalse(styles[1][4].isItalic());
        assertFalse(styles[2][0].isItalic());
        assertFalse(styles[2][1].isItalic());
        assertFalse(styles[2][2].isItalic());
        assertFalse(styles[2][3].isItalic());
        assertFalse(styles[2][4].isItalic());

        assertFalse(styles[0][0].isUnderline());
        assertFalse(styles[0][1].isUnderline());
        assertFalse(styles[0][2].isUnderline());
        assertTrue(styles[0][3].isUnderline());
        assertFalse(styles[0][4].isUnderline());
        assertTrue(styles[1][0].isUnderline());
        assertTrue(styles[1][1].isUnderline());
        assertTrue(styles[1][2].isUnderline());
        assertTrue(styles[1][3].isUnderline());
        assertFalse(styles[1][4].isUnderline());
        assertFalse(styles[2][0].isUnderline());
        assertFalse(styles[2][1].isUnderline());
        assertFalse(styles[2][2].isUnderline());
        assertFalse(styles[2][3].isUnderline());
        assertFalse(styles[2][4].isUnderline());

        assertEquals(styles[0][0].getFontSize(), -1);
        assertEquals(styles[0][1].getFontSize(), -1);
        assertEquals(styles[0][2].getFontSize(), -1);
        assertEquals(styles[0][3].getFontSize(), -1);
        assertEquals(styles[0][4].getFontSize(), -1);
        assertEquals(styles[1][0].getFontSize(), -1);
        assertEquals(styles[1][1].getFontSize(), -1);
        assertEquals(styles[1][2].getFontSize(), -1);
        assertEquals(styles[1][3].getFontSize(), -1);
        assertEquals(styles[1][4].getFontSize(), -1);
        assertEquals(styles[2][0].getFontSize(), -1);
        assertEquals(styles[2][1].getFontSize(), -1);
        assertEquals(styles[2][2].getFontSize(), -1);
        assertEquals(styles[2][3].getFontSize(), 26);
        assertEquals(styles[2][4].getFontSize(), -1);

        assertEquals(styles[0][0].getFontColor(), null);
        assertEquals(styles[0][1].getFontColor(), null);
        assertEquals(styles[0][2].getFontColor(), null);
        assertEquals(styles[0][3].getFontColor(), null);
        assertEquals(styles[0][4].getFontColor(), null);
        assertEquals(styles[1][0].getFontColor(), null);
        assertEquals(styles[1][1].getFontColor(), null);
        assertEquals(styles[1][2].getFontColor(), null);
        assertEquals(styles[1][3].getFontColor(), COLOR0);
        assertEquals(styles[1][4].getFontColor(), null);
        assertEquals(styles[2][0].getFontColor(), null);
        assertEquals(styles[2][1].getFontColor(), null);
        assertEquals(styles[2][2].getFontColor(), null);
        assertEquals(styles[2][3].getFontColor(), null);
        assertEquals(styles[2][4].getFontColor(), null);

        assertEquals(styles[0][0].getBackgroundColor(), null);
        assertEquals(styles[0][1].getBackgroundColor(), null);
        assertEquals(styles[0][2].getBackgroundColor(), null);
        assertEquals(styles[0][3].getBackgroundColor(), null);
        assertEquals(styles[0][4].getBackgroundColor(), null);
        assertEquals(styles[1][0].getBackgroundColor(), null);
        assertEquals(styles[1][1].getBackgroundColor(), null);
        assertEquals(styles[1][2].getBackgroundColor(), null);
        assertEquals(styles[1][3].getBackgroundColor(), null);
        assertEquals(styles[1][4].getBackgroundColor(), COLOR1);
        assertEquals(styles[2][0].getFontColor(), null);
        assertEquals(styles[2][1].getFontColor(), null);
        assertEquals(styles[2][2].getFontColor(), null);
        assertEquals(styles[2][3].getFontColor(), null);
        assertEquals(styles[2][4].getFontColor(), null);

        assertEquals(sheet.getColumnWidth(0), 22.58, 0.1);
        assertEquals(sheet.getColumnWidth(1), 7.11, 0.1);
        assertEquals(sheet.getColumnWidth(2), 43.06, 0.1);
        assertEquals(sheet.getColumnWidth(3), 22.58, 0.1);
        assertEquals(sheet.getColumnWidth(4), 38.31, 0.1);

        OfficeAnnotation annotations[][] = dataRange.getAnnotations();

        assertEquals(annotations[0][0], null);
        assertEquals(annotations[0][1], null);
        assertEquals(annotations[0][2], null);
        assertEquals(annotations[0][3], null);
        assertEquals(annotations[0][4], null);
        assertEquals(annotations[1][0], null);
        assertEquals(annotations[1][1], null);
        assertEquals(annotations[1][2], new OfficeAnnotation("miau", LocalDateTime.parse("2020-06-14T00:00:00")));
        assertEquals(annotations[1][3], null);
        assertEquals(annotations[1][4], null);
        assertEquals(annotations[2][0], null);
        assertEquals(annotations[2][1], null);
        assertEquals(annotations[2][2], null);
        assertEquals(annotations[2][3], new OfficeAnnotation("Test\n\naa", LocalDateTime.parse("2020-06-14T00:00:00")));
        assertEquals(annotations[2][4], null);

        sheet = spread.getSheet(1);
        Range range = sheet.getDataRange();
        arr = range.getValues();
        assertEquals(arr[0][0],1.0);
        assertEquals(arr[1][0],4.0);
        assertEquals(arr[2][0], null);
        assertEquals(arr[0][1],2.0);
        assertEquals(arr[1][1],2.0);
        assertEquals(arr[2][1],null);
        assertEquals(arr[0][2],2.0);
        assertEquals(arr[1][2],2.0);
        assertEquals(arr[2][2],null);
        assertEquals(arr[0][3],4.0);
        assertEquals(arr[1][3],7.0);
        assertEquals(arr[2][3],18.0);

        String formulas[][] = range.getFormulas();
        assertEquals(formulas[0][0],null);
        assertEquals(formulas[1][0],null);
        assertEquals(formulas[0][1],null);
        assertEquals(formulas[1][1],null);
        assertEquals(formulas[0][2],null);
        assertEquals(formulas[1][2],null);
        assertEquals(formulas[2][2],null);
        assertEquals(formulas[0][3],null);
        assertEquals(formulas[1][3],null);
        assertEquals(formulas[2][3],"=SUM(A1:C2)+D1+D2");

        group = range.getMergedCells();
        assertEquals(group.length, 1);
        assertEquals(group[0].getRow(), 0);
        assertEquals(group[0].getColumn(), 1);
        assertEquals(group[0].getNumRows(), 2);
        assertEquals(group[0].getNumColumns(), 2);

        assertEquals(sheet.getColumnWidth(0), 22.58);
        assertEquals(sheet.getColumnWidth(1), 22.58);
    }

    @Test
    public void testSetSheet() throws Exception {
        SpreadSheet spread = generateASpreadsheet();
        spread.setSheet(new Sheet("E"),1);

        List<Sheet> sheets = spread.getSheets();
        assertEquals(sheets.size(),3);

        assertEquals(sheets.get(0).getName(),"A");
        assertEquals(sheets.get(1).getName(),"E");
        assertEquals(sheets.get(2).getName(),"C");
    }

    @Test
    public void testSave() throws Exception {
        SpreadSheet spread = generateASpreadsheet();

        Sheet sheet = spread.getSheet(0);
        Range dataRange = sheet.getDataRange();
        dataRange.setValue(1.0);

        sheet = spread.getSheet(1);
        dataRange = sheet.getDataRange();
        dataRange.setValue(" 1\nadasas ");// Testing leading spaces
        dataRange.setFontBold(true);
        dataRange.setFontUnderline(true);
        dataRange.setFontColors(new Color("#43a2f5"));
        dataRange.setBackgroundColor(COLOR2);
        dataRange.setFontSize(18);
        sheet.setRowHeight(0, 40.0);
        sheet.setColumnWidth(0, 30.0);

        dataRange = spread.getSheet(2).getDataRange();
        dataRange.setValue(1.0);
        dataRange.setFontItalic(true);

        sheet = spread.getSheet(0);
        sheet.appendRow();
        sheet.appendRows(3);
        sheet.getCell(3,0).setFormula("=SUM(A1)");
        sheet.setColumnWidth(0, 42.23);
        sheet.setRowHeight(1, 74.14);
        sheet.appendColumns(2);

        Range range = sheet.getRange(0,0,2,2);
        range.merge();

        range = sheet.getRange(3,0,1,1);
        range.setAnnotation(new OfficeAnnotation("Test\nCas", LocalDateTime.of(2019, 3, 2, 3, 2)));
        range.setValue(new OfficePercentage(0.3));

        range = sheet.getRange(4,0,1,1);
        range.setAnnotation(new OfficeAnnotation("Test\nCas", null));
        range.setValue(new OfficeCurrency(Currency.getInstance(Locale.FRANCE), 5.0));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        spread.save(out);
        spread.save(new File("target/example.ods"));

        byte buff[] = out.toByteArray();
        SpreadSheet loaded = new SpreadSheet(new ByteArrayInputStream(buff));

        assertEquals(spread,loaded);
    }

    @Test
    public void testSortSheets() throws Exception {
        SpreadSheet spread = generateAnUnsortedSpreadsheet();
        spread.sortSheets();
        assertTrue(isSorted(spread.getSheets()));
    }

    @Test
    public void testSortSheetsComparator() throws Exception {
        SpreadSheet spread = generateAnUnsortedSpreadsheet();
        spread.sortSheets(Comparator.reverseOrder());
        List<Sheet> sheets = new ArrayList<>(spread.getSheets());
        Collections.reverse(sheets);
        assertTrue(isSorted(sheets));
    }

    @Test
    public void testEmptyValues() throws IOException {
        SpreadSheet spread = new SpreadSheet(new File("resources/nullvalues.ods"));
        Sheet sheet = spread.getSheet(0);
        Object values[][] = sheet.getDataRange().getValues();

        Object result[][] = {{"id", "lon", "lat", "nom_lieu", "elephant_mer", "baleine", "cachalot", "globicephal_noir", "description"},
                {1.0, "-72.063440", "41.286780", "New london", null, null, null, null, "Départ le 15 juillet 1859."}
        };

        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                assertEquals(result[i][j], values[i][j]);
            }
        }
    }

    @Test
    public void testFloatingPoint() throws IOException {
        SpreadSheet spread = new SpreadSheet(new File("resources/floatingpoint.ods"));
        // No crash? It's green then
    }
    @Test
    public void testMissingColumnTag() throws IOException {
        SpreadSheet spread = new SpreadSheet(new File("resources/missingColumnTag.ods"));
        // No crash? It's green then
    }
    
    @Test
    public void testReadContent() throws IOException {
        final String[] line1 = new String[] { "content", "content", null, "content" };
        final String[] line2 = new String[] { "content", "content", "content", };
        final String[] line3 = new String[] { "content", null, null, null };
        final String[][] expected = new String[][] { line1, line2, line3 };
        
        final SpreadSheet spreadsheet = new SpreadSheet(new File("resources/cellEmptyValues.ods"));
        final Sheet sheet = spreadsheet.getSheet(0);
        final Range dataRange = sheet.getDataRange();
        final Object[][] values = dataRange.getValues();

        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected[i].length; j++) {
                assertEquals(expected[i][j], values[i][j], "VALUE: '" + values[i][j] + "' != '" + expected[i][j] + "'. Reading line " + (i + 1) + ", column " + (j + 1));
            }
        }
    }
}

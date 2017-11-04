package com.github.miachm.SODS.spreadsheet;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

import static org.testng.AssertJUnit.*;

public class SpreadSheetTest {

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
        Sheet sheet = new Sheet("A");
        spread.appendSheet(sheet);
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

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        spread.save(out);

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

}
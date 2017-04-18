package com.github.miachm.SODS.spreadsheet

/**
 * Created by MiguelPC on 18/04/2017.
 */
class SpreadSheetTest extends GroovyTestCase {

    private SpreadSheet generateASpreadsheet(){
        SpreadSheet spread = new SpreadSheet()
        spread.appendSheet(new Sheet("A"))
        spread.appendSheet(new Sheet("B"))
        spread.appendSheet(new Sheet("C"))
        return spread
    }

    private SpreadSheet generateAnUnsortedSpreadsheet(){
        SpreadSheet spread = new SpreadSheet()
        spread.appendSheet(new Sheet("Z"))
        spread.appendSheet(new Sheet("D"))

        char aChar = 'A'
        char zchar = 'Z'
        final int tam_alphabet = zchar-aChar+1
        HashSet<Character> current_letters = new HashSet<>()
        current_letters.add('Z')
        current_letters.add('D')
        Random random = new Random()
        for (int i = 0;i < 10;i++){
            char letter = 'Z'
            while(current_letters.contains(letter)){
                letter = aChar+random.nextInt(tam_alphabet)
            }
            current_letters.add(letter)
            spread.appendSheet(new Sheet(""+letter))
        }

        return spread
    }

    static <T extends Comparable<? super T>> boolean isSorted(Iterable<T> iterable) {
            Iterator<T> iter = iterable.iterator()
        if (!iter.hasNext()) {
                return true
        }
        T t = iter.next()
        while (iter.hasNext()) {
                T t2 = iter.next()
            if (t.compareTo(t2) > 0) {
                    return false
            }
                t = t2
        }
            return true
    }

    void testClone() {
        SpreadSheet spread = new SpreadSheet()
        Sheet sheet = new Sheet("A")
        sheet.insertColumnsAfter(0,10)
        sheet.insertRowsAfter(0,10)
        sheet.getDataRange().setValues(Collections.nCopies(1,"A"))

        spread.appendSheet(sheet)
        spread.appendSheet(new Sheet("B"))

        SpreadSheet copy = spread.clone()

        assertEquals(spread,copy)
    }

    void testAppendSheet() {
        SpreadSheet spread = generateASpreadsheet()
        assertEquals(spread.getNumSheets(),3)
    }

    void testAddSheet() {
        SpreadSheet spread = new SpreadSheet()
        spread.appendSheet(new Sheet("A"))
        spread.appendSheet(new Sheet("C"))
        spread.appendSheet(new Sheet("E"))
        spread.addSheet(new Sheet("B"),1)
        spread.addSheet(new Sheet("D"),3)
        assertEquals(spread.getNumSheets(),5)
        List<String> list = new ArrayList<>()
        Collections.addAll(list,"A","B","C","D","E")

        List<Sheet> sheets = spread.getSheets()
        for (int i = 0;i < sheets.size();i++){
            assertEquals(list.get(i),sheets.get(i).getName())
        }
    }

    void testClear() {
        SpreadSheet spread = generateASpreadsheet()
        spread.clear()

        assertEquals(spread.getNumSheets(),0)
    }

    void testDeleteSheetPos() {
        SpreadSheet spread = generateASpreadsheet()
        spread.appendSheet(new Sheet("D"))
        spread.deleteSheet(1)
        spread.deleteSheet(2)
        assertEquals(spread.getNumSheets(),2)

        List<Sheet> sheets = spread.getSheets()

        assertEquals(sheets.get(0).getName(),"A")
        assertEquals(sheets.get(1).getName(),"C")
    }

    void testDeleteSheetName() {
        SpreadSheet spread = generateASpreadsheet()
        spread.appendSheet(new Sheet("D"))
        spread.deleteSheet("B")
        spread.deleteSheet("D")
        assertEquals(spread.getNumSheets(),2)

        List<Sheet> sheets = spread.getSheets()

        assertEquals(sheets.get(0).getName(),"A")
        assertEquals(sheets.get(1).getName(),"C")
    }

    void testDeleteSheet() {
        SpreadSheet spread = generateASpreadsheet()
        Sheet[] sheets = new Sheet[2]
        sheets[0] = new Sheet("D")
        sheets[1] = new Sheet("E")

        for (Sheet sheet: sheets)
            spread.appendSheet(sheet)

        for (Sheet sheet: sheets)
            spread.deleteSheet(sheet)

        assertEquals(spread.getNumSheets(),3)

        List<Sheet> sheets_list = spread.getSheets()

        assertEquals(sheets_list.get(0).getName(),"A")
        assertEquals(sheets_list.get(1).getName(),"B")
        assertEquals(sheets_list.get(2).getName(),"C")
    }

    void testGetSheets() {
        SpreadSheet spread = generateASpreadsheet()
        List<Sheet> sheets = spread.getSheets()
        assertEquals(sheets.size(),spread.getNumSheets())

        assertEquals(sheets.get(0).getName(),"A")
        assertEquals(sheets.get(1).getName(),"B")
        assertEquals(sheets.get(2).getName(),"C")
    }

    void testSetSheet() {
        SpreadSheet spread = generateASpreadsheet()
        spread.setSheet(new Sheet("E"),1)

        List<Sheet> sheets = spread.getSheets()
        assertEquals(sheets.size(),3)

        assertEquals(sheets.get(0).getName(),"A")
        assertEquals(sheets.get(1).getName(),"E")
        assertEquals(sheets.get(2).getName(),"C")
    }

    void testSave() {
        assertEquals(true,false)
    }

    void testSave1() {
        assertEquals(true,false)
    }

    void testSortSheets() {
        SpreadSheet spread = generateAnUnsortedSpreadsheet()
        spread.sortSheets()
        assertEquals(isSorted(spread.getSheets()),true)
    }

    void testSortSheetsCustomComparator() {
        SpreadSheet spread = generateAnUnsortedSpreadsheet()
        spread.sortSheets(new Comparator<Sheet>() {
            @Override
            int compare(Sheet o1, Sheet o2) {
                return o2.getName() <=> o1.getName()
            }
        })
        assertEquals(isSorted(spread.getSheets()),true)
    }
}

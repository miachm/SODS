package com.github.miachm.sods;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.fail;

public class A1NotationTest {

    private Sheet generateSheet(int size)
    {
        Sheet sheet = new Sheet("A", size, size);

        int cnt = 0;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                sheet.getRange(i, j).setValue(cnt++);

        return sheet;
    }

    @Test
    public void testSingleCell() {
        final int SIZE = 800;
        Sheet sheet = generateSheet(SIZE);

        int cnt = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                String alphaPart = toAlphapart(j);

                Object value = sheet.getRange("" + alphaPart + (i+1)).getValue();
                assertEquals(value, cnt++);
            }
        }
    }

    @Test
    public void testOutOfRange() {
        final int SIZE = 3;
        Sheet sheet = generateSheet(SIZE);

        int numRow = 1;
        String alphaPart = toAlphapart(3);

        try
        {
            sheet.getRange("" + alphaPart + (numRow));
            fail();
        }
        catch (IndexOutOfBoundsException e)
        {}


        alphaPart = toAlphapart(2);
        numRow = 4;
        try
        {
            sheet.getRange("" + alphaPart + (numRow));
            fail();
        }
        catch (IndexOutOfBoundsException e)
        {}
    }

    @Test
    public void testMultipleCell() {
        final int SIZE = 25;
        Sheet sheet = generateSheet(SIZE);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                String alphaPart = toAlphapart(j);
                String ori = "" + alphaPart + (i + 1);
                for (int l = 0; l < SIZE; l++) {
                    String destPart = toAlphapart(l);
                    for (int m = 0; m < SIZE; m++) {

                        String dest = "" + destPart + (m+1);
                        Range range = sheet.getRange(ori + ":" + dest);
                        assertEquals(range.getRow(), Math.min(i, m));
                        assertEquals(range.getColumn(), Math.min(j, l));
                        assertEquals(range.getLastRow(), Math.max(i, m));
                        assertEquals(range.getLastColumn(), Math.max(j, l));
                    }
                }
            }
        }
    }

    private String toAlphapart(int num)
    {
        int parts = 1;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts; i++) {
            char letter = 'A' - 1;
            result.append(letter);
        }

        int index = parts - 1;
        while (num >= 0)
        {
            char letter = result.charAt(index);
            letter++;
            if (letter > 'Z') {
                letter = 'A';
                index--;
                char aux = ' ';
                do {
                    if (index < 0) {
                        char myLetter = 'A' - 1;
                        result.insert(0, myLetter);
                        index++;
                        parts++;
                    }
                    aux = result.charAt(index);
                    aux++;
                    if (aux > 'Z')
                        result.setCharAt(index, 'A');
                    else
                        result.setCharAt(index, aux);
                    index--;
                }
                while (aux > 'Z');
                index = parts - 1;
            }

            result.setCharAt(index, letter);
            num--;
        }

        return result.toString();
    }
}

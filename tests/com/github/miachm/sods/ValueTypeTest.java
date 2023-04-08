package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Locale;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class ValueTypeTest {

    private Sheet saveAndLoad(Sheet sheet)
    {
        SpreadSheet spreadSheet = new SpreadSheet();
        spreadSheet.appendSheet(sheet);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            spreadSheet.save(outputStream);
        } catch (IOException e) {
            fail(e.getMessage());
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        SpreadSheet newSpreadSheet = null;
        try {
            newSpreadSheet = new SpreadSheet(inputStream);
        } catch (IOException e) {
            fail(e.getMessage());
        }

        return newSpreadSheet.getSheet(0);
    }

    @Test
    public void testPrimitiveTypes()
    {
        String text = "EXAMPLE";
        double numDouble = 3.149265363464;
        boolean booleanValue = true;

        Sheet sheet = new Sheet("A", 1, 4);
        sheet.getDataRange().setValues(null, text, numDouble, booleanValue);
        sheet = saveAndLoad(sheet);

        assertEquals(sheet.getRange(0, 0).getValue(), null);
        assertEquals(sheet.getRange(0, 1).getValue(), text);
        assertEquals(sheet.getRange(0, 2).getValue(), numDouble);
        assertEquals(sheet.getRange(0, 3).getValue(), booleanValue);
    }

    @Test
    public void testUnicodeCharacters()
    {
        // Text not in the Basic Multilingual Plane (BMP) is encoded using a surrogate pair
        // https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.1
        String text = "\uD83D\uDE00"; // 😀

        Sheet sheet = new Sheet("A", 1, 1);
        sheet.getDataRange().setValues(text);
        sheet = saveAndLoad(sheet);

        assertEquals(sheet.getRange(0, 0).getValue(), text);
    }

    @Test
    public void testCurrency()
    {
        OfficeCurrency canada = new OfficeCurrency(Currency.getInstance(Locale.CANADA), 30.0);
        OfficeCurrency eur = new OfficeCurrency(Currency.getInstance("EUR"), 2.0);

        Sheet sheet = new Sheet("A", 1, 2);
        sheet.getDataRange().setValues(canada, eur);
        sheet = saveAndLoad(sheet);

        assertEquals(sheet.getRange(0, 0).getValue(), canada);
        assertEquals(sheet.getRange(0, 1).getValue(), eur);
    }

    @Test
    public void testDate()
    {
        LocalDate now = LocalDate.now();
        LocalDate date = LocalDate.of(2002,3,3);
        LocalDateTime datetime = LocalDateTime.of(2006, 6, 6 , 14, 12, 32);

        Sheet sheet = new Sheet("A", 1, 3);
        sheet.getDataRange().setValues(now, date, datetime);
        sheet = saveAndLoad(sheet);

        assertEquals(sheet.getRange(0, 0).getValue(), now);
        assertEquals(sheet.getRange(0, 1).getValue(), date);
        assertEquals(sheet.getRange(0, 2).getValue(), datetime);
    }

    @Test
    public void testPercentage()
    {
        OfficePercentage percentage = new OfficePercentage(3.2);
        OfficePercentage otherPercentage = new OfficePercentage("40%");

        Sheet sheet = new Sheet("A", 1, 2);
        sheet.getDataRange().setValues(percentage, otherPercentage);
        sheet = saveAndLoad(sheet);

        assertEquals(sheet.getRange(0, 0).getValue(), percentage);
        assertEquals(sheet.getRange(0, 1).getValue(), otherPercentage);
    }

    @Test
    public void testDateOffice() throws IOException {
        SpreadSheet spread = new SpreadSheet(new File("resources/TestDate.ods"));
        spread.save(new ByteArrayOutputStream());
    }
}

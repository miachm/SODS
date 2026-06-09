package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.testng.AssertJUnit.*;

public class FontFamilyTest {

    private Sheet saveAndLoad(Sheet sheet) throws IOException {
        SpreadSheet spreadSheet = new SpreadSheet();
        spreadSheet.appendSheet(sheet);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        spreadSheet.save(out);
        SpreadSheet loaded = new SpreadSheet(new ByteArrayInputStream(out.toByteArray()));
        return loaded.getSheet(0);
    }

    @Test
    public void testDefaultIsNull() {
        Style style = new Style();
        assertNull(style.getFontFamily());
    }

    @Test
    public void testSetAndGet() {
        Style style = new Style();
        style.setFontFamily("Arial");
        assertEquals("Arial", style.getFontFamily());
    }

    @Test
    public void testClearWithNull() {
        Style style = new Style();
        style.setFontFamily("Arial");
        style.setFontFamily(null);
        assertNull(style.getFontFamily());
        assertTrue(style.isDefault());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testEmptyNameThrows() {
        new Style().setFontFamily("");
    }

    @Test
    public void testEquality() {
        Style a = new Style();
        Style b = new Style();
        a.setFontFamily("Arial");
        b.setFontFamily("Arial");
        assertEquals(a, b);
    }

    @Test
    public void testInequalityDifferentFamily() {
        Style a = new Style();
        Style b = new Style();
        a.setFontFamily("Arial");
        b.setFontFamily("Times New Roman");
        assertNotSame(a, b);
        assertFalse(a.equals(b));
    }

    @Test
    public void testInequalityVsNoFamily() {
        Style a = new Style();
        Style b = new Style();
        a.setFontFamily("Arial");
        assertFalse(a.equals(b));
    }

    @Test
    public void testRoundTrip() throws IOException {
        Sheet sheet = new Sheet("A", 3, 3);
        Style style = new Style();
        style.setFontFamily("Courier New");
        sheet.getRange(0, 0).setStyle(style);

        Sheet loaded = saveAndLoad(sheet);
        Style loadedStyle = loaded.getRange(0, 0).getStyle();
        assertEquals("Courier New", loadedStyle.getFontFamily());
    }

    @Test
    public void testRoundTripNoFontFamily() throws IOException {
        Sheet sheet = new Sheet("A", 3, 3);
        Style style = new Style();
        style.setBold(true);
        sheet.getRange(0, 0).setStyle(style);

        Sheet loaded = saveAndLoad(sheet);
        assertNull(loaded.getRange(0, 0).getStyle().getFontFamily());
    }

    @Test
    public void testRoundTripMultipleCells() throws IOException {
        Sheet sheet = new Sheet("A", 3, 3);

        Style arial = new Style();
        arial.setFontFamily("Arial");

        Style times = new Style();
        times.setFontFamily("Times New Roman");

        sheet.getRange(0, 0).setStyle(arial);
        sheet.getRange(1, 0).setStyle(times);

        Sheet loaded = saveAndLoad(sheet);
        assertEquals("Arial", loaded.getRange(0, 0).getStyle().getFontFamily());
        assertEquals("Times New Roman", loaded.getRange(1, 0).getStyle().getFontFamily());
        assertNull(loaded.getRange(2, 0).getStyle().getFontFamily());
    }

    @Test
    public void testCssStyles() {
        Style style = new Style();
        style.setFontFamily("Verdana");
        assertEquals("Verdana", style.getCssStyles().get("font-family"));
    }

    @Test
    public void testCssStylesAbsentWhenNull() {
        Style style = new Style();
        assertFalse(style.getCssStyles().containsKey("font-family"));
    }
}

package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.testng.AssertJUnit.*;

public class TabColorTest {

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
        Sheet sheet = new Sheet("A");
        assertNull(sheet.getTabColor());
    }

    @Test
    public void testSetAndGet() {
        Sheet sheet = new Sheet("A");
        Color red = new Color(255, 0, 0);
        sheet.setTabColor(red);
        assertEquals(red, sheet.getTabColor());
    }

    @Test
    public void testClearWithNull() {
        Sheet sheet = new Sheet("A");
        sheet.setTabColor(new Color(0, 128, 0));
        sheet.setTabColor(null);
        assertNull(sheet.getTabColor());
    }

    @Test
    public void testRoundTrip() throws IOException {
        Sheet sheet = new Sheet("A", 3, 3);
        Color blue = new Color(0, 0, 255);
        sheet.setTabColor(blue);

        Sheet loaded = saveAndLoad(sheet);
        assertNotNull(loaded.getTabColor());
        assertEquals(blue, loaded.getTabColor());
    }

    @Test
    public void testRoundTripNoColor() throws IOException {
        Sheet sheet = new Sheet("A", 3, 3);
        Sheet loaded = saveAndLoad(sheet);
        assertNull(loaded.getTabColor());
    }

    @Test
    public void testMultipleSheetsIndependent() throws IOException {
        SpreadSheet spreadSheet = new SpreadSheet();
        Sheet s1 = new Sheet("Red", 2, 2);
        Sheet s2 = new Sheet("None", 2, 2);
        Sheet s3 = new Sheet("Green", 2, 2);

        s1.setTabColor(new Color(255, 0, 0));
        s3.setTabColor(new Color(0, 255, 0));

        spreadSheet.appendSheet(s1);
        spreadSheet.appendSheet(s2);
        spreadSheet.appendSheet(s3);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        spreadSheet.save(out);
        SpreadSheet loaded = new SpreadSheet(new ByteArrayInputStream(out.toByteArray()));

        assertEquals(new Color(255, 0, 0), loaded.getSheet(0).getTabColor());
        assertNull(loaded.getSheet(1).getTabColor());
        assertEquals(new Color(0, 255, 0), loaded.getSheet(2).getTabColor());
    }

    @Test
    public void testLoadingTabColor() throws IOException {
        SpreadSheet spread = new SpreadSheet(new File("resources/tabcolour.ods"));
        assertEquals(spread.getSheet(0).getTabColor(), new Color(255,0,0));
        assertEquals(spread.getSheet(1).getTabColor(), new Color("#77bc65"));
        assertNull(spread.getSheet(2).getTabColor());
    }
}

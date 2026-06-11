package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.testng.AssertJUnit.*;

public class FreezePanesTest {

    private SpreadSheet saveAndLoad(SpreadSheet spreadSheet) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        spreadSheet.save(out);
        return new SpreadSheet(new ByteArrayInputStream(out.toByteArray()));
    }

    // --- API ---

    @Test
    public void testDefaultsAreZero() {
        Sheet sheet = new Sheet("A", 5, 5);
        assertEquals(0, sheet.getFrozenRows());
        assertEquals(0, sheet.getFrozenColumns());
    }

    @Test
    public void testFreezeRows() {
        Sheet sheet = new Sheet("A", 10, 5);
        sheet.freezeRows(3);
        assertEquals(3, sheet.getFrozenRows());
        assertEquals(0, sheet.getFrozenColumns());
    }

    @Test
    public void testFreezeColumns() {
        Sheet sheet = new Sheet("A", 5, 10);
        sheet.freezeColumns(2);
        assertEquals(0, sheet.getFrozenRows());
        assertEquals(2, sheet.getFrozenColumns());
    }

    @Test
    public void testFreezeBoth() {
        Sheet sheet = new Sheet("A", 10, 10);
        sheet.freezeRows(1);
        sheet.freezeColumns(3);
        assertEquals(1, sheet.getFrozenRows());
        assertEquals(3, sheet.getFrozenColumns());
    }

    @Test
    public void testUnfreezeByZero() {
        Sheet sheet = new Sheet("A", 10, 10);
        sheet.freezeRows(3);
        sheet.freezeColumns(2);
        sheet.freezeRows(0);
        sheet.freezeColumns(0);
        assertEquals(0, sheet.getFrozenRows());
        assertEquals(0, sheet.getFrozenColumns());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNegativeFrozenRowsThrows() {
        new Sheet("A", 5, 5).freezeRows(-1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNegativeFrozenColumnsThrows() {
        new Sheet("A", 5, 5).freezeColumns(-1);
    }

    // --- Round-trip ---

    @Test
    public void testRoundTripFrozenRows() throws IOException {
        SpreadSheet spread = new SpreadSheet();
        Sheet sheet = new Sheet("Data", 20, 5);
        sheet.freezeRows(2);
        spread.appendSheet(sheet);

        SpreadSheet loaded = saveAndLoad(spread);
        assertEquals(2, loaded.getSheet(0).getFrozenRows());
        assertEquals(0, loaded.getSheet(0).getFrozenColumns());
    }

    @Test
    public void testRoundTripFrozenColumns() throws IOException {
        SpreadSheet spread = new SpreadSheet();
        Sheet sheet = new Sheet("Data", 5, 20);
        sheet.freezeColumns(3);
        spread.appendSheet(sheet);

        SpreadSheet loaded = saveAndLoad(spread);
        assertEquals(0, loaded.getSheet(0).getFrozenRows());
        assertEquals(3, loaded.getSheet(0).getFrozenColumns());
    }

    @Test
    public void testRoundTripFreezeBoth() throws IOException {
        SpreadSheet spread = new SpreadSheet();
        Sheet sheet = new Sheet("Data", 20, 20);
        sheet.freezeRows(1);
        sheet.freezeColumns(2);
        spread.appendSheet(sheet);

        SpreadSheet loaded = saveAndLoad(spread);
        assertEquals(1, loaded.getSheet(0).getFrozenRows());
        assertEquals(2, loaded.getSheet(0).getFrozenColumns());
    }

    @Test
    public void testRoundTripNoFreeze() throws IOException {
        SpreadSheet spread = new SpreadSheet();
        spread.appendSheet(new Sheet("Data", 5, 5));

        SpreadSheet loaded = saveAndLoad(spread);
        assertEquals(0, loaded.getSheet(0).getFrozenRows());
        assertEquals(0, loaded.getSheet(0).getFrozenColumns());
    }

    @Test
    public void testRoundTripMultipleSheetsMixedFreeze() throws IOException {
        SpreadSheet spread = new SpreadSheet();

        Sheet s1 = new Sheet("Frozen", 10, 10);
        s1.freezeRows(1);
        s1.getCell(0,0).setValue("A");
        s1.getCell(1,0).setValue("B");
        s1.getCell(0,1).setValue("1");
        s1.getCell(1,1).setValue("2");

        Sheet s2 = new Sheet("Plain", 5, 5);

        Sheet s3 = new Sheet("ColsOnly", 10, 10);
        s3.freezeColumns(4);
        s3.getCell(0,0).setValue("A");
        s3.getCell(1,0).setValue("B");
        s3.getCell(0,1).setValue("1");
        s3.getCell(1,1).setValue("2");

        spread.appendSheet(s1);
        spread.appendSheet(s2);
        spread.appendSheet(s3);

        SpreadSheet loaded = saveAndLoad(spread);
        /*
        assertEquals(1, loaded.getSheet(0).getFrozenRows());
        assertEquals(2, loaded.getSheet(0).getFrozenColumns());
        assertEquals(0, loaded.getSheet(1).getFrozenRows());
        assertEquals(0, loaded.getSheet(1).getFrozenColumns());
        assertEquals(0, loaded.getSheet(2).getFrozenRows());
        assertEquals(4, loaded.getSheet(2).getFrozenColumns());*/

        spread.save(new File("freepanel.ods"));
    }

    @Test
    public void testEquality() {
        Sheet a = new Sheet("A", 5, 5);
        Sheet b = new Sheet("A", 5, 5);
        assertEquals(a, b);

        a.freezeRows(2);
        assertFalse(a.equals(b));

        b.freezeRows(2);
        assertEquals(a, b);

        a.freezeColumns(1);
        assertFalse(a.equals(b));

        b.freezeColumns(1);
        assertEquals(a, b);
    }
    @Test
    public void testLoadingFreezePane() throws IOException {
        SpreadSheet spread = new SpreadSheet(new File("resources/frozenFields.ods"));
        assertEquals(spread.getSheet(0).getFrozenRows(), 1);
        assertEquals(spread.getSheet(0).getFrozenColumns(), 0);
        assertEquals(spread.getSheet(1).getFrozenRows(), 0);
        assertEquals(spread.getSheet(1).getFrozenColumns(), 1);
    }
}

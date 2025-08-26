package com.github.miachm.sods;

import org.testng.annotations.Test;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collections;

import static org.testng.Assert.*;

public class SheetFilterTest {

    @Test
    public void testLoadAllSheets() throws Exception {
        SpreadSheet spread = new SpreadSheet(new File("resources/testLoadOptions.ods"));
        assertEquals(spread.getNumSheets(), 1);
        assertEquals(spread.getSheet(0).getName(), "Sales");
    }

    @Test
    public void testLoadSpecificSheetThatExists() throws Exception {
        OdsOptionParameters options = new OdsOptionParameters();
        options.setSheetNumbers(Arrays.asList(0));
        SpreadSheet spread = new SpreadSheet(new FileInputStream("resources/testLoadOptions.ods"), options);
        assertEquals(spread.getNumSheets(), 1);
        assertEquals(spread.getSheet(0).getName(), "Sales");
    }

    @Test
    public void testLoadSheetThatDoesntExist() throws Exception {
        OdsOptionParameters options = new OdsOptionParameters();
        options.setSheetNumbers(Arrays.asList(1));
        SpreadSheet spread = new SpreadSheet(new FileInputStream("resources/testLoadOptions.ods"), options);
        assertEquals(spread.getNumSheets(), 0);
    }

    @Test
    public void testLoadEmptySheetsList() throws Exception {
        OdsOptionParameters options = new OdsOptionParameters();
        options.setSheetNumbers(Collections.emptyList());
        SpreadSheet spread = new SpreadSheet(new FileInputStream("resources/testLoadOptions.ods"), options);
        assertEquals(spread.getNumSheets(), 0);
    }

    @Test
    public void testLoadNullSheetNumbers() throws Exception {
        OdsOptionParameters options = new OdsOptionParameters();
        options.setSheetNumbers(null);
        SpreadSheet spread = new SpreadSheet(new FileInputStream("resources/testLoadOptions.ods"), options);
        assertEquals(spread.getNumSheets(), 1);
        assertEquals(spread.getSheet(0).getName(), "Sales");
    }
}
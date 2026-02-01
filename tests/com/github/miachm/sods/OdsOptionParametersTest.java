package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.io.File;

import static org.testng.AssertJUnit.*;

public class OdsOptionParametersTest {

    @Test
    public void testLoadImagesDisabled() throws Exception {
        OdsOptionParameters options = new OdsOptionParameters();
        options.setLoadImages(false);
        SpreadSheet spread = new SpreadSheet(new File("resources/image.ods"), options);
        Sheet sheet = spread.getSheet(0);
        assertTrue(sheet.getImages().isEmpty());
    }

    @Test
    public void testLoadGraphsDisabled() throws Exception {
        OdsOptionParameters options = new OdsOptionParameters();
        options.setLoadGraphs(false);
        SpreadSheet spread = new SpreadSheet(new File("resources/bargraph.ods"), options);
        assertTrue(spread.getCharts().isEmpty());
        Sheet sheet = spread.getSheet(0);
        assertTrue(sheet.getCharts().isEmpty());
    }
}

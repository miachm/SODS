package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

import static org.testng.AssertJUnit.*;

public class ChartTest {

    @Test
    public void testReadBarChart() throws Exception {
        SpreadSheet spread = new SpreadSheet(new File("resources/bargraph.ods"));
        List<Chart> charts = spread.getCharts();

        assertFalse(charts.isEmpty());
        Chart chart = charts.get(0);

        assertEquals("bar", chart.getType());
        assertNotNull(chart.getSheet());
        assertEquals("Sheet1", chart.getSheet().getName());
        assertEquals(1, chart.getSheet().getCharts().size());
        assertEquals(chart, chart.getSheet().getCharts().get(0));
        assertEquals(12, chart.getCategories().size());
        assertEquals("January", chart.getCategories().get(0));
        assertEquals("December", chart.getCategories().get(11));

        assertEquals(1, chart.getSeries().size());
        ChartSeries series = chart.getSeries().get(0);
        assertEquals(12, series.getValues().size());
        assertEquals(452d, ((Number) series.getValues().get(0)).doubleValue());
        assertEquals(1300d, ((Number) series.getValues().get(11)).doubleValue());

        spread.save(new File("cosita.ods"));
    }

    @Test
    public void testReadLineChart() throws Exception {
        SpreadSheet spread = new SpreadSheet(new File("resources/linechart.ods"));
        List<Chart> charts = spread.getCharts();

        assertFalse(charts.isEmpty());
        Chart chart = charts.get(0);

        assertEquals("line", chart.getType());
        assertNotNull(chart.getSheet());
        assertEquals("Sheet1", chart.getSheet().getName());
        assertEquals(1, chart.getSheet().getCharts().size());
        assertEquals(chart, chart.getSheet().getCharts().get(0));
        assertEquals(12, chart.getCategories().size());
        assertEquals("January", chart.getCategories().get(0));
        assertEquals("December", chart.getCategories().get(11));

        assertEquals(1, chart.getSeries().size());
        ChartSeries series = chart.getSeries().get(0);
        assertEquals(12, series.getValues().size());
        assertEquals(452d, ((Number) series.getValues().get(0)).doubleValue());
        assertEquals(1300d, ((Number) series.getValues().get(11)).doubleValue());
        spread.save(new File("coso.ods"));
    }
}

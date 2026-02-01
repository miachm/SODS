package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

import static org.testng.AssertJUnit.*;

public class ChartTest {

    @Test
    public void testReadBarChart() throws Exception {
        SpreadSheet spread = new SpreadSheet(new File("resources/bargraph.ods"));
        List<Chart> charts = spread.getSheet(0).getCharts();

        assertFalse(charts.isEmpty());
        Chart chart = charts.get(0);

        assertEquals("bar", chart.getType());
        assertNotNull(chart.getSheet());
        assertEquals("Sheet1", chart.getSheet().getName());
        assertEquals(1, chart.getSheet().getCharts().size());
        assertEquals(chart, chart.getSheet().getCharts().get(0));
        Range categoriesRange = chart.getCategoriesRangeObject();
        Object[][] categories = categoriesRange.getValues();
        assertEquals(12, categories.length);
        assertEquals("January", categories[0][0]);
        assertEquals("December", categories[11][0]);

        assertEquals(1, chart.getSeries().size());
        ChartSeries series = chart.getSeries().get(0);
        Range valuesRange = series.getValuesRangeObject();
        Object[][] values = valuesRange.getValues();
        assertEquals(12, values.length);
        assertEquals(452d, ((Number) values[0][0]).doubleValue());
        assertEquals(1300d, ((Number) values[11][0]).doubleValue());
    }

    @Test
    public void testReadLineChart() throws Exception {
        SpreadSheet spread = new SpreadSheet(new File("resources/linechart.ods"));
        List<Chart> charts = spread.getSheet(0).getCharts();

        assertFalse(charts.isEmpty());
        Chart chart = charts.get(0);

        assertEquals("line", chart.getType());
        assertNotNull(chart.getSheet());
        assertEquals("Sheet1", chart.getSheet().getName());
        assertEquals(1, chart.getSheet().getCharts().size());
        assertEquals(chart, chart.getSheet().getCharts().get(0));
        Range categoriesRange = chart.getCategoriesRangeObject();
        Object[][] categories = categoriesRange.getValues();
        assertEquals(12, categories.length);
        assertEquals("January", categories[0][0]);
        assertEquals("December", categories[11][0]);

        assertEquals(1, chart.getSeries().size());
        ChartSeries series = chart.getSeries().get(0);
        Range valuesRange = series.getValuesRangeObject();
        Object[][] values = valuesRange.getValues();
        assertEquals(12, values.length);
        assertEquals(452d, ((Number) values[0][0]).doubleValue());
        assertEquals(1300d, ((Number) values[11][0]).doubleValue());
    }

    @Test
    public void testLiveChartRanges() throws Exception {
        SpreadSheet spread = new SpreadSheet(new File("resources/bargraph.ods"));
        Chart chart = spread.getSheet(0).getCharts().get(0);
        ChartSeries series = chart.getSeries().get(0);
        Range valuesRange = series.getValuesRangeObject();

        valuesRange.getCell(0, 0).setValue(999d);
        valuesRange.getCell(11, 0).setValue(1111d);

        File out = File.createTempFile("sods-live-chart", ".ods");
        out.deleteOnExit();
        spread.save(out);

        SpreadSheet reloaded = new SpreadSheet(out);
        Chart reloadedChart = reloaded.getSheet(0).getCharts().get(0);
        ChartSeries reloadedSeries = reloadedChart.getSeries().get(0);
        Range reloadedValues = reloadedSeries.getValuesRangeObject();
        Object[][] values = reloadedValues.getValues();

        assertEquals(999d, ((Number) values[0][0]).doubleValue());
        assertEquals(1111d, ((Number) values[11][0]).doubleValue());
    }
}

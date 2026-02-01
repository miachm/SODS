import com.github.miachm.sods.Chart;
import com.github.miachm.sods.ChartSeries;
import com.github.miachm.sods.Color;
import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;

import java.io.File;
import java.io.IOException;

/* This sample creates a line chart in a new Spreadsheet.
 */
public class LineChart {
    public static void main(String[] args) {
        try {
            String[] months = new String[]{
                    "January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"
            };
            int[] values2024 = new int[]{
                    18, 22, 25, 28, 32, 35, 38, 37, 33, 28, 23, 19
            };
            int[] values2025 = new int[]{
                    17, 21, 24, 27, 31, 34, 37, 36, 32, 27, 22, 18
            };

            Sheet sheet = new Sheet("Sheet1", 13, 3);
            sheet.getRange(0, 0).setValue("Month");
            sheet.getRange(0, 1).setValue("2024 Avg Temp (C)");
            sheet.getRange(0, 2).setValue("2025 Avg Temp (C)");

            for (int i = 0; i < months.length; i++) {
                sheet.getRange(i + 1, 0).setValue(months[i]);
                sheet.getRange(i + 1, 1).setValue(values2024[i]);
                sheet.getRange(i + 1, 2).setValue(values2025[i]);
            }

            Chart chart = new Chart();
            chart.setType("line");
            chart.setWidth("16cm");
            chart.setHeight("9cm");
            chart.setX("8cm");
            chart.setY("0.5cm");
            chart.setXAxisLabel("Month");
            chart.setYAxisLabel("Avg Temp (C)");
            chart.setCategoriesRangeAddress("Sheet1.A2:Sheet1.A13");

            chart.getStyle().getGraphicProperties().setStroke("none");
            chart.getPlotAreaStyle().getChartProperties().setAutoSize(true);
            chart.getXAxis().getStyle().getChartProperties().setDisplayLabel(true);
            chart.getYAxis().getStyle().getChartProperties().setDisplayLabel(true);
            chart.getYAxis().getStyle().getGraphicProperties().setStrokeColor(new Color("#b3b3b3"));

            ChartSeries series2024 = new ChartSeries();
            series2024.setValuesRangeAddress("Sheet1.B2:Sheet1.B13");
            series2024.setLabelRangeAddress("Sheet1.B1:Sheet1.B1");
            series2024.getStyle().getGraphicProperties().setStrokeColor(new Color("#004586"));
            series2024.getStyle().getGraphicProperties().setFillColor(new Color("#004586"));
            series2024.getStyle().getGraphicProperties().setStrokeWidth("0.08cm");
            series2024.getStyle().getChartProperties().setSymbolType("named-symbol");
            series2024.getStyle().getChartProperties().setSymbolName("circle");
            series2024.getStyle().getChartProperties().setSymbolWidth("0.25cm");
            series2024.getStyle().getChartProperties().setSymbolHeight("0.25cm");
            chart.addSeries(series2024);

            ChartSeries series2025 = new ChartSeries();
            series2025.setValuesRangeAddress("Sheet1.C2:Sheet1.C13");
            series2025.setLabelRangeAddress("Sheet1.C1:Sheet1.C1");
            series2025.getStyle().getGraphicProperties().setStrokeColor(new Color("#ff7c43"));
            series2025.getStyle().getGraphicProperties().setFillColor(new Color("#ff7c43"));
            series2025.getStyle().getGraphicProperties().setStrokeWidth("0.08cm");
            series2025.getStyle().getChartProperties().setSymbolType("named-symbol");
            series2025.getStyle().getChartProperties().setSymbolName("square");
            series2025.getStyle().getChartProperties().setSymbolWidth("0.25cm");
            series2025.getStyle().getChartProperties().setSymbolHeight("0.25cm");
            chart.addSeries(series2025);

            sheet.addChart(chart);

            SpreadSheet spread = new SpreadSheet();
            spread.appendSheet(sheet);
            spread.save(new File("Out.ods"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

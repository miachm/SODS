import com.github.miachm.sods.Chart;
import com.github.miachm.sods.ChartSeries;
import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;

import java.io.File;
import java.io.IOException;

/* This sample creates a bar chart in a new Spreadsheet.
 */
public class BarChart {
    public static void main(String[] args) {
        try {
            String[] months = new String[]{
                    "January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"
            };
            int[] values = new int[]{452, 123, 965, 1500, 1999, 1200, 2400, 3000, 653, 900, 875, 1300};

            Sheet sheet = new Sheet("Sheet1", 13, 2);
            sheet.getRange(0, 0).setValue("Month");
            sheet.getRange(0, 1).setValue("Books read");

            for (int i = 0; i < months.length; i++) {
                sheet.getRange(i + 1, 0).setValue(months[i]);
                sheet.getRange(i + 1, 1).setValue(values[i]);
            }

            Chart chart = new Chart();
            chart.setType("bar");
            chart.setWidth("15cm");
            chart.setHeight("9cm");
            chart.setX("6cm");
            chart.setY("0.5cm");
            chart.setXAxisLabel("Month");
            chart.setYAxisLabel("Books read");
            chart.setCategoriesRangeAddress("Sheet1.A2:Sheet1.A13");

            chart.getStyle().getGraphicProperties().setStroke("none");
            chart.getPlotAreaStyle().getChartProperties().setAutoSize(true);
            chart.getXAxis().getStyle().getChartProperties().setDisplayLabel(true);
            chart.getYAxis().getStyle().getChartProperties().setDisplayLabel(true);
            chart.getYAxis().getStyle().getGraphicProperties().setStrokeColor(new com.github.miachm.sods.Color("#b3b3b3"));

            ChartSeries series = new ChartSeries();
            series.setValuesRangeAddress("Sheet1.B2:Sheet1.B13");
            series.getStyle().getGraphicProperties().setFillColor(new com.github.miachm.sods.Color("#004586"));
            chart.addSeries(series);

            sheet.addChart(chart);

            SpreadSheet spread = new SpreadSheet();
            spread.appendSheet(sheet);
            spread.save(new File("Out.ods"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

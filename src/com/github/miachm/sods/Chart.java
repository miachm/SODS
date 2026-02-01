package com.github.miachm.sods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a chart embedded in a {@link Sheet}.
 *
 * Charts in SODS store metadata such as title, footer, legend configuration,
 * and range addresses used to pull data from the sheet. This object does not
 * perform rendering; it captures the information required to serialize the
 * chart into the ODS output.
 */
public class Chart {
    private String type;
    private String title;
    private String footer;
    private String legend;
    private final List<String> data = new ArrayList<>();
    private final List<Object> categories = new ArrayList<>();
    private String categoriesRangeAddress;
    private final List<ChartSeries> series = new ArrayList<>();
    private Sheet sheet;

    private String width;
    private String height;
    private String x;
    private String y;
    private String xAxisLabel;
    private String yAxisLabel;
    private boolean displayLabel = true;
    private final ChartStyle style = new ChartStyle();
    private final ChartStyle plotAreaStyle = new ChartStyle();
    private final ChartStyle wallStyle = new ChartStyle();
    private final ChartAxis xAxis = new ChartAxis();
    private final ChartAxis yAxis = new ChartAxis();
    private boolean showWall = true;

    /**
     * Returns the chart type identifier.
     *
     * The type is the ODS chart kind (for example, bar, line, or pie) as
     * stored in the document. It is used by the writer to determine how
     * to serialize the chart.
     *
     * @return The chart type.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the chart title.
     *
     * The title is typically displayed at the top of the chart and provides
     * a short description of the data being visualized.
     *
     * @return The title text.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the chart footer text.
     *
     * The footer is a secondary text area usually shown below the chart.
     * It is commonly used for data source notes or additional context.
     *
     * @return The footer text.
     */
    public String getFooter() {
        return footer;
    }

    /**
     * Returns the legend configuration string.
     *
     * The legend describes the mapping between series and their visual
     * styles (colors, markers). This value controls how the legend is
     * represented in the output document.
     *
     * @return The legend value.
     */
    public String getLegend() {
        return legend;
    }

    /**
     * Returns the data ranges used by the chart.
     *
     * Each entry is an A1-style String range address that points to a block of
     * values in the sheet. These addresses are used by the writer to link
     * chart series to sheet cells.
     *
     * @return The data range list.
     */
    public List<String> getData() {
        return data;
    }

    /**
     * Returns the category values used by the chart.
     *
     * Categories are the labels that appear along the category axis
     * (for example, months or product names).
     *
     * @return An unmodifiable list of category values.
     */
    public List<Object> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    /**
     * Returns the A1 range address for the categories.
     *
     * This address points to the cells that contain category labels
     * (typically the X-axis labels).
     *
     * @return The range address.
     */
    public String getCategoriesRangeAddress() {
        return categoriesRangeAddress;
    }

    /**
     * Resolves the categories range address into a {@link Range}.
     *
     * This is a convenience method that looks up the range against the
     * owning sheet, if present.
     *
     * @return The categories range, or null if it cannot be resolved.
     */
    public Range getCategoriesRangeObject() {
        return ChartRangeResolver.resolveRange(categoriesRangeAddress, sheet);
    }

    /**
     * Returns the series list associated with this chart.
     *
     * Each series corresponds to a plotted dataset and contains its own
     * range addresses and style configuration.
     *
     * @return An unmodifiable list of series.
     */
    public List<ChartSeries> getSeries() {
        return Collections.unmodifiableList(series);
    }

    /**
     * Returns the sheet that owns this chart.
     *
     * This link is set when a chart is added to a sheet and is used to
     * resolve range addresses.
     *
     * @return The parent sheet.
     */
    public Sheet getSheet() {
        return sheet;
    }

    /**
     * Returns the chart width.
     *
     * This is a serialized size value (for example, "8cm") stored in the
     * document for layout purposes.
     *
     * @return The width value.
     */
    public String getWidth() {
        return width;
    }

    /**
     * Returns the chart height.
     *
     * This is a serialized size value (for example, "6cm") stored in the
     * document for layout purposes.
     *
     * @return The height value.
     */
    public String getHeight() {
        return height;
    }

    /**
     * Returns the X position.
     *
     * This is the horizontal offset used to place the chart within the sheet.
     *
     * @return The X position value.
     */
    public String getX() {
        return x;
    }

    /**
     * Returns the Y position.
     *
     * This is the vertical offset used to place the chart within the sheet.
     *
     * @return The Y position value.
     */
    public String getY() {
        return y;
    }

    /**
     * Returns the label for the X axis.
     *
     * Axis labels are descriptive captions shown next to the axis to clarify
     * what the axis values represent.
     *
     * @return The X axis label.
     */
    public String getXAxisLabel() {
        return xAxisLabel;
    }

    /**
     * Returns the label for the Y axis.
     *
     * Axis labels are descriptive captions shown next to the axis to clarify
     * what the axis values represent.
     *
     * @return The Y axis label.
     */
    public String getYAxisLabel() {
        return yAxisLabel;
    }

    /**
     * Returns whether labels are displayed.
     *
     * Labels typically refer to data point labels or series labels that
     * can be shown directly on the chart.
     *
     * @return True if labels are displayed.
     */
    public boolean isDisplayLabel() {
        return displayLabel;
    }

    /**
     * Returns the chart style.
     *
     * This style groups chart, graphic, and text properties applied at the
     * chart level.
     *
     * It's safe to manipulate the returned style since it is a copy.
     * Call {@link #setStyle(ChartStyle)} to apply changes.
     *
     * @return A copy of the chart style.
     */
    public ChartStyle getStyle() {
        return style.copy();
    }

    /**
     * Returns the plot area style.
     *
     * The plot area is the region where data series are drawn. Styles here
     * affect the background and borders of that region.
     *
     * It's safe to manipulate the returned style since it is a copy.
     * Call {@link #setPlotAreaStyle(ChartStyle)} to apply changes.
     *
     * @return A copy of the plot area style.
     */
    public ChartStyle getPlotAreaStyle() {
        return plotAreaStyle.copy();
    }

    /**
     * Returns the wall style.
     *
     * The wall is the back plane used in 3D charts. Styles here affect
     * its appearance.
     *
     * It's safe to manipulate the returned style since it is a copy.
     * Call {@link #setWallStyle(ChartStyle)} to apply changes.
     *
     * @return A copy of the wall style.
     */
    public ChartStyle getWallStyle() {
        return wallStyle.copy();
    }

    /**
     * Returns the X axis configuration.
     *
     * This provides access to style information for the horizontal axis.
     *
     * @return The X axis.
     */
    public ChartAxis getXAxis() {
        return xAxis;
    }

    /**
     * Returns the Y axis configuration.
     *
     * This provides access to style information for the vertical axis.
     *
     * @return The Y axis.
     */
    public ChartAxis getYAxis() {
        return yAxis;
    }

    /**
     * Returns whether the wall is displayed.
     *
     * The wall is a background plane used mainly for 3D charts.
     *
     * @return True if the wall is displayed.
     */
    public boolean isShowWall() {
        return showWall;
    }

    /**
     * Sets the chart type identifier.
     *
     * The type is stored as an ODS chart kind and determines how the
     * chart will be serialized.
     *
     * @param type The chart type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the chart title.
     *
     * The title is typically displayed at the top of the chart.
     *
     * @param title The title text.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the chart footer text.
     *
     * The footer is a secondary text area used for notes or data sources.
     *
     * @param footer The footer text.
     */
    public void setFooter(String footer) {
        this.footer = footer;
    }

    /**
     * Sets the legend configuration value.
     *
     * The legend is used to identify series and their styling.
     *
     * @param legend The legend value.
     */
    public void setLegend(String legend) {
        this.legend = legend;
    }

    /**
     * Adds a data range address to this chart.
     *
     * Data ranges point to the sheet cells that provide values for plotting.
     *
     * @param data The range address.
     */
    public void addData(String data) {
        this.data.add(data);
    }

    /**
     * Adds a category value to this chart.
     *
     * Categories define the labels along the category axis.
     *
     * @param category The category value.
     */
    public void addCategory(Object category) {
        this.categories.add(category);
    }

    /**
     * Sets the A1 range address for categories.
     *
     * The address should reference cells that contain category labels.
     *
     * @param categoriesRangeAddress The range address.
     */
    public void setCategoriesRangeAddress(String categoriesRangeAddress) {
        this.categoriesRangeAddress = categoriesRangeAddress;
    }

    /**
     * Adds a series to this chart.
     *
     * Series define the plotted datasets and their styling. The chart will
     * set itself as the series owner so ranges can be resolved.
     *
     * @param series The series to add. If null, it is ignored.
     */
    public void addSeries(ChartSeries series) {
        if (series != null) {
            series.setChart(this);
            this.series.add(series);
        }
    }

    void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    /**
     * Sets the chart width.
     *
     * The value is stored as a serialized size (for example, "8cm").
     *
     * @param width The width value.
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * Sets the chart height.
     *
     * The value is stored as a serialized size (for example, "6cm").
     *
     * @param height The height value.
     */
    public void setHeight(String height) {
        this.height = height;
    }

    /**
     * Sets the X position.
     *
     * This value controls the horizontal placement of the chart in the sheet.
     *
     * @param x The X position value.
     */
    public void setX(String x) {
        this.x = x;
    }

    /**
     * Sets the Y position.
     *
     * This value controls the vertical placement of the chart in the sheet.
     *
     * @param y The Y position value.
     */
    public void setY(String y) {
        this.y = y;
    }

    /**
     * Sets the label for the X axis.
     *
     * Axis labels help users understand what the axis values represent.
     *
     * @param xAxisLabel The label text.
     */
    public void setXAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
    }

    /**
     * Sets the label for the Y axis.
     *
     * Axis labels help users understand what the axis values represent.
     *
     * @param yAxisLabel The label text.
     */
    public void setYAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
    }

    /**
     * Enables or disables label display.
     *
     * Labels typically refer to value labels or series labels shown directly
     * on the chart.
     *
     * @param displayLabel True to display labels.
     */
    public void setDisplayLabel(boolean displayLabel) {
        this.displayLabel = displayLabel;
    }

    /**
     * Sets the chart style.
     *
     * The provided style is copied, so subsequent changes to the input
     * object do not affect the chart.
     *
     * @param style The chart style.
     */
    public void setStyle(ChartStyle style) {
        this.style.copyFrom(style);
    }

    /**
     * Sets the plot area style.
     *
     * The provided style is copied, so subsequent changes to the input
     * object do not affect the chart.
     *
     * @param plotAreaStyle The plot area style.
     */
    public void setPlotAreaStyle(ChartStyle plotAreaStyle) {
        this.plotAreaStyle.copyFrom(plotAreaStyle);
    }

    /**
     * Sets the wall style.
     *
     * The provided style is copied, so subsequent changes to the input
     * object do not affect the chart.
     *
     * @param wallStyle The wall style.
     */
    public void setWallStyle(ChartStyle wallStyle) {
        this.wallStyle.copyFrom(wallStyle);
    }

    /**
     * Enables or disables wall display.
     *
     * The wall is a background plane used mainly for 3D charts.
     *
     * @param showWall True to display the wall.
     */
    public void setShowWall(boolean showWall) {
        this.showWall = showWall;
    }
}

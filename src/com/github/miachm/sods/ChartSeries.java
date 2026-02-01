package com.github.miachm.sods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a series within a {@link Chart}.
 *
 * A series is a single dataset plotted in the chart. It can be defined by
 * explicit values/labels or by range addresses that point to sheet cells.
 */
public class ChartSeries {
    private String valuesRangeAddress;
    private String labelRangeAddress;
    private final List<Object> values = new ArrayList<>();
    private final List<Object> labels = new ArrayList<>();
    private final ChartStyle style = new ChartStyle();
    private Chart chart;

    /**
     * Returns the A1 range address for values.
     *
     * This address points to the cells that provide the numeric values
     * for the series.
     *
     * @return The values range address.
     */
    public String getValuesRangeAddress() {
        return valuesRangeAddress;
    }

    /**
     * Returns the A1 range address for labels.
     *
     * This address points to the cells that provide labels for the series.
     *
     * @return The label range address.
     */
    public String getLabelRangeAddress() {
        return labelRangeAddress;
    }

    /**
     * Returns the series values.
     *
     * This list represents the values explicitly associated with the series,
     * which may be used when no range address is provided.
     *
     * @return An unmodifiable list of values.
     */
    public List<Object> getValues() {
        return Collections.unmodifiableList(values);
    }

    /**
     * Returns the series labels.
     *
     * This list represents the labels explicitly associated with the series,
     * which may be used when no range address is provided.
     *
     * @return An unmodifiable list of labels.
     */
    public List<Object> getLabels() {
        return Collections.unmodifiableList(labels);
    }

    /**
     * Returns the series style.
     *
     * The style controls how this series is rendered (colors, markers, text).
     *
     * It's safe to manipulate the returned style since it is a copy.
     * Call {@link #setStyle(ChartStyle)} to apply changes.
     *
     * @return A copy of the series style.
     */
    public ChartStyle getStyle() {
        return style.copy();
    }

    /**
     * Sets the series style.
     *
     * The provided style is copied, so subsequent changes to the input
     * object do not affect the series.
     *
     * @param style The series style.
     */
    public void setStyle(ChartStyle style) {
        this.style.copyFrom(style);
    }

    /**
     * Resolves the values range address into a {@link Range}.
     *
     * This is a convenience method that uses the parent chart's sheet to
     * resolve the range.
     *
     * @return The values range, or null if it cannot be resolved.
     */
    public Range getValuesRangeObject() {
        Sheet chartSheet = chart == null ? null : chart.getSheet();
        return ChartRangeResolver.resolveRange(valuesRangeAddress, chartSheet);
    }

    /**
     * Sets the A1 range address for values.
     *
     * The address should reference the cells that contain the series values.
     *
     * @param valuesRangeAddress The values range address.
     */
    public void setValuesRangeAddress(String valuesRangeAddress) {
        this.valuesRangeAddress = valuesRangeAddress;
    }

    /**
     * Sets the A1 range address for labels.
     *
     * The address should reference the cells that contain series labels.
     *
     * @param labelRangeAddress The label range address.
     */
    public void setLabelRangeAddress(String labelRangeAddress) {
        this.labelRangeAddress = labelRangeAddress;
    }

    /**
     * Adds a value to the series.
     *
     * Values added here are used when no values range is provided.
     *
     * @param value The value to add.
     */
    public void addValue(Object value) {
        values.add(value);
    }

    /**
     * Adds a label to the series.
     *
     * Labels added here are used when no label range is provided.
     *
     * @param label The label to add.
     */
    public void addLabel(Object label) {
        labels.add(label);
    }

    void setChart(Chart chart) {
        this.chart = chart;
    }

}

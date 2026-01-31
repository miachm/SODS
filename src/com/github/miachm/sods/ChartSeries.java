package com.github.miachm.sods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChartSeries {
    private String valuesRangeAddress;
    private String labelRangeAddress;
    private final List<Object> values = new ArrayList<>();
    private final List<Object> labels = new ArrayList<>();
    private final ChartStyle style = new ChartStyle();

    public String getValuesRangeAddress() {
        return valuesRangeAddress;
    }

    public String getLabelRangeAddress() {
        return labelRangeAddress;
    }

    public List<Object> getValues() {
        return Collections.unmodifiableList(values);
    }

    public List<Object> getLabels() {
        return Collections.unmodifiableList(labels);
    }

    public ChartStyle getStyle() {
        return style;
    }

    public void setValuesRangeAddress(String valuesRangeAddress) {
        this.valuesRangeAddress = valuesRangeAddress;
    }

    public void setLabelRangeAddress(String labelRangeAddress) {
        this.labelRangeAddress = labelRangeAddress;
    }

    public void addValue(Object value) {
        values.add(value);
    }

    public void addLabel(Object label) {
        labels.add(label);
    }
}

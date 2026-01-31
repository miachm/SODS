package com.github.miachm.sods;

/**
 * Chart style container with chart, graphic, and text properties.
 */
public class ChartStyle {
    private final ChartProperties chartProperties = new ChartProperties();
    private final ChartGraphicProperties graphicProperties = new ChartGraphicProperties();
    private final ChartTextProperties textProperties = new ChartTextProperties();

    public ChartProperties getChartProperties() {
        return chartProperties;
    }

    public ChartGraphicProperties getGraphicProperties() {
        return graphicProperties;
    }

    public ChartTextProperties getTextProperties() {
        return textProperties;
    }

    boolean hasAnyProperties() {
        return chartProperties.hasAny() || graphicProperties.hasAny() || textProperties.hasAny();
    }
}

package com.github.miachm.sods;

/**
 * Groups chart style properties (chart, graphic, and text).
 *
 * This class acts as a container for related style domains used when
 * serializing chart formatting.
 */
public class ChartStyle {
    private final ChartProperties chartProperties = new ChartProperties();
    private final ChartGraphicProperties graphicProperties = new ChartGraphicProperties();
    private final ChartTextProperties textProperties = new ChartTextProperties();

    /**
     * Returns the chart properties.
     *
     * These properties control behavior such as axis handling, labels,
     * and other chart-level settings.
     *
     * @return The chart properties.
     */
    public ChartProperties getChartProperties() {
        return chartProperties;
    }

    /**
     * Returns the graphic properties.
     *
     * These properties control the appearance of lines, fills, and other
     * shape-related attributes.
     *
     * @return The graphic properties.
     */
    public ChartGraphicProperties getGraphicProperties() {
        return graphicProperties;
    }

    /**
     * Returns the text properties.
     *
     * These properties control font family, size, weight, and color for
     * chart text elements such as titles and labels.
     *
     * @return The text properties.
     */
    public ChartTextProperties getTextProperties() {
        return textProperties;
    }

    boolean hasAnyProperties() {
        return chartProperties.hasAny() || graphicProperties.hasAny() || textProperties.hasAny();
    }
}

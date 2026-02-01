package com.github.miachm.sods;

/**
 * Represents a chart axis configuration.
 *
 * This object currently exposes style information for the axis and is used
 * when reading or writing chart formatting.
 */
public class ChartAxis {
    private final ChartStyle style = new ChartStyle();

    /**
     * Returns the axis style.
     *
     * The style includes chart, graphic, and text properties that affect
     * how the axis line and labels are displayed.
     *
     * @return The axis style.
     */
    public ChartStyle getStyle() {
        return style;
    }
}

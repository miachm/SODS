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
     * It's safe to manipulate the returned style since it is a copy.
     * Call {@link #setStyle(ChartStyle)} to apply changes.
     *
     * @return A copy of the axis style.
     */
    public ChartStyle getStyle() {
        return style.copy();
    }

    /**
     * Sets the axis style.
     *
     * The provided style is copied, so subsequent changes to the input
     * object do not affect the axis.
     *
     * @param style The axis style.
     */
    public void setStyle(ChartStyle style) {
        this.style.copyFrom(style);
    }
}

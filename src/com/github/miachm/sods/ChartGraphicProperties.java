package com.github.miachm.sods;

/**
 * Holds graphic properties for chart elements.
 *
 * These properties control strokes, fills, and other shape-related settings
 * used when serializing chart styles.
 */
public class ChartGraphicProperties {
    private String stroke;
    private String fill;
    private String strokeWidth;
    private Color strokeColor;
    private Color fillColor;
    private String edgeRounding;

    /**
     * Returns the stroke style.
     *
     * This corresponds to the line style used for borders or outlines
     * in the chart graphic elements.
     *
     * @return The stroke style.
     */
    public String getStroke() {
        return stroke;
    }

    /**
     * Returns the fill style.
     *
     * This corresponds to the fill pattern or mode used for chart shapes.
     *
     * @return The fill style.
     */
    public String getFill() {
        return fill;
    }

    /**
     * Returns the stroke width.
     *
     * This is stored as a size value in the document (for example, "0.03cm").
     *
     * @return The stroke width.
     */
    public String getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * Returns the stroke color.
     *
     * The color affects outlines of chart elements.
     *
     * @return The stroke color.
     */
    public Color getStrokeColor() {
        return strokeColor;
    }

    /**
     * Returns the fill color.
     *
     * The color affects the interior of chart elements.
     *
     * @return The fill color.
     */
    public Color getFillColor() {
        return fillColor;
    }

    /**
     * Returns the edge rounding value.
     *
     * This controls how rounded the corners of chart elements appear.
     *
     * @return The edge rounding value.
     */
    public String getEdgeRounding() {
        return edgeRounding;
    }

    /**
     * Sets the stroke style.
     *
     * This controls the line style used for outlines in the chart.
     *
     * @param stroke The stroke style.
     */
    public void setStroke(String stroke) {
        this.stroke = stroke;
    }

    /**
     * Sets the fill style.
     *
     * This controls how shapes are filled (solid, patterned, etc.).
     *
     * @param fill The fill style.
     */
    public void setFill(String fill) {
        this.fill = fill;
    }

    /**
     * Sets the stroke width.
     *
     * The value is stored as a size string in the document.
     *
     * @param strokeWidth The stroke width.
     */
    public void setStrokeWidth(String strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    /**
     * Sets the stroke color.
     *
     * This color is used for outlines of chart elements.
     *
     * @param strokeColor The stroke color.
     */
    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    /**
     * Sets the fill color.
     *
     * This color is used for the interior of chart elements.
     *
     * @param fillColor The fill color.
     */
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    /**
     * Sets the edge rounding value.
     *
     * This controls how rounded the corners of chart elements appear.
     *
     * @param edgeRounding The edge rounding value.
     */
    public void setEdgeRounding(String edgeRounding) {
        this.edgeRounding = edgeRounding;
    }

    boolean hasAny() {
        return stroke != null
                || fill != null
                || strokeWidth != null
                || strokeColor != null
                || fillColor != null
                || edgeRounding != null;
    }

    void copyFrom(ChartGraphicProperties other) {
        if (other == null) {
            throw new IllegalArgumentException("ChartGraphicProperties cannot be null");
        }
        stroke = other.stroke;
        fill = other.fill;
        strokeWidth = other.strokeWidth;
        strokeColor = other.strokeColor;
        fillColor = other.fillColor;
        edgeRounding = other.edgeRounding;
    }
}

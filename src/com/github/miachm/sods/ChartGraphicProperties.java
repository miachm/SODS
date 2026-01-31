package com.github.miachm.sods;

/**
 * Properties applied under style:graphic-properties for chart elements.
 */
public class ChartGraphicProperties {
    private String stroke;
    private String fill;
    private String strokeWidth;
    private Color strokeColor;
    private Color fillColor;
    private String edgeRounding;

    public String getStroke() {
        return stroke;
    }

    public String getFill() {
        return fill;
    }

    public String getStrokeWidth() {
        return strokeWidth;
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public String getEdgeRounding() {
        return edgeRounding;
    }

    public void setStroke(String stroke) {
        this.stroke = stroke;
    }

    public void setFill(String fill) {
        this.fill = fill;
    }

    public void setStrokeWidth(String strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

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
}

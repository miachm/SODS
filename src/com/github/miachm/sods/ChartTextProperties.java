package com.github.miachm.sods;

/**
 * Properties applied under style:text-properties for chart elements.
 */
public class ChartTextProperties {
    private Double fontSizePt;
    private Double fontSizeAsianPt;
    private Double fontSizeComplexPt;
    private String fontWeight;
    private String fontStyle;
    private String fontFamily;
    private Color color;

    public Double getFontSizePt() {
        return fontSizePt;
    }

    public Double getFontSizeAsianPt() {
        return fontSizeAsianPt;
    }

    public Double getFontSizeComplexPt() {
        return fontSizeComplexPt;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public Color getColor() {
        return color;
    }

    public void setFontSizePt(double fontSizePt) {
        this.fontSizePt = fontSizePt;
    }

    public void setFontSizeAsianPt(double fontSizeAsianPt) {
        this.fontSizeAsianPt = fontSizeAsianPt;
    }

    public void setFontSizeComplexPt(double fontSizeComplexPt) {
        this.fontSizeComplexPt = fontSizeComplexPt;
    }

    public void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    boolean hasAny() {
        return fontSizePt != null
                || fontSizeAsianPt != null
                || fontSizeComplexPt != null
                || fontWeight != null
                || fontStyle != null
                || fontFamily != null
                || color != null;
    }
}

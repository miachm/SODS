package com.github.miachm.sods;

/**
 * Holds text properties for chart labels and titles.
 *
 * These properties are used for titles, axis labels, legends, and other
 * textual elements inside charts.
 */
public class ChartTextProperties {
    private Double fontSizePt;
    private Double fontSizeAsianPt;
    private Double fontSizeComplexPt;
    private String fontWeight;
    private String fontStyle;
    private String fontFamily;
    private Color color;

    /**
     * Returns the font size in points.
     *
     * This value controls the main Latin font size used by chart text.
     *
     * @return The font size in points.
     */
    public Double getFontSizePt() {
        return fontSizePt;
    }

    /**
     * Returns the Asian font size in points.
     *
     * This value is used for East Asian scripts when specified.
     *
     * @return The Asian font size in points.
     */
    public Double getFontSizeAsianPt() {
        return fontSizeAsianPt;
    }

    /**
     * Returns the complex font size in points.
     *
     * This value is used for complex text layout scripts when specified.
     *
     * @return The complex font size in points.
     */
    public Double getFontSizeComplexPt() {
        return fontSizeComplexPt;
    }

    /**
     * Returns the font weight.
     *
     * Common values include "bold" or "normal".
     *
     * @return The font weight.
     */
    public String getFontWeight() {
        return fontWeight;
    }

    /**
     * Returns the font style.
     *
     * Common values include "italic" or "normal".
     *
     * @return The font style.
     */
    public String getFontStyle() {
        return fontStyle;
    }

    /**
     * Returns the font family.
     *
     * This controls which font face is used for chart text.
     *
     * @return The font family.
     */
    public String getFontFamily() {
        return fontFamily;
    }

    /**
     * Returns the text color.
     *
     * This color is used for chart titles, labels, and legend text.
     *
     * @return The text color.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the font size in points.
     *
     * This controls the main Latin font size used by chart text.
     *
     * @param fontSizePt The font size in points.
     */
    public void setFontSizePt(double fontSizePt) {
        this.fontSizePt = fontSizePt;
    }

    /**
     * Sets the Asian font size in points.
     *
     * This value is used for East Asian scripts when specified.
     *
     * @param fontSizeAsianPt The Asian font size in points.
     */
    public void setFontSizeAsianPt(double fontSizeAsianPt) {
        this.fontSizeAsianPt = fontSizeAsianPt;
    }

    /**
     * Sets the complex font size in points.
     *
     * This value is used for complex text layout scripts when specified.
     *
     * @param fontSizeComplexPt The complex font size in points.
     */
    public void setFontSizeComplexPt(double fontSizeComplexPt) {
        this.fontSizeComplexPt = fontSizeComplexPt;
    }

    /**
     * Sets the font weight.
     *
     * Common values include "bold" or "normal".
     *
     * @param fontWeight The font weight.
     */
    public void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }

    /**
     * Sets the font style.
     *
     * Common values include "italic" or "normal".
     *
     * @param fontStyle The font style.
     */
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    /**
     * Sets the font family.
     *
     * This controls which font face is used for chart text.
     *
     * @param fontFamily The font family.
     */
    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    /**
     * Sets the text color.
     *
     * This color is used for chart titles, labels, and legend text.
     *
     * @param color The text color.
     */
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

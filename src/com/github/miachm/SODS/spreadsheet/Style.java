package com.github.miachm.SODS.spreadsheet;


import java.util.HashMap;
import java.util.Map;

public final class Style implements Cloneable {
    private boolean bold;
    private boolean italic;
    private boolean underline;
    private Color fontColor;
    private Color backgroundColor;
    private int fontSize = -1;

    public Style() {

    }

    public Style(boolean bold, boolean italic, boolean underline, Color fontColor, Color backgroundColor) {
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
        this.fontColor = fontColor;
        this.backgroundColor = backgroundColor;
    }

    public boolean isDefault()
    {
        return this.equals(new Style());
    }
    
    public boolean isBold() {
        return bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public Color getFontColor() {
        return fontColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    public void setBold(boolean bold)
    {
        this.bold = bold;
    }

    public void setItalic(boolean italic)
    {
        this.italic = italic;
    }

    public boolean isUnderline() {
        return underline;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    public void setFontSize(int fontSize) {
        if (fontSize < -1)
            throw new AssertionError("Error, font size can be less of -1");
        this.fontSize = fontSize;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Style style = (Style) o;

        if (bold != style.bold) return false;
        if (italic != style.italic) return false;
        if (underline != style.underline) return false;
        if (fontSize != style.fontSize) return false;
        if (fontColor != null ? !fontColor.equals(style.fontColor) : style.fontColor != null) return false;
        return backgroundColor != null ? backgroundColor.equals(style.backgroundColor) : style.backgroundColor == null;
    }

    @Override
    public int hashCode() {
        int result = (bold ? 1 : 0);
        result = 31 * result + (italic ? 1 : 0);
        result = 31 * result + (underline ? 1 : 0);
        result = 31 * result + (fontColor != null ? fontColor.hashCode() : 0);
        result = 31 * result + (backgroundColor != null ? backgroundColor.hashCode() : 0);
        result = 31 * result + fontSize;
        return result;
    }

    public Map<String, String> getCssStyles()
    {
        Map<String, String> result = new HashMap<>();
        if (isBold())
            result.put("font-weight", "bold");

        if (isItalic())
            result.put("font-style", "italic");

        if (isUnderline())
            result.put("text-decoration", "underline");

        if (getFontSize() != -1)
            result.put("font-size", "" + getFontSize());

        if (getFontColor() != null)
            result.put("color", "" + getFontColor().toString() + ";");

        if (getBackgroundColor() != null)
            result.put("background-color", getBackgroundColor().toString());

        return result;
    }

    @Override
    public String toString()
    {
        return toString("");
    }

    public String toString(String prefix)
    {
        String result = "";
        Map<String,String> styles = getCssStyles();
        for (Map.Entry<String,String> style : styles.entrySet())
            result += prefix + style.getKey() + ": " + style.getValue() + ";\n";

        return result;
    }
}

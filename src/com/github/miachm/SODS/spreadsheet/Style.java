package com.github.miachm.SODS.spreadsheet;


public final class Style implements Cloneable {
    private boolean bold;
    private boolean italic;
    private boolean underline;
    private Color fontColor;
    private Color backgroundColor;

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
        return result;
    }
}

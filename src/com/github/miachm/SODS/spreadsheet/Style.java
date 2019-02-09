package com.github.miachm.SODS.spreadsheet;

public final class Style implements Cloneable {
    private boolean bold;
    private boolean italic;

    public Style()
    {

    }

    public Style(boolean bold, boolean italic) {
        this.bold = bold;
        this.italic = italic;
    }

    public boolean isBold() {
        return bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setBold(boolean bold)
    {
        this.bold = bold;
    }

    public void setItalic(boolean italic)
    {
        this.italic = italic;
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
        return italic == style.italic;
    }

    @Override
    public int hashCode() {
        int result = (bold ? 1 : 0);
        result = 31 * result + (italic ? 1 : 0);
        return result;
    }
}

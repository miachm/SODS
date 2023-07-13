package com.github.miachm.sods;

import java.util.Objects;

class ColumnStyle implements Cloneable {
    static final ColumnStyle default_style = new ColumnStyle();
    private Double width;
    private boolean isHidden;
    private Style defaultCellStyle = Style.default_style;
    private static final double EQUIVALENCE_CM = 10;
    private static final double EQUIVALENCE_PX = 0.264583333;
    private static final double EQUIVALENCE_IN = (2.54 * 10);
    private static final double EQUIVALENCE_PT = 72.0 / 25.4;
    private static final double EQUIVALENCE_PC = 6.0 / 25.4;

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    void setWidth(String width) {
        this.width = getValue(width);
    }

    public static Double getValue(String value)
    {
        if (value == null)
            return null;

        if (value.isEmpty() || value.equals("0"))
            return 0.0;

        if (value.length() > 2) {
            double number = Double.parseDouble(value.substring(0, value.length() - 2));
            if (value.endsWith("mm"))
                return number;
            else if (value.endsWith("cm"))
                return number * EQUIVALENCE_CM;
            else if (value.endsWith("in"))
                return number * EQUIVALENCE_IN;
            else if (value.endsWith("pt"))
                return number * EQUIVALENCE_PT;
            else if (value.endsWith("pc"))
                return number * EQUIVALENCE_PC;
            else if (value.endsWith("px"))
                return number * EQUIVALENCE_PX;
            else
                throw new IllegalArgumentException("Unit not recognized");
        }
        else
            throw new IllegalArgumentException("A unit is needed");
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public Style getDefaultCellStyleCopy() {
        return cloneStyle(defaultCellStyle);
    }

    /** The returned style object must not be mutated. */
    public Style getDefaultCellStyleDangerous() {
        return defaultCellStyle;
    }

    public void setDefaultCellStyle(Style defaultCellStyle) {
        if (defaultCellStyle == null)
            throw new IllegalArgumentException("Default cell style cannot be null");
        this.defaultCellStyle = cloneStyle(defaultCellStyle);
    }

    private static Style cloneStyle(Style style) {
        try {
            return (Style) style.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Style is not cloneable", e);
        }
    }

    @Override
    public Object clone()
    {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnStyle that = (ColumnStyle) o;

        if (isHidden != that.isHidden) return false;
        if (!Objects.equals(defaultCellStyle, that.defaultCellStyle)) return false;
        return width != null ? width.equals(that.width) : that.width == null;
    }

    @Override
    public int hashCode() {
        int result = width != null ? width.hashCode() : 0;
        result = 31 * result + (isHidden ? 1 : 0);
        result = 31 * result + (defaultCellStyle != null ? defaultCellStyle.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ColumnStyle{" +
                "width=" + width +
                ", isHidden=" + isHidden +
                ", defaultCellStyle=" + defaultCellStyle +
                '}';
    }
}

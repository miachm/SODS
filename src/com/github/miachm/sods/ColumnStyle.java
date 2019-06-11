package com.github.miachm.sods;

class ColumnStyle {
    private Double width;
    private static final double EQUIVALENCE_CM = 0.1;
    private static final double EQUIVALENCE_PX = 0.264583333;
    private static final double EQUIVALENCE_IN = 2.54 / 100.0;
    private static final double EQUIVALENCE_PT = EQUIVALENCE_IN / 72.0;
    private static final double EQUIVALENCE_PC = EQUIVALENCE_IN * 12.0;

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnStyle that = (ColumnStyle) o;

        return width != null ? width.equals(that.width) : that.width == null;
    }

    @Override
    public int hashCode() {
        return width != null ? width.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ColumnStyle{" +
                "width=" + width +
                '}';
    }
}

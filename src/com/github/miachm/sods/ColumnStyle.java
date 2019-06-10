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
        if (width == null) {
            this.width = null;
            return;
        }

        if (width.isEmpty() || width.equals("0")) {
            this.width = 0.0;
            return;
        }

        if (width.length() > 2) {
            double value = Double.parseDouble(width.substring(0, width.length() - 2));
            if (width.endsWith("mm"))
                this.width = value;
            else if (width.endsWith("cm"))
                this.width = value * EQUIVALENCE_CM;
            else if (width.endsWith("in"))
                this.width = value * EQUIVALENCE_IN;
            else if (width.endsWith("pt"))
                this.width = value * EQUIVALENCE_PT;
            else if (width.endsWith("pc"))
                this.width = value * EQUIVALENCE_PC;
            else if (width.endsWith("px"))
                this.width = value * EQUIVALENCE_PX;
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

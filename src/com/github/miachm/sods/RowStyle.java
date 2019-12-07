package com.github.miachm.sods;

class RowStyle {
    private Double height;

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    void setHeight(String height) {
        this.height = ColumnStyle.getValue(height);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RowStyle style = (RowStyle) o;

        return height != null ? height.equals(style.height) : style.height == null;
    }

    @Override
    public int hashCode() {
        return height != null ? height.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "RowStyle{" +
                "height=" + height +
                '}';
    }
}

package com.github.miachm.sods;

class RowStyle implements Cloneable {
    static final RowStyle default_style = new RowStyle();
    private Double height;
    private boolean isHidden;

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    void setHeight(String height) {
        this.height = ColumnStyle.getValue(height);
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
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

        RowStyle rowStyle = (RowStyle) o;

        if (isHidden != rowStyle.isHidden) return false;
        return height != null ? height.equals(rowStyle.height) : rowStyle.height == null;
    }

    @Override
    public int hashCode() {
        int result = height != null ? height.hashCode() : 0;
        result = 31 * result + (isHidden ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RowStyle{" +
                "height=" + height +
                ", isHidden=" + isHidden +
                '}';
    }
}

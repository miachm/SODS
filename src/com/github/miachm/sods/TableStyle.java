package com.github.miachm.sods;

class TableStyle {
    private boolean isHidden;
    private Color tabColor;

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public Color getTabColor() {
        return tabColor;
    }

    public void setTabColor(Color tabColor) {
        this.tabColor = tabColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableStyle that = (TableStyle) o;

        if (isHidden != that.isHidden) return false;
        return tabColor != null ? tabColor.equals(that.tabColor) : that.tabColor == null;
    }

    @Override
    public int hashCode() {
        int result = (isHidden ? 1 : 0);
        result = 31 * result + (tabColor != null ? tabColor.hashCode() : 0);
        return result;
    }
}

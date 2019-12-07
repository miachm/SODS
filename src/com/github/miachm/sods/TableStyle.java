package com.github.miachm.sods;

class TableStyle {
    private boolean isHidden;

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableStyle that = (TableStyle) o;

        return isHidden == that.isHidden;
    }

    @Override
    public int hashCode() {
        return (isHidden ? 1 : 0);
    }
}

package com.github.miachm.sods;

abstract class TableItem implements Cloneable{
    private int numRepeated = 1;

    public int getNumRepeated() {
        return numRepeated;
    }
    public void setNumRepeated(int numRepeated) {
        if (numRepeated == 0)
            throw new IllegalArgumentException("Numrepeated can not be zero");
        this.numRepeated = numRepeated;
    }

    @Override
    public TableItem clone() throws CloneNotSupportedException {
        return (TableItem) super.clone();
    }

    abstract public boolean isBlank();
}

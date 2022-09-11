package com.github.miachm.sods;

class TableField implements Cloneable  {
    int num_repeated = 1;

    @Override
    public Object clone()
    {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}

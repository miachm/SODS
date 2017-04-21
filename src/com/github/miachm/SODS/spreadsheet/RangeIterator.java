package com.github.miachm.SODS.spreadsheet;

interface RangeIterator{
    void call(Cell cell,int row,int column);
}
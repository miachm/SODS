package com.github.miachm.sods.spreadsheet;

interface RangeIterator{
    void call(Cell cell,int row,int column);
}
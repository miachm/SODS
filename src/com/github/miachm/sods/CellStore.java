package com.github.miachm.sods;

interface CellStore {
    Cell getCell(int row, int column);

    void iterateReadOnly(int rowStart, int numRows, int colStart, int numCols, RangeIterator consumer);

    void iterateUniformWrite(int rowStart, int numRows, int colStart, int numCols, RangeIterator consumer);

    void iterateWrite(int rowStart, int numRows, int colStart, int numCols, RangeIterator consumer);
}

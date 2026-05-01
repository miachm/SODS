package com.github.miachm.sods;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterates through all cells in a sheet in row-major order.
 *
 * This iterator exposes {@link LogicalCell} so callers never interact with the internal {@link Cell} type.
 */
class CellIterator {
    private final Sheet sheet;
    private final int startColumn;
    private final int endRowExclusive;
    private final int endColumnExclusive;
    private int rowIndex;
    private int columnIndex;

    private RowCursor rowCursor;
    private ColCursor colCursor;
    private Row currentRow;
    private Cell currentCell;
    private boolean currentPrepared;

    CellIterator(Sheet sheet) {
        this(sheet, 0, 0, sheet.getMaxRows(), sheet.getMaxColumns());
    }

    CellIterator(Sheet sheet, int rowIndex, int columnIndex) {
        this(sheet, rowIndex, columnIndex, sheet.getMaxRows(), sheet.getMaxColumns());
    }

    CellIterator(Sheet sheet, int rowIndex, int columnIndex, int endRowExclusive, int endColumnExclusive) {
        if (sheet == null) {
            throw new NullPointerException("Sheet can not be null");
        }
        this.sheet = sheet;
        this.startColumn = columnIndex;
        this.endRowExclusive = endRowExclusive;
        this.endColumnExclusive = endColumnExclusive;

        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.rowCursor = alignRowCursor(rowIndex);
        this.colCursor = new ColCursor();
        this.currentPrepared = false;
    }

    /**
     * Returns the current cell at the iterator's current position.
     * @return
     */
    public LogicalCell current() {
        if (!hasNext()) {
            throw new NoSuchElementException("Iterator is out of range");
        }
        // Read-only by default. Any write is triggered lazily by LogicalCell setters.
        return new LogicalCell(this, rowIndex, columnIndex);
    }

    public void foreachRemaining(LogicalCellConsumer consumer) {
        if (consumer == null) {
            throw new NullPointerException("Consumer can not be null");
        }
        while (hasNext()) {
            consumer.accept(current());
            next();
        }
    }

    public boolean hasNext() {
        return rowIndex < endRowExclusive && columnIndex < endColumnExclusive;
    }

    public CellIterator next() {
        if (!hasNext()) {
            return this;
        }

        // If the current logical cell was prepared for writing, we can advance cursors in O(1) for the next cell.
        if (currentPrepared) {
            colCursor.index++;
            colCursor.begin = columnIndex + 1;
        }

        // Advance logical coordinates.
        columnIndex++;
        if (columnIndex >= endColumnExclusive) {
            columnIndex = startColumn;
            rowIndex++;

            if (currentPrepared) {
                // We had split the current row into a single row run; move row cursor forward by one row.
                rowCursor.index++;
                rowCursor.begin = rowIndex;
            } else {
                // If we never prepared the row for writing, we don't know the correct cursor position.
                // Realign lazily on first write within the new row.
                this.rowCursor = alignRowCursor(rowIndex);
            }

            this.currentRow = null;
            this.colCursor = new ColCursor();
        }
        currentPrepared = false;
        return this;
    }

    void ensureWritableCurrent(int expectedRow, int expectedColumn) {
        if (expectedRow != rowIndex || expectedColumn != columnIndex) {
            throw new IllegalStateException("LogicalCell is stale; call current() again before writing");
        }
        prepareWritableCurrent();
    }

    Cell writableCellAtCurrent(int expectedRow, int expectedColumn) {
        ensureWritableCurrent(expectedRow, expectedColumn);
        return currentCell;
    }

    Cell readOnlyCellAt(int expectedRow, int expectedColumn) {
        if (expectedRow != rowIndex || expectedColumn != columnIndex) {
            throw new IllegalStateException("LogicalCell is stale; call current() again before reading");
        }

        final Cell[] holder = new Cell[1];
        sheet.getCellStore().iterateReadOnly(rowIndex, 1, columnIndex, 1, (cell, r, c) -> holder[0] = cell);
        return holder[0];
    }

    private void prepareWritableCurrent() {
        if (currentPrepared) {
            return;
        }
        // Materialize a writable row/cell for this logical coordinate using cursors,
        // splitting RLE runs so each logical cell maps to exactly one physical Cell.
        currentRow = acquireWritableRowAt(rowIndex, rowCursor);
        currentCell = acquireWritableLogicalCellAt(currentRow.cells, sheet.getMaxColumns(), columnIndex, colCursor);
        GroupCell group = currentCell.getGroup();
        if (group != null) {
            currentCell = group.getCell();
        }
        currentPrepared = true;
    }

    private RowCursor alignRowCursor(int rowStart) {
        RowCursor cursor = new RowCursor();
        List<Row> rows = sheet.getRowsInternal();
        while (cursor.index < rows.size()) {
            int width = rows.get(cursor.index).num_repeated;
            // Read-only alignment: do not mutate row RLE runs (no removal/splitting).
            if (width <= 0) {
                cursor.index++;
                continue;
            }
            if (cursor.begin + width > rowStart) {
                break;
            }
            cursor.begin += width;
            cursor.index++;
        }
        return cursor;
    }

    private Row acquireWritableRowAt(int targetRow, RowCursor cursor) {
        List<Row> rows = sheet.getRowsInternal();
        while (cursor.index < rows.size()) {
            int width = rows.get(cursor.index).num_repeated;
            if (width <= 0) {
                rows.remove(cursor.index);
                continue;
            }
            if (cursor.begin + width > targetRow) {
                break;
            }
            cursor.begin += width;
            cursor.index++;
        }
        if (cursor.index >= rows.size()) {
            Row row = new Row();
            row.num_repeated = 1;
            rows.add(row);
            cursor.index = rows.size() - 1;
            cursor.begin = targetRow;
            return row;
        }
        if (cursor.begin < targetRow) {
            splitField(rows, cursor.index, targetRow - cursor.begin);
            cursor.index++;
            cursor.begin = targetRow;
        }
        Row rowObj = rows.get(cursor.index);
        if (rowObj.num_repeated > 1) {
            splitField(rows, cursor.index, 1);
            rowObj = rows.get(cursor.index);
        }
        return rowObj;
    }

    private Cell acquireWritableLogicalCellAt(List<Cell> cells, int colEnd, int targetCol, ColCursor cursor) {
        while (cursor.index < cells.size()) {
            int width = cells.get(cursor.index).num_repeated;
            if (width <= 0) {
                cells.remove(cursor.index);
                continue;
            }
            if (cursor.begin + width > targetCol) {
                break;
            }
            cursor.begin += width;
            cursor.index++;
        }
        if (cursor.index >= cells.size()) {
            if (cursor.begin < targetCol) {
                Cell pad = new Cell();
                pad.num_repeated = targetCol - cursor.begin;
                cells.add(pad);
                cursor.begin = targetCol;
            }
            Cell tail = new Cell();
            tail.num_repeated = colEnd - targetCol;
            cells.add(tail);
            cursor.index = cells.size() - 1;
        } else if (cursor.begin < targetCol) {
            splitField(cells, cursor.index, targetCol - cursor.begin);
            cursor.index++;
            cursor.begin = targetCol;
        }

        Cell entry = cells.get(cursor.index);
        if (entry.num_repeated > 1) {
            splitField(cells, cursor.index, 1);
            entry = cells.get(cursor.index);
        }
        return entry;
    }

    /**
     * Splits fields[idx] into two entries: [0, splitPoint) and [splitPoint, num_repeated).
     */
    private <T extends TableField> void splitField(List<T> fields, int idx, int splitPoint) {
        T entry = fields.get(idx);
        @SuppressWarnings("unchecked")
        T tail = (T) entry.clone();
        tail.num_repeated = entry.num_repeated - splitPoint;
        entry.num_repeated = splitPoint;
        fields.add(idx + 1, tail);
    }

    private static final class RowCursor {
        private int index;
        private int begin;
    }

    private static final class ColCursor {
        private int index;
        private int begin;
    }
}


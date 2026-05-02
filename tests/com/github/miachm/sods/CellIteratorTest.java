package com.github.miachm.sods;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

public class CellIteratorTest {

    @Test
    public void testRangeBeginIteratesOnlyWithinBounds() {
        Sheet sheet = new Sheet("A");
        sheet.appendRows(4);
        sheet.appendColumns(5);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                sheet.getRange(i, j).setValue(i*j);
            }
        }

        int[] visited = {0};
        Range range = sheet.getRange(1, 1, 2, 3); // rows 1..2, cols 1..3
        range.forEach((cell) -> {
            int expectedRow = 1 + (visited[0] / 3);
            int expectedCol = 1 + (visited[0] % 3);
            assertEquals(cell.getRow(), expectedRow);
            assertEquals(cell.getColumn(), expectedCol);
            assertEquals(cell.getValue(), expectedRow*expectedCol);

            visited[0]++;
        });

        assertEquals(visited[0], 6);
    }

    @Test
    public void testRangeSet() {
        Sheet sheet = new Sheet("A");
        sheet.appendRows(4);
        sheet.appendColumns(5);

        Style style = new Style();
        style.setBold(true);

        sheet.getDataRange().forEach((cell) -> {
            cell.setValue(cell.getRow()*cell.getColumn());
            cell.setStyle(style);
        });

        sheet.getDataRange().forEach((cell) -> {
            assertEquals(cell.getStyle(), style);
            assertEquals(cell.getValue(), cell.getRow()*cell.getColumn());
        });
    }
}


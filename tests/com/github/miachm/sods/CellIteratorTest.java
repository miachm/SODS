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

        int[] visited = {0};
        Range range = sheet.getRange(1, 1, 2, 3); // rows 1..2, cols 1..3
        range.forEach((cell) -> {
            int expectedRow = 1 + (visited[0] / 3);
            int expectedCol = 1 + (visited[0] % 3);
            assertEquals(cell.getRow(), expectedRow);
            assertEquals(cell.getColumn(), expectedCol);

            visited[0]++;
        });

        assertEquals(visited[0], 6);
    }
}


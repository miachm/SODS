import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ReproduceSetValuesPerformance {

    public static void main(String[] args) throws IOException {
        int rows = 100000;
        int columns = 1;
        String[][] data = new String[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                data[i][j] = i + ";" + j;
            }
        }
        SpreadSheet out = new SpreadSheet();
        Sheet sheet = new Sheet("Sheet1", rows, columns);
        out.appendSheet(sheet);
        long startNano = System.nanoTime();
        sheet.getDataRange().setValues(data);
        double endNano = (double) (System.nanoTime() - startNano) / 1_000_000_000;
        System.out.println("Time for setValues: " + endNano + " seconds");
        
        // Debug check
        System.out.println("Cell(0,0) identity: " + System.identityHashCode(sheet.getRange(0, 0).getValue()));
        System.out.println("Cell(1,0) identity: " + System.identityHashCode(sheet.getRange(1, 0).getValue()));
        
        long t1 = System.currentTimeMillis();
        out.save(new File("testExport.ods"));
        long t2 = System.currentTimeMillis();
        System.out.println("Time for save: " + (t2 - t1) + " ms");

        // Verify some values
        Object[][] values = sheet.getDataRange().getValues();
        System.out.println("Value at 0,0: " + values[0][0]);
        System.out.println("Value at 1,0: " + values[1][0]);
        System.out.println("Value at 2,0: " + values[2][0]);
        System.out.println("Value at last row: " + values[rows-1][0]);
        
        if (values[0][0].equals(values[1][0])) {
            System.err.println("ERROR: All cells have the same value!");
        } else {
            System.out.println("SUCCESS: Cells have different values.");
        }
    }
}

import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ReproducePerformanceIssue {

    public static final int ROWS = 100000;
    public static final int COLUMNS = 1;

    public static void main(String[] args) throws IOException {
        long t1 = System.currentTimeMillis();
        SpreadSheet spreadSheet = new SpreadSheet();
        // Create sheet with exact dimensions to avoid append overhead in this test
        Sheet sheet = new Sheet("Test", ROWS, COLUMNS);
        System.out.println("Sheet size before save: " + sheet.getMaxRows() + " rows");
        
        sheet.getRange(0, 0, ROWS, COLUMNS).setValue("test");
        
        spreadSheet.appendSheet(sheet);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        spreadSheet.save(output);
        long t2 = System.currentTimeMillis();
        System.out.println("Sheet creation and save: " + (t2 - t1) + " ms");

        byte[] bytes = output.toByteArray();
        
        long t3 = System.currentTimeMillis();
        SpreadSheet loaded = new SpreadSheet(new ByteArrayInputStream(bytes));
        long t4 = System.currentTimeMillis();
        System.out.println("Loading sheet (parsing): " + (t4 - t3) + " ms");
        
        Sheet loadedSheet = loaded.getSheet(0);
        System.out.println("Loaded sheet rows: " + loadedSheet.getMaxRows());
        
        long t5 = System.currentTimeMillis();
        Object[][] values = loadedSheet.getDataRange().getValues();
        long t6 = System.currentTimeMillis();
        System.out.println("getValues() took: " + (t6 - t5) + " ms for " + values.length + " rows");
    }
}

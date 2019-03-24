import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

public class BenchmarkWritting {

    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();

        Random random = new Random();
        SpreadSheet spreadSheet = new SpreadSheet();
        Sheet sheet = new Sheet("Test");
        sheet.appendRows(10000);
        sheet.appendColumns(40);

        for (int i = 0; i < sheet.getMaxRows(); i++) {
            for (int j = 0; j < sheet.getMaxColumns(); j++) {
                Range range = sheet.getRange(i,j);
                range.setValue(random.nextDouble());
            }
        }
        spreadSheet.appendSheet(sheet);

        long t2 = System.currentTimeMillis();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            spreadSheet.save(output);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        long t3 = System.currentTimeMillis();

        System.out.println("SETTING VALUES: " + (t2-t1) + " ms");
        System.out.println("GENERATING ODS: " + (t3-t1) + " ms");

    }
}

import com.github.miachm.sods.Color;
import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

public class Benchmark {

    public static void main(String[] args) {
        byte[] testInput = writeTest();
        readTest(testInput);
    }

    private static byte[] writeTest() {
        long t1 = System.currentTimeMillis();
        SpreadSheet spreadSheet = getRandomSpreadSheet();
        long t2 = System.currentTimeMillis();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            spreadSheet.save(output);
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }

        long t3 = System.currentTimeMillis();

        System.out.println("SETTING VALUES: " + (t2-t1) + " ms");
        System.out.println("GENERATING ODS: " + (t3-t2) + " ms");

        return output.toByteArray();
    }

    private static void readTest(byte[] bytes) {
        long t1 = System.currentTimeMillis();
        try {
            new SpreadSheet(new ByteArrayInputStream(bytes));
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        long t2 = System.currentTimeMillis();

        System.out.println("LOADING ODS: " + (t2-t1) + " ms");
    }

    private static SpreadSheet getRandomSpreadSheet() {
        Random random = new Random();
        SpreadSheet spreadSheet = new SpreadSheet();
        Sheet sheet = new Sheet("Test");
        sheet.appendRows(10000);
        sheet.appendColumns(40);

        for (int i = 0; i < sheet.getMaxRows(); i++) {
            for (int j = 0; j < sheet.getMaxColumns(); j++) {
                Range range = sheet.getRange(i,j);
                range.setValue(random.nextDouble());

                if (random.nextInt(100) < 35) {
                    range.setFontBold(random.nextBoolean());
                    range.setFontItalic(random.nextBoolean());
                    range.setFontUnderline(random.nextBoolean());

                    if (random.nextBoolean())
                        range.setBackgroundColor(getRandomColor(random));
                    if (random.nextBoolean())
                        range.setFontSize(random.nextInt(42) + 1);
                    if (random.nextBoolean())
                        range.setFontColor(getRandomColor(random));
                }
            }
        }
        spreadSheet.appendSheet(sheet);
        return spreadSheet;
    }

    private static Color getRandomColor(Random random)
    {
        int red = random.nextInt(255);
        int green = random.nextInt(255);
        int blue = random.nextInt(255);
        return new Color(red, green, blue);
    }
}

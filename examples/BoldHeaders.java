import com.github.miachm.sods.*;

import java.io.File;
import java.io.IOException;

public class BoldHeaders {

    public static void main(String args[]) throws IOException {
        Sheet sheet = new Sheet("Sales", 11, 3);

        Range headerRange = sheet.getRange(0, 0, 1, 3);
        headerRange.setValues("Product", "Qty", "Total");

        for (int i = 1; i <= 9; i++) {
            sheet.getRange(i, 0).setValue("Item " + i);
            sheet.getRange(i, 1).setValue(i * 10);
            sheet.getRange(i, 2).setFormula("B" + (i + 1) + "*2"); // fake price calc
        }

        sheet.getRange(10, 0).setValue("Total");
        sheet.getRange(10, 1).setFormula("SUM(B2:B10)");
        sheet.getRange(10, 2).setFormula("SUM(C2:C10)");

        Style headerStyle = new Style();
        headerStyle.setBold(true);
        headerStyle.setBackgroundColor(new Color(220, 220, 220));
        headerRange.setStyle(headerStyle);

        Style totalStyle = new Style();
        totalStyle.setBold(false);
        totalStyle.setBackgroundColor(new Color(200, 255, 200));
        sheet.getRange(1, 0, 10, 3).setStyle(totalStyle);
        sheet.getRange(10, 0).setFontBold(true);

        SpreadSheet spread = new SpreadSheet();
        spread.appendSheet(sheet);
        spread.save(new File("styled-summary.ods"));
    }
}

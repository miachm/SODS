import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;

import java.io.File;
import java.io.IOException;


/* This sample creates a new Spreadsheet with some default values;
 */
public class SaveOds {
    public static void main(String args[]){
        try {
            Sheet sheet = new Sheet("A");

            // Implicitly, sheet already contains a row/column (1x1).
            // So, if we append 2 rows and 2 columns. We will have a 3x3 sheet.

            sheet.appendRows(2);
            sheet.appendColumns(2);

            sheet.getDataRange().setValues(1,2,3,4,5,6,7,8,9);

            // Set the underline style in the (3,3) cell
            sheet.getRange(2,2).setFontUnderline(true);

            // Set a bold font to the first 2x2 grid
            sheet.getRange(0,0,2,2).setFontBold(true);

            SpreadSheet spread = new SpreadSheet();
            spread.appendSheet(sheet);
            spread.save(new File("Out.ods"));
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
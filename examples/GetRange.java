import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;

import java.io.File;
import java.io.IOException;
import java.util.List;

/* A really basic example of the SODS library.

It loads a sample ODS file and prints his content in the stdout
 */

public class GetRange {
    public static void main(String args[]){
        int rows = 3;
        int columns = 3;
        Sheet sheet = new Sheet("A", rows, columns);

        /* Sheet Content:
        null | null
        null | null
        */

        System.out.println("Single cell ----");

        sheet.getRange(1,1).setValue("A"); // Using X,Y coordinates. They start at 0,0.
        /* Sheet Content:
        null | null
        null | "A"
        */
        System.out.println(sheet.getRange("B2").getValue()); // Using A1 Notation access the cell 1,1. It will return the same value we set previously

        sheet.getRange(0,0,2,2).setValue("B"); // Using X,Y coordinates. Reference a 2x2 sub-matrix in the spreadsheet and applying "B"
        /* Sheet Content:
        "B" | "B"
        "B" | "B"
        */

        // Let's print
        Object[][] values = sheet.getRange("A1:B2").getValues(); // Using A1 Notation to return the 2x2 sub-matrix
        System.out.println("Printing matrix ----");
        for (Object[] row : values) {
            for (Object value : row) {
                System.out.print(value);
            }
            System.out.println();
        }

    }
}
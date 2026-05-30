import com.github.miachm.sods.OdsOptionParameters;
import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;

import java.io.File;
import java.io.IOException;

/*
 * This example opens an ODS file protected with a password.
 *
 * It expects a file named password-protected.ods in the current directory.
 * You can create that file by running PasswordProtectedFile.java first.
 */
public class ReadPasswordProtectedFile {
    public static void main(String args[]) {
        try {
            OdsOptionParameters options = new OdsOptionParameters();
            options.setPassword("cosita");

            SpreadSheet spread = new SpreadSheet(new File("resources/password-protected.ods"), options);
            System.out.println("Number of sheets: " + spread.getNumSheets());

            for (Sheet sheet : spread.getSheets()) {
                System.out.println("In sheet " + sheet.getName());

                Range range = sheet.getDataRange();
                System.out.println(range.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;

import java.io.File;
import java.io.IOException;

/*
 * This example creates an ODS file protected with a password.
 *
 * The generated file is written to password-protected.ods and can be opened
 * with the password "cosita".
 */
public class PasswordProtectedFile {
    public static void main(String args[]) {
        try {
            Sheet sheet = new Sheet("Private data", 3, 3);
            sheet.getDataRange().setValues(
                    "Name", "Role", "Score",
                    "Alice", "Admin", 95,
                    "Bob", "User", 87);

            SpreadSheet spread = new SpreadSheet();
            spread.appendSheet(sheet);

            // Set the password before saving the file.
            spread.setDocumentPassword("cosita");
            spread.save(new File("resources/password-protected.ods"));

            System.out.println("Created resources/password-protected.ods");
            System.out.println("Password: cosita");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ForEach {
    
    public static void main(String args[]){
        try {
            readMode();
            writeMode();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void readMode() throws IOException {
        SpreadSheet spread = new SpreadSheet(new File("resources/BasicExample.ods"));
        System.out.println("Number of sheets: " + spread.getNumSheets());

        List<Sheet> sheets = spread.getSheets();

        for (Sheet sheet : sheets) {
            System.out.println("In sheet " + sheet.getName());
            sheet.getDataRange().forEach(cell -> {
                System.out.println("Cell: " + cell.getValue());
            });
        }
    }

    private static void writeMode() throws IOException {
        SpreadSheet spread = new SpreadSheet();
        Sheet sheet = new Sheet("A", 10, 10);
        sheet.getDataRange().forEach(cell -> {
            cell.setValue("Hello, World (" + cell.getRow() + ", " + cell.getColumn() + ")");
        });
        spread.appendSheet(sheet);
        spread.save(new File("out.ods"));
    }
}

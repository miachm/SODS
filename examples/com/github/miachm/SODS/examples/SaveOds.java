package com.github.miachm.SODS.examples;

import com.github.miachm.SODS.spreadsheet.Sheet;
import com.github.miachm.SODS.spreadsheet.SpreadSheet;

import java.io.File;
import java.io.IOException;

public class SaveOds {
    public static void main(String args[]){
        try {
            Sheet sheet = new Sheet("A");

            // Implicitly, sheet already contains a row/column (1x1).
            // So, if we append 2 rows and 2 columns. We will have a 3x3 sheet.

            sheet.appendRows(2);
            sheet.appendColumns(2);

            sheet.getDataRange().setValues(1,2,3,4,5,6,7,8,9);
            SpreadSheet spread = new SpreadSheet();
            spread.appendSheet(sheet);
            spread.save(new File("Out.ods"));
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

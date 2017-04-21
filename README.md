# SODS
A simple library for and process ODS files in Java.

# What is an ODS?
ODS means Open Document Spreadsheet. It's used in applications like Libreoffice or Open Office.

# What is the motivation of this?
I needed to generate ODS files in Java. I looked for libraries, but they are:

- Deprecated or dead, like Apache ODF Toolkit. It's not working with Java 8.
- It has Libre Office as dependency in the user's computer. That's so much (Libre Office Api).
- Poorly designed or bloated, like jopendocument. I really had bad time with this library.

So, i decided create my own library from scratch. Everything is possible!

# What is the current state?
It's not able to real use yet. Right now you can:

- Load the cell's values from an ODS file.
- Manipulate these values and print them.

This is an example of use:

```java
package com.github.miachm.SODS.examples;

import com.github.miachm.SODS.spreadsheet.Range;
import com.github.miachm.SODS.spreadsheet.Sheet;
import com.github.miachm.SODS.spreadsheet.SpreadSheet;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BasicUsage{
    public static void main(String args[]){
        try {
            SpreadSheet spread = new SpreadSheet(new File("BasicExample.ods"));
            System.out.println("Number of sheets: " + spread.getNumSheets());

            List<Sheet> sheets = spread.getSheets();

            for (Sheet sheet : sheets) {
                System.out.println("In sheet " + sheet.getName());

                Range range = sheet.getDataRange();
                System.out.println(range.toString());
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
```

Contributions are welcome!

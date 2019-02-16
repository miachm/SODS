# SODS

[![Build Status](https://travis-ci.org/miachm/SODS.svg?branch=master)](https://travis-ci.org/miachm/SODS)

A simple library for process ODS files in Java.

# What is an ODS?
ODS means Open Document Spreadsheet. It's used in applications like Libreoffice or Open Office.

![Libreoffice Calc](http://i.imgur.com/Mm779of.jpg)

# What is the motivation of this?
I needed to generate ODS files in Java. I looked for libraries, but they are:

- Deprecated or dead, like Apache ODF Toolkit. It's not working with Java 8.
- It has Libre Office as dependency in the user's computer. That's so much (Libre Office Api).
- Poorly designed or bloated.

So, i decided create my own library from scratch. The objetive is load and generate ODS files in a simple and easy way.

# What is the current state?
Right now you can:

- Load the cell's values from an ODS file.
- Manipulate these values.
- Some formating (bold fonts, italic style, underline...).
- Save it back to an ODS file.

This is an example of an ODS file in LibreOffice
![Sample Libreoffice](https://i.imgur.com/avJ8aLw.png)

Here, i am using SODS and rendering it with JavaFX

![Sample SODS](https://i.imgur.com/Myfustx.png)

# How it works?
This is a code example:

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
            SpreadSheet spread = new SpreadSheet(new File("resources/BasicExample.ods"));
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

Check more examples [here](https://github.com/miachm/SODS/tree/master/src/com/github/miachm/SODS/examples)

Contributions are welcome!

# SODS

[![Build Status](https://travis-ci.org/miachm/SODS.svg?branch=master)](https://travis-ci.org/miachm/SODS)
[![Javadocs](http://javadoc-badge.appspot.com/com.github.miachm.sods/SODS.svg?label=javadocs)](https://miachm.github.io/SODS/)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.miachm.sods/SODS.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.miachm.sods%22%20AND%20a:%22SODS%22)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)


A simple library for process ODS files in Java. It allows read/write ODS files.

## Install

### Maven
If you use maven, you can add it to your pom file:

```xml
<dependency>
    <groupId>com.github.miachm.sods</groupId>
    <artifactId>SODS</artifactId>
    <version>1.2.1</version>
</dependency>
```

### Manually

Check out [releases section](https://github.com/miachm/SODS/releases) for download the lastest release in a JAR file. After that, you can add the jar in your java project.

Here some help in differents IDE:

- [Intellij](https://stackoverflow.com/a/33589628/2489715)
- [Eclipse](https://stackoverflow.com/a/3280384/2489715)
- [Netbeans](https://www.quora.com/How-can-I-add-jar-files-to-libraries-in-Netbeans-IDE8-0).

## Docs
You can access the javadocs [here](https://miachm.github.io/SODS/)

There is an [examples folder](https://github.com/miachm/SODS/tree/master/examples) where you can read codes samples.

## F.A.Q

### What is an ODS?
ODS means Open Document Spreadsheet. It's used in applications like Libreoffice or Open Office.

![Libreoffice Calc](http://i.imgur.com/Mm779of.jpg)

### What is the motivation of this?
I needed to generate ODS files in Java. I looked for libraries, but they are:

  - Deprecated or dead, like Apache ODF Toolkit. It's not working with Java 8.
  - It has Libre Office as dependency in the user's computer. That's so much (Libre Office Api).
  - Poorly designed or bloated.

So, i decided create my own library from scratch. The objetive is load and generate ODS files in a simple and easy way.

### What is the current state?
Right now you can:

  - Load the cell's values from an ODS file.
  - Manipulate these values.
  - Some formating (bold fonts, italic style, underline...).
  - Save it back to an ODS file.

This is an example of an ODS file in LibreOffice
![Sample Libreoffice](https://i.imgur.com/avJ8aLw.png)

Here, i am using SODS for load the file and rendering it with JavaFX

![Sample SODS](https://i.imgur.com/Myfustx.png)

### How it works?
This is a code example:

```java
package com.github.miachm.sods.examples;

import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;

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

Check more examples [here](https://github.com/miachm/SODS/tree/master/examples)

Contributions are welcome!

###  Copyright {2019} {Miguel Chacon}
This project is released under APACHE LICENSE. More info in [LICENSE](https://github.com/miachm/SODS/blob/master/LICENSE).

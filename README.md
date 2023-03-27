# SODS

![Build Status](https://github.com/miachm/SODS/actions/workflows/maven.yml/badge.svg)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.miachm.sods/SODS.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.miachm.sods%22%20AND%20a:%22SODS%22)
[![License: Unlicense](https://img.shields.io/badge/license-Unlicense-blue.svg)](http://unlicense.org/)


A simple library for process ODS files in Java. It allows read/write ODS files.

## Install

### Maven 
```xml
<dependency>
    <groupId>com.github.miachm.sods</groupId>
    <artifactId>SODS</artifactId>
    <version>1.5.3</version>
</dependency>
```
### Gradle Kotlin DSL
```kotlin
implementation("com.github.miachm.sods:SODS:1.5.3")
```
### Gradle Groovy DSL
```groovy
implementation 'com.github.miachm.sods:SODS:1.5.3'
```
### [Other Dependency Management](https://search.maven.org/artifact/com.github.miachm.sods/SODS/1.5.2/jar)

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

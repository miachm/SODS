# SODS

[![Build Status](https://github.com/miachm/SODS/actions/workflows/maven.yml/badge.svg)](https://github.com/miachm/SODS/actions/workflows/maven.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.miachm.sods/SODS.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.github.miachm.sods/SODS)
[![Javadoc](https://img.shields.io/badge/JavaDoc-Online-green)](https://miachm.github.io/SODS/)
[![License: Unlicense](https://img.shields.io/badge/license-Unlicense-blue.svg)](http://unlicense.org/)

A simple Java library for reading and writing ODS (OpenDocument Spreadsheet) files. It supports parsing spreadsheets, editing cells and sheets, applying formatting, images, charts and saving the updated ODS documents back.

## Install

### Maven 
```xml
<dependency>
    <groupId>com.github.miachm.sods</groupId>
    <artifactId>SODS</artifactId>
    <version>1.8.1</version>
</dependency>
```
### Gradle Kotlin DSL
```kotlin
implementation("com.github.miachm.sods:SODS:1.8.1")
```
### Gradle Groovy DSL
```groovy
implementation 'com.github.miachm.sods:SODS:1.8.1'
```
### [Other Dependency Management](https://search.maven.org/artifact/com.github.miachm.sods/SODS/1.8.1/jar)

## Docs
You can access the javadocs [here](https://miachm.github.io/SODS/)

There is an [examples folder](https://github.com/miachm/SODS/tree/master/examples) where you can read codes samples.

## F.A.Q

### What is an ODS?
ODS means Open Document Spreadsheet. It's used in applications like Libreoffice or Open Office.

![Libreoffice Calc](http://i.imgur.com/Mm779of.jpg)

### Minimum JDK needed?

8

### What is the motivation of this?
I needed to generate ODS files in Java. I looked for libraries, but they are:

  - Deprecated or dead, like Apache ODF Toolkit. It's not working with Java 8.
  - It has Libre Office as dependency in the user's computer. That's so much (Libre Office Api).
  - Poorly designed or bloated.

So, i decided create my own library from scratch. The objetive is load and generate ODS files in a simple and easy way.

### What is the current state?
Right now you can:

- Reading existing ODS files (cell values and structure)
- Manipulating cell values and sheet layout (create, remove and rename sheets).
- Applying rich formatting, including:
    - Bold, italic, underline and line through styles
    - Font size and color
    - Cell background color
    - Borders
    - Horizontal and vertical text alignment
    - Text wrapping
    - Conditional formatting
- Reading and writing chart objects with series data (Bar, Line...)
- Embedding images into sheets.
- Apply hashed password protection
- Writing changes back to a new or existing ODS file

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

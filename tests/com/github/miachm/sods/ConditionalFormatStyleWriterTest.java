// SPDX-FileType: SOURCE
// SPDX-License-Identifier: Unlicense

package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.testng.AssertJUnit.assertEquals;

public class ConditionalFormatStyleWriterTest {

    @Test
    public void sharedConditionalFormatTargetStyleIsWrittenOnceInStylesXml()
            throws IOException {
        // Two different cells/owner styles both have a conditional format
        // pointing at the SAME target Style instance. styles.xml must
        // define that target style's style:style element exactly once --
        // duplicate style:name entries are invalid ODF.
        Style target = new Style();
        target.setBold(true);

        Sheet sheet = new Sheet("A", 2, 1);

        Style owner1 = new Style();
        owner1.setItalic(true);
        owner1.addCondition(
                ConditionalFormat.conditionWhenValueIsGreater(target, 10));
        Range range1 = sheet.getRange(0, 0);
        range1.setValue(50);
        range1.setStyle(owner1);

        Style owner2 = new Style();
        owner2.setUnderline(true);
        owner2.addCondition(
                ConditionalFormat.conditionWhenValueIsGreater(target, 100));
        Range range2 = sheet.getRange(1, 0);
        range2.setValue(5);
        range2.setStyle(owner2);

        SpreadSheet spreadSheet = new SpreadSheet();
        spreadSheet.appendSheet(sheet);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        spreadSheet.save(output);

        // Check that styles.xml contains no duplicate style:name entries
        String stylesXml = readEntry(output.toByteArray(), "styles.xml");

        Matcher nameMatcher = Pattern.compile(
                "<style:style[^>]*style:name=\"([^\"]+)\"")
                .matcher(stylesXml);
        java.util.List<String> names = new java.util.ArrayList<>();
        while (nameMatcher.find()) {
            names.add(nameMatcher.group(1));
        }

        Set<String> uniqueNames = new HashSet<>(names);
        assertEquals(
                "styles.xml contains duplicate style:name entries for a "
                        + "shared conditional-format target style: " + names,
                uniqueNames.size(), names.size());
    }

    private static String readEntry(byte[] ods, String entryName)
            throws IOException {
        ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(ods));
        try {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().equals(entryName)) {
                    StringBuilder builder = new StringBuilder();
                    Reader reader =
                            new InputStreamReader(zip, StandardCharsets.UTF_8);
                    char[] buffer = new char[4096];
                    int read;
                    while ((read = reader.read(buffer)) != -1) {
                        builder.append(buffer, 0, read);
                    }
                    return builder.toString();
                }
            }
        } finally {
            zip.close();
        }
        throw new IOException(
                entryName + " not found in the generated ODS file");
    }
}

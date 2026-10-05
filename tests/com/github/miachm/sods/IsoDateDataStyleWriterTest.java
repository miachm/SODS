// SPDX-FileType: SOURCE
// SPDX-License-Identifier: Unlicense

package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.testng.AssertJUnit.assertTrue;

public class IsoDateDataStyleWriterTest {

    @Test
    public void isoDateStyleZeroPadsDay() throws IOException {
        Sheet sheet = new Sheet("A", 1, 1);
        sheet.getRange(0, 0).setValue(LocalDate.of(2026, 10, 5));
        SpreadSheet spreadSheet = new SpreadSheet();
        spreadSheet.appendSheet(sheet);
        ByteArrayOutputStream ods = new ByteArrayOutputStream();
        spreadSheet.save(ods);

        // A short day renders 2026-10-5; ISO 8601 needs 2026-10-05
        Matcher dateStyle = Pattern.compile(
                "<number:date-style style:name=\"datestyle\">.*?</number:date-style>", Pattern.DOTALL)
                .matcher(readEntry(ods.toByteArray(), "content.xml"));
        assertTrue("datestyle not written", dateStyle.find());
        assertTrue(dateStyle.group(), dateStyle.group().contains("<number:day number:style=\"long\""));
    }

    private static String readEntry(byte[] ods, String name) throws IOException {
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(ods))) {
            for (ZipEntry entry; (entry = zip.getNextEntry()) != null; ) {
                if (entry.getName().equals(name)) {
                    return new Scanner(zip, "UTF-8").useDelimiter("\\A").next();
                }
            }
        }
        throw new IOException(name + " not found");
    }
}

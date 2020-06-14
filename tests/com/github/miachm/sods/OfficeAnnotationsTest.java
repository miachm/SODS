package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.testng.AssertJUnit.assertEquals;

public class OfficeAnnotationsTest {
    @Test
    public void testBuilder()
    {
        OfficeAnnotationBuilder builder = new OfficeAnnotationBuilder();
        builder.setMsg("Test\nCas");
        builder.setLastModified(LocalDateTime.of(2010, 3, 2, 3, 2));

        OfficeAnnotation annotation = builder.build();
        assertEquals(annotation, new OfficeAnnotation("Test\nCas", LocalDateTime.of(2010, 3, 2, 3, 2)));
    }

    @Test
    public void testValueMixup() throws IOException {
        // Issue #22
        // Office Annotations and office values can be mixed up
        OfficeAnnotationBuilder builder = new OfficeAnnotationBuilder();
        builder.setMsg("Test\nCas");

        Sheet sheet = new Sheet("A");
        sheet.getRange(0, 0).setValue("HUNTER\nCAT");
        sheet.getRange(0, 0).setAnnotation(builder.build());

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        SpreadSheet spreadSheet = new SpreadSheet();
        spreadSheet.appendSheet(sheet);
        spreadSheet.save(output);

        spreadSheet = new SpreadSheet(new ByteArrayInputStream(output.toByteArray()));
        Range range = spreadSheet.getSheet(0).getRange(0, 0);

        assertEquals(range.getValue(), "HUNTER\nCAT");
        assertEquals(range.getAnnotation(), new OfficeAnnotation("Test\nCas", null));
    }
}

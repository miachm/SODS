package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

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

    @Test
    public void eachAnnotationLineGetsItsOwnTextParagraph() throws IOException {
        // Newlines in an annotation must be written as separate <text:p> elements,
        // otherwise LibreOffice collapses the line breaks. Empty lines must produce
        // an empty <text:p/> element.
        Sheet sheet = new Sheet("A", 1, 1);
        sheet.getRange(0, 0).setAnnotation(new OfficeAnnotation("Hello\nNice\n\nWorld", null));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        SpreadSheet spreadSheet = new SpreadSheet();
        spreadSheet.appendSheet(sheet);
        spreadSheet.save(output);

        String content = readContentXml(output.toByteArray());

        int annotationStart = content.indexOf("<office:annotation");
        assertTrue("Annotation element not found in content.xml", annotationStart != -1);
        int annotationEnd = content.indexOf("</office:annotation>", annotationStart);
        assertTrue("Annotation end element not found in content.xml", annotationEnd != -1);
        String annotation = content.substring(annotationStart, annotationEnd);

        // One paragraph per line, including the empty third line as a self-closing element.
        assertTrue("Expected a paragraph for 'Hello'", annotation.contains("<text:p>Hello</text:p>"));
        assertTrue("Expected a paragraph for 'Nice'", annotation.contains("<text:p>Nice</text:p>"));
        assertTrue("Expected a paragraph for 'World'", annotation.contains("<text:p>World</text:p>"));
        assertTrue("Expected an empty paragraph for the blank line",
                annotation.contains("<text:p></text:p>") || annotation.contains("<text:p/>"));

        // The newlines must not survive verbatim inside a single paragraph.
        assertTrue("Newlines should not be written verbatim into the annotation text",
                !annotation.contains("Hello\nNice"));

        // The split-then-join round-trip must still reconstruct the original message.
        spreadSheet = new SpreadSheet(new ByteArrayInputStream(output.toByteArray()));
        Range range = spreadSheet.getSheet(0).getRange(0, 0);
        assertEquals(range.getAnnotation(), new OfficeAnnotation("Hello\nNice\n\nWorld", null));
    }

    private static String readContentXml(byte[] ods) throws IOException {
        ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(ods));
        try {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().equals("content.xml")) {
                    StringBuilder builder = new StringBuilder();
                    Reader reader = new InputStreamReader(zip, StandardCharsets.UTF_8);
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
        throw new IOException("content.xml not found in the generated ODS file");
    }
}

package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.testng.Assert.fail;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

public class CellCustomStyles {
    private Sheet saveAndLoad(Sheet sheet)
    {
        SpreadSheet spreadSheet = new SpreadSheet();
        spreadSheet.appendSheet(sheet);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            spreadSheet.save(outputStream);
        } catch (IOException e) {
            fail(e.getMessage());
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        SpreadSheet newSpreadSheet = null;
        try {
            newSpreadSheet = new SpreadSheet(inputStream);
        } catch (IOException e) {
            fail(e.getMessage());
        }

        return newSpreadSheet.getSheet(0);
    }

    @Test
    public void testAligment()
    {
        for (Style.TEXT_ALIGMENT aligment : Style.TEXT_ALIGMENT.values()) {
            Style style = new Style();
            assertNull(style.getTextAligment());
            style.setTextAligment(aligment);
            assertEquals(style.getTextAligment(), aligment);

            Sheet sheet = new Sheet("A", 2, 2);
            Range range = sheet.getRange(0, 0);
            range.setStyle(style);

            sheet = saveAndLoad(sheet);
            assertEquals(sheet.getRange(0, 0).getStyle().getTextAligment(), aligment);
        }
    }

    @Test
    public void testCustomFontSize() throws IOException {
        // Issue #21
        SpreadSheet spreadSheet = new SpreadSheet(new File("resources/floatfontsize.ods"));
        assertEquals(spreadSheet.getSheet(0).getDataRange().getStyle().getFontSize(), 4);
    }
}

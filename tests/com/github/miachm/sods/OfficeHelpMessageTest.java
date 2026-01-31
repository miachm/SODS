package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.testng.AssertJUnit.*;

/**
 * Tests for table:help-message (cell input help) support.
 */
public class OfficeHelpMessageTest {

    @Test
    public void testHelpMessageRoundTrip() throws IOException {
        SpreadSheet spread = new SpreadSheet();
        spread.appendSheet(new Sheet("Sheet1"));
        Sheet sheet = spread.getSheet(0);

        OfficeHelpMessage helpMsg = new OfficeHelpMessage("Hint", "Enter a value between 1 and 10");
        sheet.getRange(0, 0).setValue(5);
        sheet.getRange(0, 0).setHelpMessage(helpMsg);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        spread.save(out);
        spread.save(new File("test.ods"));
        SpreadSheet loaded = new SpreadSheet(new ByteArrayInputStream(out.toByteArray()));

        OfficeHelpMessage loadedMsg = loaded.getSheet(0).getRange(0, 0).getHelpMessage();
        assertNotNull(loadedMsg);
        assertEquals("Hint", loadedMsg.getTitle());
        assertEquals("Enter a value between 1 and 10", loadedMsg.getMessage());
        assertTrue(loadedMsg.isDisplay());
    }

    @Test
    public void testHelpMessageCopyTo() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.insertColumnsAfter(0, 3);
        sheet.insertRowAfter(0);

        OfficeHelpMessage helpMsg = new OfficeHelpMessage("Title", "Help text");
        sheet.getRange(0, 0).setValue("x");
        sheet.getRange(0, 0).setHelpMessage(helpMsg);

        Range src = sheet.getRange(0, 0, 2, 2);
        Range dest = sheet.getRange(0, 2, 2, 2);
        src.copyTo(dest);

        assertEquals(helpMsg, sheet.getRange(0, 2).getHelpMessage());
    }

    @Test
    public void testHelpMessageClear() {
        Sheet sheet = new Sheet("A");
        sheet.getRange(0, 0).setValue(1);
        sheet.getRange(0, 0).setHelpMessage(new OfficeHelpMessage("Help"));

        sheet.getRange(0, 0).clear();
        assertNull(sheet.getRange(0, 0).getHelpMessage());
    }
}

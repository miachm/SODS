package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.testng.AssertJUnit.*;

public class MacroTest {

    @Test
    public void macrosSurviveSaveAndReload() throws IOException {
        SpreadSheet spread = new SpreadSheet();
        spread.addMacro(new Macro("Module1", "BEGIN"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        spread.save(out);

        SpreadSheet reloaded = new SpreadSheet(new ByteArrayInputStream(out.toByteArray()));
        assertEquals(1, reloaded.getMacros().size());
        assertEquals("Module1", reloaded.getMacros().get(0).getName());
        assertEquals("BEGIN", reloaded.getMacros().get(0).getCode());
    }

    @Test
    public void macroZipFollowsLibreOfficeLayout() throws IOException {
        SpreadSheet spread = new SpreadSheet();
        spread.addMacro(new Macro("Module1", "Sub Main\nEnd Sub"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        spread.save(out);

        Set<String> entryNames = new HashSet<String>();
        ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(out.toByteArray()));
        ZipEntry entry;
        while ((entry = zip.getNextEntry()) != null) {
            entryNames.add(entry.getName());
        }
        zip.close();

        assertTrue(entryNames.contains("Basic/script-lc.xml"));
        assertTrue(entryNames.contains("Basic/Standard/script-lb.xml"));
        assertTrue(entryNames.contains("Basic/Standard/Module1.xml"));
        assertFalse(entryNames.contains("Basic/Standard/script-lc.xml"));
    }

    @Test
    public void loadMacrosFalseSkipsMacros() throws IOException {
        SpreadSheet spread = new SpreadSheet();
        spread.addMacro(new Macro("Module1", "BEGIN"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        spread.save(out);

        OdsOptionParameters options = new OdsOptionParameters();
        options.setLoadMacros(false);
        SpreadSheet reloaded = new SpreadSheet(new ByteArrayInputStream(out.toByteArray()), options);
        assertTrue(reloaded.getMacros().isEmpty());
    }
}

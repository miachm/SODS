package com.github.miachm.sods;

import org.testng.annotations.Test;
import java.time.LocalDate;
import static org.testng.AssertJUnit.*;

public class CellDateStyleTest {

    @Test
    public void testDateValueMutatesDefaultStyle() {
        Sheet sheet = new Sheet("A", 2, 2);
        Range range1 = sheet.getRange(0, 0);
        Range range2 = sheet.getRange(0, 1);

        // Initially, both should have the default data style (null)
        assertNull(range1.getStyle().getDataStyle());
        assertNull(range2.getStyle().getDataStyle());

        // Set a LocalDate value to range1
        range1.setValue(LocalDate.now());

        // range1 should now have the ISO date data style
        assertEquals(Style.ISO_DATE_DATA_STYLE, range1.getStyle().getDataStyle());

        // BUG: range2 should still have null data style, but it might have been mutated if it shares the default style
        assertNull("Default style was mutated!", range2.getStyle().getDataStyle());
    }

    @Test
    public void testSetNotDateValueClearsDefaultStyle() {
        Sheet sheet = new Sheet("A", 2, 2);
        Range range1 = sheet.getRange(0, 0);
        Range range2 = sheet.getRange(0, 1);

        // Set a LocalDate value to range1, which might mutate the default style
        range1.setValue(LocalDate.now());
        assertEquals(Style.ISO_DATE_DATA_STYLE, range1.getStyle().getDataStyle());

        // Set a non-date value to range2. If it shares the mutated default style, it might clear it for EVERYONE.
        range2.setValue("not a date");
        
        // range1's style should still be ISO_DATE_DATA_STYLE
        assertEquals("Range1 style was cleared because Range2 was set to a non-date value!", 
                     Style.ISO_DATE_DATA_STYLE, range1.getStyle().getDataStyle());
    }
}

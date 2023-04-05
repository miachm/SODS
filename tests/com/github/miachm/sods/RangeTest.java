package com.github.miachm.sods;

import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.testng.Assert.fail;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

public class RangeTest {
    @Test
    public void testClear() throws Exception {
        Sheet sheet = new Sheet("A");
        Range range = sheet.getDataRange();
        range.setValue(1);
        range.clear();
        assertNull(range.getValue());

        sheet.insertColumnAfter(0);
        sheet.insertRowAfter(0);

        range = sheet.getDataRange();
        range.setValues(1, 2, 3, 4);
        range.clear();

        Object[][] values = range.getValues();

        for (Object[] row : values)
            for (Object value : row)
                assertNull(value);
    }

    @Test
    public void testCopyTo() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.insertColumnsAfter(0, 3);
        sheet.insertRowAfter(0);

        Range range = sheet.getDataRange();
        range.setValues(1, 2, 3, 4, 5, 6, 7, 8);

        Range origin = sheet.getRange(0, 0, 2, 2);
        Range dest = sheet.getRange(0, 2, 2, 2);
        origin.copyTo(dest);

        Object[][] values = dest.getValues();

        assertEquals(values[0][0], 1);
        assertEquals(values[1][0], 5);
        assertEquals(values[0][1], 2);
        assertEquals(values[1][1], 6);
    }

    @Test
    public void testGetCell() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.insertColumnsAfter(0, 2);
        sheet.insertRowAfter(0);

        Range range = sheet.getDataRange();
        range.setValues(1, 2, 3, 4, 5, 6);

        assertEquals(sheet.getRange(0, 0).getValue(), 1);
        assertEquals(sheet.getRange(0, 1).getValue(), 2);
        assertEquals(sheet.getRange(0, 2).getValue(), 3);
        assertEquals(sheet.getRange(1, 0).getValue(), 4);
        assertEquals(sheet.getRange(1, 1).getValue(), 5);
        assertEquals(sheet.getRange(1, 2).getValue(), 6);
    }

    @Test
    public void testGetColumn() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRows(10);
        sheet.appendColumns(10);

        for (int i = 0; i < sheet.getMaxRows() / 2; i++) {
            for (int j = 0; j < sheet.getMaxColumns() / 2; j++) {
                Range range = sheet.getRange(i, j, 2, 2);
                assertEquals(range.getColumn(), j);
            }
        }
    }

    @Test
    public void testGetFormula() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();

        final String formula = "=SUM(A1:A2)+A1";
        final String hardFormula = "=SQRT(A1) * A2";

        Range range = sheet.getDataRange();
        range.setFormulas(formula, hardFormula);

        assertEquals(range.getFormula(), formula);
        assertEquals(sheet.getRange(1, 0).getFormula(), hardFormula);
    }

    @Test
    public void testGetFormulas() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();

        final String formula = "=SUM(A1:A2)+A1";
        final String hardFormula = "=SQRT(A1) * A2";

        Range range = sheet.getDataRange();
        range.setFormulas(formula, hardFormula);

        String formulas[][] = range.getFormulas();
        assertEquals(formulas[0][0], formula);
        assertEquals(formulas[1][0], hardFormula);
    }

    @Test
    public void testGetLastColumn() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRows(10);
        sheet.appendColumns(10);

        for (int i = 0; i < sheet.getMaxRows() / 2; i++) {
            for (int j = 0; j < sheet.getMaxColumns() / 2; j++) {
                Range range = sheet.getRange(i, j, 2, 2);
                assertEquals(range.getLastColumn(), j + 1);
            }
        }
    }

    @Test
    public void testGetLastRow() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRows(10);
        sheet.appendColumns(10);

        for (int i = 0; i < sheet.getMaxRows() / 2; i++) {
            for (int j = 0; j < sheet.getMaxColumns() / 2; j++) {
                Range range = sheet.getRange(i, j, 2, 2);
                assertEquals(range.getLastRow(), i + 1);
            }
        }
    }

    @Test
    public void testGetNumColumns() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRows(10);
        sheet.appendColumns(10);

        for (int i = 0; i < sheet.getMaxRows() / 2; i++) {
            for (int j = 0; j < sheet.getMaxColumns() / 2; j++) {
                Range range = sheet.getRange(i, j, 3, 2);
                assertEquals(range.getNumColumns(), 2);
            }
        }
    }

    @Test
    public void testGetNumRows() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRows(10);
        sheet.appendColumns(10);

        for (int i = 0; i < sheet.getMaxRows() / 2; i++) {
            for (int j = 0; j < sheet.getMaxColumns() / 2; j++) {
                Range range = sheet.getRange(i, j, 3, 2);
                assertEquals(range.getNumRows(), 3);
            }
        }
    }

    @Test
    public void testGetRow() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRows(10);
        sheet.appendColumns(10);

        for (int i = 0; i < sheet.getMaxRows() / 2; i++) {
            for (int j = 0; j < sheet.getMaxColumns() / 2; j++) {
                Range range = sheet.getRange(i, j, 2, 2);
                assertEquals(range.getRow(), i);
            }
        }
    }

    @Test
    public void testGetSheet() throws Exception {
        Sheet sheet = new Sheet("A");
        Range range = sheet.getDataRange();
        Sheet parent = range.getSheet();

        assertEquals(sheet, parent);
    }

    @Test
    public void testGetStyle() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        Range range = sheet.getDataRange();
        assertEquals(range.getStyle(), new Style());

        Style style = new Style();
        style.setBold(true);
        range.setStyle(style);
        assertEquals(range.getStyle(), style);
    }


    @Test
    public void testGetStyles() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        Range range = sheet.getDataRange();

        Style[][] styles = new Style[2][1];
        styles[0][0] = new Style(true, false, false, null, new Color(2, 3, 4), 4);
        styles[1][0] = new Style(false, true, true, new Color(200, 100, 230), null, 5);
        range.setStyles(styles);

        Style[][] result = range.getStyles();
        assertEquals(result[0][0], styles[0][0]);
        assertEquals(result[1][0], styles[1][0]);
    }

    @Test
    public void testGetValue() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Range range = sheet.getDataRange();
        range.setValues(1, 2, 3, 4);

        assertEquals(range.getValue(), 1);

        range = sheet.getRange(1, 1);
        assertEquals(range.getValue(), 4);
    }

    @Test
    public void testGetValues() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Range range = sheet.getDataRange();
        range.setValues(1, 2, 3, 4);

        Object[][] arr = range.getValues();

        assertEquals(arr[0][0], 1);
        assertEquals(arr[0][1], 2);
        assertEquals(arr[1][0], 3);
        assertEquals(arr[1][1], 4);
    }

    @Test
    public void testGetNumValues() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRows(2);
        sheet.appendColumn();

        Range range = sheet.getDataRange();
        assertEquals(range.getNumValues(), 6);
    }

    @Test
    public void testSetFormula() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();

        final String formula = "=SUM(A1:A2)+A1";

        Range range = sheet.getDataRange();
        range.setFormula(formula);

        String formulas[][] = range.getFormulas();
        assertEquals(formula, formulas[0][0]);
        assertEquals(formula, formulas[1][0]);

        range = sheet.getRange(1, 0);
        range.setFormula(null);
        assertEquals(range.getFormula(), null);
        range.setFormula(formula);
        assertEquals(range.getFormula(), formula);
    }

    @Test
    public void testSetFormulas() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();

        final String formula = "=SUM(A1:A2)+A1";
        final String hardFormula = "= A2 - COUNT(A1:A2)";

        Range range = sheet.getDataRange();
        range.setFormulas(formula, hardFormula);

        String formulas[][] = range.getFormulas();
        assertEquals(formula, formulas[0][0]);
        assertEquals(hardFormula, formulas[1][0]);
    }

    @Test
    public void testSetFormulasMat() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();

        final String formula = "=SUM(A1:A2)+A1";
        final String hardFormula = "= A2 - COUNT(A1:A2)";

        String arr[][] = new String[2][1];
        arr[0][0] = formula;
        arr[1][0] = hardFormula;

        Range range = sheet.getDataRange();
        range.setFormulas(arr);
    }

    @Test
    public void testSetFontBold() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Range range = sheet.getDataRange();
        range.setFontBold(true);

        Style[][] arr = range.getStyles();

        assertEquals(arr[0][0].isBold(), true);
        assertEquals(arr[0][1].isBold(), true);
        assertEquals(arr[1][0].isBold(), true);
        assertEquals(arr[1][1].isBold(), true);

        range = sheet.getRange(1, 0);
        range.setFontBold(false);
        assertEquals(range.getStyle().isBold(), false);
    }

    @Test
    public void testSetFontBolds() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Range range = sheet.getDataRange();
        range.setFontBolds(false, true, false, true);

        Style[][] arr = range.getStyles();

        assertEquals(arr[0][0].isBold(), false);
        assertEquals(arr[0][1].isBold(), true);
        assertEquals(arr[1][0].isBold(), false);
        assertEquals(arr[1][1].isBold(), true);
    }

    @Test
    public void testSetFontBoldsMat() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        boolean[][] arr = new boolean[2][2];
        arr[0][1] = true;
        arr[1][1] = true;

        Range range = sheet.getDataRange();
        range.setFontBolds(arr);

        Style[][] result = range.getStyles();

        assertEquals(result[0][0].isBold(), false);
        assertEquals(result[0][1].isBold(), true);
        assertEquals(result[1][0].isBold(), false);
        assertEquals(result[1][1].isBold(), true);
    }

    @Test
    public void testSetFontItalic() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Range range = sheet.getDataRange();
        range.setFontItalic(true);

        Style[][] arr = range.getStyles();

        assertEquals(arr[0][0].isItalic(), true);
        assertEquals(arr[0][1].isItalic(), true);
        assertEquals(arr[1][0].isItalic(), true);
        assertEquals(arr[1][1].isItalic(), true);

        range = sheet.getRange(1, 0);
        range.setFontItalic(false);
        assertEquals(range.getStyle().isItalic(), false);
    }

    @Test
    public void testSetFontItalics() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Range range = sheet.getDataRange();
        range.setFontItalics(false, true, false, true);

        Style[][] arr = range.getStyles();

        assertEquals(arr[0][0].isItalic(), false);
        assertEquals(arr[0][1].isItalic(), true);
        assertEquals(arr[1][0].isItalic(), false);
        assertEquals(arr[1][1].isItalic(), true);
    }

    @Test
    public void testSetFontItalicsMat() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        boolean[][] arr = new boolean[2][2];
        arr[0][1] = true;
        arr[1][1] = true;

        Range range = sheet.getDataRange();
        range.setFontItalics(arr);

        Style[][] result = range.getStyles();

        assertEquals(result[0][0].isItalic(), false);
        assertEquals(result[0][1].isItalic(), true);
        assertEquals(result[1][0].isItalic(), false);
        assertEquals(result[1][1].isItalic(), true);
    }

    @Test
    public void testSetFontUnderline() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Range range = sheet.getDataRange();
        range.setFontUnderline(true);

        Style[][] arr = range.getStyles();

        assertEquals(arr[0][0].isUnderline(), true);
        assertEquals(arr[0][1].isUnderline(), true);
        assertEquals(arr[1][0].isUnderline(), true);
        assertEquals(arr[1][1].isUnderline(), true);

        range = sheet.getRange(1, 0);
        range.setFontUnderline(false);
        assertEquals(range.getStyle().isUnderline(), false);
    }

    @Test
    public void testSetFontUnderlines() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Range range = sheet.getDataRange();
        range.setFontUnderlines(false, true, false, true);

        Style[][] arr = range.getStyles();

        assertEquals(arr[0][0].isUnderline(), false);
        assertEquals(arr[0][1].isUnderline(), true);
        assertEquals(arr[1][0].isUnderline(), false);
        assertEquals(arr[1][1].isUnderline(), true);
    }

    @Test
    public void testSetFontUnderlinesMat() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        boolean[][] arr = new boolean[2][2];
        arr[0][1] = true;
        arr[1][1] = true;

        Range range = sheet.getDataRange();
        range.setFontUnderlines(arr);

        Style[][] result = range.getStyles();

        assertEquals(result[0][0].isUnderline(), false);
        assertEquals(result[0][1].isUnderline(), true);
        assertEquals(result[1][0].isUnderline(), false);
        assertEquals(result[1][1].isUnderline(), true);
    }

    @Test
    public void testSetFontColor() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Color color = new Color(240, 23, 45);

        Range range = sheet.getDataRange();
        range.setFontColor(color);

        Style[][] arr = range.getStyles();

        assertEquals(arr[0][0].getFontColor(), color);
        assertEquals(arr[0][1].getFontColor(), color);
        assertEquals(arr[1][0].getFontColor(), color);
        assertEquals(arr[1][1].getFontColor(), color);

        range = sheet.getRange(1, 0);
        range.setFontColor(color);
        assertEquals(range.getStyle().getFontColor(), color);
    }

    @Test
    public void testSetFontColors() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Color color = new Color(240, 23, 35);
        Color otherColor = new Color(43, 12, 54);

        Range range = sheet.getDataRange();
        range.setFontColors(otherColor, color, null, otherColor);

        Style[][] arr = range.getStyles();

        assertEquals(arr[0][0].getFontColor(), otherColor);
        assertEquals(arr[0][1].getFontColor(), color);
        assertEquals(arr[1][0].getFontColor(), null);
        assertEquals(arr[1][1].getFontColor(), otherColor);
    }

    @Test
    public void testSetFonColorsMat() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Color[][] arr = new Color[2][2];
        arr[0][1] = new Color(12, 23, 45);
        arr[1][1] = new Color(23, 45, 67);

        Range range = sheet.getDataRange();
        range.setFontColors(arr);

        Style[][] result = range.getStyles();

        assertEquals(result[0][0].getFontColor(), null);
        assertEquals(result[0][1].getFontColor(), new Color(12, 23, 45));
        assertEquals(result[1][0].getFontColor(), null);
        assertEquals(result[1][1].getFontColor(), new Color(23, 45, 67));
    }

    @Test
    public void testSetBackgroundColor() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Color color = new Color(240, 23, 45);

        Range range = sheet.getDataRange();
        range.setBackgroundColor(color);

        Style[][] arr = range.getStyles();

        assertEquals(arr[0][0].getBackgroundColor(), color);
        assertEquals(arr[0][1].getBackgroundColor(), color);
        assertEquals(arr[1][0].getBackgroundColor(), color);
        assertEquals(arr[1][1].getBackgroundColor(), color);

        range = sheet.getRange(1, 0);
        range.setBackgroundColor(color);
        assertEquals(range.getStyle().getBackgroundColor(), color);

        try {
            color = new Color("transparent");
            fail();
        } catch (OperationNotSupportedException e) {
        }
    }

    @Test
    public void testSetBackgroundColors() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Color color = new Color(240, 23, 35);
        Color otherColor = new Color(43, 12, 54);

        Range range = sheet.getDataRange();
        range.setBackgroundColors(otherColor, color, null, otherColor);

        Style[][] arr = range.getStyles();

        assertEquals(arr[0][0].getBackgroundColor(), otherColor);
        assertEquals(arr[0][1].getBackgroundColor(), color);
        assertEquals(arr[1][0].getBackgroundColor(), null);
        assertEquals(arr[1][1].getBackgroundColor(), otherColor);
    }

    @Test
    public void testSetBackgroundColorsMat() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Color[][] arr = new Color[2][2];
        arr[0][1] = new Color(12, 23, 45);
        arr[1][1] = new Color(23, 45, 67);

        Range range = sheet.getDataRange();
        range.setBackgroundColors(arr);

        Style[][] result = range.getStyles();

        assertEquals(result[0][0].getBackgroundColor(), null);
        assertEquals(result[0][1].getBackgroundColor(), new Color(12, 23, 45));
        assertEquals(result[1][0].getBackgroundColor(), null);
        assertEquals(result[1][1].getBackgroundColor(), new Color(23, 45, 67));
    }

    @Test
    public void testSetFontSize() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        int fontSize = 15;

        Range range = sheet.getDataRange();
        range.setFontSize(fontSize);

        Style[][] arr = range.getStyles();

        assertEquals(arr[0][0].getFontSize(), fontSize);
        assertEquals(arr[0][1].getFontSize(), fontSize);
        assertEquals(arr[1][0].getFontSize(), fontSize);
        assertEquals(arr[1][1].getFontSize(), fontSize);

        range = sheet.getRange(1, 0);
        range.setFontSize(fontSize + 1);
        assertEquals(range.getStyle().getFontSize(), fontSize + 1);
    }

    @Test
    public void testSetFontSizes() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        int fontsize = 16;
        int otherFontSize = 22;

        Range range = sheet.getDataRange();
        range.setFontSizes(otherFontSize, fontsize, -1, otherFontSize);

        Style[][] arr = range.getStyles();

        assertEquals(arr[0][0].getFontSize(), otherFontSize);
        assertEquals(arr[0][1].getFontSize(), fontsize);
        assertEquals(arr[1][0].getFontSize(), -1);
        assertEquals(arr[1][1].getFontSize(), otherFontSize);
    }

    @Test
    public void testSetFontSizesMat() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        int[][] arr = new int[2][2];
        arr[0][1] = 11;
        arr[1][1] = 13;

        Range range = sheet.getDataRange();
        range.setFontSizes(arr);

        Style[][] result = range.getStyles();

        assertEquals(result[0][0].getFontSize(), 0);
        assertEquals(result[0][1].getFontSize(), 11);
        assertEquals(result[1][0].getFontSize(), 0);
        assertEquals(result[1][1].getFontSize(), 13);
    }

    @Test
    public void testSetValue() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Range range = sheet.getDataRange();
        range.setValue(1);

        Object[][] arr = range.getValues();

        assertEquals(arr[0][0], 1);
        assertEquals(arr[0][1], 1);
        assertEquals(arr[1][0], 1);
        assertEquals(arr[1][1], 1);
    }

    @Test
    public void testSetValues() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Range range = sheet.getDataRange();
        range.setValues(1, 2, 3, 4);

        Object[][] arr = range.getValues();

        assertEquals(arr[0][0], 1);
        assertEquals(arr[0][1], 2);
        assertEquals(arr[1][0], 3);
        assertEquals(arr[1][1], 4);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Error in setValues, the number of rows doesn't fit \\(3 against 2\\)")
    public void testSetValuesShouldThrowExceptionWithNotValidRows() {

        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Range range = sheet.getDataRange();
        range.setValues(new Integer[][] {{1, 2}, {3, 4}, {5, 6}});
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Error in setValues, the number of columns doesn't fit \\(3 against 2\\)")
    public void testSetValuesShouldThrowExceptionWithNotValidColumns() {

        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Range range = sheet.getDataRange();
        range.setValues(new Integer[][] {{1, 2, 6}, {3, 4, 5}});
    }


    @Test
    public void testSetValuesMat() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumns(2);

        Range range = sheet.getDataRange();
        Object[][] arr = new Object[2][3];
        arr[0][0] = 1;
        arr[0][1] = 2;
        arr[0][2] = 3;
        arr[1][0] = 4;
        arr[1][1] = 5;
        arr[1][2] = 6;

        range.setValues(arr);

        arr = range.getValues();

        assertEquals(arr[0][0], 1);
        assertEquals(arr[0][1], 2);
        assertEquals(arr[0][2], 3);
        assertEquals(arr[1][0], 4);
        assertEquals(arr[1][1], 5);
        assertEquals(arr[1][2], 6);
    }

    @Test
    public void testSetStyle() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Style style = new Style();
        style.setFontSize(4);
        style.setBackgroundColor(new Color(255, 0, 0));
        style.setBold(true);

        Range range = sheet.getDataRange();
        range.setStyle(style);

        Style[][] arr = range.getStyles();

        assertEquals(arr[0][0], style);
        assertEquals(arr[0][1], style);
        assertEquals(arr[1][0], style);
        assertEquals(arr[1][1], style);
    }

    @Test
    public void testSetStyles() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Style style = new Style();
        style.setFontSize(4);
        style.setBackgroundColor(new Color(255, 0, 0));
        style.setBold(true);

        Style otherStyle = new Style();
        otherStyle.setUnderline(true);
        otherStyle.setItalic(true);
        otherStyle.setFontColor(new Color(0, 23, 12));

        Range range = sheet.getDataRange();
        range.setStyles(style, new Style(), otherStyle, style);

        Style[][] arr = range.getStyles();

        assertEquals(arr[0][0], style);
        assertEquals(arr[0][1], new Style());
        assertEquals(arr[1][0], otherStyle);
        assertEquals(arr[1][1], style);
    }

    @Test
    public void testSetStylesMat() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumns(2);

        Style style = new Style();
        style.setFontSize(4);
        style.setBackgroundColor(new Color(255, 0, 0));
        style.setBold(true);

        Style otherStyle = new Style();
        otherStyle.setUnderline(true);
        otherStyle.setItalic(true);
        otherStyle.setFontColor(new Color(0, 23, 12));

        Range range = sheet.getDataRange();
        Style[][] arr = new Style[2][3];
        arr[0][0] = style;
        arr[0][1] = new Style();
        arr[0][2] = otherStyle;
        arr[1][0] = style;
        arr[1][1] = otherStyle;
        arr[1][2] = style;

        range.setStyles(arr);

        arr = range.getStyles();

        assertEquals(arr[0][0], style);
        assertEquals(arr[0][1], new Style());
        assertEquals(arr[0][2], otherStyle);
        assertEquals(arr[1][0], style);
        assertEquals(arr[1][1], otherStyle);
        assertEquals(arr[1][2], style);
    }

    @Test
    public void testSetAnnotation() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        OfficeAnnotation annotation = new OfficeAnnotation("Test", LocalDateTime.of(2020,4, 3, 2, 1));

        Range range = sheet.getDataRange();
        range.setAnnotation(annotation);

        OfficeAnnotation[][] arr = range.getAnnotations();

        assertEquals(arr[0][0], annotation);
        assertEquals(arr[0][1], annotation);
        assertEquals(arr[1][0], annotation);
        assertEquals(arr[1][1], annotation);
    }

    @Test
    public void testSetAnnotations() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        OfficeAnnotation annotation = new OfficeAnnotation("Test", LocalDateTime.of(2020,4,3,2,1));
        OfficeAnnotation otherAnnotation = new OfficeAnnotation("Hello world\nCas", LocalDateTime.of(2019,4,3,6,3));

        Range range = sheet.getDataRange();
        range.setAnnotations(annotation, new OfficeAnnotation(), otherAnnotation, null);

        OfficeAnnotation[][] arr = range.getAnnotations();

        assertEquals(arr[0][0], annotation);
        assertEquals(arr[0][1], new OfficeAnnotation());
        assertEquals(arr[1][0], otherAnnotation);
        assertEquals(arr[1][1], null);
    }

    @Test
    public void testSetAnnotationsMat() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumns(2);

        OfficeAnnotation annotation = new OfficeAnnotation("Test", LocalDateTime.of(2020,4,3,2,1));
        OfficeAnnotation otherAnnotation = new OfficeAnnotation("Hello world\nCas", LocalDateTime.of(2019,4,3,6,3));

        Range range = sheet.getDataRange();
        OfficeAnnotation[][] arr = new OfficeAnnotation[2][3];
        arr[0][0] = annotation;
        arr[0][1] = new OfficeAnnotation();
        arr[0][2] = otherAnnotation;
        arr[1][0] = annotation;
        arr[1][1] = null;
        arr[1][2] = annotation;

        range.setAnnotations(arr);

        arr = range.getAnnotations();

        assertEquals(arr[0][0], annotation);
        assertEquals(arr[0][1], new OfficeAnnotation());
        assertEquals(arr[0][2], otherAnnotation);
        assertEquals(arr[1][0], annotation);
        assertEquals(arr[1][1], null);
        assertEquals(arr[1][2], annotation);
    }

    @Test
    public void testAddLinkedValue() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Range range = sheet.getDataRange();

        Sheet linkedSheet = new Sheet("B");
        LinkedValue linkedValue = LinkedValue.builder().value("linked to").href(linkedSheet).build();
        range.addLinkedValue(linkedValue);

        List<LinkedValue>[][] arr = range.getLinkedValues();

        assertEquals(arr[0][0].get(0), linkedValue);
        assertEquals(arr[0][1].get(0), linkedValue);
        assertEquals(arr[1][0].get(0), linkedValue);
        assertEquals(arr[1][1].get(0), linkedValue);
    }

    @Test
    public void testAddLinkedValues() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Range range = sheet.getDataRange();

        Sheet linkedSheetB = new Sheet("B");
        LinkedValue linkedValue1 = LinkedValue.builder().value("1 linked to").href(linkedSheetB).build();

        Sheet linkedSheetC = new Sheet("C");
        LinkedValue linkedValue2 = LinkedValue.builder().value("2 linked to").href(linkedSheetC).build();

        Sheet linkedSheetD = new Sheet("D");
        LinkedValue linkedValue3 = LinkedValue.builder().value("3 linked to").href(linkedSheetD).build();

        Sheet linkedSheetE = new Sheet("E");
        LinkedValue linkedValue4 = LinkedValue.builder().value("4 linked to").href(linkedSheetE).build();

        range.addLinkedValues(linkedValue1, linkedValue2, linkedValue3, linkedValue4);

        List<LinkedValue>[][] arr = range.getLinkedValues();

        assertEquals(arr[0][0].get(0), linkedValue1);
        assertEquals(arr[0][1].get(0), linkedValue2);
        assertEquals(arr[1][0].get(0), linkedValue3);
        assertEquals(arr[1][1].get(0), linkedValue4);
    }

    @Test
    public void testSetLinkedValues() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumn();

        Range range = sheet.getDataRange();

        Sheet linkedSheet = new Sheet("B");

        List<LinkedValue> linkedValues = new ArrayList<>(asList(LinkedValue.builder().value("linked to").href(linkedSheet).build()));
        range.setLinkedValues(linkedValues);

        List<LinkedValue>[][] arr = range.getLinkedValues();

        assertEquals(arr[0][0], linkedValues);
        assertEquals(arr[0][1], linkedValues);
        assertEquals(arr[1][0], linkedValues);
        assertEquals(arr[1][1], linkedValues);
    }

    @Test
    public void testSetLinkedValuesMat() throws Exception {
        Sheet sheet = new Sheet("A");
        sheet.appendRow();
        sheet.appendColumns(2);

        Range range = sheet.getDataRange();

        List<LinkedValue>[][] linkedValuesArr = new ArrayList[2][3];

        Sheet linkedSheetB = new Sheet("B");
        LinkedValue linkedValue1 = LinkedValue.builder().value("1 linked to").href(linkedSheetB).build();

        Sheet linkedSheetC = new Sheet("C");
        LinkedValue linkedValue2 = LinkedValue.builder().value("2 linked to").href(linkedSheetC).build();

        Sheet linkedSheetD = new Sheet("D");
        LinkedValue linkedValue3 = LinkedValue.builder().value("3 linked to").href(linkedSheetD).build();

        Sheet linkedSheetE = new Sheet("E");
        LinkedValue linkedValue4 = LinkedValue.builder().value("4 linked to").href(linkedSheetE).build();

        Sheet linkedSheetF = new Sheet("F");
        LinkedValue linkedValue5 = LinkedValue.builder().value("5 linked to").href(linkedSheetF).build();

        Sheet linkedSheetG = new Sheet("G");
        LinkedValue linkedValue6 = LinkedValue.builder().value("6 linked to").href(linkedSheetG).build();

        linkedValuesArr[0][0] = new ArrayList<>(asList(linkedValue1));
        linkedValuesArr[0][1] = new ArrayList<>(asList(linkedValue2));
        linkedValuesArr[0][2] = new ArrayList<>(asList(linkedValue3));
        linkedValuesArr[1][0] = new ArrayList<>(asList(linkedValue4));
        linkedValuesArr[1][1] = new ArrayList<>(asList(linkedValue5));
        linkedValuesArr[1][2] = new ArrayList<>(asList(linkedValue6));

        range.setLinkedValues(linkedValuesArr);

        List<LinkedValue>[][] arr = range.getLinkedValues();

        assertEquals(arr[0][0].get(0), linkedValue1);
        assertEquals(arr[0][1].get(0), linkedValue2);
        assertEquals(arr[0][2].get(0), linkedValue3);
        assertEquals(arr[1][0].get(0), linkedValue4);
        assertEquals(arr[1][1].get(0), linkedValue5);
        assertEquals(arr[1][2].get(0), linkedValue6);
    }
}
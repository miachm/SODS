package steps;

import com.github.miachm.sods.Range;
import com.github.miachm.sods.Style;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.Arrays;

import static org.junit.Assert.*;

public class RangeCucumber {

    @When("^the client creates a Range with (\\d+),(\\d+),(\\d+),(\\d+)$")
    public void the_client_creates_a_Range_with(int row, int column, int numrows, int numcolumn) throws Throwable {
        World.range = World.sheet.getRange(row, column, numrows, numcolumn);
    }

    @Then("^the row_init of the range is (\\d+)$")
    public void the_row_init_of_the_range_is(int row) throws Throwable {
        assertEquals(World.range.getRow(), row);
    }

    @Then("^the column_init of the range is (\\d+)$")
    public void the_column_init_of_the_range_is(int column) throws Throwable {
        assertEquals(World.range.getColumn(), column);
    }

    @Then("^the numrows of the range is (\\d+)$")
    public void the_numrows_of_the_range_is(int numrows) throws Throwable {
        assertEquals(World.range.getNumRows(), numrows);
    }

    @Then("^the numcolumns of the range is (\\d+)$")
    public void the_numcolumns_of_the_range_is_num_column(int numcolumns) throws Throwable {
        assertEquals(World.range.getNumColumns(), numcolumns);

    }

    @When("^the client creates a Range with (-?\\d+),(-?\\d+),(-?\\d+),(-?\\d+) and catch the exception$")
    public void the_client_creates_a_Range_with_and_catch_the_exception(int row, int column, int numrows, int numcolumn) throws Throwable {
        try {
            World.sheet.getRange(row, column, numrows, numcolumn);
        }
        catch (IndexOutOfBoundsException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client clears the range$")
    public void the_client_clears_the_range() throws Throwable {
        World.range.clear();
    }

    @Then("^the range does not contain any value$")
    public void the_range_does_not_contain_any_value() throws Throwable {
        Object[][] arr = World.range.getValues();
        for (Object[] row : arr)
            for (Object o : row)
                assertNull(o);
    }

    @Then("^the range does not contain any formula$")
    public void the_range_does_not_contain_any_formula() throws Throwable {
        Object[][] arr = World.range.getFormulas();
        for (Object[] row : arr)
            for (Object o : row)
                assertNull(o);
    }

    @Then("^the range does not contain any style$")
    public void the_range_does_not_contain_any_style() throws Throwable {
        Object[][] arr = World.range.getStyles();
        for (Object[] row : arr)
            for (Object o : row) {
                Style style = (Style) o;
                assertTrue(style.isDefault());
            }
    }

    @Then("^the range does not contain any annotation$")
    public void the_range_does_not_contain_any_annotation() throws Throwable {
        Object[][] arr = World.range.getAnnotations();
        for (Object[] row : arr)
            for (Object o : row)
                assertNull(o);
    }

    @Then("^the range value is not null$")
    public void the_range_value_is_not_null() throws Throwable {
        assertNotNull(World.range.getValue());
    }

    @When("^the client copy to the Range (\\d+),(\\d+),(\\d+),(\\d+)$")
    public void the_client_copy_to_the_Range(int row, int column, int numrows, int numcolumns) throws Throwable {
        Range range = World.sheet.getRange(row, column, numrows, numcolumns);
        World.range.copyTo(range);
    }

    @Then("^the range is equal to the range (\\d+),(\\d+),(\\d+),(\\d+)$")
    public void the_range_is_equal_to_the_range(int row, int column, int numrows, int numcolumns) throws Throwable {
        Range range = World.sheet.getRange(row, column, numrows, numcolumns);
        assertTrue(Arrays.deepEquals(World.range.getValues(), range.getValues()));
        assertTrue(Arrays.deepEquals(World.range.getStyles(), range.getStyles()));
        assertTrue(Arrays.deepEquals(World.range.getFormulas(), range.getFormulas()));
        assertTrue(Arrays.deepEquals(World.range.getAnnotations(), range.getAnnotations()));
    }

    @When("^the client copy to the Range (\\d+),(\\d+),(\\d+),(\\d+) and catch the exception$")
    public void the_client_copy_to_the_Range_and_catch_the_exception(int row, int column, int numrows, int numcolumns) throws Throwable {
        Range range = World.sheet.getRange(row, column, numrows, numcolumns);
        try {
            World.range.copyTo(range);
        }
        catch (Exception e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client get the cell (-?\\d+), (-?\\d+) within the Range$")
    public void the_client_get_the_cell_within_the_Range(int row, int column) throws Throwable {
        World.range = World.range.getCell(row, column);
    }

    @When("^the client get the cell (-?\\d+), (-?\\d+) within the Range and catch the exception$")
    public void the_client_get_the_cell_within_the_Range_and_catch_the_exception(int row, int column) throws Throwable {
        try {
            World.range = World.range.getCell(row, column);
        }
        catch (Exception e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client sets the formula in the range to \"([^\"]*)\"$")
    public void the_clients_sets_the_formula_in_the_range_to(String formula) throws Throwable {
        World.range.setFormula(formula);
    }

    @Then("^the formula of the range is \"([^\"]*)\"$")
    public void the_formula_of_the_range_is(String formula) throws Throwable {
        assertEquals(World.range.getFormula(), formula);
    }

    @Then("^the formulas in the range are \"([^\"]*)\"$")
    public void the_formulas_in_the_range_are(String raw_formulas) throws Throwable {
        String[] rows = raw_formulas.split("\n");
        String[][] formulas = new String[rows.length][rows[0].split(",").length];
        for (int i = 0; i < rows.length; i++)
            formulas[i] = rows[i].split(",");
        assertTrue(Arrays.deepEquals(formulas, World.range.getFormulas()));
    }
}

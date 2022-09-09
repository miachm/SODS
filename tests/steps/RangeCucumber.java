package steps;

import com.github.miachm.sods.Style;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

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
}

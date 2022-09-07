package steps;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.Arrays;

import static org.testng.AssertJUnit.*;

public class GetRangeCucumber {

    @When("^get the Cell in the position (\\d+),(\\d+) and catch the exception$")
    public void get_the_Cell_in_the_position_and_catch_the_exception(int row, int column) throws Throwable {
        try {
            World.sheet.getRange(row, column);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @Then("^get the Cell in the position (\\d+),(\\d+) as World\\.range$")
    public void get_the_Cell_in_the_position_as_World_range(int row, int column) throws Throwable {
        World.range = World.sheet.getRange(row, column);
    }

    @Then("^the value in range is (\\d+)$")
    public void the_value_in_range_is(int value) throws Throwable {
        assertEquals(value, World.range.getValue());
    }

    @Then("^the value in range is null$")
    public void the_value_in_range_is_null() throws Throwable {
        assertNull(World.range.getValue());
    }

    @Then("^get the (\\d+) row Cell in the position (\\d+),(\\d+) as World\\.range$")
    public void get_the_row_Cell_in_the_position_as_World_range(int rows, int row_index, int column_index) throws Throwable {
        World.range = World.sheet.getRange(row_index, column_index, rows);
    }

    @Then("^the values in the range are (\\d+),(\\d+)$")
    public void the_values_in_the_range_are(int a, int b) throws Throwable {
        Object arr[][] = new Object[2][1];
        arr[0][0] = a;
        arr[1][0] = b;
        assertTrue(Arrays.deepEquals(arr, World.range.getValues()));
    }

    @Then("^get the (\\d+)x(\\d+) Cell in the position (\\d+),(\\d+) as World\\.range$")
    public void get_the_x_Cell_in_the_position_as_World_range(int rows, int columns, int row_index, int column_index) throws Throwable {
        World.range = World.sheet.getRange(row_index, column_index, rows, columns);
    }

    @Then("^the values in the range are (\\d+),(\\d+),(\\d+),(\\d+)$")
    public void the_values_in_the_range_are(int a, int b, int c, int d) throws Throwable {
        Object arr[][] = new Object[2][2];
        arr[0][0] = a;
        arr[0][1] = b;
        arr[1][0] = c;
        arr[1][1] = d;

        assertTrue(Arrays.deepEquals(arr, World.range.getValues()));
    }

    @When("^get the Cell range (\\d+)x(\\d+) in the position (\\d+),(\\d+) and catch the exception$")
    public void get_the_Cell_range_x_in_the_position_and_catch_the_exception(int rows, int columns, int row_index, int column_index) throws Throwable {
        try {
            World.sheet.getRange(row_index, column_index, rows, columns);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }
}

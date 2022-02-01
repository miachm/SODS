package steps;

import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

public class SheetCucumber {
    private Random random = new Random();

    @When("^the client creates a Sheet with \"([^\"]*)\" and put into World\\.sheet$")
    public void the_client_creates_a_Sheet_with_and_put_into_this_sheet(String name) throws Throwable {
        World.sheet = new Sheet(name);
    }

    @Then("^the object World\\.sheet has the name \"([^\"]*)\"$")
    public void the_object_this_sheet_has_the_name(String name) throws Throwable {
        assertEquals(World.sheet.getName(), name);
    }

    @Then("^the object World\\.sheet has (\\d+)x(\\d+) dimensions$")
    public void the_object_this_sheet_has_x_dimensions(int rows, int columns) throws Throwable {
        assertEquals(World.sheet.getMaxRows(), rows);
        assertEquals(World.sheet.getMaxColumns(), columns);
    }

    @When("^the client creates a Sheet with \"([^\"]*)\" and size (\\d+)x(\\d+) and put into World\\.sheet$")
    public void the_client_creates_a_Sheet_with_and_size_x_and_put_into_this_sheet(String name, int rows, int columns) throws Throwable {
        World.sheet = new Sheet(name, rows, columns);
    }


    @When("^the client creates a Sheet with a null name and catch the exception$")
    public void the_client_creates_a_Sheet_with_a_null_name_and_catch_the_exception() throws Throwable {
        try {
            World.sheet = new Sheet(null);
        } catch (NullPointerException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client creates a Sheet with \"([^\"]*)\" and size (-?\\d+)x(-?\\d+)$")
    public void the_client_creates_a_Sheet_with_and_size_x(String name, int rows, int columns) throws Throwable {
        World.sheet = new Sheet(name, rows, columns);
    }

    @When("^the client creates a Sheet with \"([^\"]*)\" and invalid size (-?\\d+)x(-?\\d+) then catch the exception$")
    public void the_client_creates_a_Sheet_with_and_invalid_size_x_then_catch_the_exception(String name, int rows, int columns) throws Throwable {
        try {
            World.sheet = new Sheet(name, rows, columns);
        }
        catch (Exception e) {
            ExceptionChecker.registerException(e);
        }
    }


    @Given("^a sheet \"([^\"]*)\", size (\\d+)x(\\d+) and random data$")
    public void a_sheet_size_x_and_random_data(String name, int rows, int columns) throws Throwable {
        World.sheet = new Sheet(name, rows, columns);
        Range range = World.sheet.getDataRange();
        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i < range.getNumValues(); i++) {
            integers.add(random.nextInt());
        }

        range.setValues(integers.toArray());
    }

    @When("^the client clears the sheet$")
    public void the_client_clears_the_sheet() throws Throwable {
        World.sheet.clear();
    }

    @Then("^the number of rows is (\\d+)$")
    public void the_number_of_rows_is(int rows) throws Throwable {
        assertEquals(World.sheet.getMaxRows(), rows);
    }

    @Then("^the number of columns is (\\d+)$")
    public void the_number_of_columns_is(int columns) throws Throwable {
        assertEquals(World.sheet.getMaxColumns(), columns);
    }

    @Then("^the sheet does not contain any data$")
    public void the_sheet_does_not_contain_any_data_formula_or_style() throws Throwable {
        Object[][] array = World.sheet.getDataRange().getValues();
        for (Object[] row : array) {
            for (Object value : row) {
                assertNull(value);
            }
        }
    }

    @Given("^an empty Sheet$")
    public void an_empty_Sheet() throws Throwable {
        World.sheet = new Sheet("Test", 0, 0);
    }


    @When("^the client appends a row$")
    public void the_client_appends_a_row() throws Throwable {
        World.sheet.appendRow();
    }

    @When("^the client appends (-?\\d+) rows$")
    public void the_client_appends_rows(int rows) throws Throwable {
        World.sheet.appendRows(rows);
    }

    @When("^the client appends (-?\\d+) rows and catch the exception$")
    public void the_client_appends_rows_and_catch_the_exception(int rows) throws Throwable {
        try {
            World.sheet.appendRows(rows);
        }
        catch (IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client appends the sheet contained in World\\.sheet$")
    public void the_client_appends_the_sheet_contained_in_this_sheet() throws Throwable {
        World.spread.appendSheet(World.sheet);
    }

    @When("^get the first sheet$")
    public void get_the_first_sheet() throws Throwable {
        World.sheet = World.spread.getSheet(0);
    }

    @When("^the client appends a column$")
    public void the_client_appends_a_column() throws Throwable {
        World.sheet.appendColumn();
    }

    @When("^the client appends (\\d+) columns$")
    public void the_client_appends_columns(int columns) throws Throwable {
        World.sheet.appendColumns(columns);
    }

    @When("^the client appends (-?\\d+) columns and catch the exception$")
    public void the_client_appends_columns_and_catch_the_exception(int columns) throws Throwable {
        try {
            World.sheet.appendColumns(columns);
        }
        catch (IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client deletes the column (-?\\d+) and catch the exception$")
    public void the_client_deletes_the_column_and_catch_the_exception(int column) throws Throwable {
        try {
            World.sheet.deleteColumn(column);
        }
        catch (IndexOutOfBoundsException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client deletes the column (\\d+)$")
    public void the_client_deletes_the_column(int column) throws Throwable {
        World.sheet.deleteColumn(column);
    }

    @When("^the client deletes (\\d+) columns on the index (\\d+)$")
    public void the_client_deletes_columns_on_the_index(int howmany, int column) throws Throwable {
        World.sheet.deleteColumns(column, howmany);
    }

    @When("^the client deletes the column (\\d+) on the index (-?\\d+) and catch the exception$")
    public void the_client_deletes_the_column_on_the_index_and_catch_the_exception(int howmany, int column) throws Throwable {
        try {
            World.sheet.deleteColumns(column, howmany);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client deletes the row (\\d+) and catch the exception$")
    public void the_client_deletes_the_row_and_catch_the_exception(int row) throws Throwable {
        try {
            World.sheet.deleteRow(row);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client deletes the row (\\d+)$")
    public void the_client_deletes_the_row(int row) throws Throwable {
        World.sheet.deleteRow(row);
    }

    @When("^the client deletes (\\d+) rows on the index (\\d+)$")
    public void the_client_deletes_rows_on_the_index(int howmany, int row) throws Throwable {
        World.sheet.deleteRows(row, howmany);
    }

    @When("^the client deletes the row (\\d+) on the index (-?\\d+) and catch the exception$")
    public void the_client_deletes_the_row_on_the_index_and_catch_the_exception(int howmany, int row) throws Throwable {
        try {
            World.sheet.deleteRows(row, howmany);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }
}

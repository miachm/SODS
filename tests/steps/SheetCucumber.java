package steps;

import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.lang.reflect.Array;
import java.util.*;

import static org.testng.AssertJUnit.*;
import static org.testng.AssertJUnit.assertFalse;

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

    @When("^specify the column width with the next data:$")
    public void specify_the_column_width_with_the_next_data(DataTable datatable) throws Throwable {
        Iterator<String> datatableIterator = datatable.asList(String.class).iterator();
        Sheet sheet  = World.sheet;
        int index = 0;
        while (datatableIterator.hasNext()) {
            String value = datatableIterator.next();
            if (value.equals("null"))
                sheet.setColumnWidth(index, null);
            else
                sheet.setColumnWidth(index, Double.parseDouble(value));

            index++;
        }
    }

    @Then("^the value of the column (\\d+) is (\\d.+)$")
    public void the_name_of_World_columnWidth_is(int index, double value) throws Throwable {
        assertEquals(value, World.sheet.getColumnWidth(index));
    }

    @Then("^the value of the column (\\d+) is null$")
    public void the_name_of_World_columnWidth_is_null(int index) throws Throwable {
        assertNull(World.sheet.getColumnWidth(index));
    }

    @When("^the client gets the column (-?\\d+) and catch the exception$")
    public void the_client_gets_the_column_and_catch_the_exception(int index) throws Throwable {
        try {
            World.sheet.getColumnWidth(index);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^specify the row height with the next data:$")
    public void specify_the_row_height_with_the_next_data(DataTable dataTable) throws Throwable {
        Iterator<String> datatableIterator = dataTable.asList(String.class).iterator();
        Sheet sheet  = World.sheet;
        int index = 0;
        while (datatableIterator.hasNext()) {
            String value = datatableIterator.next();
            if (value.equals("null"))
                sheet.setRowHeight(index, null);
            else
                sheet.setRowHeight(index, Double.parseDouble(value));

            index++;
        }
    }

    @Then("^the value of the row (\\d+) is (\\d.+)$")
    public void the_value_of_the_row_is(int index, double value) throws Throwable {
        assertEquals(value, World.sheet.getRowHeight(index));
    }

    @Then("^the value of the row (\\d+) is null$")
    public void the_value_of_the_row_is_null(int index) throws Throwable {
        assertNull(World.sheet.getRowHeight(index));
    }

    @When("^the client gets the row (-?\\d+) and catch the exception$")
    public void the_client_gets_the_row_and_catch_the_exception(int index) throws Throwable {
        try {
            World.sheet.getRowHeight(index);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client gets the full data range as World\\.range$")
    public void the_client_gets_the_full_data_range_as_World_dataRange() throws Throwable {
        World.range = World.sheet.getDataRange();
    }

    @Then("^World\\.range has the same content as the World\\.Sheet$")
    public void world_dataRange_has_the_same_content_as_the_World_Sheet() throws Throwable {
        assertEquals(World.sheet.getMaxRows(), World.range.getNumRows());
        assertEquals(World.sheet.getMaxColumns(), World.range.getNumColumns());

        Object[][] values = World.range.getValues();

        for (int i = 0; i < World.sheet.getMaxRows(); i++)
            for (int j = 0; j < World.sheet.getMaxRows(); j++)
                assertEquals(World.sheet.getRange(i,j).getValue(), values[i][j]);
    }

    @Then("^the name of the sheet is \"([^\"]*)\"$")
    public void the_name_of_the_sheet_is(String name) throws Throwable {
        assertEquals(World.sheet.getName(), name);
    }

    @Given("^a sheet \"([^\"]*)\", size (\\d+)x(\\d+) and the data:$")
    public void a_sheet_size_x_and_the_data(String name, int row, int column, DataTable dataTable) throws Throwable {
        World.sheet = new Sheet(name, row, column);
        Range range = World.sheet.getDataRange();

        List<List<Integer>> lists = dataTable.asLists(Integer.class);
        Integer[][] array = new Integer[lists.size()][];
        Integer[] blankArray = new Integer[0];
        for(int i=0; i < lists.size(); i++) {
            array[i] = lists.get(i).toArray(blankArray);
        }

        range.setValues(array);
    }

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

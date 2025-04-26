package steps;

import com.github.miachm.sods.Sheet;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.Iterator;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

public class SizeCucumber {

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

    @Then("^the client gets the width of the column (\\d+) is (\\d.+)$")
    public void the_name_of_World_columnWidth_is(int index, double value) throws Throwable {
        assertEquals(value, World.sheet.getColumnWidth(index));
    }

    @Then("^the client gets the width of the column (\\d+) is null$")
    public void the_name_of_World_columnWidth_is_null(int index) throws Throwable {
        assertNull(World.sheet.getColumnWidth(index));
    }

    @When("^the client gets the width of the column (-?\\d+) and catch the exception$")
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

    @Then("^the client gets the height of the row (\\d+) is (\\d.+)$")
    public void the_value_of_the_row_is(int index, double value) throws Throwable {
        assertEquals(value, World.sheet.getRowHeight(index));
    }

    @Then("^the client gets the height of the row (\\d+) is null$")
    public void the_value_of_the_row_is_null(int index) throws Throwable {
        assertNull(World.sheet.getRowHeight(index));
    }

    @When("^the client gets the height of the row (-?\\d+) and catch the exception$")
    public void the_client_gets_the_row_and_catch_the_exception(int index) throws Throwable {
        try {
            World.sheet.getRowHeight(index);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client sets the width of the column (\\d+) to (.+)$")
    public void the_client_sets_the_width_of_the_column_to(int index, double value) throws Throwable {
        World.sheet.setColumnWidth(index, value);
    }

    @When("^the client sets the width of the column (-?\\d+) and catch the exception$")
    public void the_client_sets_the_width_of_the_column_and_catch_the_exception(int index) throws Throwable {
        try {
            World.sheet.setColumnWidth(index, 1.0);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client sets the height of the row (\\d+) to (.+)$")
    public void the_client_sets_the_height_of_the_row_to(int index, double value) throws Throwable {
        World.sheet.setRowHeight(index, value);
    }

    @When("^the client sets the height of the row (-?\\d+) and catch the exception$")
    public void the_client_sets_the_height_of_the_row_and_catch_the_exception(int index) throws Throwable {
        try {
            World.sheet.setRowHeight(index, 1.0);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }
}

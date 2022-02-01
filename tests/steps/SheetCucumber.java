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
    private Sheet sheet;
    private Random random = new Random();

    @When("^the client creates a Sheet with \"([^\"]*)\" and put into this\\.sheet$")
    public void the_client_creates_a_Sheet_with_and_put_into_this_sheet(String name) throws Throwable {
        this.sheet = new Sheet(name);
    }

    @Then("^the object this\\.sheet has the name \"([^\"]*)\"$")
    public void the_object_this_sheet_has_the_name(String name) throws Throwable {
        assertEquals(this.sheet.getName(), name);
    }

    @Then("^the object this\\.sheet has (\\d+)x(\\d+) dimensions$")
    public void the_object_this_sheet_has_x_dimensions(int rows, int columns) throws Throwable {
        assertEquals(this.sheet.getMaxRows(), rows);
        assertEquals(this.sheet.getMaxColumns(), columns);
    }

    @When("^the client creates a Sheet with \"([^\"]*)\" and size (\\d+)x(\\d+) and put into this\\.sheet$")
    public void the_client_creates_a_Sheet_with_and_size_x_and_put_into_this_sheet(String name, int rows, int columns) throws Throwable {
        this.sheet = new Sheet(name, rows, columns);
    }


    @When("^the client creates a Sheet with a null name and catch the exception$")
    public void the_client_creates_a_Sheet_with_a_null_name_and_catch_the_exception() throws Throwable {
        try {
            this.sheet = new Sheet(null);
        } catch (NullPointerException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client creates a Sheet with \"([^\"]*)\" and size (-?\\d+)x(-?\\d+)$")
    public void the_client_creates_a_Sheet_with_and_size_x(String name, int rows, int columns) throws Throwable {
        this.sheet = new Sheet(name, rows, columns);
    }

    @When("^the client creates a Sheet with \"([^\"]*)\" and invalid size (-?\\d+)x(-?\\d+) then catch the exception$")
    public void the_client_creates_a_Sheet_with_and_invalid_size_x_then_catch_the_exception(String name, int rows, int columns) throws Throwable {
        try {
            this.sheet = new Sheet(name, rows, columns);
        }
        catch (Exception e) {
            ExceptionChecker.registerException(e);
        }
    }


    @Given("^a sheet \"([^\"]*)\", size (\\d+)x(\\d+) and random data$")
    public void a_sheet_size_x_and_random_data(String name, int rows, int columns) throws Throwable {
        this.sheet = new Sheet(name, rows, columns);
        Range range = sheet.getDataRange();
        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i < range.getNumValues(); i++) {
            integers.add(random.nextInt());
        }

        range.setValues(integers.toArray());
    }

    @When("^the client clears the sheet$")
    public void the_client_clears_the_sheet() throws Throwable {
        this.sheet.clear();
    }

    @Then("^the number of rows is (\\d+)$")
    public void the_number_of_rows_is(int rows) throws Throwable {
        assertEquals(this.sheet.getMaxRows(), rows);
    }

    @Then("^the number of columns is (\\d+)$")
    public void the_number_of_columns_is(int columns) throws Throwable {
        assertEquals(this.sheet.getMaxColumns(), columns);
    }

    @Then("^the sheet does not contain any data$")
    public void the_sheet_does_not_contain_any_data_formula_or_style() throws Throwable {
        Object[][] array = this.sheet.getDataRange().getValues();
        for (Object[] row : array) {
            for (Object value : row) {
                assertNull(value);
            }
        }
    }

    @Given("^an empty Sheet$")
    public void an_empty_Sheet() throws Throwable {
        this.sheet = new Sheet("Test", 0, 0);
    }
}

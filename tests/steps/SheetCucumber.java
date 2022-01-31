package steps;

import com.github.miachm.sods.Sheet;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.testng.AssertJUnit.assertEquals;

public class SheetCucumber {
    private Sheet sheet;

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
}

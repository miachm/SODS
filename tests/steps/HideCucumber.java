package steps;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

public class HideCucumber {

    @Then("^the World\\.sheet is not hidden$")
    public void the_World_sheet_is_not_hidden() throws Throwable {
        assertFalse(World.sheet.isHidden());
    }

    @When("^hide the sheet World\\.sheet$")
    public void hide_the_sheet_World_sheet() throws Throwable {
        World.sheet.hideSheet();
    }

    @Then("^the World\\.sheet is hidden$")
    public void the_World_sheet_is_hidden() throws Throwable {
        assertTrue(World.sheet.isHidden());
    }

    @When("^show the sheet World\\.sheet$")
    public void show_the_sheet_World_sheet() throws Throwable {
        World.sheet.showSheet();
    }

    @When("^hide the row (\\d+)$")
    public void hide_the_row(int index) throws Throwable {
        World.sheet.hideRow(index);
    }

    @Then("^the row (\\d+) is hidden$")
    public void the_row_is_hidden(int index) throws Throwable {
        assertTrue(World.sheet.rowIsHidden(index));
    }

    @When("^show the row (\\d+)$")
    public void show_the_row(int index) throws Throwable {
        World.sheet.showRow(index);
    }

    @Then("^the row (\\d+) is not hidden$")
    public void the_row_is_not_hidden(int index) throws Throwable {
        assertFalse(World.sheet.rowIsHidden(index));
    }

    @When("^hide the row (-?\\d+) and catch the exception$")
    public void hide_the_row_and_catch_the_exception(int index) throws Throwable {
        try {
            World.sheet.hideRow(index);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^show the row (-?\\d+) and catch the exception$")
    public void show_the_row_and_catch_the_exception(int index) throws Throwable {
        try {
            World.sheet.showRow(index);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^check row (-?\\d+) is hidden and catch the exception$")
    public void check_row_is_hidden_and_catch_the_exception(int index) throws Throwable {
        try {
            World.sheet.rowIsHidden(index);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }


    @When("^hide the column (\\d+)$")
    public void hide_the_column(int index) throws Throwable {
        World.sheet.hideColumn(index);
    }

    @Then("^the column (\\d+) is hidden$")
    public void the_column_is_hidden(int index) throws Throwable {
        assertTrue(World.sheet.columnIsHidden(index));
    }

    @When("^show the column (\\d+)$")
    public void show_the_column(int index) throws Throwable {
        World.sheet.showColumn(index);
    }

    @Then("^the column (\\d+) is not hidden$")
    public void the_column_is_not_hidden(int index) throws Throwable {
        assertFalse(World.sheet.columnIsHidden(index));
    }

    @When("^hide the column (-?\\d+) and catch the exception$")
    public void hide_the_column_and_catch_the_exception(int index) throws Throwable {
        try {
            World.sheet.hideColumn(index);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^show the column (-?\\d+) and catch the exception$")
    public void show_the_column_and_catch_the_exception(int index) throws Throwable {
        try {
            World.sheet.showColumn(index);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^check column (-?\\d+) is hidden and catch the exception$")
    public void check_column_is_hidden_and_catch_the_exception(int index) throws Throwable {
        try {
            World.sheet.columnIsHidden(index);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }
}

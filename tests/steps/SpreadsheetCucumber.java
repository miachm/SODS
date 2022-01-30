package steps;

import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static org.testng.AssertJUnit.*;


public class SpreadsheetCucumber {
    private SpreadSheet spread;
    private byte[] buffer;
    private Exception lastException;

    private String get_random_name()
    {
        return UUID.randomUUID().toString();
    }

    @Given("^an empty Spreadsheet$")
    public void emptySpreadsheet() {
        this.spread = new SpreadSheet();
    }

    @When("^the client appends an empty sheet with the name \"([^\"]*)\"$")
    public void the_client_appends_a_sheet(String name) throws Throwable {
        this.spread.appendSheet(new Sheet(name));
    }

    @Then("^the number of sheets in the spreadsheet is (\\d+)$")
    public void the_number_of_sheets_in_the_spreadsheet_is(int numSheets) throws Throwable {
        assertEquals(spread.getNumSheets(), numSheets);
    }

    @Given("^a SpreadSheet with (\\d+) random sheets$")
    public void a_SpreadSheet_with_random_sheets(int numSheets) throws Throwable {
        this.spread = new SpreadSheet();
        for (int i = 0; i < numSheets; i++) {
            this.spread.appendSheet(new Sheet(get_random_name()));
        }
    }

    @Then("^the name of the sheet number (\\d+) is \"([^\"]*)\"$")
    public void the_name_of_the_sheet_is(int index, String name) throws Throwable {
        Sheet sheet = this.spread.getSheet(index);
        assertEquals(sheet.getName(), name);
    }

    @When("^save the spreadsheet in the memory$")
    public void save_it_in_the_memory() throws Throwable {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        spread.save(output);
        this.buffer = output.toByteArray();
    }

    @When("^load a spreadsheet from memory$")
    public void load_it_from_memory() throws Throwable {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(this.buffer);
        spread = new SpreadSheet(inputStream);
    }

    @When("^the client appends a null sheet and catch the exception$")
    public void the_client_appends_a_null_sheet() throws Throwable {
        try {
            this.spread.appendSheet(null);
        }
        catch (NullPointerException e) {
            lastException = e;
        }
    }

    @Then("^the exception is a NullPointerException$")
    public void it_throws_a_NullPointerException() throws Throwable {
        assertNotNull(lastException);
        assertTrue(lastException instanceof NullPointerException);
    }

    @When("^the client add an empty sheet in the index (\\d+) with the name \"([^\"]*)\"$")
    public void the_client_add_an_empty_sheet_in_the_index_with_the_name(int index, String name) throws Throwable {
        this.spread.addSheet(new Sheet(name), index);
    }

    @When("^the client adds a null sheet in the index (\\d+) and catch the exception$")
    public void the_client_adds_a_null_sheet_and_catch_the_exception(int index) throws Throwable {
        try {
            this.spread.addSheet(null, index);
        }
        catch (NullPointerException exception) {
            this.lastException = exception;
        }
    }

    @When("^the client add an empty sheet in the invalid index (-?\\d+) with the name \"([^\"]*)\" and catch the exception$")
    public void the_client_add_an_empty_sheet_in_the_index_with_the_name_and_catch_the_exception(int index, String name) throws Throwable {
        try {
            this.spread.addSheet(new Sheet(name), index);
        }
        catch (IndexOutOfBoundsException exception) {
            this.lastException = exception;
        }
    }

    @Then("^the exception is a IndexOutOfBoundsException$")
    public void the_exception_is_a_IndexOutOfBoundsException() throws Throwable {
        assertNotNull(lastException);
        assertTrue(lastException instanceof IndexOutOfBoundsException);
    }

    @When("^the client clears the spreadsheet$")
    public void the_client_clears_the_spreadsheet() throws Throwable {
        this.spread.clear();
    }

    @When("^the client deletes a sheet in the index (\\d+)$")
    public void the_client_deletes_a_sheet_in_the_index(int index) throws Throwable {
        this.spread.deleteSheet(index);
    }

    @When("^the client deletes a sheet in the index (-?\\d+) and catch the exception$")
    public void the_client_deletes_a_sheet_in_the_index_and_catch_the_exception(int index) throws Throwable {
        try {
            this.spread.deleteSheet(index);
        }
        catch (IndexOutOfBoundsException e) {
            lastException = e;
        }
    }

    @When("^the client deletes a sheet with the name \"([^\"]*)\"$")
    public void the_client_deletes_a_sheet_with_the_name(String name) throws Throwable {
        this.spread.deleteSheet(name);
    }
}
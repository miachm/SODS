package steps;

import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;
import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;


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
        assertEquals(lastException.getClass(), NullPointerException.class);
    }
}
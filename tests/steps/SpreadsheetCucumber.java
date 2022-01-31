package steps;

import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static org.testng.AssertJUnit.*;


public class SpreadsheetCucumber {
    private SpreadSheet spread;
    private byte[] buffer;
    private Exception lastException;
    private Sheet sheet;
    private List<Sheet> list_sheets;

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

    @Given("^creating a sheet with the name \"([^\"]*)\" into this\\.sheet$")
    public void creating_a_sheet_with_the_name_into_this_sheet(String name) throws Throwable {
        this.sheet = new Sheet(name);
    }

    @When("^the client deletes a sheet which corresponds with the object this\\.sheet$")
    public void the_client_deletes_a_sheet_which_corresponds_with_the_object_this_sheet() throws Throwable {
        this.spread.deleteSheet(this.sheet);
    }

    @When("^the client gets all the sheets into this\\.list_sheets$")
    public void the_client_gets_all_the_sheets_into_this_list_sheets() throws Throwable {
        this.list_sheets = this.spread.getSheets();
    }

    @Then("^the size of the list this\\.list_sheets is (\\d+)$")
    public void the_size_of_the_list_this_list_sheets_is(int num) throws Throwable {
        assertEquals(this.list_sheets.size(), num);
    }

    @Then("^the sheets in the list this\\.list_sheets have the following names:$")
    public void the_sheets_in_the_list_this_list_sheets_have_the_following_names(DataTable datatable) throws Throwable {
        Iterator<String> datatableIterator = datatable.asList(String.class).iterator();
        Iterator<Sheet> sheetIterator = this.list_sheets.iterator();
        while (datatableIterator.hasNext() && sheetIterator.hasNext()) {
            String name = datatableIterator.next();
            Sheet sheet = sheetIterator.next();
            assertEquals(name, sheet.getName());
        }
        assertFalse(datatableIterator.hasNext());
        assertFalse(sheetIterator.hasNext());
    }

    @When("^the client adds a random sheet into this\\.list_sheets and catch the exception$")
    public void the_client_adds_a_random_sheet_into_this_list_sheets_and_catch_the_exception() throws Throwable {
        try {
            this.list_sheets.add(new Sheet(get_random_name()));
        } catch (UnsupportedOperationException e) {
            this.lastException = e;
        }
    }

    @Then("^the last exception is UnsupportedOperationException$")
    public void the_last_exception_is_UnsupportedOperationException() throws Throwable {
        assertNotNull(lastException);
        assertTrue(lastException instanceof UnsupportedOperationException);
    }

    @When("^the client deletes the sheet (\\d+) from this\\.list_sheets and catch the exception$")
    public void the_client_deletes_the_sheet_from_this_list_sheets_and_catch_the_exception(int pos) throws Throwable {
        try {
            this.list_sheets.remove(pos);
        } catch (UnsupportedOperationException e) {
            this.lastException = e;
        }
    }

    @When("^the client get a sheet with the name \"([^\"]*)\" into this\\.sheet$")
    public void the_client_get_a_sheet_with_the_name_into_this_sheet(String name) throws Throwable {
        this.sheet = this.spread.getSheet(name);
    }

    @Then("^the name of this\\.sheet is \"([^\"]*)\"$")
    public void the_name_of_this_sheet_is(String name) throws Throwable {
        assertEquals(this.sheet.getName(), name);
    }

    @Then("^this\\.sheet is a null pointer$")
    public void this_sheet_is_a_null_pointer() throws Throwable {
        assertNull(this.sheet);
    }
}
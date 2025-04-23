package steps;

import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static org.testng.AssertJUnit.*;


public class SpreadsheetCucumber {

    private String get_random_name()
    {
        return UUID.randomUUID().toString();
    }

    @Given("^an empty Spreadsheet$")
    public void emptySpreadsheet() {
        World.spread = new SpreadSheet();
    }

    @When("^the client appends an empty sheet with the name \"([^\"]*)\"$")
    public void the_client_appends_a_sheet(String name) throws Throwable {
        World.spread.appendSheet(new Sheet(name));
    }

    @Then("^the number of sheets in the spreadsheet is (\\d+)$")
    public void the_number_of_sheets_in_the_spreadsheet_is(int numSheets) throws Throwable {
        assertEquals(World.spread.getNumSheets(), numSheets);
    }

    @Given("^a SpreadSheet with (\\d+) random sheets$")
    public void a_SpreadSheet_with_random_sheets(int numSheets) throws Throwable {
        World.spread = new SpreadSheet();
        for (int i = 0; i < numSheets; i++) {
            World.spread.appendSheet(new Sheet(get_random_name()));
        }
    }

    @Then("^the name of the sheet number (\\d+) is \"([^\"]*)\"$")
    public void the_name_of_the_sheet_is(int index, String name) throws Throwable {
        Sheet sheet = World.spread.getSheet(index);
        assertEquals(sheet.getName(), name);
    }
    
    @When("^save the spreadsheet in a file ([^\"]*)$")
    public void save_it_in_a_file(String file) throws Throwable {
        World.spread.save(new File(file));
    }

    @When("^save the spreadsheet in the memory$")
    public void save_it_in_the_memory() throws Throwable {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        World.spread.save(output);
        World.buffer = output.toByteArray();
    }

    @When("^load a spreadsheet from memory$")
    public void load_it_from_memory() throws Throwable {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(World.buffer);
        World.spread = new SpreadSheet(inputStream);
    }

    @When("^the client appends a null sheet and catch the exception$")
    public void the_client_appends_a_null_sheet() throws Throwable {
        try {
            World.spread.appendSheet(null);
        }
        catch (NullPointerException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client add an empty sheet in the index (\\d+) with the name \"([^\"]*)\"$")
    public void the_client_add_an_empty_sheet_in_the_index_with_the_name(int index, String name) throws Throwable {
        World.spread.addSheet(new Sheet(name), index);
    }

    @When("^the client adds a null sheet in the index (\\d+) and catch the exception$")
    public void the_client_adds_a_null_sheet_and_catch_the_exception(int index) throws Throwable {
        try {
            World.spread.addSheet(null, index);
        }
        catch (NullPointerException exception) {
            ExceptionChecker.registerException(exception);
        }
    }

    @When("^the client add an empty sheet in the invalid index (-?\\d+) with the name \"([^\"]*)\" and catch the exception$")
    public void the_client_add_an_empty_sheet_in_the_index_with_the_name_and_catch_the_exception(int index, String name) throws Throwable {
        try {
            World.spread.addSheet(new Sheet(name), index);
        }
        catch (IndexOutOfBoundsException exception) {
            ExceptionChecker.registerException(exception);
        }
    }

    @When("^the client clears the spreadsheet$")
    public void the_client_clears_the_spreadsheet() throws Throwable {
        World.spread.clear();
    }

    @When("^the client deletes a sheet in the index (\\d+)$")
    public void the_client_deletes_a_sheet_in_the_index(int index) throws Throwable {
        World.spread.deleteSheet(index);
    }

    @When("^the client deletes a sheet in the index (-?\\d+) and catch the exception$")
    public void the_client_deletes_a_sheet_in_the_index_and_catch_the_exception(int index) throws Throwable {
        try {
            World.spread.deleteSheet(index);
        }
        catch (IndexOutOfBoundsException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client deletes a sheet with the name \"([^\"]*)\"$")
    public void the_client_deletes_a_sheet_with_the_name(String name) throws Throwable {
        World.spread.deleteSheet(name);
    }

    @Given("^creating a sheet with the name \"([^\"]*)\" into World\\.sheet$")
    public void creating_a_sheet_with_the_name_into_this_sheet(String name) throws Throwable {
        World.sheet = new Sheet(name);
    }

    @When("^the client deletes a sheet which corresponds with the object World\\.sheet$")
    public void the_client_deletes_a_sheet_which_corresponds_with_the_object_this_sheet() throws Throwable {
        World.spread.deleteSheet(World.sheet);
    }

    @When("^the client gets all the sheets into World\\.list_sheets$")
    public void the_client_gets_all_the_sheets_into_this_list_sheets() throws Throwable {
        World.list_sheets = World.spread.getSheets();
    }

    @Then("^the size of the list World\\.list_sheets is (\\d+)$")
    public void the_size_of_the_list_this_list_sheets_is(int num) throws Throwable {
        assertEquals(World.list_sheets.size(), num);
    }

    @Then("^the sheets in the list World\\.list_sheets have the following names:$")
    public void the_sheets_in_the_list_this_list_sheets_have_the_following_names(DataTable datatable) throws Throwable {
        Iterator<String> datatableIterator = datatable.asList(String.class).iterator();
        Iterator<Sheet> sheetIterator = World.list_sheets.iterator();
        while (datatableIterator.hasNext() && sheetIterator.hasNext()) {
            String name = datatableIterator.next();
            Sheet sheet = sheetIterator.next();
            assertEquals(name, sheet.getName());
        }
        assertFalse(datatableIterator.hasNext());
        assertFalse(sheetIterator.hasNext());
    }

    @When("^the client adds a random sheet into World\\.list_sheets and catch the exception$")
    public void the_client_adds_a_random_sheet_into_this_list_sheets_and_catch_the_exception() throws Throwable {
        try {
            World.list_sheets.add(new Sheet(get_random_name()));
        } catch (UnsupportedOperationException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client deletes the sheet (-?\\d+) from World\\.list_sheets and catch the exception$")
    public void the_client_deletes_the_sheet_from_this_list_sheets_and_catch_the_exception(int pos) throws Throwable {
        try {
            World.list_sheets.remove(pos);
        } catch (UnsupportedOperationException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client get a sheet with the name \"([^\"]*)\" into World\\.sheet$")
    public void the_client_get_a_sheet_with_the_name_into_this_sheet(String name) throws Throwable {
        World.sheet = World.spread.getSheet(name);
    }

    @Then("^the name of World\\.sheet is \"([^\"]*)\"$")
    public void the_name_of_this_sheet_is(String name) throws Throwable {
        assertEquals(World.sheet.getName(), name);
    }

    @Then("^World\\.sheet is a null pointer$")
    public void this_sheet_is_a_null_pointer() throws Throwable {
        assertNull(World.sheet);
    }

    @When("^the client get a sheet in the index (-?\\d+) and catch the exception$")
    public void the_client_get_a_sheet_in_the_index_and_catch_the_exception(int index) throws Throwable {
        try {
            World.spread.getSheet(index);
        } catch (IndexOutOfBoundsException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client set a sheet in the index (\\d+) with the content from World\\.sheet$")
    public void the_client_set_a_sheet_in_the_index_with_the_content_from_this_sheet(int index) throws Throwable {
        World.spread.setSheet(World.sheet, index);
    }

    @When("^the client set a sheet in the index (-?\\d+) and catch the exception$")
    public void the_client_set_a_sheet_in_the_index_and_catch_the_exception(int index) throws Throwable {
        try {
            World.spread.setSheet(new Sheet(get_random_name()), index);
        } catch (IndexOutOfBoundsException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^the client set a null sheet in the index (\\d+) and catch the exception$")
    public void the_client_set_a_null_sheet_in_the_index_and_catch_the_exception(int index) throws Throwable {
        try {
            World.spread.setSheet(null, index);
        } catch (NullPointerException e) {
            ExceptionChecker.registerException(e);
        }
    }

    @When("^load a spreadsheet from the resource \"([^\"]*)\"$")
    public void load_a_spreadsheet_from_the_resource(String name) throws Throwable {
        World.spread = new SpreadSheet(new File("resources/" + name + ".ods"));
    }

    @When("^get the sheet (\\d+)$")
    public void get_the_sheet(int index) throws Throwable {
        World.sheet = World.spread.getSheet(index - 1);
    }

    @Then("^the cell values are:$")
    public void the_cell_values_are(DataTable dataTable) throws Throwable {
        List<List<String>> table = dataTable.asLists(String.class);

        Object[][] items = World.sheet.getDataRange().getValues();

        for (int i = 0; i < table.size(); i++) {
            for (int j = 0; j < table.get(i).size(); j++) {
                String value = table.get(i).get(j);
                if (value.equals(""))
                    value = null;
                assertEquals("Pos: " + i + ", " + j, value, items[i][j]);
            }
        }
    }
    
    @When("^get the sheet \"([^\"]*)\" from the spreadsheet$")
    public void get_the_sheet_from_the_spreadsheet(String name) throws Throwable {
        World.sheet = World.spread.getSheet(name);
    }

    @Given("^a timer started$")
    public void a_timer_started()
    {
        World.millis_start =  System.currentTimeMillis();
    }

    @Then("^the time elapsed is less than (\\d+) seconds$")
    public void time_elapsed(long seconds)
    {
        long millis_endend =  System.currentTimeMillis();
        assertTrue(millis_endend-World.millis_start < seconds*1000);
    }
}
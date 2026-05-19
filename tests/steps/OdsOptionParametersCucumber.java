package steps;

import com.github.miachm.sods.OdsOptionParameters;
import com.github.miachm.sods.SpreadSheet;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class OdsOptionParametersCucumber {

    @Given("^an ODS file \"([^\"]*)\" with styled cells$")
    public void an_ODS_file_with_styled_cells(String filename) throws Throwable {
        // This step just documents that we expect a test file with styles
        // The actual file should exist in the resources directory
        File testFile = new File("resources/" + filename);
        assertTrue("Test file should exist: " + testFile.getPath(), testFile.exists());
    }

    @When("^I load a spreadsheet from the resource \"([^\"]*)\" with load_styles true$")
    public void i_load_a_spreadsheet_with_load_styles_true(String resourceName) throws Throwable {
        OdsOptionParameters options = new OdsOptionParameters();
        options.setLoadStyles(true);
        World.spread = new SpreadSheet(new FileInputStream(new File("resources/" + resourceName + ".ods")), options);
    }

    @When("^I load a spreadsheet from the resource \"([^\"]*)\" with load_styles false$")
    public void i_load_a_spreadsheet_with_load_styles_false(String resourceName) throws Throwable {
        OdsOptionParameters options = new OdsOptionParameters();
        options.setLoadStyles(false);
        World.spread = new SpreadSheet(new FileInputStream(new File("resources/" + resourceName + ".ods")), options);
    }

    @When("^load a spreadsheet from memory with load_macros false$")
    public void load_a_spreadsheet_from_memory_with_load_macros_false() throws Throwable {
        OdsOptionParameters options = new OdsOptionParameters();
        options.setLoadMacros(false);
        World.spread = new SpreadSheet(new ByteArrayInputStream(World.buffer), options);
    }

    @When("^I load a spreadsheet from the resource \"([^\"]*)\"$")
    public void i_load_a_spreadsheet_from_resource(String resourceName) throws Throwable {
        World.spread = new SpreadSheet(new File("resources/" + resourceName + ".ods"));
    }

    @When("^I try to load a spreadsheet from the resource \"([^\"]*)\" with null options$")
    public void i_try_to_load_spreadsheet_with_null_options(String resourceName) throws Throwable {
        try {
            World.spread = new SpreadSheet(new FileInputStream(new File("resources/" + resourceName + ".ods")), null);
        } catch (NullPointerException e) {
            ExceptionChecker.registerException(e);
        }
    }


    @Then("^the range has bold style$")
    public void the_range_has_bold_style() throws Throwable {
        assertTrue("Range should have bold style", World.range.getStyle().isBold());
    }

    @Then("^it should throw a NullPointerException$")
    public void it_should_throw_null_pointer_exception() throws Throwable {
        ExceptionChecker.checkNullPointer();
    }

    @When("^I load a spreadsheet from the resource \"([^\"]*)\" with sheet numbers \\[([^\\]]*)\\]$")
    public void i_load_a_spreadsheet_with_sheet_numbers(String resourceName, String sheetNumbersStr) throws Throwable {
        OdsOptionParameters options = new OdsOptionParameters();
        if (!sheetNumbersStr.trim().isEmpty()) {
            List<Integer> sheetNumbers = Arrays.stream(sheetNumbersStr.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            options.setSheetNumbers(sheetNumbers);
        } else {
            options.setSheetNumbers(Arrays.asList());
        }
        World.spread = new SpreadSheet(new FileInputStream(new File("resources/" + resourceName + ".ods")), options);
    }

    @When("^I load a spreadsheet from the resource \"([^\"]*)\" with null sheet numbers$")
    public void i_load_a_spreadsheet_with_null_sheet_numbers(String resourceName) throws Throwable {
        OdsOptionParameters options = new OdsOptionParameters();
        options.setSheetNumbers(null);
        World.spread = new SpreadSheet(new FileInputStream(new File("resources/" + resourceName + ".ods")), options);
    }

    @Then("^the spreadsheet should have (\\d+) sheets$")
    public void the_spreadsheet_should_have_sheets(int expectedCount) throws Throwable {
        assertEquals("Expected " + expectedCount + " sheets", expectedCount, World.spread.getNumSheets());
    }

    @Then("^the sheet at index (\\d+) should be named \"([^\"]*)\"$")
    public void the_sheet_at_index_should_be_named(int index, String expectedName) throws Throwable {
        assertEquals("Sheet at index " + index + " should be named " + expectedName, 
                     expectedName, World.spread.getSheet(index).getName());
    }
}
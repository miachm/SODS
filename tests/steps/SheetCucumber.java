package steps;

import com.github.miachm.sods.*;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
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
    private Color getRandomColor(Random random)
    {
        int red = random.nextInt(255);
        int green = random.nextInt(255);
        int blue = random.nextInt(255);
        return new Color(red, green, blue);
    }
    Style getRandomStyle()
    {
        Style style = new Style();
        style.setBold(random.nextBoolean());
        style.setItalic(random.nextBoolean());
        style.setUnderline(random.nextBoolean());

        if (random.nextBoolean())
            style.setBackgroundColor(getRandomColor(random));
        if (random.nextBoolean())
            style.setFontSize(random.nextInt(42) + 1);
        if (random.nextBoolean())
            style.setFontColor(getRandomColor(random));

        return style;
    }


    @Given("^a sheet \"([^\"]*)\", size (\\d+)x(\\d+) and random data$")
    public void a_sheet_size_x_and_random_data(String name, int rows, int columns) throws Throwable {
        World.sheet = new Sheet(name, rows, columns);
        Range range = World.sheet.getDataRange();
        List<Integer> integers = new ArrayList<>();
        List<Style> styles = new ArrayList<>();
        List<String> formulas = new ArrayList<>();
        List<OfficeAnnotation> annotations = new ArrayList<>();
        for (int i = 0; i < range.getNumValues(); i++) {
            integers.add(random.nextInt());
            styles.add(getRandomStyle());
            formulas.add(getRandomFormula());
            annotations.add(getRandomAnnotations());
        }

        range.setValues(integers.toArray());
        range.setStyles(styles.toArray(new Style[0]));
        range.setFormulas(formulas.toArray(new String[0]));
        range.setAnnotations(annotations.toArray(new OfficeAnnotation[0]));
    }

    private OfficeAnnotation getRandomAnnotations() {
        if (random.nextBoolean())
            return null;

        LocalDateTime date = LocalDateTime.of(1970+random.nextInt(50), Month.APRIL,
                1 + random.nextInt(29), random.nextInt(24), 1, 1);
        String msg = "MSG: " + random.nextInt();
        return new OfficeAnnotation(msg, date);
    }

    private String getRandomFormula() {
        String[] arr = {"=A1+B1", "=A2-B2", "=SUM(A1:A2)"};

        if (random.nextBoolean() && random.nextBoolean())
            return arr[random.nextInt(arr.length)];
        else
            return null;
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

    @When("^the client appends the sheet contained in World\\.sheet$")
    public void the_client_appends_the_sheet_contained_in_this_sheet() throws Throwable {
        World.spread.appendSheet(World.sheet);
    }

    @When("^get the first sheet$")
    public void get_the_first_sheet() throws Throwable {
        World.sheet = World.spread.getSheet(0);
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

}

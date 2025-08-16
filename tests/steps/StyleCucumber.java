package steps;

import com.github.miachm.sods.Color;
import com.github.miachm.sods.ConditionalFormat;
import com.github.miachm.sods.Style;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class StyleCucumber {

    @When("^get the first conditionalformat of the cell in (\\d+),(\\d+) as World\\.conditionalFormat$")
    public void get_the_conditionalformat_of_the_cell_in_as_World_conditionalFormat(int row, int column) throws Throwable {
        World.conditionalFormat = World.sheet.getRange(row, column).getStyle().getConditions().get(0);
    }

    @When("^get the first conditionalformat of the cell in (\\d+),(\\d+) as World\\.otherConditionalFormat$")
    public void get_the_conditionalformat_of_the_cell_in_as_World_otherConditionalFormat(int row, int column) throws Throwable {
        World.otherConditionalFormat = World.sheet.getRange(row, column).getStyle().getConditions().get(0);
    }

    @Then("^the style applied in World\\.conditionalFormat is not default$")
    public void the_style_applied_in_World_conditionalFormat_is_not_default() throws Throwable {
        assertFalse(World.conditionalFormat.getStyleApplied().isDefault());
    }

    @When("^create a background-color style as World\\.style$")
    public void create_a_background_color_style_as_World_style() throws Throwable {
        World.style = new Style();
        World.style.setBackgroundColor(new Color(255, 0, 0));
    }

    @When("^create a conditionalFormat of greater-value (\\d+) with World\\.style$")
    public void create_a_conditionalFormat_of_greater_value_with_World_style(int value) throws Throwable {
        World.conditionalFormat = ConditionalFormat.conditionWhenValueIsGreater(World.style, value);
    }

    @When("^create a conditionalFormat of greater-or-equal-value (\\d+) with World\\.style$")
    public void create_a_conditionalFormat_of_greater_or_equal_value_with_World_style(int value) throws Throwable {
        World.conditionalFormat = ConditionalFormat.conditionWhenValueIsGreaterOrEqual(World.style, value);
    }

    @When("^create a conditionalFormat of lower-value (\\d+) with World\\.style$")
    public void create_a_conditionalFormat_of_lower_value_with_World_style(int value) throws Throwable {
        World.conditionalFormat = ConditionalFormat.conditionWhenValueIsLower(World.style, value);
    }

    @When("^create a conditionalFormat of lower-or-equal-value (\\d+) with World\\.style$")
    public void create_a_conditionalFormat_of_lower_or_equal_value_with_World_style(int value) throws Throwable {
        World.conditionalFormat = ConditionalFormat.conditionWhenValueIsLowerOrEqual(World.style, value);
    }

    @When("^create a conditionalFormat of equal-value (\\d+) with World\\.style$")
    public void create_a_conditionalFormat_of_equal_value_with_World_style(int value) throws Throwable {
        World.conditionalFormat = ConditionalFormat.conditionWhenValueIsEqual(World.style, value);
    }

    @When("^create a conditionalFormat of not-equal-value (\\d+) with World\\.style$")
    public void create_a_conditionalFormat_of_not_equal_value_with_World_style(int value) throws Throwable {
        World.conditionalFormat = ConditionalFormat.conditionWhenValueIsNotEqual(World.style, value);
    }

    @When("^create a conditionalFormat of equal-text \"([^\"]+)\" with World\\.style$")
    public void create_a_conditionalFormat_of_equal_text_with_World_style(String text) throws Throwable {
        World.conditionalFormat = ConditionalFormat.conditionWhenValueIsEqual(World.style, text);
    }

    @When("^create a conditionalFormat of not-equal-text \"([^\"]+)\" with World\\.style$")
    public void create_a_conditionalFormat_of_not_equal_text_with_World_style(String text) throws Throwable {
        World.conditionalFormat = ConditionalFormat.conditionWhenValueIsNotEqual(World.style, text);
    }

    @When("^create a conditionalFormat of text-contains \"([^\"]+)\" with World\\.style$")
    public void create_a_conditionalFormat_of_text_contains_with_World_style(String text) throws Throwable {
        World.conditionalFormat = ConditionalFormat.conditionWhenTextContains(World.style, text);
    }

    @When("^create a conditionalFormat of text-does-not-contain \"([^\"]+)\" with World\\.style$")
    public void create_a_conditionalFormat_of_text_does_not_contain_with_World_style(String text) throws Throwable {
        World.conditionalFormat = ConditionalFormat.conditionWhenTextDoesNotContain(World.style, text);
    }

    @When("^create a conditionalFormat of text-starts-with \"([^\"]+)\" with World\\.style$")
    public void create_a_conditionalFormat_of_text_starts_with_with_World_style(String text) throws Throwable {
        World.conditionalFormat = ConditionalFormat.conditionWhenTextStartsWith(World.style, text);
    }

    @When("^create a conditionalFormat of text-ends-with \"([^\"]+)\" with World\\.style$")
    public void create_a_conditionalFormat_of_text_ends_with_with_World_style(String text) throws Throwable {
        World.conditionalFormat = ConditionalFormat.conditionWhenTextEndsWith(World.style, text);
    }

    @When("^create a conditionalFormat of cell-is-empty with World\\.style$")
    public void create_a_conditionalFormat_of_cell_is_empty_with_World_style() throws Throwable {
        World.conditionalFormat = ConditionalFormat.conditionWhenCellIsEmpty(World.style);
    }

    @When("^create a conditionalFormat of cell-is-not-empty with World\\.style$")
    public void create_a_conditionalFormat_of_cell_is_not_empty_with_World_style() throws Throwable {
        World.conditionalFormat = ConditionalFormat.conditionWhenCellIsNotEmpty(World.style);
    }

    @When("^create a conditionalFormat of between-values (\\d+) and (\\d+) with World\\.style$")
    public void create_a_conditionalFormat_of_between_values_with_World_style(int init, int end) throws Throwable {
        World.conditionalFormat = ConditionalFormat.conditionWhenValueIsBetween(World.style, init, end);
    }

    @When("^apply the conditionalFormat to the cell (\\d+),(\\d+)$")
    public void apply_the_conditionalFormat_to_the_cell(int row, int column) throws Throwable {
        World.sheet.getRange(row, column).getStyle().addCondition(World.conditionalFormat);
    }

    @Then("^the style of the conditionalFormat is World\\.style$")
    public void the_style_of_the_conditionalFormat_is_World_style() throws Throwable {
        assertEquals(World.style, World.conditionalFormat.getStyleApplied());
    }

    @Then("^the World\\.conditionalFormat is equal to World\\.otherConditionalFormat$")
    public void the_World_conditionalFormat_is_equal_to_World_otherConditionalFormat() throws Throwable {
        assertEquals(World.conditionalFormat, World.otherConditionalFormat);
    }
}

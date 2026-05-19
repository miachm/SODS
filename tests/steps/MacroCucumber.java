package steps;

import com.github.miachm.sods.Macro;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class MacroCucumber {

    private boolean lastRemoveResult;

    @When("^the client adds a macro named \"([^\"]*)\" with code \"([^\"]*)\"$")
    public void the_client_adds_a_macro_named_with_code(String name, String code) {
        code = code.replace("\\n", "\n");
        World.spread.addMacro(new Macro(name, code));
    }

    @When("^the client removes the macro named \"([^\"]*)\"$")
    public void the_client_removes_the_macro_named(String name) {
        lastRemoveResult = World.spread.removeMacro(name);
    }

    @When("^the client sets all macros to a list containing \"([^\"]*)\" with code \"([^\"]*)\"$")
    public void the_client_sets_all_macros_to_list(String name, String code) {
        code = code.replace("\\n", "\n");
        World.spread.setMacros(Collections.singletonList(new Macro(name, code)));
    }

    @Then("^the spreadsheet should have (\\d+) macros?$")
    public void the_spreadsheet_should_have_macros(int expected) {
        assertEquals("Macro count mismatch", expected, World.spread.getMacros().size());
    }

    @Then("^the macro \"([^\"]*)\" should have code \"([^\"]*)\"$")
    public void the_macro_should_have_code(String name, String expectedCode) {
        expectedCode = expectedCode.replace("\\n", "\n");
        List<Macro> macros = World.spread.getMacros();
        for (Macro m : macros) {
            if (m.getName().equals(name)) {
                assertEquals("Code mismatch for macro " + name, expectedCode, m.getCode());
                return;
            }
        }
        fail("No macro named '" + name + "' found in spreadsheet");
    }

    @Then("^the macro removal result should be true$")
    public void the_macro_removal_result_should_be_true() {
        assertTrue("Expected removeMacro to return true", lastRemoveResult);
    }

    @Then("^the macro removal result should be false$")
    public void the_macro_removal_result_should_be_false() {
        assertFalse("Expected removeMacro to return false", lastRemoveResult);
    }
}

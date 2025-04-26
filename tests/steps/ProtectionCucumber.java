package steps;

import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProtectionCucumber {

    @When("^the client sets the password \"([^\"]*)\"$")
    public void the_client_sets_the_password(String password) throws Throwable {
        World.sheet.setPassword(password);
    }

    @Then("^the sheet is protected$")
    public void the_sheet_is_protected() throws Throwable {
        assertTrue(World.sheet.isProtected());
    }

    @Then("^the sheet is not protected$")
    public void the_sheet_is_not_protected() throws Throwable {
        assertFalse(World.sheet.isProtected());
    }

    @When("^the client sets the password as null$")
    public void the_client_sets_the_password_as_null() throws Throwable {
        World.sheet.setPassword(null);
    }

    @When("^the clients sets an empty password and catch the exception$")
    public void the_clients_sets_an_empty_password_and_catch_the_exception() throws Throwable {
        try {
            World.sheet.setPassword("");
        }
        catch (IllegalArgumentException e) {
            ExceptionChecker.registerException(e);
        }
    }
}

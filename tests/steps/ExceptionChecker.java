package steps;

import io.cucumber.java.en.Then;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

public class ExceptionChecker {
    private static Exception lastException;

    static void registerException(Exception e)
    {
        lastException = e;
    }

    @Then("^the last exception is \"([^\"]*)\"$")
    public void the_last_exception_is(String name) throws Throwable {
        assertNotNull(lastException);
        Package packageName = lastException.getClass().getPackage();
        String fullClassName = packageName.getName() + "." + name;
        Exception exception = (Exception) Class.forName(fullClassName).getConstructor().newInstance();
        assertTrue("The exception expected was " + name + " but received "  + lastException.getClass().getName(),
                exception.getClass().isInstance(lastException));
    }

    public static void reset() {
        lastException = null;
    }
}

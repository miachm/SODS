package steps;

import io.cucumber.java.Before;

public class Hooks {

    @Before
    public void setState() {
        World.reset();
        ExceptionChecker.reset();
    }
}

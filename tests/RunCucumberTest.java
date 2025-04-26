import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import steps.ExceptionChecker;
import steps.World;

@CucumberOptions(
        features = "resources/features",
        glue = {"steps"},
        plugin = {"pretty", "html:target/cucumber-reports"}
)
public class RunCucumberTest extends AbstractTestNGCucumberTests {

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        World.reset();
        ExceptionChecker.reset();
    }

    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
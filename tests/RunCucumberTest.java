import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(features = "classpath:features", glue = "steps")
public class RunCucumberTest extends AbstractTestNGCucumberTests {
}

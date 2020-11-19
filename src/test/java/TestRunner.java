import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;

@CucumberOptions(features = {"src/test/resources/"},
        plugin = {"pretty","html:target/cucumber-reports"},
        monochrome = true)
public class TestRunner extends AbstractTestNGCucumberTests{
}
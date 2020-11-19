import Constants.Constants;
import cloudCommunication.GCPConnection;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import restCommunication.RestResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import static org.testng.Assert.assertEquals;

public class PreTranscriptionReportStepdefs implements Constants {

    TriggerDag triggerDag = new TriggerDag();
    GCPConnection gcpConnection = new GCPConnection();
    RestResponse restResponse = new RestResponse();

    int before_reportgeneration_count;
    int before_csvreport_count;



    @Then("^the dag should run successfully$")
    public void theDagShouldRunSuccessfully() throws InterruptedException {
        String dagstatus = triggerDag.triggerAndWait(REPORT_PRE_DAG_ID, DAG_STATE_API, 3,35000);
        assertEquals(dagstatus,"success");

    }

    @When("^i trigger the report generation dag$")
    public void iTriggerTheReportGenerationDag() {
        before_reportgeneration_count = gcpConnection.bucketSize(Constants.PRE_REPORT_PATH);
        before_csvreport_count = gcpConnection.bucketSize(Constants.PRE_REPORT__CSV_PATH);
        restResponse = triggerDag.triggerDag(TRIGGER_API, REPORT_PRE_DAG_ID,triggerDag.setformatteddate());
        assertEquals(restResponse.getStatus(),"SUCCESS");
        
    }

    @And("^reports should be generated in Reports folder in Google Bucket$")
    public void reportsShouldBeGeneratedInReportsFolderInGoogleBucket() {
        int after_reportgeneration_count = gcpConnection.bucketSize(Constants.PRE_REPORT_PATH);
        int after_csvreport_count = gcpConnection.bucketSize(Constants.PRE_REPORT__CSV_PATH);
        assertEquals(after_reportgeneration_count,before_reportgeneration_count+1);
        assertEquals(after_csvreport_count,before_csvreport_count+1);

    }

    @When("^Airflow variable is uploaded with wrong source name$")
    public void airflowVariableIsUploadedWithWrongSourceName() throws IOException, URISyntaxException {
        triggerDag.setAirflowVariable(VARIABLE_API,"set","validation_report_source_pre-transcription ","[\"test\"]");
        
    }

    @And("^I trigger the Pre Transcription Report dag$")
    public void iTriggerThePreTranscriptionReportDag() {
        restResponse = triggerDag.triggerDag(TRIGGER_API, REPORT_PRE_DAG_ID,triggerDag.setformatteddate());
        assertEquals(restResponse.getStatus(),"SUCCESS");
        
    }


    @Then("^The \"([^\"]*)\" Dag should fail$")
    public void theDagShouldFail(String dagid) throws InterruptedException {
        String dagstatus = triggerDag.triggerAndWait(dagid, DAG_STATE_API, 2,25000);
        assertEquals(dagstatus,"failed");
    }
}








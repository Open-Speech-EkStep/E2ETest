import Constants.Constants;
import cloudCommunication.GCPConnection;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import restCommunication.RestResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import static org.testng.Assert.assertEquals;

public class PostTranscriptionReportStepdefs implements Constants {

    TriggerDag triggerDag = new TriggerDag();
    GCPConnection gcpConnection = new GCPConnection();
    RestResponse restResponse = new RestResponse();
    Commons commonmethods = new Commons();
    int before_csvreport_count;



    @When("^i Trigger the Report Generation Dag$")
    public void iTriggerTheReportGenerationDag() {
        before_csvreport_count = gcpConnection.bucketSize(Constants.POST_REPORT__CSV_PATH);
        restResponse = triggerDag.triggerDag(TRIGGER_API, REPORT_POST_DAG_ID,triggerDag.setformatteddate());
        assertEquals(restResponse.getStatus(),"SUCCESS");
    }

    @And("^Report should be generated in Reports folder in Google Bucket$")
    public void reportShouldBeGeneratedInReportsFolderInGoogleBucket() {
        int after_csvreport_count = gcpConnection.bucketSize(Constants.POST_REPORT__CSV_PATH);
        assertEquals(after_csvreport_count,before_csvreport_count+1);
    }

    @When("^Airflow variable is uploaded with wrong source name for Post Transcription Report$")
    public void airflowVariableIsUploadedWithWrongSourceNameForPostTranscriptionReport() throws IOException, URISyntaxException {
        triggerDag.setAirflowVariable(VARIABLE_API,"set","validation_report_source_post-transcription ","[\"test\"]");
    }

    @And("^I trigger the Post Transcription Report dag$")
    public void iTriggerThePostTranscriptionReportDag() throws InterruptedException {
        restResponse = triggerDag.triggerDag(TRIGGER_API, REPORT_POST_DAG_ID,triggerDag.setformatteddate());
        assertEquals(restResponse.getStatus(),"SUCCESS");
    }


    @When("^Data is deleted from the DB$")
    public void dataIsDeletedFromTheDB() throws SQLException {
        commonmethods.deletrecords();
    }

    @And("^Correct value of Airflow variable is uploaded$")
    public void correctValueOfAirflowVariableIsUploaded() throws IOException, URISyntaxException {
        triggerDag.setAirflowVariable(VARIABLE_API,"set","validation_report_source_post-transcription ","[\"testamulya2\"]");

    }
}
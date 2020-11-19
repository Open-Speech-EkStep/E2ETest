import Constants.Constants;
import cloudCommunication.GCPConnection;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import databaseConnection.Postgresclient;
import restCommunication.RestResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SNRStepdefs implements Constants {

    TriggerDag triggerDag = new TriggerDag();
    GCPConnection gcpConnection = new GCPConnection();
    Postgresclient postgresclient = Postgresclient.getPostgresClient();
    Commons commonmethods = new Commons();
    RestResponse restResponse = new RestResponse();
    String audio_id_testamulya2 = "";
    private int beforeSNRFilecount;
    private int beforeDuplicatefilecount;


    @Given("Airflow variables are uploaded")
    public void airflowVariablesAreUploaded() throws URISyntaxException, IOException {
        commonmethods.uploadAirflowVariables();

    }


    @And("^Test files are uploaded to the Google bucket$")
    public void testFilesAreUploadedToTheGoogleBucket() throws IOException {
        commonmethods.uploadTestFiles();
    }

    @And("^Files are removed from Duplicate,SNR Done Folder in GCP Bucket$")
    public void filesAreRemovedFromDuplicateSNRDoneFolderInGCPBucket() throws IOException {
        commonmethods.removeFilesfromDulicateFolder();
    }

    @When("^I trigger test SNR DAG$")
    public void iTriggerTestDAG() throws SQLException {
        commonmethods.deletrecords();
        beforeSNRFilecount = gcpConnection.bucketSize(Constants.SNR_DONE_PATH);
        System.out.println("before file count" + beforeSNRFilecount);
        restResponse = triggerDag.triggerDag(TRIGGER_API, CATALOGUE_DAG_ID, triggerDag.setformatteddate());
        assertEquals(restResponse.getStatus(), "SUCCESS");

    }


    @And("^DB tables should be updated successfully$")
    public void dbTablesShouldBeUpdatedSuccessfully() throws SQLException {
        ResultSet mediametadata = postgresclient.select_query("select * FROM media_metadata_staging where source = 'testamulya2' ");
        while (mediametadata.next()) {
            boolean isnormalised = mediametadata.getBoolean("is_normalized");
            BigDecimal audio_id = mediametadata.getBigDecimal("audio_id");
            audio_id_testamulya2 = audio_id.toString();
            System.out.println(audio_id_testamulya2);

            assertEquals(isnormalised, true);
            assertTrue(isnormalised);

        }

        ResultSet media = postgresclient.select_query("select count(*) FROM media where source= 'testamulya2' ");
        while (media.next()) {
            int numberofrows = media.getInt(1);
            assertEquals(numberofrows, 1);
        }

        ResultSet media_speaker_mapping_count = postgresclient.select_query("select count(*) FROM media_speaker_mapping where audio_id in (select audio_id FROM media_metadata_staging where source = 'testamulya2')");
        while (media_speaker_mapping_count.next()) {
            int numberofrows = media_speaker_mapping_count.getInt(1);
            assertEquals(numberofrows, 13);
        }


        ResultSet media_speaker_mapping_statuscount = postgresclient.select_query("select count(*) FROM media_speaker_mapping where audio_id ='" + audio_id_testamulya2 + "' ");
        while (media_speaker_mapping_statuscount.next()) {
            int numberofrows = media_speaker_mapping_statuscount.getInt(1);
            assertEquals(numberofrows, 13);
        }

    }

    @And("^Correct number of files should be present in the Google bucket$")
    public void correctNumberOfFilesShouldBePresentInTheGoogleBucket() {
        int CatalogueFilecount = gcpConnection.bucketSize(Constants.RAW_CATALOGUED_PATH + audio_id_testamulya2 + "/clean/");
        assertEquals(CatalogueFilecount, 13);
        int afterSNRFilecount = gcpConnection.bucketSize(Constants.SNR_DONE_PATH);
        assertEquals(afterSNRFilecount, beforeSNRFilecount + 2);

    }


    @And("^Files should be moved to Duplicate folder in Google Bucket$")
    public void filesShouldBeMovedToDuplicateFolderInGoogleBucket() {

        int afterDuplicatefilecount = gcpConnection.bucketSize(Constants.DUPLICATE_FILE_PATH);
        //check duplicate file
        assertEquals(afterDuplicatefilecount, beforeDuplicatefilecount + 2);
    }


    @When("^I trigger test DAG again for the same file$")
    public void iTriggerTestDAGAgainForTheSameFile() {
        beforeDuplicatefilecount = gcpConnection.bucketSize(Constants.DUPLICATE_FILE_PATH);
        System.out.println(beforeDuplicatefilecount);
        restResponse = triggerDag.triggerDag(TRIGGER_API, CATALOGUE_DAG_ID, triggerDag.setformatteddate());
        assertEquals(restResponse.getStatus(), "SUCCESS");

    }


    @Then("^The \"([^\"]*)\" Dag should run successfully$")
    public void theDagShouldRunSuccessfully(String dagid) throws InterruptedException {
      System.out.println("--------------------------------------------------------"+dagid);
       String dagstatus = triggerDag.triggerAndWait(dagid, DAG_STATE_API, 5, 45000);
        assertEquals(dagstatus, "success");
    }

}





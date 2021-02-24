import Constants.Constants;
import cloudCommunication.GCPConnection;
import io.cucumber.java.en.*;
import databaseConnection.Postgresclient;
import restCommunication.RestResponse;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.testng.Assert.assertEquals;

public class DataMarkerStepdefs implements Constants {

    TriggerDag triggerDag = new TriggerDag();
    GCPConnection gcpConnection = new GCPConnection();
    Postgresclient postgresclient = Postgresclient.getPostgresClient();
    RestResponse restResponse = new RestResponse();
    String audio_id_test_source = "";





    @When("^I trigger the dag$")
    public void iTriggerTheDag() {

        restResponse = triggerDag.triggerDag(TRIGGER_API, DATA_MARKER_DAG_ID,triggerDag.setformatteddate());
        assertEquals(restResponse.getStatus(),"SUCCESS");

    }

    @Then("^The dag should run successfully$")
    public void theDagShouldRunSuccessfully() throws InterruptedException {
        String dagstatus;
        dagstatus = triggerDag.triggerAndWait(DATA_MARKER_DAG_ID, DAG_STATE_API, 3,55000);
        assertEquals(dagstatus,"success");
    }

    @And("^media meta data staging table should be updated with staged_for_transcription = TRUE$")
    public void mediaMetaDataStagingTableShouldBeUpdatedWithStaged_for_transcriptionTRUE() throws SQLException {

        ResultSet media_speaker_mapping_count = postgresclient.select_query("select count(*) FROM media_speaker_mapping where audio_id in (select audio_id FROM media_metadata_staging where source = 'test_source') AND staged_for_transcription ='TRUE'");
        while (media_speaker_mapping_count.next())
        {
            int numberofrows = media_speaker_mapping_count.getInt(1);
            assertEquals(numberofrows,9);
        }

        ResultSet mediametadata = postgresclient.select_query("select * FROM media_metadata_staging where source = 'test_source' ");
        while (mediametadata.next())
        {
            BigDecimal audio_id = mediametadata.getBigDecimal ("audio_id");
            audio_id_test_source = audio_id.toString();
        }
    }

    @And("^Files should be moved to Landing Folder in Google Bucket$")
    public void filesShouldBeMovedToLandingFolderInGoogleBucket() {
        int bucketsize =  gcpConnection.bucketSize(Constants.RAW_LANDING_PATH+audio_id_test_source+"/clean/");
        assertEquals(bucketsize,9);
    }
}

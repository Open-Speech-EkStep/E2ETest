import Constants.Constants;
import cloudCommunication.GCPConnection;
import io.cucumber.java.en.*;
import databaseConnection.Postgresclient;
import restCommunication.RestResponse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.testng.Assert.assertEquals;

public class STTStepdefs implements Constants {

    TriggerDag triggerDag = new TriggerDag();
    GCPConnection gcpConnection = new GCPConnection();
    Postgresclient postgresclient = Postgresclient.getPostgresClient();
    RestResponse restResponse = new RestResponse();
    String audio_id_test = "";
    Commons commonmethods = new Commons();


    @When("^I trigger the STT Dag$")
    public void iTriggerTheSTTDag() {
        restResponse = triggerDag.triggerDag(TRIGGER_API, STT_DAG,triggerDag.setformatteddate());
        assertEquals(restResponse.getStatus(),"SUCCESS");

    }

    @And("^Database tables should be updated with correct data$")
    public void databaseTablesShouldBeUpdatedWithCorrectData() throws SQLException {
        ResultSet mediametadata = postgresclient.select_query("select * FROM media_metadata_staging where source = 'test_source' ");
        while (mediametadata.next())
        {
            boolean isnormalised = mediametadata.getBoolean("is_normalized");
            BigDecimal audio_id = mediametadata.getBigDecimal ("audio_id");
            audio_id_test = audio_id.toString();
            System.out.println(audio_id_test);
            assertTrue(isnormalised);
        }

        ResultSet media = postgresclient.select_query("select count(*) FROM media where source= 'test_source' ");
        while (media.next())
        {
            int numberofrows = media.getInt(1);
            assertEquals(numberofrows,1);
        }

        ResultSet media_speaker_mapping_count = postgresclient.select_query("select count(*) FROM media_speaker_mapping where audio_id in (select audio_id FROM media_metadata_staging where source = 'test_source') AND status= 'Rejected' ");
        while (media_speaker_mapping_count.next())
        {
            int numberofrows = media_speaker_mapping_count.getInt(1);
            assertEquals(numberofrows,3);
        }
    }

    @And("^File should be uploaded to clean and rejected folder in STT path$")
    public void fileShouldBeUploadedToCleanAndRejectedFolderInSTTPath() {

        System.out.println(Constants.RAW_CATALOGUED_PATH+audio_id_test+"/clean/");

        int rejectedfilecount =  gcpConnection.bucketSize(Constants.STT_PATH+audio_id_test+"/rejected/");
        assertEquals(rejectedfilecount,3);

        int cleanfilecount =  gcpConnection.bucketSize(Constants.STT_PATH+audio_id_test+"/clean/");
        assertEquals(cleanfilecount,13);

    }

    @Given("Files are removed from Landing Folder")
    public void filesAreRemovedFromLandingFolder() throws IOException {
       commonmethods.removeFilesfromLanding();
    }
}






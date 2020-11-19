import Constants.Constants;
import cloudCommunication.GCPConnection;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import databaseConnection.Postgresclient;
import restCommunication.RestResponse;
import static org.testng.Assert.assertTrue;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.testng.Assert.assertEquals;

public class STTStepdefs implements Constants {

    TriggerDag triggerDag = new TriggerDag();
    GCPConnection gcpConnection = new GCPConnection();
    Postgresclient postgresclient = Postgresclient.getPostgresClient();
    RestResponse restResponse = new RestResponse();
    String audio_id_testamulya2 = "";


    @When("^I trigger the STT Dag$")
    public void iTriggerTheSTTDag() {
        restResponse = triggerDag.triggerDag(TRIGGER_API, STT_DAG,triggerDag.setformatteddate());
        assertEquals(restResponse.getStatus(),"SUCCESS");

    }

    @And("^Database tables should be updated with correct data$")
    public void databaseTablesShouldBeUpdatedWithCorrectData() throws SQLException {
        ResultSet mediametadata = postgresclient.select_query("select * FROM media_metadata_staging where source = 'testamulya2' ");
        while (mediametadata.next())
        {
            boolean isnormalised = mediametadata.getBoolean("is_normalized");
            BigDecimal audio_id = mediametadata.getBigDecimal ("audio_id");
            audio_id_testamulya2 = audio_id.toString();
            System.out.println(audio_id_testamulya2);
            assertTrue(isnormalised);
        }

        ResultSet media = postgresclient.select_query("select count(*) FROM media where source= 'testamulya2' ");
        while (media.next())
        {
            int numberofrows = media.getInt(1);
            assertEquals(numberofrows,1);
        }

        ResultSet media_speaker_mapping_count = postgresclient.select_query("select count(*) FROM media_speaker_mapping where audio_id in (select audio_id FROM media_metadata_staging where source = 'testamulya2') AND status= 'Rejected' ");
        while (media_speaker_mapping_count.next())
        {
            int numberofrows = media_speaker_mapping_count.getInt(1);
            assertEquals(numberofrows,3);
        }
    }

    @And("^File should be uploaded to clean and rejected folder in STT path$")
    public void fileShouldBeUploadedToCleanAndRejectedFolderInSTTPath() {

        System.out.println(Constants.RAW_CATALOGUED_PATH+audio_id_testamulya2+"/clean/");

        int rejectedfilecount =  gcpConnection.bucketSize(Constants.STT_PATH+audio_id_testamulya2+"/rejected/");
        assertEquals(rejectedfilecount,3);

        int cleanfilecount =  gcpConnection.bucketSize(Constants.STT_PATH+audio_id_testamulya2+"/clean/");
        assertEquals(cleanfilecount,13);

    }
}






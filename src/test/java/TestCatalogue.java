import Constants.Constants;
import cloudCommunication.GCPConnection;
import cloudCommunication.UploadObject;
import databaseConnection.Postgresclient;
import org.testng.annotations.Test;
import restCommunication.RestResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.testng.Assert.*;

public class TestCatalogue implements Constants {

    TriggerDag triggerDag = new TriggerDag();
    RestResponse restResponse = new RestResponse();
    UploadObject upload = new UploadObject();
    GCPConnection gcpConnection = new GCPConnection();
    Postgresclient postgresclient = new Postgresclient();
    String audio_id_testamulya2="";


    public void testdataprep() throws IOException {
        upload.uploadObject(PROJECT_ID,BUCKET_NAME,CSVOBJECT_PATH,CSV_PATH);
        upload.uploadObject(PROJECT_ID,BUCKET_NAME,AUDIOOBJECT_PATH,AUDIOFILE_PATH);


    }

    public void deletrecords() throws SQLException{
        postgresclient.delete_data("Delete FROM media_speaker_mapping where audio_id in (select audio_id FROM media_metadata_staging where source in ('testamulya2'))");
        postgresclient.delete_data("Delete FROM media_metadata_staging where source in ('testamulya2')");
        postgresclient.delete_data("Delete FROM media where source in ('testamulya2')");
    }

    @Test (enabled = true,priority = 0)
    public void validateTriggerSuccessful() throws InterruptedException, IOException, SQLException {
        String dagstatus;
            deletrecords();
            testdataprep();
            restResponse = triggerDag.triggerDag(TRIGGER_API, CATALOGUE_DAG_ID,triggerDag.setformatteddate());
            assertEquals(restResponse.getStatus(),"SUCCESS");
            dagstatus = triggerDag.triggerAndWait(CATALOGUE_DAG_ID, DAG_STATE_API, 5,45000);

            assertEquals(dagstatus,"success");



    ResultSet mediametadata = postgresclient.select_query("select * FROM media_metadata_staging where source = 'testamulya2' ");
    while (mediametadata.next())
    {
     boolean isnormalised = mediametadata.getBoolean("is_normalized");
     BigDecimal audio_id = mediametadata.getBigDecimal ("audio_id");
     String utterance_list= mediametadata.getString("utterances_files_list");
      audio_id_testamulya2 = audio_id.toString();
     System.out.println(audio_id_testamulya2);

    //assertEquals(isnormalised,"true");
    assertEquals(isnormalised,true);
    assertTrue(isnormalised);

    }

    ResultSet media = postgresclient.select_query("select count(*) FROM media where source= 'testamulya2' ");
    while (media.next())
    {
       int numberofrows = media.getInt(1);
        assertEquals(numberofrows,1);
    }

        ResultSet media_speaker_mapping_count = postgresclient.select_query("select count(*) FROM media_speaker_mapping where audio_id in (select audio_id FROM media_metadata_staging where source = 'testamulya2')");
        while (media_speaker_mapping_count.next())
        {
            int numberofrows = media_speaker_mapping_count.getInt(1);
            assertEquals(numberofrows,13);
        }


        ResultSet media_speaker_mapping_statuscount = postgresclient.select_query("select count(*) FROM media_speaker_mapping where audio_id ='" +audio_id_testamulya2+ "' ");
        while (media_speaker_mapping_statuscount.next())
        {
            int numberofrows = media_speaker_mapping_statuscount.getInt(1);
            assertEquals(numberofrows,13);
        }

        System.out.println(Constants.RAW_CATALOGUED_PATH+audio_id_testamulya2+"/clean/");

        int bucketsize =  gcpConnection.bucketSize(Constants.RAW_CATALOGUED_PATH+audio_id_testamulya2+"/clean/");
        assertEquals(bucketsize,13);

    }



    @Test (enabled = true , priority = 1)
    public void validate_uniquekey_constraint() throws IOException, InterruptedException, SQLException {
        String dagstatus;
        testdataprep();
        restResponse = triggerDag.triggerDag(TRIGGER_API, CATALOGUE_DAG_ID,triggerDag.setformatteddate());
        assertEquals(restResponse.getStatus(),"SUCCESS");
        dagstatus = triggerDag.triggerAndWait(CATALOGUE_DAG_ID, DAG_STATE_API, 5,45000);

        assertEquals(dagstatus,"failed");

    }



    @Test (enabled = true , priority = 2)
    public void validate_Data_Marker_pipeline() throws InterruptedException, SQLException {
        String dagstatus;

      //{
        //  "testamulya2": {
        //    "filter": {
        //      "by_snr": {
        //        "lte": 45,
        //        "gte": 24
        //      },
        //      "with_randomness": "true"
        //    }
        //    }
        //}

        restResponse = triggerDag.triggerDag(TRIGGER_API, DATA_MARKER_DAG_ID,triggerDag.setformatteddate());
        assertEquals(restResponse.getStatus(),"SUCCESS");
        dagstatus = triggerDag.triggerAndWait(DATA_MARKER_DAG_ID, DAG_STATE_API, 3,45000);

        assertEquals(dagstatus,"success");
        //deletrecords();


        ResultSet media_speaker_mapping_count = postgresclient.select_query("select count(*) FROM media_speaker_mapping where audio_id in (select audio_id FROM media_metadata_staging where source = 'testamulya2') AND staged_for_transcription ='TRUE'");
        while (media_speaker_mapping_count.next())
        {
            int numberofrows = media_speaker_mapping_count.getInt(1);
            assertEquals(numberofrows,9);
        }

        ResultSet mediametadata = postgresclient.select_query("select * FROM media_metadata_staging where source = 'testamulya2' ");
        while (mediametadata.next())
        {
            BigDecimal audio_id = mediametadata.getBigDecimal ("audio_id");
            audio_id_testamulya2 = audio_id.toString();
        }
        int bucketsize =  gcpConnection.bucketSize(Constants.RAW_LANDING_PATH+audio_id_testamulya2+"/clean/");
        assertEquals(bucketsize,9);

    }


    @Test(enabled = true,priority = 3)
    public void validate_Pre_Transcription_Report() throws InterruptedException, IOException, URISyntaxException {
        String dagstatus;
        triggerDag.setAirflowVariable(VARIABLE_API,"set","validation_report_source_pre-transcription ","[\"testamulya2\"]");
        int before_reportgeneration_count = gcpConnection.bucketSize(Constants.PRE_REPORT_PATH);
        System.out.println("----------------------------------------");
        int before_csvreport_count = gcpConnection.bucketSize(Constants.PRE_REPORT__CSV_PATH);
        restResponse = triggerDag.triggerDag(TRIGGER_API, REPORT_PRE_DAG_ID,triggerDag.setformatteddate());
        assertEquals(restResponse.getStatus(),"SUCCESS");
        dagstatus = triggerDag.triggerAndWait(REPORT_PRE_DAG_ID, DAG_STATE_API, 3,20000);
        assertEquals(dagstatus,"success");
        int after_reportgeneration_count = gcpConnection.bucketSize(Constants.PRE_REPORT_PATH);
        int after_csvreport_count = gcpConnection.bucketSize(Constants.PRE_REPORT__CSV_PATH);

        assertEquals(after_reportgeneration_count,before_reportgeneration_count+1);
        assertEquals(after_csvreport_count,before_csvreport_count+1);


    }



    @Test(enabled = true,priority = 4)
    public void validate_Pre_Transcription_Report_InvalidSource() throws InterruptedException, IOException, URISyntaxException {
    String dagstatus;
    triggerDag.setAirflowVariable(VARIABLE_API,"set","validation_report_source_pre-transcription ","[\"test\"]");

    restResponse = triggerDag.triggerDag(TRIGGER_API, REPORT_PRE_DAG_ID,triggerDag.setformatteddate());
    assertEquals(restResponse.getStatus(),"SUCCESS");
    dagstatus = triggerDag.triggerAndWait(REPORT_PRE_DAG_ID, DAG_STATE_API, 2,15000);
    assertEquals(dagstatus,"failed");

    }


    @Test(enabled = true,priority = 6)
    public void validate_Post_Transcription_Report() throws InterruptedException, IOException, URISyntaxException {
        String dagstatus;
        triggerDag.setAirflowVariable(VARIABLE_API,"set","validation_report_source_post-transcription ","[\"testamulya2\"]");
        int before_reportgeneration_count = gcpConnection.bucketSize(Constants.POST_REPORT_PATH);
        System.out.println("----------------------------------------");
        int before_csvreport_count = gcpConnection.bucketSize(Constants.POST_REPORT__CSV_PATH);
        restResponse = triggerDag.triggerDag(TRIGGER_API, REPORT_POST_DAG_ID,triggerDag.setformatteddate());
        assertEquals(restResponse.getStatus(),"SUCCESS");
        dagstatus = triggerDag.triggerAndWait(REPORT_POST_DAG_ID, DAG_STATE_API, 3,20000);
        assertEquals(dagstatus,"success");
        int after_reportgeneration_count = gcpConnection.bucketSize(Constants.POST_REPORT_PATH);
        int after_csvreport_count = gcpConnection.bucketSize(Constants.POST_REPORT__CSV_PATH);

        assertEquals(after_reportgeneration_count,before_reportgeneration_count+1);
        assertEquals(after_csvreport_count,before_csvreport_count+1);

    }



    @Test(enabled = true,priority = 7)
    public void validate_Post_Transcription_Report_InvalidSource() throws InterruptedException, IOException, URISyntaxException {
        String dagstatus;
        triggerDag.setAirflowVariable(VARIABLE_API,"set","validation_report_source_post-transcription ","[\"test\"]");

        restResponse = triggerDag.triggerDag(TRIGGER_API, REPORT_POST_DAG_ID,triggerDag.setformatteddate());
        assertEquals(restResponse.getStatus(),"SUCCESS");
        dagstatus = triggerDag.triggerAndWait(REPORT_POST_DAG_ID, DAG_STATE_API, 2,15000);
        assertEquals(dagstatus,"failed");

    }

    @Test(enabled = true,priority = 8)
    public void validate_Post_Transcription_Report_wihoutDB_DATA() throws InterruptedException, IOException, URISyntaxException, SQLException {
        String dagstatus;
        deletrecords();
        triggerDag.setAirflowVariable(VARIABLE_API,"set","validation_report_source_post-transcription ","[\"testamulya2\"]");
//        int before_reportgeneration_count = gcpConnection.bucketSize(Constants.POST_REPORT_PATH);
//        System.out.println("----------------------------------------");
//        int before_csvreport_count = gcpConnection.bucketSize(Constants.POST_REPORT__CSV_PATH);
        restResponse = triggerDag.triggerDag(TRIGGER_API, REPORT_POST_DAG_ID,triggerDag.setformatteddate());
        assertEquals(restResponse.getStatus(),"SUCCESS");
        dagstatus = triggerDag.triggerAndWait(REPORT_POST_DAG_ID, DAG_STATE_API, 3,20000);
        assertEquals(dagstatus,"failed");
//        int after_reportgeneration_count = gcpConnection.bucketSize(Constants.POST_REPORT_PATH);
//        int after_csvreport_count = gcpConnection.bucketSize(Constants.POST_REPORT__CSV_PATH);
//
//        assertEquals(after_reportgeneration_count,before_reportgeneration_count+1);
//        assertEquals(after_csvreport_count,before_csvreport_count+1);

    }

    @Test(enabled  =true,priority =5)
    public void validate_stt() throws InterruptedException, SQLException {
        String dagstatus;
        restResponse = triggerDag.triggerDag(TRIGGER_API, STT_DAG,triggerDag.setformatteddate());
        assertEquals(restResponse.getStatus(),"SUCCESS");
        dagstatus = triggerDag.triggerAndWait(STT_DAG, DAG_STATE_API, 3,45000);

        assertEquals(dagstatus,"success");


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

        System.out.println(Constants.RAW_CATALOGUED_PATH+audio_id_testamulya2+"/clean/");

        int rejectedfilecount =  gcpConnection.bucketSize(Constants.STT_PATH+audio_id_testamulya2+"/rejected/");
        assertEquals(rejectedfilecount,3);

        int cleanfilecount =  gcpConnection.bucketSize(Constants.STT_PATH+audio_id_testamulya2+"/clean/");
        assertEquals(cleanfilecount,13);


    }


}

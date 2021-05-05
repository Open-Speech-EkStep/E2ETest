import Constants.Constants;
import cloudCommunication.Upload_Delete_Object;
import databaseConnection.Postgresclient;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

public class Commons implements Constants {


    Postgresclient postgresclient = Postgresclient.getPostgresClient();
    Upload_Delete_Object upload_Delete_Object = new Upload_Delete_Object();
    TriggerDag triggerDag = new TriggerDag();


    public void uploadAirflowVariables() throws IOException, URISyntaxException {
        triggerDag.setAirflowVariable(VARIABLE_API,"set","data_filter_config","{\n" +
                "  \"test_source\": {\n" +
                "    \"language\": \"hindi\",\n" +
                "    \"filter\": {\n" +
                "      \"by_snr\": {\n" +
                "        \"lte\": 45,\n" +
                "        \"gte\": 24    \n" +
                "  },\n" +
                "      \"with_randomness\": \"true\"\n" +
                "    }\n" +
                "  }\n" +
                "}");

        triggerDag.setAirflowVariable(VARIABLE_API,"set","language","hindi");
        triggerDag.setAirflowVariable(VARIABLE_API,"set","bucket","ekstepspeechrecognition-test");
        triggerDag.setAirflowVariable(VARIABLE_API,"set","validation_report_source_pre-transcription","{\"test_source\": { \"language\":\"hindi\"} } ");
        triggerDag.setAirflowVariable(VARIABLE_API,"set","validation_report_source_post-transcription","{\"test_source\": { \"language\":\"hindi\"} } ");
        triggerDag.setAirflowVariable(VARIABLE_API,"set","audiofields","{\"test_source\": []}");
        triggerDag.setAirflowVariable(VARIABLE_API,"set","audiofilelist","{\"test_source\": []}");
        triggerDag.setAirflowVariable(VARIABLE_API,"set","audioidsforstt","{\"test_source\": []}");
        triggerDag.setAirflowVariable(VARIABLE_API,"set","snrcatalogue","{ \"test_source\": { \"count\": 1,\"language\": \"hindi\", \"format\": \"mp3\" } } ");
        triggerDag.setAirflowVariable(VARIABLE_API,"set","sourceinfo","{\n" +
                "  \"test_source\": {\n" +
                "    \"count\": 1,\n" +
                "    \"language\":\"hindi\",\n" +
                "    \"stt\":\"google\"\n" +
                "  }\n" +
                "}");

    }

    public void deletrecords() throws SQLException {
        postgresclient.delete_data("Delete FROM media_speaker_mapping where audio_id in (select audio_id FROM media_metadata_staging where source in ('test_source'))");
        postgresclient.delete_data("Delete FROM media_metadata_staging where source in ('test_source')");
        postgresclient.delete_data("Delete FROM media where source in ('test_source')");
    }

    public void uploadTestFiles() throws IOException {
        upload_Delete_Object.uploadObject(PROJECT_ID,BUCKET_NAME,CSVOBJECT_PATH,CSV_PATH);
        upload_Delete_Object.uploadObject(PROJECT_ID,BUCKET_NAME,AUDIOOBJECT_PATH,AUDIOFILE_PATH);
    }

    public void removeFilesfromDulicateFolder() throws IOException
    {
        upload_Delete_Object.deleteObject(Constants.PROJECT_ID,Constants.BUCKET_NAME,Constants.SNR_DONE_PATH+"testfile.mp3");
        upload_Delete_Object.deleteObject(Constants.PROJECT_ID,Constants.BUCKET_NAME,Constants.SNR_DONE_PATH+"testfile.csv");
        upload_Delete_Object.deleteObject(Constants.PROJECT_ID,Constants.BUCKET_NAME,Constants.DUPLICATE_FILE_PATH+"testfile.mp3");
        upload_Delete_Object.deleteObject(Constants.PROJECT_ID,Constants.BUCKET_NAME,Constants.DUPLICATE_FILE_PATH+"testfile.csv");
    }

        public void removeFilesfromLanding() throws IOException
    {
        upload_Delete_Object.deleteObject(Constants.PROJECT_ID,Constants.BUCKET_NAME,Constants.LANDING_PATH);
    }

}
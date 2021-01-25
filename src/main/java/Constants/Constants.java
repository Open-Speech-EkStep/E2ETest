package Constants;

public interface Constants{

    String  COMPOSER_ENDPOINT= "https://jcb87ab9c69cf3b4dp-tp.appspot.com/admin/rest_api/api";
    String CLIENT_ID = "498707616447-2lc3ehrpui3spn0shu6q66svqvapvrbq.apps.googleusercontent.com";
    String IAM_SCOPE = "https://www.googleapis.com/auth/iam";
    String PROJECT_ID="ekstepspeechrecognition";
    String  BUCKET_NAME="ekstepspeechrecognition-test";
    String  CSV_PATH="./src/main/resources/testfile.csv";
    String  AUDIOFILE_PATH="./src/main/resources/testfile.mp3";
    String CATALOGUE_DAG_ID="test_source";
    String STT_DAG="test_source_stt";
    String  DATA_MARKER_DAG_ID="data_marker_pipeline";
    String REPORT_PRE_DAG_ID="report_generation_pipeline_pre-transcription";
    String REPORT_POST_DAG_ID="report_generation_pipeline_post-transcription";
    String TRIGGER_API="trigger_dag";
    String DAG_STATE_API="dag_state";
    String VARIABLE_API="variables";
    String CSVOBJECT_PATH="data/audiotospeech/raw/download/downloaded/hindi/audio/test_source/testfile.csv";
    String AUDIOOBJECT_PATH="data/audiotospeech/raw/download/downloaded/hindi/audio/test_source/testfile.mp3";
    String JOB_FAILED_STATUS="failed";
    String JOB_SUCCESS_STATUS="success";
    String RAW_CATALOGUED_PATH ="data/audiotospeech/raw/download/catalogued/hindi/audio/test_source/";
    String SNR_DONE_PATH ="data/audiotospeech/raw/download/snrdonepath/hindi/audio/test_source/";
    String DUPLICATE_FILE_PATH ="data/audiotospeech/raw/download/duplicate/test_source/";
    String RAW_LANDING_PATH ="data/audiotospeech/raw/landing/hindi/audio/test_source/";
    String PRE_REPORT_PATH="data/audiotospeech/raw/download/catalogued/hindi/reports/";
    String PRE_REPORT__CSV_PATH="data/audiotospeech/raw/download/catalogued/hindi/reports/Final_csv_reports/";
    String POST_REPORT__CSV_PATH="data/audiotospeech/integration/processed/hindi/reports/Final_csv_reports/";
    String STT_PATH = "data/audiotospeech/integration/processed/hindi/audio/test_source/";
}
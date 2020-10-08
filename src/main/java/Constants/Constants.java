package Constants;

public interface Constants{

    String  COMPOSER_ENDPOINT= "https://jf48cb859cc84047dp-tp.appspot.com/admin/rest_api/api";
    String CLIENT_ID = "304385116606-cpqn3bf1ihte6ccaoqdhros84ogpmv65.apps.googleusercontent.com";
    String IAM_SCOPE = "https://www.googleapis.com/auth/iam";
    String PROJECT_ID="ekstepspeechrecognition";
    String  BUCKET_NAME="ekstepspeechrecognition-test";
    String  CSV_PATH="./src/main/resources/testamulya2.csv";
    String  AUDIOFILE_PATH="./src/main/resources/testamulya2.mp3";
    String  CATALOGUE_DAG_ID="testamulya2";
    String STT_DAG="testamulya2_stt";
    String  DATA_MARKER_DAG_ID="data_marker_pipeline";
    String REPORT_PRE_DAG_ID="report_generation_pipeline_pre-transcription";
    String REPORT_POST_DAG_ID="report_generation_pipeline_post-transcription";
    String TRIGGER_API="trigger_dag";
    String DAG_STATE_API="dag_state";
    String VARIABLE_API="variables";
    String CSVOBJECT_PATH="data/audiotospeech/raw/download/downloaded/hindi/audio/testamulya2/testamulya2.csv";
    String AUDIOOBJECT_PATH="data/audiotospeech/raw/download/downloaded/hindi/audio/testamulya2/testamulya2.mp3";
    String JOB_FAILED_STATUS="failed";
    String JOB_SUCCESS_STATUS="success";
    String RAW_CATALOGUED_PATH ="data/audiotospeech/raw/download/catalogued/hindi/audio/testamulya2/";
    String RAW_LANDING_PATH ="data/audiotospeech/raw/landing/hindi/audio/testamulya2/";
    String PRE_REPORT_PATH="data/audiotospeech/raw/download/catalogued/hindi/reports/";
    String PRE_REPORT__CSV_PATH="data/audiotospeech/raw/download/catalogued/hindi/reports/Final_csv_reports/";
    String POST_REPORT_PATH="data/audiotospeech/raw/download/catalogued/hindi/reports/";
    String POST_REPORT__CSV_PATH="data/audiotospeech/raw/download/catalogued/hindi/reports/Final_csv_reports/";
    String STT_PATH = "data/audiotospeech/integration/processed/hindi/audio/testamulya2/";
}
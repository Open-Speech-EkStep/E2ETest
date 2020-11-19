Feature: This feature helps us to generate a report before STT is done.We will validate that the report is generated in the correct folder in GCP bucket
  Scenario: Test Valid Scenario and validate 2 reports are getting generated (csv,xlsx)
    When i trigger the report generation dag
    Then The "report_generation_pipeline_pre-transcription" Dag should run successfully
    And reports should be generated in Reports folder in Google Bucket


  Scenario: Test Invalid Scenario and validate that dags failed when the source name does not exists
    When Airflow variable is uploaded with wrong source name
    And I trigger the Pre Transcription Report dag
    Then The "report_generation_pipeline_pre-transcription" Dag should fail
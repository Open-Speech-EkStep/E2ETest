Feature: Pre Transcription Report Generation
  Scenario: Test positive scenario and validate CSV Report is getting generated for Pre Transcription Report
    When i Trigger the Report Generation Dag
    Then The "report_generation_pipeline_post-transcription" Dag should run successfully
    And Report should be generated in Reports folder in Google Bucket


  Scenario: Test Invalid Scenario and validate that dag fails when the source name does not exist
    Given Airflow variable is uploaded with wrong source name for Post Transcription Report
    When I trigger the Post Transcription Report dag
    Then The "report_generation_pipeline_post-transcription" Dag should fail


  Scenario: Test Invalid Scenario and validate that dag fails when there is no data in the DB
    Given Data is deleted from the DB
    And Correct value of Airflow variable is uploaded
    When I trigger the Post Transcription Report dag
    Then The "report_generation_pipeline_post-transcription" Dag should fail
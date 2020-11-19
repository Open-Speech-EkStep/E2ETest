Feature: Test Data Marker Pipeline
  Scenario: Test the Data Marker Pipeline. This pipeline marks the data for STT as per the configuration and move the files to a particular location in Google Bucket
    When I trigger the dag
    Then The "data_marker_pipeline" Dag should run successfully
    And media meta data staging table should be updated with staged_for_transcription = TRUE
    And Files should be moved to Landing Folder in Google Bucket
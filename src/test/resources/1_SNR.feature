Feature: Test SNR Utility. We will run the SNR Dag and verify that mp3 file is broken into chunks and moved to correct directory in Google bucket and Correct table in the test DB
  Scenario: Test SNR Utility Happy path using testfile in resources and using the testdag in airflow
    Given Airflow variables are uploaded
    And Files are removed from Duplicate,SNR Done Folder in GCP Bucket
    And Test files are uploaded to the Google bucket
    When I trigger test SNR DAG
    Then The "testamulya2" Dag should run successfully
    And DB tables should be updated successfully
    And Correct number of files should be present in the Google bucket


  Scenario: Test SNR utility for the already processed file
    Given Test files are uploaded to the Google bucket
    When I trigger test DAG again for the same file
    Then The "testamulya2" Dag should run successfully
    And Files should be moved to Duplicate folder in Google Bucket
Feature: Test STT
  Scenario: Trigger STT pipeline ( Google's STT) and validate that transcription is done and DB is updated
    Given Files are removed from Landing Folder
    When I trigger the STT Dag
    Then The "test_source_stt" Dag should run successfully
    And Database tables should be updated with correct data
    And File should be uploaded to clean and rejected folder in STT path
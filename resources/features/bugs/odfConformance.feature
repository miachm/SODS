Feature: ODF specification conformance for ZIP structure

  The ODF specification requires that the "mimetype" file must be:
  1. The first entry in the ZIP archive
  2. Stored uncompressed (STORED method, not DEFLATED)

  Scenario: The mimetype entry is the first file in the ODS archive
    Given a sheet "A", size 3x3 and random data
    Given an empty Spreadsheet
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    Then the first entry in the ZIP archive is "mimetype"

  Scenario: The mimetype entry is stored uncompressed
    Given a sheet "A", size 3x3 and random data
    Given an empty Spreadsheet
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    Then the "mimetype" entry in the ZIP archive uses STORED compression method

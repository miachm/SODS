Feature: Get num sheets

  Scenario: Get num sheets from an empty Spreadhseet
    Given an empty Spreadsheet
    Then the number of sheets in the spreadsheet is 0

  Scenario: Get num sheets with operations
    Given a SpreadSheet with 100 random sheets
    Then the number of sheets in the spreadsheet is 100
    When the client deletes a sheet in the index 2
    Then the number of sheets in the spreadsheet is 99
    When the client appends an empty sheet with the name "whatevername"
    Then the number of sheets in the spreadsheet is 100

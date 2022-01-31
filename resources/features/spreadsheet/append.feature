Feature: Append sheet
  Try to append a sheet to a Spreadsheet

  Scenario: Append a sheet to an empty Spreadsheet
    Given an empty Spreadsheet
    When the client appends an empty sheet with the name "A"
    Then the number of sheets in the spreadsheet is 1
    And the name of the sheet number 0 is "A"

  Scenario: Append a sheet to a non-empty spreadsheet
    Given a SpreadSheet with 3 random sheets
    When the client appends an empty sheet with the name "test-cucumber"
    Then the number of sheets in the spreadsheet is 4
    And the name of the sheet number 3 is "test-cucumber"

  Scenario: Append a sheet and save it
    Given an empty Spreadsheet
    When the client appends an empty sheet with the name "A"
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    Then the number of sheets in the spreadsheet is 1
    And the name of the sheet number 0 is "A"

  Scenario: Append a null-pointer sheet
    Given an empty Spreadsheet
    When the client appends a null sheet and catch the exception
    Then the last exception is "NullPointerException"

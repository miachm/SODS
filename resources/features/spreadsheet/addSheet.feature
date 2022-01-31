Feature: Add a sheet
  Try to add a sheet to a Spreadsheet

  Scenario: Add a sheet to an empty Spreadsheet
    Given an empty Spreadsheet
    When the client add an empty sheet in the index 0 with the name "A"
    Then the number of sheets in the spreadsheet is 1
    And the name of the sheet number 0 is "A"

  Scenario: Add a sheet to a non-empty spreadsheet
    Given a SpreadSheet with 3 random sheets
    When the client add an empty sheet in the index 1 with the name "test-cucumber"
    Then the number of sheets in the spreadsheet is 4
    And the name of the sheet number 1 is "test-cucumber"

  Scenario: Add a sheet and save it
    Given an empty Spreadsheet
    When the client add an empty sheet in the index 0 with the name "A"
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    Then the number of sheets in the spreadsheet is 1
    And the name of the sheet number 0 is "A"

  Scenario: Add a null-pointer sheet
    Given an empty Spreadsheet
    When the client adds a null sheet in the index 0 and catch the exception
    Then the last exception is "NullPointerException"

  Scenario: Add a sheet with a negative index
    Given a SpreadSheet with 3 random sheets
    When the client add an empty sheet in the invalid index -1 with the name "A" and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

  Scenario: Add a sheet with a index out of bounds
    Given a SpreadSheet with 3 random sheets
    When the client add an empty sheet in the invalid index 4 with the name "A" and catch the exception
    Then the last exception is "IndexOutOfBoundsException"
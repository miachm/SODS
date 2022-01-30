Feature: Delete a sheet by pos

  Background:
    Given an empty Spreadsheet
    When the client appends an empty sheet with the name "A"
    When the client appends an empty sheet with the name "B"
    When the client appends an empty sheet with the name "C"

  Scenario: Delete a sheet
    When the client deletes a sheet in the index 1
    Then the number of sheets in the spreadsheet is 2
    And the name of the sheet number 1 is "C"

  Scenario: Delete a sheet from the last position
    When the client deletes a sheet in the index 2
    Then the number of sheets in the spreadsheet is 2
    And the name of the sheet number 1 is "B"

  Scenario: Delete a sheet from an empty Spreadsheet
    Given an empty Spreadsheet
    When the client deletes a sheet in the index 0 and catch the exception
    Then the exception is a IndexOutOfBoundsException

  Scenario: Delete a sheet and save it
    When the client deletes a sheet in the index 1
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    Then the number of sheets in the spreadsheet is 2
    And the name of the sheet number 1 is "C"

  Scenario: Delete a sheet with a negative index
    When the client deletes a sheet in the index -1 and catch the exception
    Then the exception is a IndexOutOfBoundsException

  Scenario: Delete a sheet with an index out of bounds
    When the client deletes a sheet in the index 3 and catch the exception
    Then the exception is a IndexOutOfBoundsException
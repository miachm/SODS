Feature: Delete a sheet by name

  Background:
    Given an empty Spreadsheet
    When the client appends an empty sheet with the name "A"
    When the client appends an empty sheet with the name "B"
    When the client appends an empty sheet with the name "C"

  Scenario Outline: Delete a sheet
    When the client deletes a sheet with the name "<name>"
    Then the number of sheets in the spreadsheet is 2
    And the name of the sheet number 1 is "<result>"

    Examples:
      | name | result |
      | A    | C      |
      | B    | C      |
      | C    | B      |

  Scenario: Delete a sheet from an empty Spreadsheet
    Given an empty Spreadsheet
    When the client deletes a sheet with the name "A"

  Scenario: Delete a sheet and save it
    When the client deletes a sheet with the name "B"
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    Then the number of sheets in the spreadsheet is 2
    And the name of the sheet number 1 is "C"

  Scenario: Delete a non-existent sheet
    When the client deletes a sheet with the name "garbagename"
    Then the number of sheets in the spreadsheet is 3

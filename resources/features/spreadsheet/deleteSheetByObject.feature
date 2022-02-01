Feature: Delete a sheet by object

  Background:
    Given an empty Spreadsheet
    When the client appends an empty sheet with the name "A"
    When the client appends an empty sheet with the name "B"
    When the client appends an empty sheet with the name "C"

  Scenario Outline: Delete a sheet
    Given creating a sheet with the name "<name>" into World.sheet
    When the client deletes a sheet which corresponds with the object World.sheet
    Then the number of sheets in the spreadsheet is 2
    And the name of the sheet number 1 is "<result>"

    Examples:
      | name | result |
      | A    | C      |
      | B    | C      |
      | C    | B      |

  Scenario: Delete a sheet from an empty Spreadsheet
    Given an empty Spreadsheet
    Given creating a sheet with the name "A" into World.sheet
    When the client deletes a sheet which corresponds with the object World.sheet

  Scenario: Delete a sheet and save it
    Given creating a sheet with the name "B" into World.sheet
    When the client deletes a sheet which corresponds with the object World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    Then the number of sheets in the spreadsheet is 2
    And the name of the sheet number 1 is "C"

  Scenario: Delete a non-existent sheet
    Given creating a sheet with the name "garbagename" into World.sheet
    When the client deletes a sheet which corresponds with the object World.sheet
    When the client deletes a sheet with the name "garbagename"
    Then the number of sheets in the spreadsheet is 3

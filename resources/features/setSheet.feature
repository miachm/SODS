Feature: Set a sheet by pos

  Background:
    Given an empty Spreadsheet
    When the client appends an empty sheet with the name "A"
    When the client appends an empty sheet with the name "B"
    When the client appends an empty sheet with the name "C"

  Scenario Outline: Set a sheet
    Given creating a sheet with the name "uniquename" into this.sheet
    When the client set a sheet in the index <index> with the content from this.sheet
    Then the number of sheets in the spreadsheet is 3
    And the name of the sheet number <index> is "uniquename"
    And the name of the sheet number <control_index> is "<control_value>"
    Examples:
      | index | control_index | control_value |
      | 0     | 1             | B             |
      | 1     | 2             | C             |
      | 2     | 1             | B             |

  Scenario: Set a sheet from an empty Spreadsheet
    Given an empty Spreadsheet
    When the client set a sheet in the index 0 and catch the exception
    Then the exception is a IndexOutOfBoundsException

  Scenario: Set a sheet and save it
    Given creating a sheet with the name "uniquename" into this.sheet
    When the client set a sheet in the index 1 with the content from this.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    Then the number of sheets in the spreadsheet is 3
    And the name of the sheet number 1 is "uniquename"
    And the name of the sheet number 0 is "A"
    And the name of the sheet number 2 is "C"

  Scenario: Set a null sheet
    When the client set a null sheet in the index 0 and catch the exception
    Then the exception is a NullPointerException

  Scenario: Set a sheet with a negative index
    When the client set a sheet in the index -1 and catch the exception
    Then the exception is a IndexOutOfBoundsException

  Scenario: Delete a sheet with an index out of bounds
    When the client set a sheet in the index 3 and catch the exception
    Then the exception is a IndexOutOfBoundsException
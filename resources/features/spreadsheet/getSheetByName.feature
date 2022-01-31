Feature: Get a sheet by name

  Background:
    Given an empty Spreadsheet
    When the client appends an empty sheet with the name "A"
    When the client appends an empty sheet with the name "B"
    When the client appends an empty sheet with the name "C"

  Scenario Outline: Get a sheet by name
    When the client get a sheet with the name "<name>" into this.sheet
    Then the name of this.sheet is "<name>"

    Examples:
      | name |
      | A    |
      | B    |
      | C    |

  Scenario: Get a sheet by name and it does not exist
    When the client get a sheet with the name "garbage" into this.sheet
    Then this.sheet is a null pointer

  Scenario: Get a sheet from an empty Spreadsheet
    Given an empty Spreadsheet
    When the client get a sheet with the name "<name>" into this.sheet
    Then this.sheet is a null pointer


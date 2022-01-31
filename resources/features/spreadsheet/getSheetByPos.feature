Feature: Get a sheet by pos

  Background:
    Given an empty Spreadsheet
    When the client appends an empty sheet with the name "A"
    When the client appends an empty sheet with the name "B"
    When the client appends an empty sheet with the name "C"

  Scenario Outline: Get a sheet by an index
    Then the name of the sheet number <index> is "<name>"
    Examples:
      | index | name |
      | 0     | A    |
      | 1     | B    |
      | 2     | C    |

  Scenario: Get a sheet by an index in an empty Spreadsheet
    Given an empty Spreadsheet
    When the client get a sheet in the index 0 and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

  Scenario Outline: Get a sheet with an invalid index
    When the client get a sheet in the index <index> and catch the exception
    Then the last exception is "IndexOutOfBoundsException"
    Examples:
      | index |
      | -1    |
      | 3     |
      | 4     |

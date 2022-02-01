Feature: Get sheets

  Scenario: Get all sheets from an empty Spreadhseet
    Given an empty Spreadsheet
    When the client gets all the sheets into World.list_sheets
    Then the size of the list World.list_sheets is 0

  Scenario: Get all sheets
    Given an empty Spreadsheet
    When the client appends an empty sheet with the name "A"
    When the client appends an empty sheet with the name "B"
    When the client appends an empty sheet with the name "C"
    When the client gets all the sheets into World.list_sheets
    Then the size of the list World.list_sheets is 3
    And the sheets in the list World.list_sheets have the following names:
      | A    |
      | B    |
      | C    |

  Scenario: Get all sheets and try to add an item to the list
    Given an empty Spreadsheet
    When the client appends an empty sheet with the name "A"
    When the client gets all the sheets into World.list_sheets
    When the client adds a random sheet into World.list_sheets and catch the exception
    Then the last exception is "UnsupportedOperationException"


  Scenario: Get all sheets and try to delete a item from the list
    Given an empty Spreadsheet
    When the client appends an empty sheet with the name "A"
    When the client gets all the sheets into World.list_sheets
    When the client deletes the sheet 0 from World.list_sheets and catch the exception
    Then the last exception is "UnsupportedOperationException"
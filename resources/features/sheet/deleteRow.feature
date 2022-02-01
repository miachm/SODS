Feature: Delete a row in a sheet

  Background:
    Given a sheet "A", size 3x3 and random data

  Scenario: Delete a row from an empty Sheet
    Given an empty Sheet
    When the client deletes the row 0 and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

  Scenario: Delete a row
    When the client deletes the row 2
    Then the number of rows is 2
    And the number of columns is 3

  Scenario: Delete a row from the last position
    When the client deletes the row 2
    Then the number of rows is 2
    And the number of columns is 3

  Scenario: Delete 2 rows
    When the client deletes 2 rows on the index 1
    Then the number of rows is 1
    And the number of columns is 3

  Scenario: Delete a row and save it
    When the client deletes 2 rows on the index 1
    Given an empty Spreadsheet
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    Then the number of rows is 1
    And the number of columns is 3

  Scenario: Delete a row with a negative index
    When the client deletes the row 1 on the index -1 and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

  Scenario Outline: Delete a row with an index out of bounds
    When the client deletes the row <num_rows> on the index <index> and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

    Examples:
      | num_rows | index |
      | 1           | 3     |
      | 4           | 1     |

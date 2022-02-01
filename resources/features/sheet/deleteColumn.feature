Feature: Delete a column in a sheet

  Background:
    Given a sheet "A", size 3x3 and random data

  Scenario: Delete a column from an empty Sheet
    Given an empty Sheet
    When the client deletes the column 0 and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

  Scenario: Delete a column
    When the client deletes the column 2
    Then the number of rows is 3
    And the number of columns is 2

  Scenario: Delete a column from the last position
    When the client deletes the column 2
    Then the number of rows is 3
    And the number of columns is 2

  Scenario: Delete 2 columns
    When the client deletes 2 columns on the index 1
    Then the number of rows is 3
    And the number of columns is 1

  Scenario: Delete a column and save it
    When the client deletes 2 columns on the index 1
    Given an empty Spreadsheet
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    Then the number of rows is 3
    And the number of columns is 1

  Scenario: Delete a column with a negative index
    When the client deletes the column 1 on the index -1 and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

  Scenario Outline: Delete a column with an index out of bounds
    When the client deletes the column <num_columns> on the index <index> and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

    Examples:
      | num_columns | index |
      | 1           | 3     |
      | 4           | 1     |

Feature: Insert column to Sheet

  Background:
    Given a sheet "A", size 3x3 and the data:
      | 1 | 2 | 3 |
      | 4 | 5 | 6 |
      | 7 | 8 | 9 |

  Scenario: Insert a column-before to an empty Sheet
    Given an empty Sheet
    When the client inserts a column before the index 0
    Then the number of rows is 0
    And the number of columns is 1

  Scenario: Insert a column-after to an empty Sheet
    Given an empty Sheet
    When the client inserts a column after the index 0 and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

  Scenario: Insert a column-before to a Sheet
    When the client inserts a column before the index 2
    Then the number of rows is 3
    And the number of columns is 4
    Then get the Cell in the position 0,2 as World.range
    Then the value in range is null

  Scenario: Insert a column-after to a Sheet
    When the client inserts a column after the index 2
    Then the number of rows is 3
    And the number of columns is 4
    Then get the Cell in the position 0,3 as World.range
    Then the value in range is null

  Scenario: Insert multiple column-before to a Sheet
    When the client inserts 100 columns before the index 2
    Then the number of rows is 3
    And the number of columns is 103
    Then get the Cell in the position 0,2 as World.range
    Then the value in range is null
    Then get the Cell in the position 0,50 as World.range
    Then the value in range is null

  Scenario: Insert multiple column-after to a Sheet
    When the client inserts 100 columns after the index 2
    Then the number of rows is 3
    And the number of columns is 103
    Then get the Cell in the position 0,2 as World.range
    Then the value in range is 3
    Then get the Cell in the position 0,3 as World.range
    Then the value in range is null
    Then get the Cell in the position 0,50 as World.range
    Then the value in range is null

  Scenario: Insert negative number of column-before to a Sheet
    When the client inserts -2 columns before the index 1 and catch the exception
    Then the last exception is "IllegalArgumentException"
    And the number of columns is 3

  Scenario: Insert negative number of column-after to a Sheet
    When the client inserts -2 columns after the index 1 and catch the exception
    Then the last exception is "IllegalArgumentException"
    And the number of columns is 3

  Scenario: Insert multiple column to a Sheet save and load
    When the client inserts 10 columns before the index 2
    Given an empty Spreadsheet
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    Then the number of rows is 3
    And the number of columns is 13

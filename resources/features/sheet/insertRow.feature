Feature: Insert row to Sheet

  Background:
    Given a sheet "A", size 3x3 and the data:
      | 1 | 2 | 3 |
      | 4 | 5 | 6 |
      | 7 | 8 | 9 |

  Scenario: Insert a row-before to an empty Sheet
    Given an empty Sheet
    When the client inserts a row before the index 0
    Then the number of rows is 1
    And the number of columns is 0

  Scenario: Insert a row-after to an empty Sheet
    Given an empty Sheet
    When the client inserts a row after the index 0 and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

  Scenario: Insert a row-before to a Sheet
    When the client inserts a row before the index 2
    Then the number of rows is 4
    And the number of columns is 3
    Then get the Cell in the position 2,0 as World.range
    Then the value in range is null

  Scenario: Insert a row-after to a Sheet
    When the client inserts a row after the index 2
    Then the number of rows is 4
    And the number of columns is 3
    Then get the Cell in the position 3,0 as World.range
    Then the value in range is null

  Scenario: Insert multiple row-before to a Sheet
    When the client inserts 100 rows before the index 2
    Then the number of rows is 103
    And the number of columns is 3
    Then get the Cell in the position 2,0 as World.range
    Then the value in range is null
    Then get the Cell in the position 50,0 as World.range
    Then the value in range is null

  Scenario: Insert multiple row-after to a Sheet
    When the client inserts 100 rows after the index 2
    Then the number of rows is 103
    And the number of columns is 3
    Then get the Cell in the position 2,0 as World.range
    Then the value in range is 7
    Then get the Cell in the position 3,0 as World.range
    Then the value in range is null
    Then get the Cell in the position 50,0 as World.range
    Then the value in range is null

  Scenario: Insert negative number of row-before to a Sheet
    When the client inserts -2 rows before the index 1 and catch the exception
    Then the last exception is "IllegalArgumentException"
    And the number of columns is 3

  Scenario: Insert negative number of row-after to a Sheet
    When the client inserts -2 rows after the index 1 and catch the exception
    Then the last exception is "IllegalArgumentException"
    And the number of columns is 3

  Scenario: Insert multiple rows to a Sheet save and load
    When the client inserts 10 rows before the index 2
    Given an empty Spreadsheet
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    Then the number of rows is 13
    And the number of columns is 3

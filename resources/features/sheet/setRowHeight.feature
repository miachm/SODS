Feature: Set height of a row

  Background:
    Given a sheet "A", size 3x3 and random data

  Scenario Outline: Set a row height by an index
    When the client sets the height of the row <index> to <value>
    Then the client gets the height of the row <index> is <value>
    Examples:
      | index | value |
      | 0     | 1.2   |
      | 1     | 6.0   |
      | 2     | 4.0   |

  Scenario: Set a row height from an empty Sheet
    Given an empty Sheet
    When the client sets the height of the row 0 and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

  Scenario Outline: Set a row height with an invalid index
    When the client sets the height of the row <index> and catch the exception
    Then the last exception is "IndexOutOfBoundsException"
    Examples:
      | index |
      | -1    |
      | 3     |
      | 4     |

  Scenario: Test IO in the set row height
    When the client sets the height of the row 1 to 4.3
    Given an empty Spreadsheet
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    Then the client gets the height of the row 1 is 4.3
    Then the client gets the height of the row 2 is null

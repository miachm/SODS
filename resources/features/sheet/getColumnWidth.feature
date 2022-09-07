Feature: Get width of a column

  Background:
    Given a sheet "A", size 3x3 and random data
    When specify the column width with the next data:
      |  1.2         |
      |  6           |
      |  null        |

  Scenario Outline: Get a column width by an index
    Then the client gets the width of the column <index> is <value>
    Examples:
      | index | value |
      | 0     | 1.2   |
      | 1     | 6.0   |
      | 2     | null  |

  Scenario: Get a column width from an empty Sheet
    Given an empty Sheet
    When the client gets the width of the column 0 and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

  Scenario Outline: Get a column width with an invalid index
    When the client gets the width of the column <index> and catch the exception
    Then the last exception is "IndexOutOfBoundsException"
    Examples:
      | index |
      | -1    |
      | 3     |
      | 4     |

  Scenario: Test IO in an existing ODS file
    When load a spreadsheet from the resource "widthAndHeight"
    And get the first sheet
    Then the client gets the width of the column 0 is 120.385416666667
    Then the client gets the width of the column 1 is 99.3510416666667
    Then the client gets the width of the column 2 is 36.6447916666667

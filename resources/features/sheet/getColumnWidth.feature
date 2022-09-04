Feature: Get width of a column

  Background:
    Given a sheet "A", size 3x3 and random data
    When specify the column width with the next data:
      |  1.2         |
      |  6           |
      |  null        |

  Scenario Outline: Get a column width by an index
    When the client gets the column <index> as columnWidth
    Then the value of World.columnWidth is <value>
    Examples:
      | index | value |
      | 0     | 1.2   |
      | 1     | 6.0   |
      | 2     | null  |

  Scenario: Get a column width from an empty Sheet
    Given an empty Sheet
    When the client gets the column 0 and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

  Scenario Outline: Get a column width with an invalid index
    When the client gets the column <index> and catch the exception
    Then the last exception is "IndexOutOfBoundsException"
    Examples:
      | index |
      | -1    |
      | 3     |
      | 4     |
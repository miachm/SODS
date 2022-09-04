Feature: Get height of a row

  Background:
    Given a sheet "A", size 3x3 and random data
    When specify the row height with the next data:
      |  1.2         |
      |  6           |
      |  null        |

  Scenario Outline: Get a row height by an index
    Then the value of the row <index> is <value>
    Examples:
      | index | value |
      | 0     | 1.2   |
      | 1     | 6.0   |
      | 2     | null  |

  Scenario: Get a row height from an empty Sheet
    Given an empty Sheet
    When the client gets the row 0 and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

  Scenario Outline: Get a row height with an invalid index
    When the client gets the row <index> and catch the exception
    Then the last exception is "IndexOutOfBoundsException"
    Examples:
      | index |
      | -1    |
      | 3     |
      | 4     |
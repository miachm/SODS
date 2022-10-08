Feature: Range getCell() method

  Background:
    Given a sheet "A", size 3x3 and random data

  Scenario Outline: Get Cell within the range
    When the client creates a Range with 1,1,2,2
    When the client get the cell <row>, <column> within the Range
    Then the range is equal to the range <row_match>,<column_match>,1,1
    Examples:
      | row | column | row_match | column_match |
      | 0   | 0      | 1         | 1            |
      | 0   | 1      | 1         | 2            |
      | 1   | 0      | 2         | 1            |
      | 1   | 1      | 2         | 2            |

  Scenario Outline: GetCell providing invalid values return InvalidArgumentException
    When the client creates a Range with 1,1,2,2
    When the client get the cell <row>, <column> within the Range and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

    Examples:
      | row | column |
      | -1  | 1      |
      | 1   | -1     |
      | 2   | 1      |
      | 1   | 2      |
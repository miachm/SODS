Feature: Get the range from a Sheet

  Background:
    Given a sheet "A", size 3x3 and the data:
      | 1 | 2 | 3 |
      | 4 | 5 | 6 |
      | 7 | 8 | 9 |

  Scenario: Get the range from an empty Sheet
    Given an empty Sheet
    When get the Cell in the position 0,0 and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

  Scenario Outline: Get the Cell from a Range
    Then get the Cell in the position <row>,<column> as World.range
    Then the value in range is <value>

    Examples:
      | row | column | value |
      | 0   | 0      | 1     |
      | 0   | 1      | 2     |
      | 0   | 2      | 3     |
      | 1   | 0      | 4     |
      | 1   | 1      | 5     |
      | 1   | 2      | 6     |

   Scenario Outline: Get multiple row Range
     Then get the 2 row Cell in the position <row>,<column> as World.range
     Then the values in the range are <value>

     Examples:
       | row | column | value |
       | 0   | 0      | 1,4   |
       | 0   | 1      | 2,5   |
       | 0   | 2      | 3,6   |
       | 1   | 0      | 4,7   |
       | 1   | 1      | 5,8   |
       | 1   | 2      | 6,9   |

  Scenario Outline: Get multiple Range
    Then get the 2x2 Cell in the position <row>,<column> as World.range
    Then the values in the range are <value>

    Examples:
      | row | column | value    |
      | 0   | 0      | 1,2,4,5  |
      | 0   | 1      | 2,3,5,6  |
      | 1   | 0      | 4,5,7,8  |
      | 1   | 1      | 5,6,8,9  |

  Scenario Outline: Get invalid Ranges
    When get the Cell range 2x2 in the position <row>,<column> and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

    Examples:
      | row | column |
      | 0   | 2      |
      | 2   | 0      |
      | 1   | 2      |
      | 3   | 1      |

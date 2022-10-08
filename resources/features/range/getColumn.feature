Feature: Get the initial column of a Range

  Scenario Outline: Get initial column of the range
    Given a sheet "A", size 4x4 and random data
    When the client creates a Range with 1,<column>,1,2
    Then the column_init of the range is <column>

    Examples:
      | column |
      | 0      |
      | 1      |
      | 2      |


Feature: Build a range

  Scenario Outline: Creates a simple range
    Given a sheet "A", size 3x3 and random data
    When the client creates a Range with <row_init>,<column_init>,<numrows>,<numcolumn>
    Then the row_init of the range is <row_init>
    Then the column_init of the range is <column_init>
    Then the numrows of the range is <numrows>
    Then the numcolumns of the range is <numcolumn>

    Examples:
    | row_init | column_init | numrows | numcolumn |
    | 1        | 1           | 1       | 1         |
    | 0        | 1           | 2       | 1         |
    | 1        | 0           | 1       | 2         |
    | 1        | 1           | 2       | 2         |
    | 0        | 0           | 3       | 3         |

  Scenario Outline: Creates a range out of bounds
    Given a sheet "A", size 3x3 and random data
    When the client creates a Range with <row_init>,<column_init>,<numrows>,<numcolumn> and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

    Examples:
      | row_init | column_init | numrows | numcolumn |
      | -1       | 1           | 1       | 1         |
      | 1        | -1          | 1       | 1         |
      | 1        | 1           | -1      | 1         |
      | 1        | 1           | 1       | -1        |
      | 4        | 1           | 1       | 1         |
      | 1        | 4           | 1       | 1         |
      | 1        | 1           | 3       | 1         |
      | 1        | 1           | 1       | 3         |


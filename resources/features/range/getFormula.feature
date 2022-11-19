Feature: Get the initial formula of a Range


  Scenario Outline: Get initial formula of the range
    Given a sheet "A", size 3x3 and random data
    When the client creates a Range with 1,1,1,1
    When the client sets the formula in the range to "<formula>"
    When the client creates a Range with 1,1,1,<numcolumns>
    Then the formula of the range is "<formula>"

    Examples:
      | formula      | numcolumns |
      | =now()       | 2          |
      | =SUM(A1:A2)  | 2          |
      | =SUM(A1:A2)  | 1          |

  Scenario Outline: Get multiple formulas in the range
    Given a sheet "A", size 4x4 and random data
    When the client creates a Range with 1,1,2,2
    When the client sets the formula in the range to "<formula>"
    Then the formulas in the range are "<formulas>"

    Examples:
      | formula      | formulas                                          |
      | =now()       | =now(),=now()\n=now(),=now()                       |
      | =SUM(A1:A2)  | =SUM(A1:A2),=SUM(A1:A2)\n=SUM(A1:A2),=SUM(A1:A2),  |
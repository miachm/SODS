Feature: Bugs related to invalid cell value content in SODS

  Scenario: SODS ignore empty cells
    When load a spreadsheet from the resource "cellEmptyValues"
    When get the first sheet
    Then the cell values are:
      | content | content |         | content |
      | content | content | content |         |
      | content |         |         |         |

  Scenario: Cross formula cells ignore symbols
    Given an empty Spreadsheet
    Given a sheet "A", size 2x2 and random data
    When the client appends the sheet contained in World.sheet
    Given a sheet "B", size 2x2 and random data
    When the client creates a Range with 1,1,1,1
    When the client sets the formula in the range to "SUM(Operations!C:C)"
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And save the spreadsheet in a file test.ods
    And load a spreadsheet from memory
    And get the sheet "B" from the spreadsheet
    When the client creates a Range with 1,1,1,1
    Then the formula of the range is "SUM(Operations!C:C)"

  Scenario: Data tags returns a null value
    When load a spreadsheet from the resource "emptyCell"
    When get the first sheet
    Then the cell values are:
      | Test 1 |
      | A      |

  Scenario: Repeated rows were ignoring cell data
    When load a spreadsheet from the resource "repeatedRows"
    When get the first sheet
    Then the cell values are:
      | header1	|  header2	| header3 |
      | 1       | 2             | 3       |
      | 2       | 3             | 1       |
      | 1       | 2             | 2       |
      | 1       | 2             | 3       |
      | 1       | 2             | 3       |
      | 5       | 5             | 5       |
      | 3       | 3             | 3       |
      | 3       | 3             | 3       |
      | 3       | 3             | 3       |
      | 3       | 3             | 3       |
      | 3       | 3             | 3       |
      | 3       | 3             | 3       |
      | 2       | 2             | 2       |
      | 1       | 1             | 1       |


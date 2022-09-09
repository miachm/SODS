Feature: Clear a Range

  Scenario: Clear a Range
    Given a sheet "A", size 5x3 and random data
    When the client creates a Range with 1,0,2,2
    When the client clears the range
    Then the range does not contain any value
    Then the range does not contain any formula
    Then the range does not contain any style
    Then the range does not contain any annotation
    When the client creates a Range with 0,0,1,1
    Then the range value is not null

  Scenario: Clear a Range
    Given a sheet "A", size 2x2 and random data
    When the client creates a Range with 1,0,1,1
    When the client clears the range
    Given an empty Spreadsheet
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    When the client creates a Range with 1,0,1,1
    Then the range does not contain any value
    Then the range does not contain any formula
    Then the range does not contain any style
    Then the range does not contain any annotation
    When the client creates a Range with 0,0,1,1
    Then the range value is not null
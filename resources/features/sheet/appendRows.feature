Feature: Append a row
  Try to append a row to a Sheet

  Scenario: Append a row to an empty Sheet
    Given an empty Sheet
    When the client appends a row
    Then the number of rows is 1
    And the number of columns is 0

  Scenario: Append a row to a Sheet
    Given a sheet "A", size 3x3 and random data
    When the client appends a row
    Then the number of rows is 4
    And the number of columns is 3

  Scenario: Append multiple rows to a Sheet
    Given a sheet "A", size 3x3 and random data
    When the client appends 500 rows
    Then the number of rows is 503
    And the number of columns is 3

  Scenario: Append negative number of rows to a Sheet
    Given a sheet "A", size 3x3 and random data
    When the client appends -10 rows and catch the exception
    Then the last exception is "IllegalArgumentException"
    And the number of rows is 3

  Scenario: Append a row and save it
    When the client creates a Sheet with "A" and put into World.sheet
    When the client appends 500 rows
    Given an empty Spreadsheet
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    Then the number of rows is 501
    And the number of columns is 1

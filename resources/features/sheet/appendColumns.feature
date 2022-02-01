Feature: Append a column
  Try to append a column to a Sheet

  Scenario: Append a column to an empty Sheet
    Given an empty Sheet
    When the client appends a column
    Then the number of rows is 0
    And the number of columns is 1

  Scenario: Append a column to a Sheet
    Given a sheet "A", size 3x3 and random data
    When the client appends a column
    Then the number of rows is 3
    And the number of columns is 4

  Scenario: Append multiple columns to a Sheet
    Given a sheet "A", size 3x3 and random data
    When the client appends 500 columns
    Then the number of rows is 3
    And the number of columns is 503

  Scenario: Append negative number of columns to a Sheet
    Given a sheet "A", size 3x3 and random data
    When the client appends -10 columns and catch the exception
    Then the last exception is "IllegalArgumentException"
    And the number of columns is 3

  Scenario: Append a row to a sheet, save it and load it again
    When the client creates a Sheet with "A" and put into World.sheet
    When the client appends 500 columns
    Given an empty Spreadsheet
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    Then the number of rows is 1
    And the number of columns is 501

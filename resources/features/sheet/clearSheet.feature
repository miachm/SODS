Feature: Clear a Sheet

  Scenario: Clear a Sheet
    Given a sheet "A", size 5x3 and random data
    When the client clears the sheet
    Then the number of rows is 5
    Then the number of columns is 3
    Then the sheet does not contain any data

  Scenario: Clear an empty Sheet
    Given an empty Sheet
    When the client clears the sheet
    Then the number of rows is 0
    Then the number of columns is 0
    Then the sheet does not contain any data
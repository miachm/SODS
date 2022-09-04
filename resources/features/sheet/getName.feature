Feature: Get the name of a Sheet

  Scenario: Get the name from a Sheet
    Given a sheet "A", size 3x3 and random data
    Then the name of the sheet is "A"

  Scenario: Get the name from a Sheet saved by SODS
    Given a sheet "B", size 3x3 and random data
    Given an empty Spreadsheet
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    Then the name of the sheet is "B"

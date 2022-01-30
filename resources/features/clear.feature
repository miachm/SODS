Feature: Clear Spreadsheet sheets

  Scenario: Clear a Spreadsheet with many sheets
    Given a SpreadSheet with 3 random sheets
    When the client clears the spreadsheet
    Then the number of sheets in the spreadsheet is 0

  Scenario: Clear an empty Spreadsheet
    Given an empty Spreadsheet
    When the client clears the spreadsheet
    Then the number of sheets in the spreadsheet is 0
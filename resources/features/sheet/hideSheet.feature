Feature: Hide a sheet

  Background:
    Given a sheet "A", size 1x1 and random data

  Scenario: By default, a sheet is not hidden
    Then the World.sheet is not hidden

  Scenario: Mark a sheet as hidden
    When hide the sheet World.sheet
    Then the World.sheet is hidden

  Scenario: Mark a sheet as unhidden
    When hide the sheet World.sheet
    When show the sheet World.sheet
    Then the World.sheet is not hidden

  Scenario: Test IO in the hide/show feature
    When hide the sheet World.sheet
    Given an empty Spreadsheet
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    Then the World.sheet is hidden
    When show the sheet World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    Then the World.sheet is not hidden

  Scenario: Test IO in an existing ODS file
    When load a spreadsheet from the resource "hiddenItems"
    And get the first sheet
    Then the World.sheet is not hidden
    When get the sheet 2
    Then the World.sheet is hidden


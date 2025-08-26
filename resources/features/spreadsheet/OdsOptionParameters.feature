Feature: Loading ODS files with OdsOptionParameters
  As a user, I want to control whether styles are loaded when opening an ODS file to optimize performance.

  Background:
    Given an ODS file "testLoadOptions.ods" with styled cells

  Scenario: Load spreadsheet with load_styles true
    When I load a spreadsheet from the resource "testLoadOptions" with load_styles true
    And get the sheet "Sales" from the spreadsheet
    And the client creates a Range with 0,0,1,1
    Then the range value is not null
    And the range has bold style

  Scenario: Load spreadsheet with load_styles false
    When I load a spreadsheet from the resource "testLoadOptions" with load_styles false
    And get the sheet "Sales" from the spreadsheet
    And the client creates a Range with 0,0,1,1
    Then the range value is not null
    And the range does not contain any style

  Scenario: Load spreadsheet without specifying options (default behavior)
    When load a spreadsheet from the resource "testLoadOptions"
    And get the sheet "Sales" from the spreadsheet
    And the client creates a Range with 0,0,1,1
    Then the range value is not null
    And the range has bold style

  Scenario: Attempt to load spreadsheet with null options
    When I try to load a spreadsheet from the resource "testLoadOptions" with null options
    Then it should throw a NullPointerException

  Scenario: Load spreadsheet with specific sheet numbers that exist
    When I load a spreadsheet from the resource "testLoadOptions" with sheet numbers [0]
    Then the spreadsheet should have 1 sheets
    And the sheet at index 0 should be named "Sales"

  Scenario: Load spreadsheet with sheet number that doesn't exist
    When I load a spreadsheet from the resource "testLoadOptions" with sheet numbers [1]
    Then the spreadsheet should have 0 sheets

  Scenario: Load spreadsheet with empty sheet numbers list
    When I load a spreadsheet from the resource "testLoadOptions" with sheet numbers []
    Then the spreadsheet should have 0 sheets

  Scenario: Load spreadsheet with null sheet numbers (default behavior)
    When I load a spreadsheet from the resource "testLoadOptions" with null sheet numbers
    Then the spreadsheet should have 1 sheets
    And the sheet at index 0 should be named "Sales"
Feature: Perfomance related issues with Spreadsheet

  Scenario: SODS takes too much time reading a value-only spreadsheet
    Given a timer started
    When load a spreadsheet from the resource "Crime_Data_from_2023"
    Then the time elapsed is less than 30 seconds

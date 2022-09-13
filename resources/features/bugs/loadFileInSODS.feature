Feature: General bugs related to load files in SODS


  Scenario: Exception: Invalid number of columns created
    When load a spreadsheet from the resource "Top5Browsers"
    When load a spreadsheet from the resource "Timelog"
    When load a spreadsheet from the resource "Jay"
    # No crash so far? Then it's fine!
Feature: ISO date data style

  Scenario: A date cell is written with a zero-padded day
    Given an empty Spreadsheet
    Given a sheet "A", size 1x1
    When set the date "2026-10-05" in cell 0,0
    And the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And gets the "content.xml" entry in the spreadsheet saved in the memory
    And gets the first tag "number:day" of the xml entry
    Then the tag has the attribute "number:style" with the value "long"

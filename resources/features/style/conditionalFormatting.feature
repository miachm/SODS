Feature: Conditional formatting

  Scenario: Load a file with ConditionalFormat
    When load a spreadsheet from the resource "conditionalFormatting"
    And get the first sheet
    When get the first conditionalformat of the cell in 1,1 as World.conditionalFormat
    Then the style applied in World.conditionalFormat is not default

  Scenario: Create a ConditionalFormat, save it and load it
    Given an empty Spreadsheet
    Given a sheet "A", size 3x3 and random data
    When create a background-color style as World.style
    When create a conditionalFormat of greater-value 4 with World.style
    When apply the conditionalFormat to the cell 0,2
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    When get the first conditionalformat of the cell in 0,2 as World.otherConditionalFormat
    Then the World.conditionalFormat is equal to World.otherConditionalFormat
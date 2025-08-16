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

  Scenario: Test greater-or-equal conditional formatting
    Given an empty Spreadsheet
    Given a sheet "Test", size 2x2 and random data
    When create a background-color style as World.style
    When create a conditionalFormat of greater-or-equal-value 50 with World.style
    When apply the conditionalFormat to the cell 0,0
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    When get the first conditionalformat of the cell in 0,0 as World.otherConditionalFormat
    Then the World.conditionalFormat is equal to World.otherConditionalFormat

  Scenario: Test lower-or-equal conditional formatting
    Given an empty Spreadsheet
    Given a sheet "Test", size 2x2 and random data
    When create a background-color style as World.style
    When create a conditionalFormat of lower-or-equal-value 25 with World.style
    When apply the conditionalFormat to the cell 1,0
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    When get the first conditionalformat of the cell in 1,0 as World.otherConditionalFormat
    Then the World.conditionalFormat is equal to World.otherConditionalFormat

  Scenario: Test not-equal value conditional formatting
    Given an empty Spreadsheet
    Given a sheet "Test", size 2x2 and random data
    When create a background-color style as World.style
    When create a conditionalFormat of not-equal-value 0 with World.style
    When apply the conditionalFormat to the cell 0,1
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    When get the first conditionalformat of the cell in 0,1 as World.otherConditionalFormat
    Then the World.conditionalFormat is equal to World.otherConditionalFormat

  Scenario: Test text-contains conditional formatting
    Given an empty Spreadsheet
    Given a sheet "Test", size 2x2
    When create a background-color style as World.style
    When create a conditionalFormat of text-contains "error" with World.style
    When apply the conditionalFormat to the cell 0,0
    When set the value "error message" in cell 0,0
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    When get the first conditionalformat of the cell in 0,0 as World.otherConditionalFormat
    Then the World.conditionalFormat is equal to World.otherConditionalFormat

  Scenario: Test text-starts-with conditional formatting
    Given an empty Spreadsheet
    Given a sheet "Test", size 2x2
    When create a background-color style as World.style
    When create a conditionalFormat of text-starts-with "PREFIX" with World.style
    When apply the conditionalFormat to the cell 1,1
    When set the value "PREFIX_test" in cell 1,1
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    When get the first conditionalformat of the cell in 1,1 as World.otherConditionalFormat
    Then the World.conditionalFormat is equal to World.otherConditionalFormat

  Scenario: Test text-ends-with conditional formatting
    Given an empty Spreadsheet
    Given a sheet "Test", size 2x2
    When create a background-color style as World.style
    When create a conditionalFormat of text-ends-with "SUFFIX" with World.style
    When apply the conditionalFormat to the cell 1,0
    When set the value "test_SUFFIX" in cell 1,0
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    When get the first conditionalformat of the cell in 1,0 as World.otherConditionalFormat
    Then the World.conditionalFormat is equal to World.otherConditionalFormat

  Scenario: Test cell-is-empty conditional formatting
    Given an empty Spreadsheet
    Given a sheet "Test", size 2x2
    When create a background-color style as World.style
    When create a conditionalFormat of cell-is-empty with World.style
    When apply the conditionalFormat to the cell 0,1
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    When get the first conditionalformat of the cell in 0,1 as World.otherConditionalFormat
    Then the World.conditionalFormat is equal to World.otherConditionalFormat

  Scenario: Test cell-is-not-empty conditional formatting
    Given an empty Spreadsheet
    Given a sheet "Test", size 2x2
    When create a background-color style as World.style
    When create a conditionalFormat of cell-is-not-empty with World.style
    When apply the conditionalFormat to the cell 1,1
    When set the value "non-empty" in cell 1,1
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    When get the first conditionalformat of the cell in 1,1 as World.otherConditionalFormat
    Then the World.conditionalFormat is equal to World.otherConditionalFormat
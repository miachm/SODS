Feature: Set the password feature

  Scenario: Set the password mark the sheet as protected
    Given an empty Sheet
    Then the sheet is not protected
    When the client sets the password "cas"
    Then the sheet is protected

  Scenario: Disable the password verification
    Given an empty Sheet
    When the client sets the password "cas"
    When the client sets the password as null
    Then the sheet is not protected

  Scenario: Set an empty password throws an exception
    Given an empty Sheet
    When the clients sets an empty password and catch the exception
    Then the last exception is "IllegalArgumentException"

  Scenario: Test IO in the password feature
    Given an empty Sheet
    When the client sets the password "cas"
    Given an empty Spreadsheet
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    Then the sheet is protected
    When the client sets the password as null
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    Then the sheet is not protected

  Scenario: Test IO in an existing ODS file
    When load a spreadsheet from the resource "protected"
    And get the first sheet
    Then the sheet is protected

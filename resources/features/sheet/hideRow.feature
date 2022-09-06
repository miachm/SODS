Feature: Hide a row

  Background:
    Given a sheet "A", size 3x3 and random data

  Scenario Outline: Mark a row as hidden
    When hide the row <index>
    Then the row <index> is hidden

    Examples:
      | index |
      | 0     |
      | 1     |
      | 2     |

  Scenario Outline: Mark a row as unhidden
    When hide the row <index>
    When show the row <index>
    Then the row <index> is not hidden

    Examples:
      | index |
      | 0     |
      | 1     |
      | 2     |

  Scenario: Test IO in the hide/show feature
    When hide the row 1
    Given an empty Spreadsheet
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    Then the row 1 is hidden
    When show the row 1
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    Then the row 1 is not hidden

  Scenario: Test IO in an existing ODS file
    When load a spreadsheet from the resource "hiddenItems"
    And get the first sheet
    Then the row 0 is not hidden
    Then the row 1 is hidden
    Then the row 2 is not hidden

  Scenario Outline: Mark a row as hidden providing an invalid index
    When hide the row <index> and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

    Examples:
      | index  |
      | -1     |
      | 3      |

  Scenario Outline: Mark a row as shown providing an invalid index
    When show the row <index> and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

    Examples:
      | index  |
      | -1     |
      | 3      |


  Scenario Outline: Check a row is hidden providing an invalid index
    When check row <index> is hidden and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

    Examples:
      | index  |
      | -1     |
      | 3      |
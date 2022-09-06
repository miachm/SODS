Feature: Hide a column

  Background:
    Given a sheet "A", size 3x3 and random data

  Scenario Outline: Mark a column as hidden
    When hide the column <index>
    Then the column <index> is hidden

    Examples:
      | index |
      | 0     |
      | 1     |
      | 2     |

  Scenario Outline: Mark a column as unhidden
    When hide the column <index>
    When show the column <index>
    Then the column <index> is not hidden

    Examples:
      | index |
      | 0     |
      | 1     |
      | 2     |

  Scenario: Test IO in the hide/show feature
    When hide the column 1
    Given an empty Spreadsheet
    When the client appends the sheet contained in World.sheet
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    Then the column 1 is hidden
    When show the column 1
    And save the spreadsheet in the memory
    And load a spreadsheet from memory
    And get the first sheet
    Then the column 1 is not hidden

  Scenario: Test IO in an existing ODS file
    When load a spreadsheet from the resource "hiddenItems"
    And get the first sheet
    Then the column 0 is not hidden
    Then the column 1 is not hidden
    Then the column 2 is hidden

  Scenario Outline: Mark a column as hidden providing an invalid index
    When hide the column <index> and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

    Examples:
      | index  |
      | -1     |
      | 3      |

  Scenario Outline: Mark a column as shown providing an invalid index
    When show the column <index> and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

    Examples:
      | index  |
      | -1     |
      | 3      |


  Scenario Outline: Check a column is hidden providing an invalid index
    When check column <index> is hidden and catch the exception
    Then the last exception is "IndexOutOfBoundsException"

    Examples:
      | index  |
      | -1     |
      | 3      |
Feature: Build a sheet

  Scenario: Creates a simple sheet
    When the client creates a Sheet with "A" and put into this.sheet
    Then the object this.sheet has the name "A"
    And the object this.sheet has 1x1 dimensions

  Scenario: Creates a Sheet with bigger dimensions
    When the client creates a Sheet with "A" and size 5x3 and put into this.sheet
    Then the object this.sheet has the name "A"
    And the object this.sheet has 5x3 dimensions

  Scenario: Creates a simple sheet with null name
    When the client creates a Sheet with a null name and catch the exception
    Then the last exception is "NullPointerException"

  Scenario Outline: Creates a simple sheet with invalid dimensions
    When the client creates a Sheet with "A" and invalid size <rows>x<columns> then catch the exception
    Then the last exception is "IllegalArgumentException"

    Examples:
      | rows  | columns |
      | -1    |  2      |
      | 2     |  -1     |
      | -4    |  -3     |
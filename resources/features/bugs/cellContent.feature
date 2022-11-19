Feature: Bugs related to invalid cell value content in SODS

  Scenario: SODS ignore empty cells
    When load a spreadsheet from the resource "cellEmptyValues"
    When get the first sheet
    Then the cell values are:
      | content | content |         | content |
      | content | content | content |         |
      | content |         |         |         |
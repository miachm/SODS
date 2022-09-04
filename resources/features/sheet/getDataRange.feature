Feature: Get the full data range

  Scenario: Get the full data range from a Sheet
    Given a sheet "A", size 3x3 and random data
    When the client gets the full data range as World.range
    Then World.range has the same content as the World.Sheet

  Scenario: Get the full data range from an empty sheet
    Given an empty Sheet
    When the client gets the full data range as World.range

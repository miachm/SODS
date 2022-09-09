Feature: Range copyTo() method

  Background:
    Given a sheet "A", size 4x4 and random data

  Scenario: Copy range to another
    When the client creates a Range with 2,2,2,2
    When the client copy to the Range 0,0,2,2
    When the client creates a Range with 0,0,2,2
    Then the range is equal to the range 2,2,2,2

   Scenario: Copy range to another of different size throws an exception
     When the client creates a Range with 1,0,1,2
     When the client copy to the Range 0,0,2,2 and catch the exception
     Then the last exception is "IllegalArgumentException"

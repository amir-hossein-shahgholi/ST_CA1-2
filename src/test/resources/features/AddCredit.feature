Feature: Add credit to user account

  Scenario: Add a valid amount of credit
    Given A user with a credit balance of 100
    When the user tries to add 50 credit
    Then The new credit balance should be 150

  Scenario: Add a negative amount of credit
    Given A user with a credit balance of 100
    When the user tries to add -20 credit
    Then An InvalidCreditRange exception should be thrown

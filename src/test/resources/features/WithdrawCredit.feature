Feature: Withdraw credit from user account

  Scenario: Withdraw a valid amount of credit
    Given a user with a credit balance of 100
    When the user tries to withdraw 50
    Then the new credit balance should be 50

  Scenario: Withdraw an amount exceeding the credit balance
    Given a user with a credit balance of 30
    When the user tries to withdraw 50
    Then an InsufficientCredit exception should be thrown

  Scenario: Withdraw a negative amount
    Given a user with a credit balance of 100
    When the user tries to withdraw -20
    Then an InvalidCreditRange exception should be thrown

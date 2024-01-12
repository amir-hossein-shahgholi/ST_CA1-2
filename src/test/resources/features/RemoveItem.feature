Feature: Remove item from buy list

  Scenario: Commodity present in buy list with quantity greater than one
    Given a user with a commodity "X" in their buy list with quantity 2
    When the user tries to remove commodity "X" from their buy list
    Then the quantity of commodity "X" in the buy list should be 1

  Scenario: Commodity present in buy list with quantity one
    Given a user with a commodity "Y" in their buy list with quantity 1
    When the user tries to remove commodity "Y" from their buy list
    Then the buy list should not contain commodity "Y"

  Scenario: Commodity not present in buy list
    Given a user without commodity "Z" in their buy list
    When the user tries to remove commodity "Z" from their buy list
    Then a CommodityIsNotInBuyList exception should be thrown

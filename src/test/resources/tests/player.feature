Feature: Player

  Scenario: Build Research Station on City that already has one
    Given A city "Atlanta" with color "Red"
    And A research station on the city
    And A player on the city
    When The player attempts to build a research station on the city
    Then the player is notified that the action failed

  Scenario: Drive to a disconnected city
    Given A city "Atlanta" with color "Red"
    And a second city "Lagos" with color "Black"
    And A player on the city
    When The player attempts to drive to the second city
    Then the player is notified that the action failed

  Scenario: Direct flight to city without valid card
    Given A city "Atlanta" with color "Red"
    And a second city "Lagos" with color "Black"
    And A player on the city
    When The player attempts to take a direct flight to the second city
    Then the player is notified that the action failed

  Scenario: Charter flight to city without valid card
    Given A city "Atlanta" with color "Red"
    And a second city "Lagos" with color "Black"
    And A player on the city
    When The player attempts to charter a flight to the second city
    Then the player is notified that the action failed

  Scenario: Shuttle flight to city without research station
    Given A city "Atlanta" with color "Red"
    And a second city "Lagos" with color "Blue"
    And A player on the city
    When The player attempts to take a shuttle flight to the second city
    Then the player is notified that the action failed

  Scenario: Sharing knowledge when players are not in the same city
    Given A city "Atlanta" with color "Red"
    And A player on the city
    And a second city "Lagos" with color "Blue"
    And A player on the second city
    When the players attempt to share knowledge
    Then the player is notified that the action failed

  Scenario: Treat a disease on a city with no cubes
    Given A city "Atlanta" with color "Red"
    And A player on the city
    When the player attempts to treat a disease
    Then the player is notified that the action failed

  Scenario: Researcher shares knowledge
    Given A city "Atlanta" with color "Red"
    And a second city "Lagos" with color "Blue"
    And A Researcher on the city
    And The player has a card for the second city
    And A second player on the city
    When The first player gives the card to the second
    Then The second player has the card

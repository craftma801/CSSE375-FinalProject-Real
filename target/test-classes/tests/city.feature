Feature: City
  Scenario Outline: Treat a disease in a city
    Given A city "Atlanta" with color <city_color>
    And a <disease_color> disease level of <initial_level>
    When <disease_color> is treated
    Then the <disease_color> disease level is <new_level>

    Examples:
    |city_color|disease_color|initial_level|new_level|
    |"yellow"  |"yellow"     |2            |1        |
    |"black"   |"black"      |2            |1        |
    |"blue"    |"blue"       |2            |1        |
    |"red"     |"red"        |2            |1        |
    |"yellow"  |"yellow"     |0            |0        |
    |"black"   |"black"      |0            |0        |
    |"blue"    |"blue"       |0            |0        |
    |"red"     |"red"        |0            |0        |

    Scenario Outline: Maximum infection level
      Given A city "Atlanta" with color <disease_color>
      And a <disease_color> disease level of 3
      When an additional <disease_color> cube is added
      Then the <disease_color> disease level is 3

      Examples:
      |disease_color|
      |"yellow"     |
      |"red"        |
      |"blue"       |
      |"black"      |


  Scenario: Infect city with Quarantine Specialist
    Given A city "Atlanta" with color "Blue"
    And A quarantine specialist on the city
    When an attempt is made to infect the city
    Then the city will not be infected

  Scenario: Infect city adjacent to Quarantine Specialist
    Given A city "Atlanta" with color "Blue"
    And a second city "Chicago" with color "Blue"
    And the cities are connected
    And A quarantine specialist on the city
    When an attempt is made to infect the second city
    Then the second city will not be infected
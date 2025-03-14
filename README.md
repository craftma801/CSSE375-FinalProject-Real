# CSSE376-S2G2
S2G2 definition of done for Pandemic Board Game.

## Player setup:
- Each player has a unique role. No player control, roles stored in Set to guarantee uniqueness. 
- Each player gets two cards. BVA values to test: 2 or not 2
- Set game difficulty using either 4,5, or 6 Epidemic cards, remove any unused cards. BVA values to test: 3,4,6,7

## Board Setup:
- Randomly draw 9 city cards, level 3 infection on 3, level 2 infection on three, and level 1 infection on three. BVA to be done by Mocking Random. 
- Place those 9 drawn city cards in discard pile. Discard pile is a Collection of cards. BVA values to test: -1, 0, 48, 49 (There are 48 city cards)

## Actions:
- May do up to 4 actions each turn. BVA values to test: -1(physically impossible),0,4,5
- Drive/Ferry. BVA value to test: connected by white line or not
- Direct flight. BVA values to test: you have at least 1 city card, you have no city cards, the city you want to go to exists, the city you want to go to doesn't exist, you have the card of the city you want to go to, you do not have the card of the city you want to go to
- Charter flight. BVA values to test: you have at least 1 city card, you don't have any city cards, of the cards you posses one matches the city you are in, of the cards you posses none match the city you are in, the city you want to go to exists, the city you want to go to doesn't exist
- Shuttle flight. BVA values to test: the city you are in does/doesn't have a research station, the city you want to go to does/doesn't have a research station, the city you want to go to does/doesn't exist
- Build a research station: you have at least 1 city card, you don't have any city cards, of the cards you posses one matches the city you are in, of the cards you posses none match the city you are in, research stations left in pile, no research stations left in pile
- Treat disease. BVA values to test: city you are in does/doesn't have disease cubes, disease color has/hasn't been cured, is/isn't last cube, is/isn't last cube AND disease color has/hasn't been cured, removed proper amount of cubes
-  Share Knowledge. BVA values to test: you are/aren't in the city, the other player is/isn't in the city, the card that is being traded does/doesn't match the city, the card that is being traded is/isn't owned by one of the players, trade does/doesn't results in reciever having more than 7 cards
-  Discover a cure. BVA values to test: is/isn't at a research station, does/doesn't have 5 city cards, city cards do/don't match color, cure marker did/didn't move, disease was/wasn't eradicated

## Drawing Cards:
- Done after actions. BVA value to test: was/wasn't after actions
- Draw the top 2 cards together from the Player Deck. BVA values to test: Player deck does/doesn't have at least 2 cards, player did/didn't draw epidemic card, player's hand is/isn't greater than 7 cards
- Epidemic time! BVA values to test: Infection rate did/didn't increase, drawn card from Infection deck was/wasn't really the bottom card, drawn card's disease color has/hasn't been eradicated, city does/doesn't already have cubes of this color, there are/aren't enough disease cubes of that color to play


## Infection:
- Flip over a number equal to the infection level of infection cards. BVA values to test: level does/doesn't match cards flipped
- Infect a city. BVA values to test: city does/doesn't already have 3 cubes, disease has/hasn't been eradicated

## Outbreaks:
- Outbreak marker moves forward 1 space. BVA value to test: marker did/didn't move when it should/shouldn't
- Place 1 cube on every city connected. BVA values to test: cities are/aren't connected, cites do/don't already have 3 cubes, cities did/didn't already have an outbreak

## Turn End:
- Player on your left begins a turn. BVA value to test: player was/wasn't on the left, turn did/didn't begin/end

## Event Card:
- Event card can be played at any time. BVA values to test: player does/doesn't have event card, is/isn't in drawing phase, card is/isn't being resolved

## Game End:
-Win. BVA values to test: 4 cures were/weren't discovered, outbreak marker did/didn't reach last space, disease cubes can/can't still be placed, players can/cannot draw 2 Player cards after action
-Lose. BVA values to test: same as win

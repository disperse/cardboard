# Ludum Dare 35 Starting Point

This is a fork of sjrd's [scalajs-phaser-demo](https://github.com/sjrd/scalajs-phaser-demo) project. 

I'm going to modify it for my needs and then share it with the community before starting my project
on Friday.

## Changes

Added Phaser.Sound and load.audio to Phaser.scala, pretty easy to do.

## Notes

Assets (images, sounds, music, etc) should be copied to the /server/src/main/resources/assets directory.

The client-side code is /client/src/main/scala/pairs.client/PairsClient.scala

The server-side code is /server/src/main/scala/pairs.server/Server.scala

# THEME: Shapeshift

## Ideas
 - Thinking about the Abomination from Darkest dungeon, how he has two completely different sets of abilities:
   - A card game where you 'shift' and change decks of cards as a strategy
   - This could be multiplayer
   - Different decks counter other decks, the strategy is knowing when to switch and when to stick with your current deck
   - This is going to be a common theme, obvious
 - I want to use Wayne's (3yo) art:
   - A game where a child pretends to be a cat, dog, fish which allows him to navigate an imaginary environment
   - To cross an imaginary sea, the child could pretend to be a fish
   - Seems adventure gamey...
 - Combining the previous two ideas I could create a board game or card game with the theme of a child, through imagination, transforming into animals:
   - I'm thinking of a Forbidden Island type of map
   - The goal is to collect certain trinkets spread around the map
   - Only being able to transform to each animal once, you need to retrieve the trinkets
   - Seems puzzle gamey...
 - Ideally, it could be cooperative board game:
   - Thinking about a simplified Mage Knight
   - Cards supply resources that can be used to move around the map and succeed at various challenges
   - A magical pool tile may require swimming resources + magic resources
   - Back of tiles could show terrain to plan movement: mountain goat to cross mountains, fish to swim past sea
   - Front of tiles shows either normal terrain or treasure location
   - Dangers:
     - Creatures defend treasure locations
     - Sleepy cards instead of wound cards, too many sleepy cards and the child needs to go to bed
     - Time is the ultimate resource, need to complete your goals before the sun rises
   - Like MK, any card can be played for 1 resource of any type: Move (+ special types), Strength (Attack), Quickness (Block) 
   - Cons: Requires a lot of drawings, cards, tiles
   - Pros: Doesn't require animation
 - Literal shapes: circle -> square -> triangle with different abilities:
   - Seems like Flatland
   - Or Little Blue and Little Yellow
 - Selkie
 
## Wayne's Dream

Landslide (Mountain)
Passing requires Strength (to move the rocks)
Or mouse movement (to climb under them)
 
Shape shifting requires discarding the rest of the deck, so the trick is maximizing the use of each deck before shifting to the next form

Will have to limit it to, say, 4 animal forms

### Roles:
 - Ninja: Sneaky, can sneak by unattentive creatures
 - Superhero: Can fly, very strong
 - Pirate: Has a ship, can travel across water, reasonably strong
 - Adventurer: Lots of moves, can traverse difficult terrain easier
 

## TODO

 - Decks
 - Monsters
 - Animate water sprite
 - Ambient sounds


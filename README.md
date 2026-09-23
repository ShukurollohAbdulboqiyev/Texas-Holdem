# Texas Hold'em

A Texas Hold'em poker game built in Java.

The project is being built as an object-oriented Java application, with the goal of eventually turning it into a proper web-based poker game.

## Current Features

* 52-card deck
* Card ranks and suits
* Deck shuffling
* Drawing cards
* Multiple players
* Player hole cards
* Player chip management
* Poker table
* Pot management
* Betting
* Raising
* Tracking the highest bet
* Basic console interface
* Game logic separated from the UI

## Project Structure

```text
src/
├── Action.java
├── Card.java
├── ConsoleGame.java
├── Deck.java
├── Game.java
├── GameSetup.java
├── HandComparator.java
├── HandEvaluation.java
├── HandResult.java
├── Main.java
├── Player.java
├── Rank.java
├── Suit.java
└── Table.java
```

## Main Classes

**Player**
Stores player information like name, chips, hole cards, bets, position, and player status.

**Table**
Keeps track of the players, community cards, pot, and highest bet.

**Deck**
Creates the 52-card deck, shuffles it, and deals cards.

**Game**
Handles the main poker logic, game stages, betting rounds, and player actions.

**HandEvaluation**
Checks the player's cards and determines what poker hand they have.

**HandResult**
Stores the result of a hand evaluation, including the hand type and values used to compare hands.

**HandComparator**
Compares two evaluated poker hands to determine which one is stronger.

**Action**
Defines the possible poker actions:

```text
BET
CALL
RAISE
CHECK
FOLD
ALL_IN
```

**GameSetup**
Creates and sets up the initial game objects.

**ConsoleGame**
The temporary console interface used to interact with and test the game.

## Architecture

The main idea is to keep the poker logic separate from the interface.

```text
Console Interface
       ↓
      Game
     ↙    ↘
 Player   Table
    ↓       ↓
  Cards    Pot
```

The console interface is temporary. Later, it can be replaced with a web interface without having to rewrite the main poker logic.

## Development Status

🚧 **In development**

Currently working on:

* Complete betting system
* Call
* Check
* Fold
* All-in
* Betting rounds
* Flop, turn, and river
* Hand evaluation
* Hand comparison
* Showdown
* Winner determination
* Web interface

## Technologies

* Java
* Object-Oriented Programming
* Git
* GitHub

## Future Plans

The final goal is to turn this into a playable web-based Texas Hold'em game with:

* Web-based player interface
* Multiple players
* Complete Texas Hold'em rules
* Betting rounds
* Hand evaluation
* Game state management
* Backend API
* Persistent game/session management

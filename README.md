# Texas Hold'em

A Texas Hold'em poker game built in Java.

This project is being developed as an object-oriented Java application with the goal of eventually becoming an interactive web-based poker game.

## Current Features

- 52-card deck
- Card ranks and suits
- Deck shuffling
- Drawing cards
- Multiple players
- Player hole cards
- Player chip management
- Poker table
- Pot management
- Betting
- Raising
- Tracking the highest bet
- Basic console interface
- Game and UI logic separated for future web development

## Project Structure

```text
src/
├── Action.java
├── Card.java
├── ConsoleGame.java
├── Deck.java
├── Game.java
├── GameSetup.java
├── Main.java
├── Player.java
├── Rank.java
├── Suit.java
└── Table.java
Main Classes

Player
Stores player information such as name, chips, hole cards, and current bet.

Table
Manages players, community cards, the pot, and the highest bet.

Deck
Creates, shuffles, and deals cards.

Game
Contains the core poker game logic and processes player actions.

Action
Defines the possible poker actions:

BET
CALL
RAISE
CHECK
FOLD

GameSetup
Responsible for creating and assembling the initial game objects.

ConsoleGame
Temporary console interface used to interact with and test the game.

Architecture

The project is intentionally structured so that the poker logic is separated from the user interface.

Console Interface
       ↓
      Game
     ↙    ↘
 Player   Table
    ↓       ↓
   Cards   Pot

The current console interface is temporary.

The long-term goal is to replace it with a web interface without having to rewrite the core poker logic.

Development Status

🚧 In development

Currently working on:

Complete betting system
Call
Check
Fold
Betting rounds
Flop, turn, and river
Hand evaluation
Showdown
Winner determination
Web interface
Technologies
Java
Object-Oriented Programming
Git
GitHub
Future Plans

The final version is planned to include an interactive web-based Texas Hold'em game with:

Web-based player interface
Multiple players
Complete Texas Hold'em rules
Betting rounds
Hand evaluation
Game state management
Backend API
Persistent game/session management

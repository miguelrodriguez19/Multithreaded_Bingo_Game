# Multithreaded Bingo Game

This is a simple multithreaded Bingo game implemented in Java. The game involves a presenter who draws numbers from a drum (bombo) and several players with Bingo cards (carton). The players compete to mark all the numbers on their cards as the presenter calls them out. The game continues until a player successfully marks all the numbers on their card and shouts "Bingo!".

## Table of Contents
- [Features](#features)
- [How to Run](#how-to-run)
- [Game Rules](#game-rules)

## Features
- Simulates a Bingo game with multiple players and a presenter.
- Uses multithreading to handle players, presenter, and the drawing of numbers.
- Players' cards are randomly generated.
- The game ends when a player wins or when all numbers are drawn.

## How to Run
1. Clone this repository to your local machine using `git clone`.
2. Compile the Java source files using `javac Bingo.java`.
3. Run the game with the command `java Bingo`.

## Game Rules
- Each player is given a Bingo card with 5 random numbers between 1 and 10.
- The presenter draws numbers from 1 to 10 from the drum (bombo).
- Players mark numbers on their cards when they match the drawn number.
- The game continues until one player successfully marks all numbers and shouts "Bingo!".
- The presenter announces the winning player and the game ends.

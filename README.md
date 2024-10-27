# Java Sudoku Desktop Application

---

## Introduction

The objective of this project is to design a desktop application using Java and JavaFX that allows the user to play the game of sudoku. Developing this application involves complex algorithm design and the use of the model-view-controller (MVC) design pattern, both of which help to practice and improve Java software development skills.

---

## Rules of Sudoku

Sudoku is played on a 9x9 grid, where each tile in the grid can be filled with an integer from 1 to 9. The grid contains 9 rows, 9 columns, and 9 3x3 boxes (each box therefore contains 9 tiles as well). The only rule is that every row, column, and box must contain each value from 1 to 9. Thus, if a number already appears elsewhere in a row, column, or box, it cannot be used again. A filled board will abide by this rule and contain each number exactly 9 times.

---

## Design

This section discusses the various design elements of the project and what each of them accomplishes. The structure of the program follows object-oriented design practices in order to create a functional and readable design.

### SudokuTile Class

The `SudokuTile` class is the basic object from which the game's board is constructed. Each tile in the 9x9 grid has data and properties that are stored within its `SudokuTile` object, such as its coordinates in the grid, the value stored in the tile, whether the tile is editable, and properties indicating how the user is interacting with the tile. These properties act as values that are stored in the "model" part of MVC design which the "view" then represents visually to the user. The `SudokuTile` class also includes many accessors and mutators for retrieving and modifying tile data as necessary.

### PuzzleGenerator Class

The `PuzzleGenerator` class contains the bulk of the logic for the program. It handles initializing the state of the board, randomly filling the board with values to create a valid solution state, and then removing values from the board methodically to reach a minimum number of remaining "clues" while ensuring the puzzle has only one unique solution. The `PuzzleGenerator` uses methods in the `SudokuTile` class to keep track of the remaining valid values for each tile, also known as "candidates." In a filled board, for example, each tile has only one candidate equal to its value, since by the rules of the game each value must appear exactly once in each row, column, and box. This means that a tile whose row, column, or box already has eight of the possible nine values can only have one remaining choice. An empty board, by contrast, has tiles with nine remaining candidates each, since all values are valid when no tiles are filled.

#### Filling the Board

The generator starts by creating a grid of 81 empty `SudokuTiles` as well as two lists (sets): the first contains all the coordinates of filled tiles, while the other contains all the coordinates of unfilled tiles. The generator populates nine random unfilled coordinates with each unique value from one to nine to start filling the board. Then, the board is filled by continually selecting a random unfilled tile and attempting to fill that tile with one of its candidates.

Initially, randomly filling tiles does not invalidate the board, since there are very few values present and many possible solutions. However, eventually this strategy leads to an invalid board, where one or multiple tiles have no candidates remaining. Thus, tile filling is optimized using cross-hatch scanning, which is a technique used in solving sudoku puzzles that attempts to find any tiles whose value is already known based on the values of tiles around them. This includes "naked singles," which are tiles with only one remaining candidate, and "hidden singles," which are tiles that have a candidate that no other tile in the row, column, or box contains. Prioritizing filling these tiles over random tiles helps to avoid invalidating the board. 

Unfortunately, even with this strategy, the board is still often invalidated. Therefore, the generator implements a backtracking algorithm that allows it to back up to the last tile that was filled, removing the candidate that was tried and failed. The filled tiles are stored on a stack to support this backtracking, since stacks are a LIFO (last-in-first-out) data structure. The candidate states of the board are recorded as each tile is filled so that they can be properly restored after backtracking (This is necessary because situations that require backtracking multiple times may need to restore candidates that were removed by earlier backtracking steps).

Backtracking still does not guarantee that a board will be filled quickly. While any board can be filled in this way, some states are harder to solve than others. Since the goal of filling the board requires finding any valid solution and not one specific solution, it is better to employ a heuristic algorithm here than a perfect one. Therefore, the method for filling the grid has a set maximum number of iterations for attempting to fill the board. If that maximum number is reached, the board is reset to the initial nine values to try again. Benchmarking reveals that non-optimal board states occur infrequently, but are extremely costly in terms of the added time to solve them. Therefore, simply resetting the board in this way allows a board to be generated faster than a human can perceive, despite not being the most elegant solution.

#### Creating the Puzzle

Once the board is filled, clues must be removed from the board until only a certain amount remain in order to create a puzzle for the player to solve. The key here is that a good sudoku puzzle has only one valid solution. Therefore, clues must be removed strategically so as not to create a puzzle with multiple solutions.

The generator starts by removing diagonally opposite clues four at a time. Since the board is initially full, many clues can be removed at once without affecting the solution count. Removing clues that are diagonally opposite each other is important because clues that are nearby to one another have a much higher impact on each other than clues that are far apart. For instance, removing two whole rows from a filled board creates a board with two valid solutions after removing only 18 clues. This happens because two rows (that span the same three horizontal boxes) can be interchanged without affecting the validity of the puzzle. Diagonally opposite clues will not share their row, column, or box (unless they are in the center box). Thus, they can be removed simultaneously while minimizing the chance of increasing the solution count.

After enough clues have been removed, clues are removed two at a time, and then eventually one at a time after a certain number of iterations. Each removal is checked for validity before taking place to ensure the board retains a unique solution. The removal algorithm takes a minimum number of clues and a maximum number of iterations as parameters. Just as some boards are harder to fill than others, some boards are more difficult to remain unique while removing clues. Thus, the minimum number of clues acts as a soft goal that the algorithm attempts to reach. If it reaches the maximum number of iterations without reaching the minimum number of clues, it terminates early. This is necessary because the number of iterations required to remove a clue increases exponentially as the number of remaining clues decreases. In other words, it gets harder and harder to find a clue that can be removed while retaining a unique solution. Thus, even with this maximum iteration count in place, most puzzles end up with a similar number of remaining clues.

---

## Model-View-Controller (MVC)

The model-view-controller (MVC) design pattern is a method of organizing code for software that supports a user interface. The model contains the underlying data of the application, whether that is raw data or definitions for tasks or components of an application. The view defines all the elements that the user directly interacts with in order to use the application. The controller serves as the connection between the model and the view, taking user input from the view and communicating it to the model to instruct the program. 

In this application, the model contains the `PuzzleGenerator` object. It defines methods for all the allowed interactions with the puzzle's data. For example, the model mediates changes to the properties of `SudokuTiles` and the state of the board. 

The view is split into two views: one for the puzzle and one for the menu. Each view defines the JavaFX elements that display information from the model to the user. For example, the `PuzzleView` includes visual elements organized in various containers to create a 9x9 grid for the sudoku board that scales with the size of the display window.

The puzzle and menu each have a controller as well. The controllers establish bindings between properties defined in both the model and the view and also incorporate the functionalities of buttons, mouse presses, and keyboard inputs. For example, when a tile in the view is clicked, the controller tells the model to update the "clicked" property for that tile.

Separating the program into these three different design elements helps with both understanding the code and debugging issues. If something does not look correct when running the application, the problem can be found in the view. If numbers or other data are invalid, the problem is in the model. If clicking a button does something unintended, it is possible that the controller is incorrectly communicating between the view and the model. 

---

## Conclusion and Improvements

This project helped to develop skills in algorithm design, UI development, code maintainability and readability, and testing and troubleshooting. Understanding the MVC design pattern helps to standardize application development and keep code easy to understand and debug. It was also useful to develop a larger application such as this, as it provides perspective on the need for proper documentation in order to keep track of all the disparate elements of a large program. 

The application itself could be improved in several ways. Additional features could be added, such as hints, highlights and other visual aids while interacting with the board, and options for saving or loading puzzles. Initially, there were plans to add different difficulty levels for puzzles based on the number of clues remaining. However, upon researching and implementing the puzzle generation algorithm, it became clear that the number of clues does not directly correspond to the difficulty of a puzzle. Two puzzles with the same amount of clues may require very different strategies to solve, and some strategies are far more complicated than others.

Usually, sudoku puzzles are graded to assign them a difficulty rating. Incorporating this would require generating many puzzles ahead of time and then creating a grading algorithm using the many known solving strategies. The algorithm would identify which solving strategies are necessary and assign a grade based on that information. Then, when the user selects a difficulty level to play, the program would choose a random puzzle from the requested category.

Overall, the program succeeds as a simple sudoku game that can be played on a computer. The lessons learned from designing this application will be instrumental in designing more complicated and more functional apps in the future.

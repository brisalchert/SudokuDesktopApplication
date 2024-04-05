package Sudoku.UserInterface;

import Sudoku.GameLogic.PuzzleGenerator;

public class SudokuModel {
    private PuzzleGenerator puzzleGenerator;

    // Constructor
    public SudokuModel() {
        puzzleGenerator = new PuzzleGenerator(25);
    }

    // Accessors
    public PuzzleGenerator getPuzzleGenerator() {
        return puzzleGenerator;
    }

    public SudokuTile getLastClickedTile() {
        return SudokuTile.getLastClickedTile();
    }
}

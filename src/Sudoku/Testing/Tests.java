package Sudoku.Testing;

import Sudoku.GameLogic.PuzzleGenerator;
import Sudoku.UserInterface.SudokuTile;

import java.util.Random;

public class Tests {
    /**
     * Prepares a random relevant tile to be invalidated by the placement of candidate in tileToFill
     * @param tileToFill the tile whose value will be candidate
     * @param candidate the value to fill the tile with
     */
    public static void invalidateTile(SudokuTile tileToFill, int candidate) {
        Random generator = new Random();
        SudokuTile tileToInvalidate;

        // Randomly select to invalidate a tile in the relevant row, column, or box
        tileToInvalidate = switch (generator.nextInt(3)) {
            case 0 -> tileToFill.getRow().get(generator.nextInt(9));
            case 1 -> tileToFill.getColumn().get(generator.nextInt(9));
            case 2 -> tileToFill.getBox().get(generator.nextInt(9));
            default -> null;
        };

        // Set the candidates for the invalidated tile to the random candidate
        tileToInvalidate.setCandidates(Integer.toString(candidate));
    }
}

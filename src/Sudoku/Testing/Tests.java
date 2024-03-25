package Sudoku.Testing;

import Sudoku.GameLogic.PuzzleGenerator;
import Sudoku.UserInterface.SudokuTile;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    /**
     * Prints the current grid state to the console
     */
    public static void printGrid() {
        SudokuTile[][] tileGrid = SudokuTile.getTileGrid();
        int rowIndex = 1;

        for (List<SudokuTile> row : SudokuTile.getRows()) {
            int columnIndex = 1;
            for (SudokuTile tile : row) {
                // Print "*" if the tile's value is null
                if (tile.isEmpty()) {
                    System.out.print("* ");
                }
                else {
                    System.out.print(tile.getValue() + " ");
                }

                // Print an extra space between boxes
                if (columnIndex == 3 || columnIndex == 6) {
                    System.out.print(" ");
                }

                columnIndex++;
            }

            System.out.println();

            // Print an extra line between boxes
            if (rowIndex == 3 || rowIndex == 6) {
                System.out.println();
            }

            rowIndex++;
        }

        // Print separator between grids
        System.out.println();
        System.out.println("--------------------");
        System.out.println();
    }

    /**
     * Generates a certain number of Sudoku grids and reports back the minimum, maximum, and average runtimes
     * @param numGrids the number of Sudoku grids to generate
     */
    public static void generateGrids(int numGrids) {
        ArrayList<Long> generationTimes = new ArrayList<>(numGrids);
        DecimalFormat twoPlaces = new DecimalFormat("0.00");
        long minimum;
        long maximum;
        double average;

        for (int i = 0; i < numGrids; i++) {
            long startTime;
            long endTime;
            long runtime;

            // Empty the grid
            emptyTileGrid();

            // Record start time
            startTime = System.nanoTime();

            // Generate a grid
            PuzzleGenerator puzzle = new PuzzleGenerator();

            // Record end time
            endTime = System.nanoTime();

            // Calculate runtime
            runtime = endTime - startTime;

            // Add the runtime to the ArrayList of generation times
            generationTimes.add(runtime);
        }

        // Calculate statistics
        minimum = Collections.min(generationTimes);
        maximum = Collections.max(generationTimes);

        long sum = 0;

        for (Long time : generationTimes) {
            sum += time;
        }

        average = (double) sum / generationTimes.size();

        System.out.println("STATISTICS FOR GENERATION OF " + numGrids + " GRIDS:");
        System.out.println("------------------------------------------------------------");
        System.out.println("- Minimum time: " + twoPlaces.format(minimum / 1000000.0) + " ms");
        System.out.println("- Maximum time: " + twoPlaces.format(maximum / 1000000.0) + " ms");
        System.out.println("- Average time: " + twoPlaces.format(average / 1000000.0) + " ms");
    }

    /**
     * Removes all values in the tileGrid, setting them to null
     */
    private static void emptyTileGrid() {
        SudokuTile[][] tileGrid = SudokuTile.getTileGrid();

        // Remove all values
        for (SudokuTile[] column : tileGrid) {
            for (SudokuTile tile : column) {
                tile.setValue(null);
            }
        }
    }

    /**
     * Sets all the values on the board using a 2D-array of values, where 0 specifies an empty tile
     * @param boardValues the 2D-array of values to fill the board with
     */
    public static void setBoard(int[][] boardValues) {
        SudokuTile[][] tileGrid = SudokuTile.getTileGrid();

        for (int columnIndex = 0; columnIndex < tileGrid.length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < tileGrid[columnIndex].length; rowIndex++) {
                if (boardValues[rowIndex][columnIndex] != 0) {
                    tileGrid[columnIndex][rowIndex].setValue(boardValues[rowIndex][columnIndex]);
                }
                else {
                    tileGrid[columnIndex][rowIndex].setValue(null);
                }
            }
        }
    }
}

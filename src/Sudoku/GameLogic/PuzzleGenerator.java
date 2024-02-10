package Sudoku.GameLogic;

import Sudoku.UserInterface.Coordinates;
import Sudoku.UserInterface.SudokuTile;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class PuzzleGenerator {
    private Set<Coordinates> unfilledCoordinates;

    /**
     * Constructor: Creates a PuzzleGenerator object and calls puzzle generation methods
     */
    public PuzzleGenerator() {
        setInitialCandidates();
        assignFirstNine();
        fillGrid();
    }

    /**
     * Sets the candidates for each tile in the grid to all possible values and adds all tiles to unfilledCoordinates
     */
    private void setInitialCandidates() {
        // Add all tiles to the set of unfilled coordinates
        initUnfilledCoordinates();

        for (SudokuTile[] column : SudokuTile.getTileGrid()) {
            for (SudokuTile tile : column) {
                tile.setCandidates("123456789");
            }
        }
    }

    /**
     * Adds the coordinates of all tiles to the set of unfilled coordinates
     */
    private void initUnfilledCoordinates() {
        unfilledCoordinates = new HashSet<>();

        for (SudokuTile[] column : SudokuTile.getTileGrid()) {
            for (SudokuTile tile : column) {
                unfilledCoordinates.add(tile.getCoordinates());
            }
        }
    }

    /**
     * Gets a random Coordinates object from the set of unfilled coordinates
     * @return the Coordinates of a random unfilled tile
     */
    private Coordinates getRandomUnfilledCoordinates() {
        Random generator = new Random();
        int randomCoordinateIndex;
        int currentIndex;
        Iterator<Coordinates> coordinatesIterator;
        Coordinates randomUnfilledCoordinates = null;

        // Generate a random index from the set of unfilled coordinates
        randomCoordinateIndex = generator.nextInt(unfilledCoordinates.size());

        coordinatesIterator = unfilledCoordinates.iterator();
        currentIndex = 0;

        // Iterate over the set of coordinates
        while (coordinatesIterator.hasNext()) {
            randomUnfilledCoordinates = coordinatesIterator.next();

            // Return the coordinates if the index matches the randomCoordinateIndex
            if (currentIndex == randomCoordinateIndex) {
                return randomUnfilledCoordinates;
            }

            currentIndex++;
        }

        return randomUnfilledCoordinates;
    }

    /**
     * Adds the given coordinates to the list of unfilled coordinates
     * @param coordinates the coordinates of the unfilled tile
     */
    private void addUnfilledCoordinates(Coordinates coordinates) {
        unfilledCoordinates.add(coordinates);
    }

    /**
     * Removes the given coordinates from the list of unfilled coordinates
     * @param coordinates the coordinates of the filled tile
     */
    private void removeUnfilledCoordinates(Coordinates coordinates) {
        unfilledCoordinates.remove(coordinates);
    }

    /**
     * Assigns nine random tiles in the grid with the values 1-9.
     */
    private void assignFirstNine() {
        SudokuTile[][] tileGrid = SudokuTile.getTileGrid();
        int value = 1;

        while (value <= 9) {
            Coordinates randomUnfilledCoordinates = getRandomUnfilledCoordinates();
            SudokuTile randomTile = tileGrid[randomUnfilledCoordinates.x()][randomUnfilledCoordinates.y()];

            // Set the candidate to the current value (1-9)
            int candidate = value;


            // On last number, make sure placement is not invalid (very small chance it is)
            if (value == 9) {
                SudokuTile firstInvalidatedTile = getFirstInvalidatedTile(randomTile, candidate);

                if (firstInvalidatedTile != null) {
                    continue;
                }
            }

            // Fill the tile and update relevant tiles' candidates
            fillTileAndUpdate(randomTile, candidate);

            // Remove the randomly-selected tile from unfilledCoordinates
            removeUnfilledCoordinates(randomTile.getCoordinates());

            value++;
        }
    }

    private void fillGrid() {
        SudokuTile nextTile = null;

        // Repeat until all tiles are filled
        while (!unfilledCoordinates.isEmpty()) {
            // Check if the next tile has already been picked
            if (nextTile == null) {
                // Get a random unfilled tile
                Coordinates nextCoordinates = getRandomUnfilledCoordinates();
                nextTile = SudokuTile.getTileGrid()[nextCoordinates.x()][nextCoordinates.y()];
            }

            // Pick a random valid candidate
            int candidate = nextTile.getRandomCandidate();

            // Check if the candidate will invalidate relevant tiles
            SudokuTile firstInvalidatedTile = getFirstInvalidatedTile(nextTile, candidate);

            if (firstInvalidatedTile != null) {
                // Remove the invalid candidate
                // TODO: Implement backtracking here if the invalid candidate is the last
                if (nextTile.onlyCandidateEquals(candidate)) {
                    System.out.println("Tiles filled: " + (81 - unfilledCoordinates.size()));
                    return;
                }

                nextTile.removeCandidate(candidate);

                // Set the invalidated tile as the next tile to fill and iterate again
                nextTile = firstInvalidatedTile;
                continue;
            }

            // Fill the tile and update relevant tiles
            fillTileAndUpdate(nextTile, candidate);

            // Remove the tile from the list of unfilled coordinates
            unfilledCoordinates.remove(nextTile.getCoordinates());

            nextTile = null;
        }
    }

    /**
     * Returns the first tile that will be invalidated by a candidate placement, or null if no tiles are invalidated
     * @param tileToFill the tile in which the candidate is to be placed
     * @param candidate the value that will fill the tile
     * @return the first invalidated tile, or null if none are invalidated
     */
    private SudokuTile getFirstInvalidatedTile(SudokuTile tileToFill, int candidate) {
        // Check tiles in the same row
        for (SudokuTile relevantTile : tileToFill.getRow()) {
            if (relevantTile.onlyCandidateEquals(candidate)) {
                return relevantTile;
            }
        }

        // Check tiles in the same column
        for (SudokuTile relevantTile : tileToFill.getColumn()) {
            if (relevantTile.onlyCandidateEquals(candidate)) {
                return relevantTile;
            }
        }

        // Check tiles in the same box
        for (SudokuTile relevantTile : tileToFill.getBox()) {
            if (relevantTile.onlyCandidateEquals(candidate)) {
                return relevantTile;
            }
        }

        // Return null if there are no invalidated tiles
        return null;
    }

    /**
     * Fills a tile with the given value candidate and updates candidates for relevant tiles
     * @param tile the tile to fill
     * @param candidate the value to place in the tile
     */
    private void fillTileAndUpdate(SudokuTile tile, int candidate) {
        // Fill the tile and update its candidates
        tile.setValue(candidate);
        tile.setCandidates(Integer.toString(candidate));

        // Update candidates for tiles in the same row
        for (SudokuTile rowTile : tile.getRow()) {
            rowTile.removeCandidate(candidate);
        }

        // Update candidates for tiles in the same column
        for (SudokuTile columnTile : tile.getColumn()) {
            columnTile.removeCandidate(candidate);
        }

        // Update candidates for tiles in the same box
        for (SudokuTile boxTile : tile.getBox()) {
            boxTile.removeCandidate(candidate);
        }
    }
}

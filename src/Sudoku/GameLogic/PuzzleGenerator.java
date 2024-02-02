package Sudoku.GameLogic;

import Sudoku.UserInterface.Coordinates;
import Sudoku.UserInterface.SudokuTile;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class PuzzleGenerator {
    private Set<Coordinates> unfilledCoordinates;

    public PuzzleGenerator() {
        setInitialCandidates();
        assignNineRandom();
    }

    /**
     * Adds the coordinates of all tiles to the set of unfilled coordinates
     */
    private void initUnfilledTiles() {
        unfilledCoordinates = new HashSet<>();

        for (SudokuTile[] column : SudokuTile.getTileGrid()) {
            for (SudokuTile tile : column) {
                unfilledCoordinates.add(tile.getCoordinates());
            }
        }
    }

    /**
     * Gets a random Coordinates object from the set unfilled coordinates
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
     * Assigns nine random tiles in the grid with a random (valid) value.
     */
    private void assignNineRandom() {
        Random generator = new Random();
        SudokuTile[][] tileGrid = SudokuTile.getTileGrid();
        int count = 0;

        while (count < 9) {
            Coordinates randomUnfilledCoordinates = getRandomUnfilledCoordinates();
            SudokuTile randomTile = tileGrid[randomUnfilledCoordinates.x()][randomUnfilledCoordinates.y()];

            if (randomTile.isEmpty()) {
                // Generate a random index from the String of valid candidates
                int candidateIndex = generator.nextInt(randomTile.getCandidates().length());
                int value = Integer.parseInt(String.valueOf(randomTile.getCandidates().charAt(candidateIndex)));

                // Check if value is a valid candidate
                if (randomTile.getCandidates().indexOf(Integer.toString(value)) != -1) {
                    // Update the value and candidates for the tile
                    randomTile.setValue(value);
                    randomTile.setCandidates(Integer.toString(value));

                    // Update candidates for tiles in the same row
                    for (SudokuTile tile : randomTile.getRow()) {
                        tile.removeCandidate(value);
                    }

                    // Update candidates for tiles in the same column
                    for (SudokuTile tile : randomTile.getColumn()) {
                        tile.removeCandidate(value);
                    }

                    // Update candidates for tiles in the same box
                    for (SudokuTile tile : randomTile.getBox()) {
                        tile.removeCandidate(value);
                    }

                    // Remove the randomly-selected tile from unfilledCoordinates
                    removeUnfilledCoordinates(randomTile.getCoordinates());

                    count++;
                }
            }
        }
    }

    /**
     * Sets the candidates for each tile in the grid to all possible values and adds all tiles to unfilledTiles
     */
    private void setInitialCandidates() {
        initUnfilledTiles();

        for (SudokuTile[] column : SudokuTile.getTileGrid()) {
            for (SudokuTile tile : column) {
                tile.setCandidates("123456789");
            }
        }
    }
}

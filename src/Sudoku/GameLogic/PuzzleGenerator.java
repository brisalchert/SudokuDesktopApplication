package Sudoku.GameLogic;

import Sudoku.UserInterface.Coordinates;
import Sudoku.UserInterface.SudokuTile;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class PuzzleGenerator {
    private Set<Coordinates> unfilledTiles;

    public PuzzleGenerator() {
        setInitialCandidates();
        assignNineRandom();
    }

    /**
     * Adds the coordinates of all tiles to the set unfilledTiles
     */
    private void initUnfilledTiles() {
        unfilledTiles = new HashSet<>();

        for (SudokuTile[] column : SudokuTile.getTileGrid()) {
            for (SudokuTile tile : column) {
                unfilledTiles.add(tile.getCoordinates());
            }
        }
    }

    /**
     * Gets a random Coordinates object from the set unfilledTiles
     * @return the Coordinates of a random unfilled tile
     */
    private Coordinates getRandomUnfilledCoordinates() {
        Random generator = new Random();
        int randomCoordinateIndex;
        int currentIndex;
        Iterator<Coordinates> coordinatesIterator;
        Coordinates randomUnfilledCoordinates = null;

        // Generate a random index from the set of unfilledTiles
        randomCoordinateIndex = generator.nextInt(unfilledTiles.size());

        coordinatesIterator = unfilledTiles.iterator();
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

                    // Remove the randomly-selected tile from unfilledTiles
                    unfilledTiles.remove(randomTile.getCoordinates());

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

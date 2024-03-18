package Sudoku.GameLogic;

import Sudoku.UserInterface.Coordinates;
import Sudoku.UserInterface.SudokuTile;
import java.util.*;

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
        Stack<SudokuTile> filledTileStack = new Stack<>();
        HashMap<SudokuTile, String[][]> candidateStates = new HashMap<>();

        // TODO: Add more candidate removal strategies to speed up the algorithm

        // Repeat until all tiles are filled
        while (!unfilledCoordinates.isEmpty()) {
            // Check if the next tile has already been picked
            if (nextTile == null) {
                // Get a random unfilled tile
                Coordinates nextCoordinates = getRandomUnfilledCoordinates();
                nextTile = SudokuTile.getTileGrid()[nextCoordinates.x()][nextCoordinates.y()];
            }

            // Check if a backtracked tile has no more candidates
            if (nextTile.getNumCandidates() == 0) {
                // Backtrack further
                nextTile = backtrackToLastFilled(filledTileStack, candidateStates);

                continue;
            }

            // Pick a random valid candidate
            int candidate = nextTile.getRandomCandidate();

            // Check if the candidate will create an invalid pair of tiles with the same single candidatez
            if (createsInvalidPair(nextTile, candidate)) {
                // Check if the invalid candidate is the only remaining candidate
                if (nextTile.onlyCandidateEquals(candidate)) {
                    nextTile = backtrackToLastFilled(filledTileStack, candidateStates);

                    continue;
                }

                // Remove the invalid candidate and try to fill the tile again
                nextTile.removeCandidate(candidate);

                continue;
            }

            // Check if the candidate will invalidate relevant tiles
            SudokuTile firstInvalidatedTile = getFirstInvalidatedTile(nextTile, candidate);

            if (firstInvalidatedTile != null) {
                // Check if the invalid candidate is the only remaining candidate
                if (nextTile.onlyCandidateEquals(candidate)) {
                    nextTile = backtrackToLastFilled(filledTileStack, candidateStates);

                    continue;
                }

                // Remove the invalid candidate
                nextTile.removeCandidate(candidate);

                // Set the invalidated tile as the next tile to fill and iterate again
                nextTile = firstInvalidatedTile;
                continue;
            }

            // Add the pre-fill candidate state to candidateStates
            candidateStates.put(nextTile, getBoardCandidates());

            // Fill the tile and update relevant tiles
            fillTileAndUpdate(nextTile, candidate);

            // Add the tile to the Stack of filled tiles
            filledTileStack.push(nextTile);

            // Remove the tile from the list of unfilled coordinates
            removeUnfilledCoordinates(nextTile.getCoordinates());

            nextTile = null;
        }
    }

    /**
     * Backtracks to the last tile that was filled, restoring the old candidate state and removing that tile's last
     * tried value from its candidates
     * @param filledTileStack the Stack of filled tiles
     * @param candidateStates a HashMap mapping SudokuTiles to the states of the board's candidates before they were
     *                        filled
     * @return the tile that was backtracked to
     */
    private SudokuTile backtrackToLastFilled(Stack<SudokuTile> filledTileStack,
                                             HashMap<SudokuTile, String[][]> candidateStates) {
        SudokuTile nextTile;

        // Set nextTile to the last filled tile
        nextTile = filledTileStack.pop();

        // Restore the state of candidates before filling that tile
        setBoardCandidates(candidateStates.get(nextTile));

        // Remove the candidate that was tried and failed
        nextTile.removeCandidate(nextTile.getValue());

        // Add nextTile back to the list of unfilledCoordinates
        addUnfilledCoordinates(nextTile.getCoordinates());
        nextTile.setValue(0);

        return nextTile;
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
            if (relevantTile.onlyCandidateEquals(candidate) && !relevantTile.equals(tileToFill)) {
                return relevantTile;
            }
        }

        // Check tiles in the same column
        for (SudokuTile relevantTile : tileToFill.getColumn()) {
            if (relevantTile.onlyCandidateEquals(candidate) && !relevantTile.equals(tileToFill)) {
                return relevantTile;
            }
        }

        // Check tiles in the same box
        for (SudokuTile relevantTile : tileToFill.getBox()) {
            if (relevantTile.onlyCandidateEquals(candidate) && !relevantTile.equals(tileToFill)) {
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

    /**
     * Returns a boolean corresponding to whether the value placement will create a pair of tiles
     * with the same single candidate (an invalid pair)
     * @param tileToFill the tile being filled
     * @param candidate the value to place in the tile
     * @return true if the placement creates an invalid pair, false otherwise
     */
    private boolean createsInvalidPair(SudokuTile tileToFill, int candidate) {
        // Add the relevant row, column, and box to a list
        List<List<SudokuTile>> tileGroups = new ArrayList<>();
        tileGroups.add(tileToFill.getRow());
        tileGroups.add(tileToFill.getColumn());
        tileGroups.add(tileToFill.getBox());

        // Check for invalid pairs of tiles in each group
        for (List<SudokuTile> group : tileGroups) {
            List<SudokuTile> potentialInvalidTiles = new ArrayList<>();

            // Add any tiles with two candidates and the relevant candidate to the list of potentially invalid tiles
            for (SudokuTile tile : group) {
                if (tile.getNumCandidates() == 2 && tile.hasCandidate(candidate)) {
                    potentialInvalidTiles.add(tile);
                }
            }

            // Check if any potentially invalid tiles are an invalid pair
            if (containsSameCandidatePair(potentialInvalidTiles)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a boolean corresponding to whether the list of tiles has a pair with equivalent candidates
     * @param tiles the list of SudokuTiles
     * @return true if at least two tiles have equivalent candidates, false otherwise
     */
    private boolean containsSameCandidatePair(List<SudokuTile> tiles) {
        Set<StringBuilder> candidateSet = new HashSet<>();

        // Attempt to add all the candidate lists to the set
        for (SudokuTile tile : tiles) {
            if (!candidateSet.add(tile.getCandidates())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a 2D-array of the board's current state of candidates with the first dimension corresponding to the
     * x-coordinate and the second dimension corresponding to the y-coordinate
     * @return the 2D-array of candidates
     */
    private String[][] getBoardCandidates() {
        SudokuTile[][] tileGrid = SudokuTile.getTileGrid();
        String[][] boardCandidates = new String[tileGrid.length][tileGrid.length];

        // Add each candidate String to its place in boardCandidates
        for (int row = 0; row < tileGrid.length; row++) {
            for (int column = 0; column < tileGrid[0].length; column++) {
                boardCandidates[column][row] = tileGrid[column][row].getCandidates().toString();
            }
        }

        return boardCandidates;
    }

    /**
     * Sets the candidates for each tile in the grid using a given 2D-array of candidates
     * @param boardCandidates the 2D-array of candidates
     */
    private void setBoardCandidates(String[][] boardCandidates) {
        SudokuTile[][] tileGrid = SudokuTile.getTileGrid();

        // Set the candidates for each tile in the grid
        for (int row = 0; row < tileGrid.length; row++) {
            for (int column = 0; column < tileGrid[0].length; column++) {
                tileGrid[column][row].setCandidates(boardCandidates[column][row]);
            }
        }
    }
}

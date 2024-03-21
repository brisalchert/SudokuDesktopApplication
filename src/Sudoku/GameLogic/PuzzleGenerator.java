package Sudoku.GameLogic;

import Sudoku.Testing.Tests;
import Sudoku.UserInterface.Coordinates;
import Sudoku.UserInterface.SudokuTile;
import java.util.*;

public class PuzzleGenerator {
    private Set<Coordinates> unfilledCoordinates;

    /**
     * Constructor: Creates a PuzzleGenerator object and calls puzzle generation methods
     */
    public PuzzleGenerator() {
        initializeFullGrid();
    }

    /**
     * Initializes a valid, randomly-generated full Sudoku grid
     */
    private void initializeFullGrid() {
        setInitialCandidates();
        assignFirstNine();

        // Attempt to fill the grid, restarting from scratch if an invalid triple (rarely) occurs
        try {
            fillGrid();
        }
        catch (EmptyStackException error) {
            initializeFullGrid();
        }
    }

    /**
     * Sets the candidates for each tile in the grid to all possible values, removing any values present, and adds all
     * tiles to unfilledCoordinates
     */
    private void setInitialCandidates() {
        // Add all tiles to the set of unfilled coordinates
        initUnfilledCoordinates();

        for (SudokuTile[] column : SudokuTile.getTileGrid()) {
            for (SudokuTile tile : column) {
                if (!tile.isEmpty()) {
                    tile.setValue(null);
                }

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

    private boolean boxesHaveInvalidTriple() {
        for (List<SudokuTile> box : SudokuTile.getBoxes()) {
            ArrayList<SudokuTile> twoOrFewerCandidates  = new ArrayList<>();

            // Add all tiles with 2 candidates or fewer to the ArrayList
            for (SudokuTile tile : box) {
                if (tile.getNumCandidates() <= 2) {
                    twoOrFewerCandidates.add(tile);
                }
            }

            // 9 choose 3 = 84 possible combinations of tiles at maximum
            Set<Set<SudokuTile>> combinations = getAllCombinationsOfTiles(twoOrFewerCandidates, 3);

            // If any particular combination contains tiles with two or fewer candidates, it is an invalid triple
            for (Set<SudokuTile> combination: combinations) {
                // Create a set to store the unique candidates in the tiles
                Set<Integer> candidateSet = new HashSet<>();

                for (SudokuTile tile : combination) {
                    StringBuilder candidates = tile.getCandidates();

                    // Add each candidate to the set
                    for (int index = 0; index < candidates.length(); index++) {
                        int candidate = Integer.parseInt(candidates.substring(index, (index + 1)));

                        candidateSet.add(candidate);
                    }
                }

                // If candidateSet has two or fewer candidates, return true
                if (candidateSet.size() <= 2) {
                    return true;
                }
            }
        }

        // If no combination has fewer than 3 candidates, return false
        return false;
    }

    /**
     * Utility for getting the every combination of the items in an ArrayList of SudokuTiles
     * (https://www.geeksforgeeks.org/print-all-possible-combinations-of-r-elements-in-a-given-array-of-size-n/)
     * @param combinationSet the output set of all combinations of size combinationSize
     * @param tileSet the input ArrayList of SudokuTiles
     * @param currentCombination a temporary ArrayList for storing the current combination of tiles being processed
     * @param startIndex the starting index to select combination items from
     * @param endIndex the ending index to select combination items from
     * @param currentItemIndex the index of the next item to be placed in currentCombination
     * @param combinationSize the number of items in each combination
     */
    private void combinationsOfTiles(Set<Set<SudokuTile>> combinationSet, ArrayList<SudokuTile> tileSet,
                                     ArrayList<SudokuTile> currentCombination, int startIndex,
                                     int endIndex, int currentItemIndex, int combinationSize) {
        // If the current combination has (combinationSize) elements, add it to the output set
        if (currentItemIndex == combinationSize) {
            Set<SudokuTile> combination = new HashSet<>();

            for (int i = 0; i < combinationSize; i++) {
                combination.add(currentCombination.get(i));
            }

            // Add the current combination to the output set
            combinationSet.add(combination);

            return;
        }

        // Add every possible element to the current index of currentCombination
        // Condition ensures that the number of unused items (itemsInList - index) is greater than or equal to the
        // number of remaining spots in the current combination
        int itemsInList = endIndex + 1;
        int remainingSpotsInCombo = combinationSize - currentItemIndex;

        for (int tileSetIndex = startIndex;
             tileSetIndex <= endIndex && (itemsInList - tileSetIndex) >= remainingSpotsInCombo;
             tileSetIndex++) {
            // Add the item at tileSetIndex to currentCombination
            currentCombination.add(currentItemIndex, tileSet.get(tileSetIndex));

            // Get combinations from the remaining elements in the tileSet
            combinationsOfTiles(combinationSet, tileSet, currentCombination, startIndex + 1, endIndex,
                    currentItemIndex + 1, combinationSize);
        }
    }

    /**
     * Gets a Set of each combination (of size combinationSize) of the input ArrayList of SudokuTiles
     * @param tileSet the input ArrayList of SudokuTiles to get combinations from
     * @param combinationSize the number of items in each combination
     * @return the Set of all combinations
     */
    private Set<Set<SudokuTile>> getAllCombinationsOfTiles(ArrayList<SudokuTile> tileSet, int combinationSize) {
        Set<Set<SudokuTile>> combinationSet = new HashSet<>();
        // Create temporary ArrayList to store the combination items during method execution
        ArrayList<SudokuTile> currentCombination = new ArrayList<>(combinationSize);

        // Get the combinations
        combinationsOfTiles(combinationSet, tileSet, currentCombination, 0, (tileSet.size() - 1),
                0, combinationSize);

        return combinationSet;
    }

    private void fillGrid() throws EmptyStackException {
        SudokuTile nextTile = null;
        Stack<SudokuTile> filledTileStack = new Stack<>();
        HashMap<SudokuTile, String[][]> candidateStates = new HashMap<>();
        int count = 0;
        int maxIterations = 100;

        // TODO: Add more candidate removal strategies to speed up the algorithm

        // Repeat until all tiles are filled
        while (!unfilledCoordinates.isEmpty()) {
            count++;

            // If filling the grid takes too many iterations, reset the board to start over
            if (count > maxIterations) {
                while (!filledTileStack.isEmpty()) {
                    nextTile = filledTileStack.pop();
                    addUnfilledCoordinates(nextTile.getCoordinates());
                    nextTile.setValue(null);
                }

                setBoardCandidates(candidateStates.get(nextTile));

                break;
            }

            // Check for any singles using cross-hatch scanning
            boolean crossHatchResult = crossHatchScan(filledTileStack, candidateStates);

            // If the cross-hatch failed, backtrack
            if (!crossHatchResult) {
                nextTile = backtrackToLastFilled(filledTileStack, candidateStates);

                continue;
            }

            // If the cross-hatch finished the board, exit the loop
            if (unfilledCoordinates.isEmpty()) {
                continue;
            }

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

            // Check if the candidate will create an invalid pair of tiles with the same single candidate
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

            // Fill the tile and update method variables
            updateFillStack(nextTile, candidate, filledTileStack, candidateStates);

            nextTile = null;
        }

        // If the board was not filled in the maximum number of iterations, try again
        if (!unfilledCoordinates.isEmpty()) {
            fillGrid();
        }
    }

    /**
     * Backtracks to the last tile that was filled, restoring the old candidate state and removing that tile's last
     * tried value from its candidates
     * @param filledTileStack the Stack of filled tiles
     * @param candidateStates a HashMap mapping SudokuTiles to the states of the board's candidates before they were
     *                        filled
     * @return the tile that was backtracked to
     * @throws EmptyStackException when an invalid triple occurs in assignFirstNine()
     */
    private SudokuTile backtrackToLastFilled(Stack<SudokuTile> filledTileStack,
                                             HashMap<SudokuTile, String[][]> candidateStates)
            throws EmptyStackException {
        SudokuTile nextTile;

        // Set nextTile to the last filled tile
        nextTile = filledTileStack.pop();

        // Restore the state of candidates before filling that tile
        setBoardCandidates(candidateStates.get(nextTile));

        // Remove the candidate that was tried and failed
        nextTile.removeCandidate(nextTile.getValue());

        // Add nextTile back to the list of unfilledCoordinates
        addUnfilledCoordinates(nextTile.getCoordinates());
        nextTile.setValue(null);

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
     * Fills a tile and updates relevant candidates, while also updating the Stack of filled tiles and the HashMap of
     * candidate states
     * @param tile the tile being filled
     * @param candidate the candidate to fill the tile with
     * @param filledTileStack the Stack of last-filled tiles
     * @param candidateStates the HashMap of previous candidate states
     */
    private void updateFillStack(SudokuTile tile, int candidate, Stack<SudokuTile> filledTileStack,
                                 HashMap<SudokuTile, String[][]> candidateStates) {
        // Add the pre-fill candidate state to candidateStates
        candidateStates.put(tile, getBoardCandidates());

        // Fill the tile and update relevant tiles
        fillTileAndUpdate(tile, candidate);

        // Add the tile to the Stack of filled tiles
        filledTileStack.push(tile);

        // Remove the tile from the list of unfilled coordinates
        removeUnfilledCoordinates(tile.getCoordinates());
    }

    /**
     * Checks for unfilled singles in the board using cross-hatch scanning, looking for naked singles and hidden singles
     * @param filledTileStack the Stack of last-filled tiles
     * @param candidateStates the HashMap of previous candidate states
     * @return true if successful, or false if the board state is invalid
     */
    private boolean crossHatchScan(Stack<SudokuTile> filledTileStack, HashMap<SudokuTile, String[][]> candidateStates) {
        // Save the starting number of unfilledCoordinates
        int startingNumUnfilled = unfilledCoordinates.size();

        // Check each unfilled tile for naked singles (tiles with only one remaining candidate)
        boolean nakedSinglesResult = checkNakedSingles(filledTileStack, candidateStates);

        // Stop checking if board is invalid
        if (!nakedSinglesResult) {
            return false;
        }

        // Check each box, row, and column for hidden singles (only possible cell for a candidate)
        boolean hiddenSinglesResult = checkHiddenSingles(filledTileStack, candidateStates);

        // Stop checking if board is invalid
        if (!hiddenSinglesResult) {
            return false;
        }

        // Call method recursively until no more singles can be found
        if (unfilledCoordinates.size() != startingNumUnfilled) {
            return crossHatchScan(filledTileStack, candidateStates);
        }

        // Return true if no tiles were invalidated
        return true;
    }

    /**
     * Checks the set of unfilled tiles for "naked singles" -- tiles with only one remaining candidate -- and fills
     * any that are found
     * @param filledTileStack the Stack of last-filled tiles
     * @param candidateStates the HashMap of previous candidate states
     * @return true if successful, or false if the board state is invalid
     */
    private boolean checkNakedSingles(Stack<SudokuTile> filledTileStack, HashMap<SudokuTile, String[][]> candidateStates) {
        // Create a copy of unfilledCoordinates (to avoid concurrent modification)
        Set<Coordinates> unfilledCoordinatesCopy = new HashSet<>(unfilledCoordinates);

        // Check each unfilled tile for a naked single
        for (Coordinates unfilledCoordinates : unfilledCoordinatesCopy) {
            SudokuTile unfilledTile = SudokuTile.getTileGrid()[unfilledCoordinates.x()][unfilledCoordinates.y()];

            // Fill the tile if it only has one candidate
            if (unfilledTile.getNumCandidates() == 1) {
                int candidate = Integer.parseInt(unfilledTile.getCandidates().toString());

                // Check if the candidate will invalidate other tiles
                SudokuTile firstInvalidTile = getFirstInvalidatedTile(unfilledTile, candidate);

                // If a tile is invalidated, stop checking and return
                if (firstInvalidTile != null) {
                    return false;
                }

                // Update variables for fillGrid()
                updateFillStack(unfilledTile, candidate, filledTileStack, candidateStates);
            }
        }

        // If no tiles are invalidated, return true
        return true;
    }

    /**
     * Checks the board for "hidden singles" -- tiles that are the only valid spot for a candidate within a row,
     * column, or box -- and fills any that are found
     * @param filledTileStack the Stack of last-filled tiles
     * @param candidateStates the HashMap of previous candidate states
     * @return true if successful, false if the board state is invalid
     */
    private boolean checkHiddenSingles(Stack<SudokuTile> filledTileStack,
                                    HashMap<SudokuTile, String[][]> candidateStates) {
        // Check each row for hidden singles
        List<List<SudokuTile>> rowsList = SudokuTile.getRows();

        boolean rowsResult = checkHiddenSingleGroup(rowsList, filledTileStack, candidateStates);

        if (!rowsResult) {
            return false;
        }

        // Check each column for hidden singles
        List<List<SudokuTile>> columnsList = SudokuTile.getColumns();

        boolean columnsResult = checkHiddenSingleGroup(columnsList, filledTileStack, candidateStates);

        if (!columnsResult) {
            return false;
        }

        // Check each box for hidden singles
        List<List<SudokuTile>> boxesList = SudokuTile.getBoxes();

        boolean boxesResult = checkHiddenSingleGroup(boxesList, filledTileStack, candidateStates);

        if (!boxesResult) {
            return false;
        }

        // If no tiles are invalidated, return true
        return true;
    }

    /**
     * Support method for checking for hidden singles within set of tileGroups (rows, columns, or boxes)
     * @param tileGroups the groups of tiles (rows, columns, or boxes)
     * @param filledTileStack the Stack of last-filled tiles
     * @param candidateStates the HashMap of previous candidate states
     * @return true if successful, or false if the board state is invalid
     */
    private boolean checkHiddenSingleGroup(List<List<SudokuTile>> tileGroups,
                                                Stack<SudokuTile> filledTileStack,
                                                HashMap<SudokuTile, String[][]> candidateStates) {
        for (List<SudokuTile> group : tileGroups) {
            for (int candidate = 1; candidate <= 9; candidate++) {
                Set<SudokuTile> candidateTileSet = new HashSet<>();

                // Add all tiles with the candidate that are unfilled, quitting if more than one is found
                for (SudokuTile tile : group) {
                    if (tile.hasCandidate(candidate) && unfilledCoordinates.contains(tile.getCoordinates())) {
                        candidateTileSet.add(tile);
                    }

                    if (candidateTileSet.size() > 1) {
                        break;
                    }
                }

                // Try to fill the tile if it is the only possible placement for the candidate
                if (candidateTileSet.size() == 1) {
                    SudokuTile candidateTile = candidateTileSet.iterator().next();

                    // Check if the candidate will invalidate other tiles
                    SudokuTile firstInvalidatedTile = getFirstInvalidatedTile(candidateTile, candidate);

                    // If a tile is invalidated, stop checking and return
                    if (firstInvalidatedTile != null) {
                        return false;
                    }

                    // Update variables for fillGrid()
                    updateFillStack(candidateTile, candidate, filledTileStack, candidateStates);
                }
            }
        }

        // If no tiles are invalidated, return true
        return true;
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

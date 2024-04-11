package Sudoku.GameLogic;

import Sudoku.UserInterface.Coordinates;
import Sudoku.UserInterface.SudokuTile;
import java.util.*;

public class PuzzleGenerator {
    private Set<Coordinates> unfilledCoordinates;
    private Set<Coordinates> filledCoordinates;
    private int solutionCount;
    private final SudokuTile[][] tileGrid = SudokuTile.getTileGrid();

    /**
     * Constructor: Creates a PuzzleGenerator object and calls puzzle generation methods
     * @param minimumClues the minimum number of clues to leave in the board
     */
    public PuzzleGenerator(int minimumClues) {
        initializeFullGrid();

        removeClues(minimumClues, 4, 1, 50, 0);
    }

    public SudokuTile[][] getTileGrid() {
        return this.tileGrid;
    }

    /**
     * Initializes the SudokuTile objects in the tileGrid
     */
    private void initializeTileGrid() {
        for (int row = 0; row < tileGrid.length; row++) {
            for (int col = 0; col < tileGrid[row].length; col++) {
                tileGrid[row][col] = new SudokuTile(row, col);
            }
        }
    }

    /**
     * Initializes a valid, randomly-generated full Sudoku grid
     */
    private void initializeFullGrid() {
        initializeTileGrid();
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
        initCoordinatesSets();

        for (SudokuTile[] row : tileGrid) {
            for (SudokuTile tile : row) {
                if (!tile.isEmpty()) {
                    tile.setValue(0);
                }

                tile.setCandidates("123456789");
            }
        }
    }

    /**
     * Adds the coordinates of all tiles to the set of unfilled coordinates and initializes filledCoordinates
     */
    private void initCoordinatesSets() {
        unfilledCoordinates = new HashSet<>();
        filledCoordinates = new HashSet<>();

        for (SudokuTile[] row : tileGrid) {
            for (SudokuTile tile : row) {
                unfilledCoordinates.add(tile.getCoordinates());
            }
        }
    }

    /**
     * Gets a random Coordinates object from the set of unfilled coordinates
     * @return the Coordinates of a random unfilled tile
     */
    private Coordinates getRandomCoordinates(Set<Coordinates> coordinatesSet) {
        Random generator = new Random();
        int randomCoordinateIndex;
        int currentIndex;
        Iterator<Coordinates> coordinatesIterator;
        Coordinates randomCoordinates = null;

        // Generate a random index from the set of coordinates
        randomCoordinateIndex = generator.nextInt(coordinatesSet.size());

        coordinatesIterator = coordinatesSet.iterator();
        currentIndex = 0;

        // Iterate over the set of coordinates
        while (coordinatesIterator.hasNext()) {
            randomCoordinates = coordinatesIterator.next();

            // Return the coordinates if the index matches the randomCoordinateIndex
            if (currentIndex == randomCoordinateIndex) {
                return randomCoordinates;
            }

            currentIndex++;
        }

        return randomCoordinates;
    }

    /**
     * Adds the given coordinates to the list of unfilled coordinates and removes it from the list of filled coordinates
     * @param coordinates the coordinates of the unfilled tile
     */
    private void addUnfilledCoordinates(Coordinates coordinates) {
        unfilledCoordinates.add(coordinates);
        filledCoordinates.remove(coordinates);
    }

    /**
     * Removes the given coordinates from the list of unfilled coordinates and adds it to the list of filled coordinates
     * @param coordinates the coordinates of the filled tile
     */
    private void removeUnfilledCoordinates(Coordinates coordinates) {
        unfilledCoordinates.remove(coordinates);
        filledCoordinates.add(coordinates);
    }

    /**
     * Returns a boolean corresponding to whether or not the specified coordinates are filled
     * @param coordinates the coordinates to check
     * @return true if the coordinates are filled, false otherwise
     */
    private boolean coordinatesAreFilled(Coordinates coordinates) {
        return filledCoordinates.contains(coordinates);
    }

    /**
     * Assigns nine random tiles in the grid with the values 1-9.
     */
    private void assignFirstNine() {
        int value = 1;

        while (value <= 9) {
            Coordinates randomUnfilledCoordinates = getRandomCoordinates(unfilledCoordinates);
            SudokuTile randomTile = SudokuTile.getTileByCoordinates(randomUnfilledCoordinates.row(),
                    randomUnfilledCoordinates.column());

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

        // Repeat until all tiles are filled
        while (!unfilledCoordinates.isEmpty()) {
            count++;

            // If filling the grid takes too many iterations, reset the board to start over
            if (count > maxIterations) {
                while (!filledTileStack.isEmpty()) {
                    nextTile = filledTileStack.pop();
                    addUnfilledCoordinates(nextTile.getCoordinates());
                    nextTile.setValue(0);
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
                Coordinates nextCoordinates = getRandomCoordinates(unfilledCoordinates);
                nextTile = SudokuTile.getTileByCoordinates(nextCoordinates.row(), nextCoordinates.column());
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
     * Sets a tile's value to "null" and updates candidates for relevant tiles
     * @param tile the SudokuTile to empty
     */
    private void emptyTileAndUpdate(SudokuTile tile) {
        // Save the current value of the tile
        int previousValue = tile.getValue();

        // Empty the tile
        tile.setValue(0);

        // Update candidates for the empty tile
        restoreCandidates(tile);

        // Update candidates for tiles in the same row, column, and box
        restoreCandidates(tile.getRow(), previousValue);
        restoreCandidates(tile.getColumn(), previousValue);
        restoreCandidates(tile.getBox(), previousValue);
    }

    /**
     * Restores a missing candidate to each tile in a group if that candidate is valid
     * @param group the group of SudokuTiles to restore candidates for
     * @param candidate the candidate to restore
     */
    private void restoreCandidates(List<SudokuTile> group, int candidate) {
        for (SudokuTile tile : group) {
            // Check if the value is in a relevant row, column, or box
            if (!(SudokuTile.collectionHasValue(tile.getRow(), candidate)) &&
                    !(SudokuTile.collectionHasValue(tile.getColumn(), candidate)) &&
                    !(SudokuTile.collectionHasValue(tile.getBox(), candidate))) {
                // Add back the candidate
                tile.addCandidate(candidate);
            }
        }
    }

    /**
     * Restores any missing candidates to a tile
     * @param tile the tile to restore candidates for
     */
    private void restoreCandidates(SudokuTile tile) {
        // Try to restore each candidate
        for (int candidate = 1; candidate <= 9; candidate++) {
            // Check if the value is in the same row, column, or box
            if (!(SudokuTile.collectionHasValue(tile.getRow(), candidate)) &&
                    !(SudokuTile.collectionHasValue(tile.getColumn(), candidate)) &&
                    !(SudokuTile.collectionHasValue(tile.getBox(), candidate))) {
                // Add back the candidate
                tile.addCandidate(candidate);
            }
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
    private boolean checkNakedSingles(Stack<SudokuTile> filledTileStack, HashMap<SudokuTile,
            String[][]> candidateStates) {
        // Create a copy of unfilledCoordinates (to avoid concurrent modification)
        Set<Coordinates> unfilledCoordinatesCopy = new HashSet<>(unfilledCoordinates);

        // Check each unfilled tile for a naked single
        for (Coordinates unfilledCoordinates : unfilledCoordinatesCopy) {
            SudokuTile unfilledTile = SudokuTile.getTileByCoordinates(unfilledCoordinates.row(),
                    unfilledCoordinates.column());

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

        // If no tiles are invalidated, return true. Otherwise, return false
        return boxesResult;
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
     * Returns a boolean corresponding to whether or not the input ArrayList tileGrid has a unique solution
     * @param tileGrid the 2D-ArrayList Sudoku board
     * @return true if tileGrid has a unique solution, false otherwise
     */
    private boolean hasUniqueSolution(ArrayList<ArrayList<Integer>> tileGrid) {
        solutionCount = 0;

        updateSolutionCount(tileGrid, 2);

        return solutionCount != 2;
    }

    /**
     * Gets the number of unique solutions for the current board
     * @return an integer number of unique solutions for the current board
     */
    private int getSolutionCount() {
        ArrayList<ArrayList<Integer>> tileGrid = SudokuTile.tileGridToArrayList();
        solutionCount = 0;

        updateSolutionCount(tileGrid, null);

        return solutionCount;
    }

    /**
     * Increments solutionCount for each unique solution the current board has up to a specified maximum number
     * https://www.101computing.net/backtracking-algorithm-sudoku-solver/
     * @param tileGrid the 2D-ArrayList of values on the tileGrid
     * @param maxSolutions the maximum number of solutions to find, or 0 for no maximum
     */
    private void updateSolutionCount(ArrayList<ArrayList<Integer>> tileGrid, Integer maxSolutions) {
        // If the maximum number of solutions has been reached, stop searching for new solutions
        // If maxSolutions is null, ignore
        if (maxSolutions != null) {
            if (solutionCount == maxSolutions) {
                return;
            }
        }

        // Find the next empty tile (loop breaks, so not O(n^2) runtime)
        for (int tileIndex = 0; tileIndex < 81; tileIndex++) {
            int rowIndex = tileIndex / 9;
            int columnIndex = tileIndex % 9;

            if (tileGrid.get(rowIndex).get(columnIndex) == 0) {
                // Try to fill the tile with every possible value
                for (int value = 1; value <= 9; value++) {
                    // Check that the value is not present in the row
                    if (!tileGrid.get(rowIndex).contains(value)) {
                        // Check that the value is not present in the column
                        boolean columnHasValue = false;

                        for (ArrayList<Integer> row : tileGrid) {
                            if (row.get(columnIndex) == value) {
                                columnHasValue = true;
                                break;
                            }
                        }

                        if (!columnHasValue) {
                            ArrayList<Integer> box;

                            // Identify which box the tile is in
                            if (rowIndex < 3) {
                                if (columnIndex < 3) {
                                    box = getBoxArrayList(tileGrid, 0, 0);
                                }
                                else if (columnIndex < 6) {
                                    box = getBoxArrayList(tileGrid, 0, 3);
                                }
                                else {
                                    box = getBoxArrayList(tileGrid, 0, 6);
                                }
                            }
                            else if (rowIndex < 6) {
                                if (columnIndex < 3) {
                                    box = getBoxArrayList(tileGrid, 3, 0);
                                }
                                else if (columnIndex < 6) {
                                    box = getBoxArrayList(tileGrid, 3, 3);
                                }
                                else {
                                    box = getBoxArrayList(tileGrid, 3, 6);
                                }
                            }
                            else {
                                if (columnIndex < 3) {
                                    box = getBoxArrayList(tileGrid, 6, 0);
                                }
                                else if (columnIndex < 6) {
                                    box = getBoxArrayList(tileGrid, 6, 3);
                                }
                                else {
                                    box = getBoxArrayList(tileGrid, 6, 6);
                                }
                            }

                            // Check that the value is not present in the box
                            if (!box.contains(value)) {
                                // Set the tile's value
                                tileGrid.get(rowIndex).set(columnIndex, value);



                                // If the grid is full, increment the solution count and remove the previous value.
                                // Otherwise, continue to next empty cell
                                if (tileGridFull(tileGrid)) {
                                    solutionCount++;
                                    tileGrid.get(rowIndex).set(columnIndex, 0);

                                    return;
                                }
                                else {
                                    updateSolutionCount(tileGrid, maxSolutions);
                                }
                            }
                        }
                    }
                }

                // If no value works for the tile or all values have been checked, reset the tile's value and backtrack
                // (Backtracking happens through the recursion stack)
                tileGrid.get(rowIndex).set(columnIndex, 0);

                break;
            }
        }
    }

    /**
     * Gets an ArrayList of all values in the box of the 2D-ArrayList tileGrid that starts at the given indices
     * @param tileGrid the 2D-ArrayList of tile values
     * @param startingRowIndex the starting row index of the box
     * @param startingColumnIndex the starting column index of the box
     * @return an ArrayList of the box values
     */
    private ArrayList<Integer> getBoxArrayList(ArrayList<ArrayList<Integer>> tileGrid, int startingRowIndex,
                                               int startingColumnIndex) {
        ArrayList<Integer> box = new ArrayList<>();

        // Add all values within the box
        for (int i = startingRowIndex; i < (startingRowIndex + 3); i++) {
            for (int j = startingColumnIndex; j < (startingColumnIndex + 3); j++) {
                box.add(tileGrid.get(i).get(j));
            }
        }

        return box;
    }

    /**
     * Checks if the 2D-ArrayList tileGrid is full, returning true if all values are set to non-zero
     * @param tileGrid the 2D-ArrayList of grid values
     * @return true if the grid is filled, false otherwise
     */
    private boolean tileGridFull(ArrayList<ArrayList<Integer>> tileGrid) {
        for (ArrayList<Integer> row : tileGrid) {
            for (Integer value : row) {
                if (value == 0) {
                    return false;
                }
            }
        }

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
     * row-coordinate and the second dimension corresponding to the column-coordinate
     * @return the 2D-array of candidates
     */
    private String[][] getBoardCandidates() {
        String[][] boardCandidates = new String[tileGrid.length][tileGrid[0].length];

        // Add each candidate String to its place in boardCandidates
        for (int row = 0; row < tileGrid.length; row++) {
            for (int column = 0; column < tileGrid[row].length; column++) {
                boardCandidates[row][column] = tileGrid[row][column].getCandidates().toString();
            }
        }

        return boardCandidates;
    }

    /**
     * Sets the candidates for each tile in the grid using a given 2D-array of candidates
     * @param boardCandidates the 2D-array of candidates
     */
    private void setBoardCandidates(String[][] boardCandidates) {
        // Set the candidates for each tile in the grid
        for (int row = 0; row < tileGrid.length; row++) {
            for (int column = 0; column < tileGrid[row].length; column++) {
                tileGrid[row][column].setCandidates(boardCandidates[row][column]);
            }
        }
    }

    /**
     * Removes clues from a full Sudoku board until the minimum number of clues is achieved, or until the maximum
     * number of iterations is reached
     * @param minimumClues the minimum number of clues to leave in the board
     * @param removalCount the number of clues to remove in this iteration
     * @param currentIteration the current iteration number in the removal process
     * @param maxIterations the maximum number of iterations to allow
     * @param lastIterationModified the last iteration that clues were removed on
     * @return the last iteration that board was modified
     */
    private int removeClues(int minimumClues, int removalCount, int currentIteration, int maxIterations,
                             int lastIterationModified) {
        // Set starting clues to the current number of clues remaining
        int startingClues = filledCoordinates.size();

        // If maxIterations is reached, return
        if (currentIteration == maxIterations) {
            return lastIterationModified;
        }

        // If minimumClues is reached, return
        if ((filledCoordinates.size()) <= minimumClues) {
            return lastIterationModified;
        }

        // Get the current board state as an ArrayList
        ArrayList<ArrayList<Integer>> currentBoard = SudokuTile.tileGridToArrayList();

        // Get a copy of filledCoordinates
        Set<Coordinates> filledCoordinatesCopy = new HashSet<>(filledCoordinates);

        if (removalCount == 4) {
            // Remove 4 diagonally opposite clues from the ArrayList board
            removeOppositeDiagonalClues(currentBoard, filledCoordinatesCopy, true);
        }

        if (removalCount == 2) {
            // Remove 2 diagonally opposite clues from the ArrayList board
            removeOppositeDiagonalClues(currentBoard, filledCoordinatesCopy, false);
        }

        if (removalCount == 1) {
            // Remove a single clue randomly from the ArrayList board
            Coordinates randomCoordinates = getRandomCoordinates(filledCoordinates);

            currentBoard.get(randomCoordinates.row()).set(randomCoordinates.column(), 0);

            if (hasUniqueSolution(currentBoard)) {
                // Remove the clue and update unfilledCoordinates
                emptyTileAndUpdate(SudokuTile.getTileByCoordinates(randomCoordinates.row(),
                        randomCoordinates.column()));

                addUnfilledCoordinates(randomCoordinates);
            }
        }

        // If clues were removed this iteration, set lastIterationModified to currentIteration
        if (startingClues > filledCoordinates.size()) {
            lastIterationModified = currentIteration;
        }

        // Iterate again, choosing how many clues to try to remove
        if (unfilledCoordinates.size() < 20) {
            return removeClues(minimumClues, 4, (currentIteration + 1), maxIterations, lastIterationModified);
        }
        else if ((filledCoordinates.size() > 30) && (currentIteration < (maxIterations / 5))) {
            return removeClues(minimumClues, 2, (currentIteration + 1), maxIterations, lastIterationModified);
        }
        else {
            return removeClues(minimumClues, 1, (currentIteration + 1), maxIterations, lastIterationModified);
        }
    }

    /**
     * Removes a pair of opposite diagonal clues (or two pairs if removeQuad is true), making sure the resulting board
     * retains a unique solution
     * @param board the 2D-ArrayList of the values in the tileGrid
     * @param eligibleCoordinates a set of Coordinates eligible for removal
     * @param removeQuad boolean corresponding to whether or not two pairs of clues should be removed
     * @return true if the removal is successful, false otherwise
     */
    private boolean removeOppositeDiagonalClues(ArrayList<ArrayList<Integer>> board,
                                                              Set<Coordinates> eligibleCoordinates,
                                                              boolean removeQuad) {
        // If no more diagonal clues can be removed while maintaining a unique solution, return false
        if (eligibleCoordinates.isEmpty()) {
            return false;
        }

        // Get a pair of diagonal filled coordinates
        Set<Coordinates> diagonalCoordinates = getDiagonalFilledCoordinates(eligibleCoordinates);

        // Add the coordinates to the list to remove
        Set<Coordinates> coordinatesToRemove = new HashSet<>(diagonalCoordinates);

        // Remove the coordinates from eligibleCoordinates
        eligibleCoordinates.removeAll(coordinatesToRemove);

        // Get a second pair of random filled tiles if removeQuad is true
        if (removeQuad) {
            diagonalCoordinates = getDiagonalFilledCoordinates(eligibleCoordinates);

            // Add the coordinates to the list to remove
            coordinatesToRemove.addAll(diagonalCoordinates);

            // Remove the coordinates from eligibleCoordinates
            eligibleCoordinates.removeAll(coordinatesToRemove);
        }

        // Remove the clues from the board
        for (Coordinates coordinates : coordinatesToRemove) {
            board.get(coordinates.row()).set(coordinates.column(), 0);
        }

        // If the board still has a unique solution, remove the clues from the tileGrid. Otherwise, try again with
        // different coordinates
        if (hasUniqueSolution(board)) {
            for (Coordinates coordinates : coordinatesToRemove) {
                emptyTileAndUpdate(SudokuTile.getTileByCoordinates(coordinates.row(), coordinates.column()));

                // Add the tile's coordinates back to unfilledCoordinates
                addUnfilledCoordinates(coordinates);
            }

            return true;
        }
        else {
            // Try again, refreshing the current board state
            return removeOppositeDiagonalClues(SudokuTile.tileGridToArrayList(), eligibleCoordinates, removeQuad);
        }
    }

    /**
     * Returns a Set of two Coordinates objects that correspond to diagonal filled coordinates from the Set of
     * eligible coordinates
     * @param eligibleCoordinates the set of coordinates to draw the pair from
     * @return a Set of two diagonal filled Coordinates
     */
    private Set<Coordinates> getDiagonalFilledCoordinates(Set<Coordinates> eligibleCoordinates) {
        Set<Coordinates> diagonalPair = new HashSet<>();
        Set<Coordinates> eligibleCoordinatesCopy = new HashSet<>(eligibleCoordinates);
        Coordinates randomCoordinates;
        Coordinates inverseCoordinates;
        int inverseFactor = tileGrid.length - 1;

        // Get a random set of filled coordinates
        randomCoordinates = getRandomCoordinates(eligibleCoordinatesCopy);

        // Invert the random tile's coordinates
        int inverseRow = (inverseFactor - randomCoordinates.row());
        int inverseColumn = (inverseFactor - randomCoordinates.column());

        inverseCoordinates = new Coordinates(inverseRow, inverseColumn);

        // Add the coordinates to a set and return
        diagonalPair.add(randomCoordinates);
        diagonalPair.add(inverseCoordinates);

        return diagonalPair;
    }
}

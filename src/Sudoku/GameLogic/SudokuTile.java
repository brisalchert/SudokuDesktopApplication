package Sudoku.GameLogic;

import Sudoku.UserInterface.Coordinates;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SudokuTile {
    // Used for accessing tiles by index
    private static final SudokuTile[][] tileGrid = new SudokuTile[9][9];
    private static SudokuTile lastClickedTile;
    // First coordinate is row, second coordinate is column
    private final Coordinates coordinates;
    private boolean editable = true;
    private SimpleIntegerProperty valueProperty = new SimpleIntegerProperty();
    private StringBuilder candidates = new StringBuilder();
    private SimpleBooleanProperty clickedProperty = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty relevantProperty = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty hoveredProperty = new SimpleBooleanProperty(false);
    private final Color TILE_NEUTRAL_COLOR = Color.rgb(0, 0, 0, 0.0);
    private final Color TILE_RELEVANT_COLOR = Color.rgb(0, 0, 0, 0.1);
    private final Color TILE_HOVERED_COLOR = Color.rgb(0, 0, 0, 0.25);
    private final Color TILE_CLICKED_COLOR = Color.rgb(0, 0, 0, 0.4);
    private ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(TILE_NEUTRAL_COLOR);

    public SudokuTile(int row, int column) {
        coordinates = new Coordinates(row, column);

        // Add the tile to the global tileGrid
        tileGrid[getRowIndex()][getColumnIndex()] = this;
    }

    public static SudokuTile[][] getTileGrid() {
        return tileGrid;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public int getRowIndex() {
        return coordinates.row();
    }

    public int getColumnIndex() {
        return coordinates.column();
    }

    public Color getColor() {
        return colorProperty.get();
    }

    public void setColorNeutral() {
        colorProperty.set(TILE_NEUTRAL_COLOR);
    }

    public void setColorRelevant() {
        colorProperty.set(TILE_RELEVANT_COLOR);
    }

    public void setColorHovered() {
        colorProperty.set(TILE_HOVERED_COLOR);
    }

    public void setColorClicked() {
        colorProperty.set(TILE_CLICKED_COLOR);
    }

    public ObjectProperty<Color> colorProperty() {
        return colorProperty;
    }

    public StringBuilder getCandidates() {
        return candidates;
    }

    public void setCandidates(String candidates) {
        if (!this.candidates.isEmpty()) {
            this.candidates.delete(0, this.candidates.length());
        }

        this.candidates.append(candidates);
    }

    /**
     * Adds a candidate to the list of candidates for a tile
     * @param candidate the candidate to add
     */
    public void addCandidate(int candidate) {
        if (this.candidates.indexOf(Integer.toString(candidate)) == -1) {
            this.candidates.append(candidate);
        }
    }

    /**
     * Removes a candidate from the list of candidates for a tile
     * @param candidate the candidate to remove
     */
    public void removeCandidate(int candidate) {
        int candidateIndex = this.candidates.indexOf(Integer.toString(candidate));

        if (candidateIndex != -1) {
            this.candidates.deleteCharAt(candidateIndex);
        }
    }

    /**
     * Gets a random candidate from this tile's list of valid candidates
     * @return the random candidate as an integer
     * @throws IllegalArgumentException if there are no valid candidates
     */
    public int getRandomCandidate() throws IllegalArgumentException {
        Random generator = new Random();

        // Generate a random index from the String of valid candidates
        int candidateIndex = generator.nextInt(this.getCandidates().length());
        return Integer.parseInt(String.valueOf(this.getCandidates().charAt(candidateIndex)));
    }

    /**
     * Returns a boolean corresponding to whether the provided integer matches the tile's only candidate
     * @param candidate the provided integer
     * @return true if the candidate matches, false if it does not or there are multiple candidates
     */
    public boolean onlyCandidateEquals(int candidate) {
        // Return false if there is more than one candidate
        if (this.candidates.length() > 1) {
            return false;
        }

        // Return true if the candidate matches, false otherwise
        return this.candidates.toString().equals(Integer.toString(candidate));
    }

    /**
     * Gets the number of candidates remaining for this tile
     * @return the number of remaining candidates
     */
    public int getNumCandidates() {
        return this.candidates.length();
    }

    /**
     * Returns a boolean corresponding to whether this tile has the specified candidate or not
     * @param candidate the candidate being checked
     * @return true if the candidate is present, false otherwise
     */
    public boolean hasCandidate(int candidate) {
        if (this.candidates.indexOf(Integer.toString(candidate)) != -1) {
            return true;
        }

        return false;
    }

    /**
     * Gets a reference to the SudokuTile at coordinates (row, column)
     * @param row the row coordinate of the tile
     * @param column the column coordinate of the tile
     * @return the SudokuTile at the given coordinates
     */
    public static SudokuTile getTileByCoordinates(int row, int column) {
        return tileGrid[row][column];
    }

    /**
     * Gets a reference to the SudokuTile at the coordinates of the given Coordinates object
     * @param coordinates the Coordinates object with the coordinates of the tile
     * @return the SudokuTile at the given coordinates
     */
    public static SudokuTile getTileByCoordinates(Coordinates coordinates) {
        return tileGrid[coordinates.row()][coordinates.column()];
    }

    /**
     * Retrieves a collection of all the SudokuTiles in the same row as the current one
     * @return the list of tiles in the same row
     */
    public List<SudokuTile> getRow() {
        int rowIndex = this.getRowIndex();

        // Return an ArrayList of all tiles in this tile's row
        return new ArrayList<>(Arrays.asList(tileGrid[rowIndex]));
    }

    /**
     * Retrieves a collection of all the SudokuTiles in the same column as the current one
     * @return the list of tiles in the same column
     */
    public List<SudokuTile> getColumn() {
        List<SudokuTile> columnList = new ArrayList<>();
        int columnIndex = this.getColumnIndex();

        // For each row in the tileGrid, add the tile from the corresponding column
        for (SudokuTile[] row : tileGrid) {
            columnList.add(row[columnIndex]);
        }

        return columnList;
    }

    /**
     * Retrieves a collection of all the SudokuTiles in the same box as the current one
     * @return the list of tiles in the same box
     */
    public List<SudokuTile> getBox() {
        List<SudokuTile> boxList = new ArrayList<>();
        int boxRowIndex = (this.getRowIndex() / 3);
        int boxColumnIndex = (this.getColumnIndex() / 3);

        for (int rowIndex = (boxRowIndex * 3); rowIndex < ((boxRowIndex + 1) * 3); rowIndex++) {
            boxList.addAll(Arrays.asList(tileGrid[rowIndex]).subList((boxColumnIndex * 3), ((boxColumnIndex + 1) * 3)));
        }

        return boxList;
    }

    /**
     * Gets a 2D-list of the rows of SudokuTiles in the tileGrid
     * @return the list of rows
     */
    public static List<List<SudokuTile>> getRows() {
        List<List<SudokuTile>> rowsList = new ArrayList<>();

        // Add each row in the tileGrid
        for (SudokuTile[] row : tileGrid) {
            rowsList.add(row[0].getRow());
        }

        return rowsList;
    }

    /**
     * Gets a 2D-list of the columns of SudokuTiles in the tileGrid
     * @return the list of columns
     */
    public static List<List<SudokuTile>> getColumns() {
        List<List<SudokuTile>> columnsList = new ArrayList<>();

        // Add each column in the tileGrid
        for (int columnIndex = 0; columnIndex < tileGrid[0].length; columnIndex++) {
            columnsList.add(tileGrid[0][columnIndex].getColumn());
        }

        return columnsList;
    }

    /**
     * Gets a 2D-list of the boxes of SudokuTiles in the tileGrid
     * @return the list of boxes
     */
    public static List<List<SudokuTile>> getBoxes() {
        List<List<SudokuTile>> boxesList = new ArrayList<>();

        // Add each box in the tileGrid
        for (int rowIndex = 0; rowIndex < tileGrid.length; rowIndex += 3) {
            for (int columnIndex = 0; columnIndex < tileGrid[rowIndex].length; columnIndex += 3) {
                boxesList.add(tileGrid[rowIndex][columnIndex].getBox());
            }
        }

        return boxesList;
    }

    /**
     * Checks if any tile in the given collection of tiles has the specified value
     * @param collection the collection of SudokuTiles
     * @param value the value to check for
     * @return true if the value is found, false otherwise
     */
    public static boolean collectionHasValue(List<SudokuTile> collection, int value) {
        for (SudokuTile tile : collection) {
            if (!tile.isEmpty()) {
                if (tile.getValue() == value) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns a boolean value corresponding to whether the tileGrid is full or not
     * @return true if the tileGrid is full, false otherwise
     */
    public static boolean tileGridFull() {
        for (SudokuTile[] row : tileGrid) {
            for (SudokuTile tile : row) {
                if (tile.getValue() == null) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Converts the tileGrid's values to an integer array and returns it
     * @return the integer array of the tileGrid's values
     */
    public static ArrayList<ArrayList<Integer>> tileGridToArrayList() {
        ArrayList<ArrayList<Integer>> board = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < tileGrid.length; rowIndex++) {
            ArrayList<Integer> row = new ArrayList<>();

            for (int columnIndex = 0; columnIndex < tileGrid[rowIndex].length; columnIndex++) {
                Integer value = tileGrid[rowIndex][columnIndex].getValue();

                if (value != null) {
                    row.add(value);
                }
                else {
                    row.add(0);
                }
            }

            board.add(row);
        }

        return board;
    }

    public boolean isEmpty() {
        return getValue() == null || getValue() == 0;
    }

    public Integer getValue() {
        return valueProperty.get();
    }

    /**
     * Sets the value associated with the SudokuTile and displays it if it is not 0
     * @param value the value to set for the SudokuTile
     */
    protected void setValue(int value) {
        // Do not accept a value not in the range 1-9 unless it is 0
        if (value == 0 || (value >= 1 && value <= 9)) {
            this.valueProperty.set(value);
        }
    }

    /**
     * Gets a reference to the tile's valueProperty
     * @return the tile's valueProperty itself
     */
    public SimpleIntegerProperty valueProperty() {
        return valueProperty;
    }

    /**
     * Gets the value corresponding to whether the tile is editable
     * @return the boolean value of editable
     */
    public boolean getEditable() {
        return editable;
    }

    /**
     * Sets the editable value for the tile
     * @param editable the new value for whether the tile is editable
     */
    protected void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     * Gets the SudokuTile object for the tile that was last clicked by the user
     * @return the last clicked SudokuTile
     */
    public static SudokuTile getLastClickedTile() {
        return lastClickedTile;
    }

    /**
     * Sets lastClickedTile to the specified tile
     * @param tile the tile to set lastClickedTile to
     */
    public static void setLastClickedTile(SudokuTile tile) {
        lastClickedTile = tile;
    }

    /**
     * Gets the value of the tile's clickedProperty
     * @return the value of the tile's clickedProperty
     */
    public boolean getClicked() {
        return clickedProperty.get();
    }

    /**
     * Sets the value of the tile's clickedProperty
     * @param clicked the new value for the tile's clickedProperty
     */
    public void setClicked(boolean clicked) {
        clickedProperty.set(clicked);
    }

    /**
     * Gets a reference to the tile's clickedProperty
     * @return the tile's clickedProperty itself
     */
    public SimpleBooleanProperty clickedProperty() {
        return clickedProperty;
    }

    /**
     * Gets the value of the tile's relevantProperty
     * @return the value of the tile's relevantProperty
     */
    public boolean getRelevant() {
        return relevantProperty.get();
    }

    /**
     * Sets the value of the tile's relevantProperty
     * @param relevant the new value for the tile's relevantProperty
     */
    public void setRelevant(boolean relevant) {
        relevantProperty.set(relevant);
    }

    /**
     * Gets a reference to the tile's relevantProperty
     * @return the tile's relevantProperty itself
     */
    public SimpleBooleanProperty relevantProperty() {
        return relevantProperty;
    }

    /**
     * Gets the value of the tile's hoveredProperty
     * @return the value of the tile's hoveredProperty
     */
    public boolean getHovered() {
        return hoveredProperty.get();
    }

    /**
     * Sets the value of the tile's hoveredProperty
     * @param hovered the new value for the tile's hoveredProperty
     */
    public void setHovered(boolean hovered) {
        hoveredProperty.set(hovered);
    }

    /**
     * Gets a reference to the tile's hoveredProperty
     * @return the tile's hoveredProperty itself
     */
    public SimpleBooleanProperty hoveredProperty() {
        return hoveredProperty;
    }

    public void setRelevantTiles() {
        for (SudokuTile[] row : tileGrid) {
            for (SudokuTile tile : row) {
                if (tile != this && (tile.getRowIndex() == this.getRowIndex() || tile.getColumnIndex() == this.getColumnIndex())) {
                    tile.setRelevant(true);
                }
            }
        }
    }

    public void unsetRelevantTiles() {
        for (SudokuTile[] row : tileGrid) {
            for (SudokuTile tile : row) {
                if (tile != this && (tile.getRowIndex() == this.getRowIndex() || tile.getColumnIndex() == this.getColumnIndex())) {
                    tile.setRelevant(false);
                }
            }
        }
    }
}

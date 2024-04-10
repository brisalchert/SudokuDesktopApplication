package Sudoku.UserInterface;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

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
    private SimpleObjectProperty<Integer> valueProperty = new SimpleObjectProperty<>();
    private StringBuilder candidates = new StringBuilder();
    private boolean clicked = false;
    private StackPane sudokuTile;
    private Rectangle tile;
    private Text tileText;
    private Color lastColor;
    private Color tileNeutralColor = Color.rgb(0, 0, 0, 0.0);
    private Color tileRelevantColor = Color.rgb(0, 0, 0, 0.1);
    private Color tileHoveredColor = Color.rgb(0, 0, 0, 0.25);
    private Color tileClickedColor = Color.rgb(0, 0, 0, 0.4);

    // TODO: Separate MVC elements of SudokuTile class

    public SudokuTile(int row, int column) {
        coordinates = new Coordinates(row, column);
        tile = new Rectangle();
        tileText = new Text();
        sudokuTile = new StackPane(tileText, tile);
        Font tileFont = new Font("Century", 30);
        tileText.setFont(tileFont);

        // Add the tile to the global tileGrid
        tileGrid[getRowIndex()][getColumnIndex()] = this;

        tile.setWidth(PuzzleView.getTileSize());
        tile.setHeight(PuzzleView.getTileSize());
        tile.setFill(tileNeutralColor);

        tile.setOnMouseEntered(mouseEvent -> {
            if (!clicked) {
                this.lastColor = (Color) tile.getFill();
                tile.setFill(tileHoveredColor);
            }
        });

        tile.setOnMouseExited(mouseEvent -> {
            if (!clicked) {
                tile.setFill(lastColor);
            }
        });

        tile.setOnMouseClicked(mouseEvent -> {
            // Unselect the tile if it is clicked again
            if (getLastClickedTile() == this) {
                unselectTile();
            }
            else {
                // Update clicked status of the last clicked tile
                if (getLastClickedTile() != null) {
                    unselectTile();
                }

                // Update clicked status of current clicked tile
                lastClickedTile = this;
                this.setTileColor(tileClickedColor);
                this.clicked = true;
                this.showRelevantTiles();
            }
        });
    }

    public static SudokuTile[][] getTileGrid() {
        return tileGrid;
    }

    /**
     * Gets the JavaFX node for the SudokuTile
     * @return The SudokuTile's JavaFX node
     */
    public Node getTileNode() {
        return sudokuTile;
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

    private void setTileColor(Color tileColor) {
        tile.setFill(tileColor);
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
        return getValue() == null;
    }

    public Integer getValue() {
        return valueProperty.get();
    }

    /**
     * Sets the value associated with the SudokuTile and displays it if it is not null
     * @param value the value to set for the SudokuTile
     */
    public void setValue(Integer value) {
        // Do not accept a value not in the range 1-9 unless it is null
        if (value == null || (value >= 1 && value <= 9)) {
            this.valueProperty.set(value);

            // Do not display value of null
            if (value == null) {
                tileText.setText("");
            }
            else {
                tileText.setText(String.valueOf(value));
            }
        }
    }

    public SimpleObjectProperty<Integer> ValueProperty() {
        return valueProperty;
    }

    /**
     * Gets the SudokuTile object for the tile that was last clicked by the user
     * @return the last clicked SudokuTile
     */
    public static SudokuTile getLastClickedTile() {
        // Check that the last clicked tile is still selected
        if (lastClickedTile != null) {
            return lastClickedTile;
        }

        return null;
    }

    /**
     * Sets lastClickedTile to the specified tile
     * @param tile the tile to set lastClickedTile to
     */
    public static void setLastClickedTile(SudokuTile tile) {
        lastClickedTile = tile;
    }

    /**
     * Unselects the last clicked tile
     */
    public void unselectTile() {
        lastClickedTile.setTileColor(tileNeutralColor);
        lastClickedTile.clicked = false;
        lastClickedTile.hideRelevantTiles();
        lastClickedTile = null;
    }

    private void showRelevantTiles() {
        for (SudokuTile[] row : tileGrid) {
            for (SudokuTile tile : row) {
                if (tile != this && (tile.getRowIndex() == this.getRowIndex() || tile.getColumnIndex() == this.getColumnIndex())) {
                    tile.setTileColor(tileRelevantColor);
                }
            }
        }
    }

    private void hideRelevantTiles() {
        for (SudokuTile[] row : tileGrid) {
            for (SudokuTile tile : row) {
                if (tile != this && (tile.getRowIndex() == this.getRowIndex() || tile.getColumnIndex() == this.getColumnIndex())) {
                    tile.setTileColor(tileNeutralColor);
                }
            }
        }
    }
}

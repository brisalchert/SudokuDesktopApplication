package Sudoku.UserInterface;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SudokuTile {
    // Used for accessing tiles by index
    private static final SudokuTile[][] tileGrid = new SudokuTile[9][9];
    private static SudokuTile lastClickedTile;
    // X corresponds to column index, Y corresponds to row index
    private final Coordinates coordinates;
    private Integer value;
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

    public SudokuTile(int xIndex, int yIndex) {
        coordinates = new Coordinates(xIndex, yIndex);
        tile = new Rectangle();
        tileText = new Text();
        sudokuTile = new StackPane(tileText, tile);
        Font tileFont = new Font("Century", 30);
        tileText.setFont(tileFont);

        // Add the tile to the global tileGrid
        tileGrid[getXIndex()][getYIndex()] = this;

        tile.setWidth(UserInterface.getTileSize());
        tile.setHeight(UserInterface.getTileSize());
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

    public int getXIndex() {
        return coordinates.x();
    }

    public int getYIndex() {
        return coordinates.y();
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
     * Retrieves a collection of all the SudokuTiles in the same row as the current one
     * @return the list of tiles in the same row
     */
    public List<SudokuTile> getRow() {
        List<SudokuTile> rowList = new ArrayList<>();
        int rowIndex = this.getYIndex();

        // For each column in the tileGrid, add the tile from the corresponding row
        for (int columnIndex = 0; columnIndex < tileGrid.length; columnIndex++) {
            rowList.add(tileGrid[columnIndex][rowIndex]);
        }

        return rowList;
    }

    /**
     * Retrieves a collection of all the SudokuTiles in the same column as the current one
     * @return the list of tiles in the same column
     */
    public List<SudokuTile> getColumn() {
        List<SudokuTile> columnList = new ArrayList<>();
        int columnIndex = this.getXIndex();

        // For each row in the tileGrid, add the tile from the corresponding column
        for (int rowIndex = 0; rowIndex < tileGrid.length; rowIndex++) {
            columnList.add(tileGrid[columnIndex][rowIndex]);
        }

        return columnList;
    }

    /**
     * Retrieves a collection of all the SudokuTiles in the same box as the current one
     * @return the list of tiles in the same box
     */
    public List<SudokuTile> getBox() {
        List<SudokuTile> boxList = new ArrayList<>();
        int boxColumnIndex = (this.getXIndex() / 3);
        int boxRowIndex = (this.getYIndex() / 3);

        for (int rowIndex = (boxRowIndex * 3); rowIndex < ((boxRowIndex + 1) * 3); rowIndex++) {
            for (int columnIndex = (boxColumnIndex * 3); columnIndex < ((boxColumnIndex + 1) * 3); columnIndex++) {
                boxList.add(tileGrid[columnIndex][rowIndex]);
            }
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
        for (int rowIndex = 0; rowIndex < tileGrid.length; rowIndex++) {
            rowsList.add(tileGrid[0][rowIndex].getRow());
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
        for (int columnIndex = 0; columnIndex < tileGrid.length; columnIndex++) {
            columnsList.add(tileGrid[columnIndex][0].getColumn());
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
            for (int columnIndex = 0; columnIndex < tileGrid.length; columnIndex += 3) {
                boxesList.add(tileGrid[columnIndex][rowIndex].getBox());
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
            if (tile.getValue() == value) {
                return true;
            }
        }

        return false;
    }

    public boolean isEmpty() {
        return getValue() == null;
    }

    public Integer getValue() {
        return value;
    }

    /**
     * Sets the value associated with the SudokuTile and displays it if it is not null
     * @param value the value to set for the SudokuTile
     */
    public void setValue(Integer value) {
        // Do not accept a value not in the range 1-9 unless it is null
        if (value == null || (value >= 1 && value <= 9)) {
            this.value = value;

            // Do not display value of null
            if (value == null) {
                tileText.setText("");
            }
            else {
                tileText.setText(String.valueOf(value));
            }
        }
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
     * Unselects the last clicked tile
     */
    public void unselectTile() {
        lastClickedTile.setTileColor(tileNeutralColor);
        lastClickedTile.clicked = false;
        lastClickedTile.hideRelevantTiles();
        lastClickedTile = null;
    }

    private void showRelevantTiles() {
        for (SudokuTile[] column : tileGrid) {
            for (SudokuTile tile : column) {
                if (tile != this && (tile.getXIndex() == this.getXIndex() || tile.getYIndex() == this.getYIndex())) {
                    tile.setTileColor(tileRelevantColor);
                }
            }
        }
    }

    private void hideRelevantTiles() {
        for (SudokuTile[] column : tileGrid) {
            for (SudokuTile tile : column) {
                if (tile != this && (tile.getXIndex() == this.getXIndex() || tile.getYIndex() == this.getYIndex())) {
                    tile.setTileColor(tileNeutralColor);
                }
            }
        }
    }
}

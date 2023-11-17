package Sudoku.UserInterface;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class SudokuTile {
    // Used for accessing tiles by index
    private static final SudokuTile[][] tileGrid = new SudokuTile[9][9];
    private static SudokuTile lastClickedTile;
    private int xIndex;
    private int yIndex;
    private int value;
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
        this.xIndex = xIndex;
        this.yIndex = yIndex;
        tile = new Rectangle();
        tileText = new Text();
        sudokuTile = new StackPane(tileText, tile);
        Font tileFont = new Font("Century", 30);
        tileText.setFont(tileFont);

        // Add the tile to the global tileGrid
        tileGrid[xIndex][yIndex] = this;

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
                hideRelevantTiles();
            }
            else {
                // Update clicked status of the last clicked tile
                if (getLastClickedTile() != null) {
                    SudokuTile lastClickedTile = getLastClickedTile();
                    lastClickedTile.setTileColor(tileNeutralColor);
                    lastClickedTile.clicked = false;
                    lastClickedTile.hideRelevantTiles();
                }

                // Update clicked status of current clicked tile
                lastClickedTile = this;
                this.setTileColor(tileClickedColor);
                this.clicked = true;
                this.showRelevantTiles();
            }
        });
    }

    /**
     * Gets the JavaFX node for the SudokuTile
     * @return The SudokuTile's JavaFX node
     */
    public Node getTileNode() {
        return sudokuTile;
    }

    public int getXIndex() {
        return xIndex;
    }

    public int getYIndex() {
        return yIndex;
    }

    private void setTileColor(Color tileColor) {
        tile.setFill(tileColor);
    }

    public int getValue() {
        return value;
    }

    /**
     * Sets the value associated with the SudokuTile and displays it if it is not 0
     * @param value the value to set for the SudokuTile
     */
    public void setValue(int value) {
        this.value = value;

        if (value == 0) {
            tileText.setText("");
        }
        else {
            tileText.setText(String.valueOf(value));
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
        for (SudokuTile[] row : tileGrid) {
            for (SudokuTile tile : row) {
                if (tile != this && (tile.getXIndex() == this.xIndex || tile.getYIndex() == this.yIndex)) {
                    tile.setTileColor(tileRelevantColor);
                }
            }
        }
    }

    private void hideRelevantTiles() {
        for (SudokuTile[] row : tileGrid) {
            for (SudokuTile tile : row) {
                if (tile != this && (tile.getXIndex() == this.xIndex || tile.getYIndex() == this.yIndex)) {
                    tile.setTileColor(tileNeutralColor);
                }
            }
        }
    }
}

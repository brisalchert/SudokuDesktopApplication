package Sudoku.UserInterface;

import Sudoku.GameLogic.PuzzleGenerator;
import Sudoku.GameLogic.SudokuTile;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class SudokuModel {
    private PuzzleGenerator puzzleGenerator;

    /**
     * Constructor: Creates a new SudokuModel instance
     */
    public SudokuModel() {
    }

    /**
     * Generates a new puzzle with a certain minimum number of clues
     * @param minimumClues the minimum number of clues
     */
    public void generateNewPuzzle(int minimumClues) {
        puzzleGenerator = new PuzzleGenerator(minimumClues);
    }

    /**
     * Returns a boolean corresponding to whether or not a puzzle has been initialized
     * @return true if a puzzle instance exists
     */
    public boolean hasPuzzle() {
        return puzzleGenerator != null;
    }

    // Accessors & Modifiers
    public PuzzleGenerator getPuzzleGenerator() {
        return puzzleGenerator;
    }

    public SudokuTile[][] getTileGrid() {
        return puzzleGenerator.getTileGrid();
    }

    public Coordinates getLastClickedTile() {
        SudokuTile tile = SudokuTile.getLastClickedTile();

        if (tile == null) {
            return null;
        }

        return SudokuTile.getLastClickedTile().getCoordinates();
    }

    public void setLastClickedTile(Coordinates coordinates) {
        SudokuTile tile = null;

        if (coordinates != null) {
            tile = SudokuTile.getTileByCoordinates(coordinates);
        }

        // Unset old relevant tiles if applicable
        if (getLastClickedTile() != null) {
            SudokuTile.getTileByCoordinates(getLastClickedTile()).unsetRelevantTiles();
        }

        // Set the new lastClickedTile
        SudokuTile.setLastClickedTile(tile);

        // Set new relevant tiles if the new lastClickedTile is not null
        if (tile != null) {
            tile.setRelevantTiles();
        }
    }

    public void updateLastClickedTile(Coordinates coordinates) {
        SudokuTile tile = null;

        if (coordinates != null) {
            tile = SudokuTile.getTileByCoordinates(coordinates);
        }

        // If no tile was clicked, unselect the last tile
        if (tile == null) {
            SudokuTile.getTileByCoordinates(getLastClickedTile()).setClicked(false);
            setLastClickedTile(null);

            return;
        }

        // Unselect the tile if it is clicked again
        if (getLastClickedTile() == tile.getCoordinates()) {
            tile.setClicked(false);
            setLastClickedTile(null);

            return;
        }

        // Switch the selected tile if a new tile is clicked
        if (getLastClickedTile() != null) {
            SudokuTile.getTileByCoordinates(getLastClickedTile()).setClicked(false);
        }

        tile.setClicked(true);
        setLastClickedTile(coordinates);
    }

    public SimpleIntegerProperty tileValueProperty(Coordinates coordinates) {
        return SudokuTile.getTileByCoordinates(coordinates).valueProperty();
    }

    public Integer getTileValue(Coordinates coordinates) {
        return SudokuTile.getTileByCoordinates(coordinates).getValue();
    }

    public void setValueLastClickedTile(KeyEvent keyEvent) {
        // Check that there is a tile selected
        if (getLastClickedTile() != null) {
            // Check that the input is valid
            if (keyEvent.getText().matches("[1-9]")) {
                int value = Integer.parseInt(keyEvent.getText());

                // Assign the input to the current tile
                SudokuTile.getTileByCoordinates(getLastClickedTile()).setValue(value);
            }
            else {
                if (keyEvent.getCode() == KeyCode.BACK_SPACE) {
                    SudokuTile.getTileByCoordinates(getLastClickedTile()).setValue(0);
                }
            }
        }
    }

    public void updateFill(Coordinates coordinates) {
        if (getTileClicked(coordinates)) {
            setColorClicked(coordinates);

            return;
        }

        if (getTileHovered(coordinates)) {
            setColorHovered(coordinates);

            return;
        }

        if (getTileRelevant(coordinates)) {
            setColorRelevant(coordinates);

            return;
        }

        setColorNeutral(coordinates);
    }

    public void updateFillAllTiles() {
        for (SudokuTile[] row : getTileGrid()) {
            for (SudokuTile tile : row) {
                updateFill(tile.getCoordinates());
            }
        }
    }

    public SimpleBooleanProperty tileClickedProperty(Coordinates coordinates) {
        return SudokuTile.getTileByCoordinates(coordinates).clickedProperty();
    }

    public boolean getTileClicked(Coordinates coordinates) {
        return SudokuTile.getTileByCoordinates(coordinates).getClicked();
    }

    public void setTileClicked(Coordinates coordinates, boolean clicked) {
        SudokuTile.getTileByCoordinates(coordinates).setClicked(clicked);
    }

    public SimpleBooleanProperty tileRelevantProperty(Coordinates coordinates) {
        return SudokuTile.getTileByCoordinates(coordinates).relevantProperty();
    }

    public boolean getTileRelevant(Coordinates coordinates) {
        return SudokuTile.getTileByCoordinates(coordinates).getRelevant();
    }

    public void setTileRelevant(Coordinates coordinates, boolean relevant) {
        SudokuTile.getTileByCoordinates(coordinates).setRelevant(relevant);
    }

    public boolean getTileHovered(Coordinates coordinates) {
        return SudokuTile.getTileByCoordinates(coordinates).getHovered();
    }

    public void setTileHovered(Coordinates coordinates, boolean hovered) {
        SudokuTile.getTileByCoordinates(coordinates).setHovered(hovered);
    }

    public SimpleBooleanProperty tileHoveredProperty(Coordinates coordinates) {
        return SudokuTile.getTileByCoordinates(coordinates).hoveredProperty();
    }

    public Color getTileColor(Coordinates coordinates) {
        return SudokuTile.getTileByCoordinates(coordinates).getColor();
    }

    public void setColorNeutral(Coordinates coordinates) {
        SudokuTile.getTileByCoordinates(coordinates).setColorNeutral();
    }

    public void setColorRelevant(Coordinates coordinates) {
        SudokuTile.getTileByCoordinates(coordinates).setColorRelevant();
    }

    public void setColorHovered(Coordinates coordinates) {
        SudokuTile.getTileByCoordinates(coordinates).setColorHovered();
    }

    public void setColorClicked(Coordinates coordinates) {
        SudokuTile.getTileByCoordinates(coordinates).setColorClicked();
    }

    public ObjectProperty<Color> tileColorProperty(Coordinates coordinates) {
        return SudokuTile.getTileByCoordinates(coordinates).colorProperty();
    }
}

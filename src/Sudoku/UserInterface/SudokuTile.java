package Sudoku.UserInterface;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SudokuTile {
    private int xIndex;
    private int yIndex;
    private Rectangle tile;
    private Color tileUnfocusedColor = Color.rgb(0, 0, 0, 0.0);
    private Color tileFocusedColor = Color.rgb(0, 0, 0, 0.2);

    public SudokuTile(int xIndex, int yIndex) {
        this.xIndex = xIndex;
        this.yIndex = yIndex;
        tile = new Rectangle();
        tile.setWidth(UserInterface.getTileSize());
        tile.setHeight(UserInterface.getTileSize());
        tile.setFill(tileUnfocusedColor);
        tile.setOnMouseEntered(mouseEvent -> tile.setFill(tileFocusedColor));
        tile.setOnMouseExited(mouseEvent -> tile.setFill(tileUnfocusedColor));
    }

    public Node getTileNode() {
        return tile;
    }

    public int getXIndex() {
        return xIndex;
    }

    public int getYIndex() {
        return yIndex;
    }
}

package Sudoku.UserInterface;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SudokuTile {
    private double x;
    private double y;
    private Rectangle tile;
    private Color tileUnfocusedColor = Color.rgb(0, 0, 0, 0.0);
    private Color tileFocusedColor = Color.rgb(0, 0, 0, 0.2);

    public SudokuTile(double x, double y) {
        this.x = x;
        this.y = y;
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

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}

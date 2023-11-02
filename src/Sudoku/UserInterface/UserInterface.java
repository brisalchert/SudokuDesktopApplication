package Sudoku.UserInterface;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class UserInterface {
    private final Stage stage;
    private final Group root;
    private final int BOARD_WIDTH_AND_HEIGHT = 576;
    private final int WINDOW_WIDTH = 676;
    private final int WINDOW_HEIGHT = 776;
    private final Color WINDOW_BACKGROUND_COLOR = Color.rgb(168, 136, 98, 1);
    private final Color TILE_BACKGROUND_COLOR = Color.WHEAT;

    public UserInterface(Stage stage) {
        this.stage = stage;
        this.root = new Group();
        initializeUserInterface();
    }

    private void initializeUserInterface() {
        VBox sudokuElements = new VBox();
        sudokuElements.setAlignment(Pos.CENTER);

        drawBackground(root);
        drawTitle(sudokuElements);
        drawSudokuBoard(sudokuElements);
        drawGridLines(sudokuElements);

        root.getChildren().add(sudokuElements);
        stage.show();
    }

    private void drawBackground(Group root) {
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setFill(WINDOW_BACKGROUND_COLOR);
        stage.setTitle("Sudoku");
        stage.setScene(scene);
    }

    private void drawTitle(Pane root) {
        HBox titleBox = new HBox();
        Text title = new Text("Sudoku");
        Font titleFont = new Font("Century", 50);
        title.setFont(titleFont);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().add(title);
        titleBox.setPadding(new Insets(20));
        titleBox.setPrefWidth(WINDOW_WIDTH);

        root.getChildren().add(titleBox);
    }

    private void drawSudokuBoard(Pane root) {
        GridPane boxGrid = new GridPane(100, 100);
        int boxGridWidthAndHeight = 3;

        boxGrid.setAlignment(Pos.CENTER);

        // Set spacing between boxes to 3 pixels
        boxGrid.setHgap(3);
        boxGrid.setVgap(3);

        // Create a 3x3 grid of boxes
        for (int row = 0; row < boxGridWidthAndHeight; row++) {
            for (int column = 0; column < boxGridWidthAndHeight; column++) {
                drawSudokuBox(boxGrid, row, column);
            }
        }

        // Add the boxGrid to the board
        root.getChildren().add(boxGrid);
    }

    private void drawGridLines(Pane root) {

    }

    // Indices will be used for alternating tile colors
    private void drawSudokuBox(GridPane root, int xIndex, int yIndex) {
        GridPane box = new GridPane();
        int boxWidthAndHeight = 3;

        // Set spacing between tiles to 2 pixels
        box.setHgap(2);
        box.setVgap(2);

        // Create one 3x3 box of 9 tiles
        for (int row = 0; row < boxWidthAndHeight; row++) {
            for (int column = 0; column < boxWidthAndHeight; column++) {
                Rectangle tile = new Rectangle(64, 64);
                tile.setFill(TILE_BACKGROUND_COLOR);
                box.add(tile, row, column);
            }
        }

        // Add the box to the boxGrid
        root.add(box, xIndex, yIndex);
    }
}

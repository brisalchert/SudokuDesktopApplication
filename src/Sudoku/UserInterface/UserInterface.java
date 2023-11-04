package Sudoku.UserInterface;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class UserInterface {
    private final Stage stage;
    private final Group root;
    private final int TILE_WIDTH_AND_HEIGHT = 64;
    private final int TILE_SPACING = 2;
    private final int BOX_SPACING = 4;
    private final int BOARD_WIDTH_AND_HEIGHT = (9 * TILE_WIDTH_AND_HEIGHT) + (6 * TILE_SPACING) + (2 * BOX_SPACING);
    private final int WINDOW_WIDTH = 694;
    private final int WINDOW_HEIGHT = 794;
    private final Color WINDOW_BACKGROUND_COLOR = Color.rgb(168, 136, 98, 1);
    private final Color TILE_BACKGROUND_COLOR = Color.rgb(245, 222, 179, 0.7);
    private final Color TILE_BORDER_COLOR = Color.rgb(30, 30, 30, 0.7);

    public UserInterface(Stage stage) {
        this.stage = stage;
        this.root = new Group();
        initializeUserInterface();
    }

    private void initializeUserInterface() {
        // Create VBox to vertically stack elements
        VBox sudokuElements = new VBox();
        sudokuElements.setAlignment(Pos.CENTER);

        drawBackground(root);
        drawTitle(sudokuElements);
        drawSudokuBoard(sudokuElements);

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
        StackPane board = new StackPane();
        GridPane boxGrid = new GridPane();
        int boxGridWidthAndHeight = 3;

        boxGrid.setAlignment(Pos.CENTER);

        // Set spacing between boxes
        boxGrid.setHgap(BOX_SPACING);
        boxGrid.setVgap(BOX_SPACING);

        // Create a 3x3 grid of boxes
        for (int row = 0; row < boxGridWidthAndHeight; row++) {
            for (int column = 0; column < boxGridWidthAndHeight; column++) {
                drawSudokuBox(boxGrid, row, column);
            }
        }

        // Add the boxGrid to the board
        board.getChildren().add(boxGrid);

        // Draw the grid lines
        drawGridLines(board);

        root.getChildren().add(board);
    }

    // Indices will be used for alternating tile colors
    private void drawSudokuBox(GridPane root, int xIndex, int yIndex) {
        GridPane box = new GridPane();
        int boxWidthAndHeight = 3;

        // Set spacing between tiles
        box.setHgap(TILE_SPACING);
        box.setVgap(TILE_SPACING);

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

    private void drawGridLines(Pane board) {
        drawHorizontalGridLines(board);
        drawVerticalGridLines(board);
    }

    private void drawHorizontalGridLines(Pane board) {
        VBox horizontalGridLines = new VBox();

        horizontalGridLines.setAlignment(Pos.CENTER);
        horizontalGridLines.setSpacing(TILE_WIDTH_AND_HEIGHT);

        // Draw the horizontal grid lines
        drawLines(horizontalGridLines, false);

        // Add the horizontal grid lines to the board
        board.getChildren().add(horizontalGridLines);
    }

    private void drawVerticalGridLines(Pane board) {
        HBox verticalGridLines = new HBox();

        verticalGridLines.setAlignment(Pos.CENTER);
        verticalGridLines.setSpacing(TILE_WIDTH_AND_HEIGHT);

        // Draw the vertical grid lines
        drawLines(verticalGridLines, true);

        // Add the horizontal grid lines to the board
        board.getChildren().add(verticalGridLines);
    }

    private void drawLines(Pane gridLines, boolean isVertical) {
        int totalGridLines = 10;

        // Draw the grid lines
        for (int index = 0; index < totalGridLines; index++) {
            int thickness;

            // Set larger thickness and different color for lines between boxes and on the edges
            if (index == 0 || index == 3 || index == 6 || index == 9) {
                thickness = BOX_SPACING;
            }
            else {
                thickness = TILE_SPACING;
            }

            Rectangle gridLine = new Rectangle();
            gridLine.setFill(TILE_BORDER_COLOR);

            // Set rectangle dimensions for proper orientation
            if (isVertical) {
                // Set larger height for edge lines to include corners
                if (index == 0 || index == 9) {
                    gridLine.setHeight(BOARD_WIDTH_AND_HEIGHT + 8);
                }
                else {
                    gridLine.setHeight(BOARD_WIDTH_AND_HEIGHT);
                }

                gridLine.setWidth(thickness);
            }
            else {
                gridLine.setHeight(thickness);
                gridLine.setWidth(BOARD_WIDTH_AND_HEIGHT);
            }

            gridLines.getChildren().add(gridLine);

            // Set lower z-index for tile lines so that they don't overlap the box lines
            if (index != 0 && index != 3 && index != 6 && index != 9) {
                gridLine.setViewOrder(10);
            }
        }
    }
}

package Sudoku.UserInterface;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class UserInterface {
    private final Stage stage;
    private final BorderPane root;
    private final static int TILE_WIDTH_AND_HEIGHT = 64;
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
        this.root = new BorderPane();
        initializeUserInterface();
    }

    public static int getTileSize() {
        return TILE_WIDTH_AND_HEIGHT;
    }

    private void initializeUserInterface() {
        drawBackground(root);
        drawTitle(root);
        drawSudokuBoard(root);

        stage.show();
    }

    private void drawBackground(BorderPane root) {
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setFill(WINDOW_BACKGROUND_COLOR);

        // Add event filters to the scene for key events and mouse clicks
        scene.addEventFilter(KeyEvent.KEY_PRESSED, createKeyEventHandler());
        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, createMouseEventHandler());

        stage.setTitle("Sudoku");
        stage.setScene(scene);
    }

    private void drawTitle(BorderPane root) {
        HBox titleBox = new HBox();
        Text title = new Text("Sudoku");
        Font titleFont = new Font("Century", 50);
        title.setFont(titleFont);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().add(title);
        titleBox.setPadding(new Insets(20));
        titleBox.setPrefWidth(WINDOW_WIDTH);

        root.setTop(titleBox);
    }

    private void drawSudokuBoard(BorderPane root) {
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
                drawSudokuBox(boxGrid, column, row);
            }
        }

        // Draw the grid lines
        drawGridLines(board);

        // Add the boxGrid to the board
        board.getChildren().add(boxGrid);

        root.setCenter(board);
    }

    // Indices will be used for alternating tile colors
    private void drawSudokuBox(GridPane boxGrid, int xIndex, int yIndex) {
        GridPane box = new GridPane();
        int boxWidthAndHeight = 3;
        int gridRow = (3 * yIndex);
        int gridColumn;

        // Set spacing between tiles
        box.setHgap(TILE_SPACING);
        box.setVgap(TILE_SPACING);

        // Create one 3x3 box of 9 tiles
        for (int boxRow = 0; boxRow < boxWidthAndHeight; boxRow++) {
            gridColumn = (3*xIndex);

            for (int boxColumn = 0; boxColumn < boxWidthAndHeight; boxColumn++) {
                Rectangle tileBackground = new Rectangle(64, 64);
                tileBackground.setFill(TILE_BACKGROUND_COLOR);
                box.add(tileBackground, boxColumn, boxRow);

                // Assign tile coordinates relative to the entire grid
                SudokuTile tile = new SudokuTile(gridColumn, gridRow);
                box.add(tile.getTileNode(), boxColumn, boxRow);

                gridColumn++;
            }

            gridRow++;
        }

        // Add the box to the boxGrid
        boxGrid.add(box, xIndex, yIndex);
    }

    private void drawGridLines(StackPane board) {
        VBox horizontalGridLines = createHorizontalGridLines();
        HBox verticalGridLines = createVerticalGridLines();

        StackPane gridLineStack = new StackPane(horizontalGridLines, verticalGridLines);
        board.getChildren().add(gridLineStack);
    }

    private VBox createHorizontalGridLines() {
        VBox horizontalGridLines = new VBox();

        horizontalGridLines.setAlignment(Pos.CENTER);
        horizontalGridLines.setSpacing(TILE_WIDTH_AND_HEIGHT);

        // Draw the horizontal grid lines
        drawLines(horizontalGridLines, false);

        return horizontalGridLines;
    }

    private HBox createVerticalGridLines() {
        HBox verticalGridLines = new HBox();

        verticalGridLines.setAlignment(Pos.CENTER);
        verticalGridLines.setSpacing(TILE_WIDTH_AND_HEIGHT);

        // Draw the vertical grid lines
        drawLines(verticalGridLines, true);

        return verticalGridLines;
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


    public EventHandler<KeyEvent> createKeyEventHandler() {
        EventHandler<KeyEvent> eventHandler = keyEvent -> {
            // Check if there is a tile selected
            if (SudokuTile.getLastClickedTile() != null) {
                if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                    // Check that the input is valid
                    if (keyEvent.getText().matches("[1-9]")) {
                        int value = Integer.parseInt(keyEvent.getText());

                        // Assign the input to the current tile
                        SudokuTile.getLastClickedTile().setValue(value);
                    }
                    else {
                        if (keyEvent.getCode() == KeyCode.BACK_SPACE) {
                            SudokuTile.getLastClickedTile().setValue(0);
                        }
                    }
                }
            }

            keyEvent.consume();
        };

        return eventHandler;
    }

    public EventHandler<MouseEvent> createMouseEventHandler() {
        EventHandler<MouseEvent> eventHandler = mouseEvent -> {
            Object target = mouseEvent.getTarget();

            // Only unselect the last clicked tile if the click is not on a tile
            if (!(target instanceof Rectangle)) {
                if (SudokuTile.getLastClickedTile() != null) {
                    SudokuTile.getLastClickedTile().unselectTile();
                }
            }
        };

        return eventHandler;
    }
}

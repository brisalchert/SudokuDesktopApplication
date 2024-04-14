package Sudoku.UserInterface;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class PuzzleView {
    private final SudokuModel sudokuModel;
    private final PuzzleController puzzleController;
    private final AnchorPane puzzleRoot;
    private final Scene puzzleScene;
    private final static int TILE_WIDTH_AND_HEIGHT = 64;
    private final int TILE_SPACING = 2;
    private final int BOX_SPACING = 4;
    private final int BOARD_WIDTH_AND_HEIGHT = (9 * TILE_WIDTH_AND_HEIGHT) + (6 * TILE_SPACING) + (2 * BOX_SPACING);
    private final int WINDOW_WIDTH = 694;
    private final int WINDOW_HEIGHT = 794;
    private final Color TILE_BACKGROUND_COLOR = Color.rgb(245, 222, 179, 0.7);
    private final Color TILE_BORDER_COLOR = Color.rgb(30, 30, 30, 0.7);

    public PuzzleView(SudokuModel sudokuModel, PuzzleController puzzleController) {
        this.sudokuModel = sudokuModel;
        this.puzzleController = puzzleController;
        this.puzzleRoot = new AnchorPane();
        this.puzzleScene = new Scene(puzzleRoot, WINDOW_WIDTH, WINDOW_HEIGHT);
        puzzleScene.getStylesheets().add(PuzzleView.class.getResource("style.css").toExternalForm());
        initializePuzzleInterface();
    }

    public Scene getPuzzleScene() {
        return puzzleScene;
    }

    public static int getTileSize() {
        return TILE_WIDTH_AND_HEIGHT;
    }

    private void initializePuzzleInterface() {
        initializePuzzlePane(puzzleRoot);
    }

    private void initializePuzzlePane(AnchorPane puzzleRoot) {
        // Create a BorderPane for the board elements
        BorderPane puzzlePane = new BorderPane();
        puzzlePane.setId("puzzlePane");

        // Add the puzzlePane to the root
        puzzleRoot.getChildren().add(puzzlePane);
        puzzlePane.prefWidthProperty().bind(puzzleScene.widthProperty());
        puzzlePane.prefHeightProperty().bind(puzzleScene.heightProperty());

        // Call interface initialization functions
        drawTitle(puzzlePane);
        drawSudokuBoard(puzzlePane);
    }

    private void drawTitle(BorderPane puzzleRoot) {
        HBox titleBox = new HBox();
        Text title = new Text("Sudoku");
        Font titleFont = new Font("Century", 50);
        title.setFont(titleFont);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().add(title);
        titleBox.setPadding(new Insets(20));
        titleBox.setPrefWidth(WINDOW_WIDTH);

        puzzleRoot.setTop(titleBox);
    }

    private void drawSudokuBoard(BorderPane puzzleRoot) {
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

        // Draw the grid lines
        drawGridLines(board);

        // Add the boxGrid to the board
        board.getChildren().add(boxGrid);

        puzzleRoot.setCenter(board);
    }

    // Indices will be used for alternating tile colors
    private void drawSudokuBox(GridPane boxGrid, int rowIndex, int columnIndex) {
        GridPane box = new GridPane();
        int boxWidthAndHeight = 3;
        int gridRow = (3 * rowIndex);
        int gridColumn;

        // Set spacing between tiles
        box.setHgap(TILE_SPACING);
        box.setVgap(TILE_SPACING);

        // Create one 3x3 box of 9 tiles
        for (int boxRow = 0; boxRow < boxWidthAndHeight; boxRow++) {
            gridColumn = (3*columnIndex);

            for (int boxColumn = 0; boxColumn < boxWidthAndHeight; boxColumn++) {
                // Create a coordinates object for the grid coordinates of the tile
                Coordinates tileCoordinates = new Coordinates(gridRow, gridColumn);
                drawSudokuTile(box, tileCoordinates, boxRow, boxColumn);

                gridColumn++;
            }

            gridRow++;
        }

        // Add the box to the boxGrid (GridPane.add() uses column first)
        boxGrid.add(box, columnIndex, rowIndex);
    }

    private void drawSudokuTile(GridPane box, Coordinates coordinates, int boxRow, int boxColumn) {
        StackPane tilePane;

        // Draw the tile background
        Rectangle tileBackground = new Rectangle(TILE_WIDTH_AND_HEIGHT, TILE_WIDTH_AND_HEIGHT);
        tileBackground.setFill(TILE_BACKGROUND_COLOR);
        box.add(tileBackground, boxRow, boxColumn);

        // Draw the tileTint Rectangle (used for shading the tile)
        Rectangle tileTint = new Rectangle(TILE_WIDTH_AND_HEIGHT, TILE_WIDTH_AND_HEIGHT);

        // Set userData for the tileTint so that it can be looked up by its coordinates
        tileTint.setUserData(coordinates);

        // Add a listener that updates the tile's hovered property on hover
        tileTint.hoverProperty().addListener((observable, oldHover, newHover) ->
                puzzleController.updateTileHovered(coordinates, newHover));

        // Add a listener that updates the tileTint on hover
        tileTint.hoverProperty().addListener((observable, oldValue, newValue) ->
                puzzleController.updateTileFill(coordinates));

        // Bind the tile's fillProperty to the color from the model
        puzzleController.bindTileFill(tileTint, coordinates);

        // Add tile text
        Text tileText = new Text();
        Font tileFont = new Font("Century", 30);
        tileText.setFont(tileFont);

        // Add a listener that sets tile text to visible only if the value is 1-9
        tileText.textProperty().addListener((observable, oldText, newText) ->
                puzzleController.updateTileText(tileText, newText));

        // Bind the tile's textProperty to the actual value from the model
        puzzleController.bindTileText(tileText, coordinates);

        // Add background, text, and the tileTint to the tilePane
        tilePane = new StackPane(tileBackground, tileText, tileTint);

        // Add the tilePane to the box (GridPane.add() uses column first
        box.add(tilePane, boxColumn, boxRow);
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

    /**
     * Gets the Coordinates of a tileTint Rectangle relative to the tileGrid
     * @param tileTint the tileTint in the GridPane tileGrid
     * @return a Coordinates object with the coordinates of the tileTint, or null if the rectangle is not part of the
     * tileGrid
     */
    public Coordinates getCoordinatesByTileTint(Rectangle tileTint) {
        // Ensure that the tileTint is part of the tileGrid
        if (tileTint.getUserData() != null) {
            return (Coordinates) tileTint.getUserData();
        }

        return null;
    }

    /**
     * Gets the Rectangle tileTint for a tile in the view from a given set of Coordinates
     * @param coordinates the coordinates of the tileTint
     * @return the Rectangle tileTint
     */
    public Rectangle getTileTintByCoordinates(Coordinates coordinates) {
        // Get a reference to the tileTint at the given coordinates
        return (Rectangle) DFSNodeByUserData(coordinates, puzzleRoot);
    }

    /**
     * Support function for getTileTintByCoordinates that performs a DFS on the scene graph, searching for the node
     * whose userData matches the coordinates of the tileTint
     * @param userData the userdata stored in the target node
     * @param node the current node being searched
     * @return the target node, or null if it could not be found
     */
    private Node DFSNodeByUserData(Object userData, Node node) {
        if (node.getUserData() != null) {
            if (node.getUserData().equals(userData)) {
                return node;
            }
        }

        if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                Node result = DFSNodeByUserData(userData, child);

                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    public void setKeyEventHandler(EventHandler<KeyEvent> keyEventHandler) {
        puzzleScene.addEventFilter(KeyEvent.KEY_PRESSED, keyEventHandler);
    }

    public void setMouseEventHandler(EventHandler<MouseEvent> mouseEventHandler) {
        puzzleScene.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEventHandler);
    }
}

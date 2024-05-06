package Sudoku.UserInterface;

import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class PuzzleView {
    private final SudokuModel sudokuModel;
    private final PuzzleController puzzleController;
    private final AnchorPane puzzleRoot;
    private final Scene puzzleScene;
    private final String boardFontName = "Lucida Bright";
    private final static int TILE_WIDTH_AND_HEIGHT = 48;
    private final int TILE_SPACING = 2;
    private final int BOX_SPACING = 3;
    private final int BOARD_WIDTH_AND_HEIGHT = (9 * TILE_WIDTH_AND_HEIGHT) + (6 * TILE_SPACING) + (2 * BOX_SPACING);
    private final int STARTING_WINDOW_WIDTH = BOARD_WIDTH_AND_HEIGHT + 100;
    private final int STARTING_WINDOW_HEIGHT = BOARD_WIDTH_AND_HEIGHT + 200;
    private final Color TILE_BACKGROUND_COLOR = Color.rgb(245, 222, 179, 0.7);
    private final Color TILE_BORDER_COLOR = Color.rgb(30, 30, 30, 0.7);

    public PuzzleView(SudokuModel sudokuModel, PuzzleController puzzleController) {
        this.sudokuModel = sudokuModel;
        this.puzzleController = puzzleController;
        this.puzzleRoot = new AnchorPane();
        this.puzzleScene = new Scene(puzzleRoot);
        puzzleScene.getStylesheets().add(PuzzleView.class.getResource("style.css").toExternalForm());
        initializePuzzleInterface();
    }

    public Scene getPuzzleScene() {
        return puzzleScene;
    }

    public static int getTileSize() {
        return TILE_WIDTH_AND_HEIGHT;
    }

    public int getStartingWindowWidth() {
        return STARTING_WINDOW_WIDTH;
    }

    public int getStartingWindowHeight() {
        return STARTING_WINDOW_HEIGHT;
    }

    private void initializePuzzleInterface() {
        initializePuzzlePane(puzzleRoot);
        initializeSideBarMenu(puzzleRoot);
    }

    private void initializePuzzlePane(AnchorPane puzzleRoot) {
        // Create a BorderPane for the board elements
        BorderPane puzzlePane = new BorderPane();
        puzzlePane.setId("puzzle-pane");

        // Add the puzzlePane to the root
        puzzleRoot.getChildren().add(puzzlePane);
        puzzlePane.prefWidthProperty().bind(puzzleScene.widthProperty());
        puzzlePane.prefHeightProperty().bind(puzzleScene.heightProperty());

        // Call interface initialization functions
        drawTitle(puzzlePane);
        drawSudokuBoard(puzzlePane);
    }

    private void initializeSideBarMenu(AnchorPane puzzleRoot) {
        // Create a Pane to shade the puzzle when the menu is active
        Pane shadowPane = new Pane();
        shadowPane.setId("shadow-pane");
        puzzleRoot.getChildren().add(shadowPane);
        shadowPane.prefWidthProperty().bind(puzzleScene.widthProperty());
        shadowPane.prefHeightProperty().bind(puzzleScene.heightProperty());
        shadowPane.setVisible(false);

        // Create a Pane for the sidebar menu
        Pane sidebarPane = new AnchorPane();
        sidebarPane.setId("sidebar-pane");
        sidebarPane.prefWidthProperty().bind(puzzleScene.widthProperty().divide(3));
        sidebarPane.prefHeightProperty().bind(puzzleScene.heightProperty());

        // Create a button to access the sidebar menu
        Button sidebarButton = new Button("Menu");
        sidebarButton.setPrefWidth(100);
        sidebarButton.setPrefHeight(50);
        sidebarButton.setId("sidebar-button");

        // Create a main menu button in the sidebar menu
        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.prefWidthProperty().bind(sidebarPane.prefWidthProperty().divide(3).multiply(2));
        mainMenuButton.setPrefHeight(50);
        mainMenuButton.setId("main-menu-button");

        // Create a VBox for sidebar menu options
        VBox sidebarVBox = new VBox();
        sidebarVBox.getChildren().add(mainMenuButton);
        sidebarVBox.translateXProperty().bind(sidebarPane.prefWidthProperty().divide(6));
        sidebarVBox.translateYProperty().bind(sidebarPane.prefHeightProperty().divide(10));

        // Add the VBox to an HBox in the sidebarPane
        sidebarPane.getChildren().add(sidebarVBox);

        // Add the button and menu to a group
        Group sidebarGroup = new Group(sidebarButton, sidebarPane);
        puzzleRoot.getChildren().add(sidebarGroup);

        // Set the menu to sit to the right of the button
        sidebarPane.setTranslateX(sidebarButton.getPrefWidth());

        // Set the sidebar group to sit on the right side of the scene, hiding the menu
        sidebarGroup.setTranslateX(puzzleScene.getWidth() - sidebarButton.getPrefWidth());

        // Add listener for moving the sidebar group with window size changes
        puzzleScene.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (sidebarGroup.getTranslateX() == ((double) oldValue - sidebarButton.getPrefWidth())) {
                sidebarGroup.setTranslateX(puzzleScene.getWidth() - sidebarButton.getPrefWidth());
            }
            else {
                sidebarGroup.setTranslateX(puzzleScene.getWidth() - sidebarButton.getPrefWidth() - sidebarPane.getPrefWidth());
            }
        });

        // Define animations for the menu
        TranslateTransition openSidebarMenu = new TranslateTransition(Duration.millis(200), sidebarGroup);
        TranslateTransition closeSidebarMenu = new TranslateTransition(Duration.millis(200), sidebarGroup);

        // Define logic for the sidebar button
        sidebarButton.setOnAction(actionEvent -> {
            if (sidebarGroup.getTranslateX() == (puzzleScene.getWidth() - sidebarButton.getPrefWidth())) {
                openSidebarMenu.setByX(-sidebarPane.getPrefWidth());
                openSidebarMenu.play();
                shadowPane.setVisible(true);
                sidebarButton.setText("Close");
            }
            else {
                closeSidebarMenu.setByX(sidebarPane.getPrefWidth());
                closeSidebarMenu.play();
                shadowPane.setVisible(false);
                sidebarButton.setText("Menu");
            }
        });

        // Define logic for the main menu button
        puzzleController.initMainMenuButton(mainMenuButton);

        // Define logic for clicking the shadowPane
        shadowPane.setOnMouseClicked(actionEvent -> {
            closeSidebarMenu.setByX(sidebarPane.getPrefWidth());
            closeSidebarMenu.play();
            shadowPane.setVisible(false);
            sidebarButton.setText("Menu");
        });
    }

    private void drawTitle(BorderPane puzzleRoot) {
        HBox titleBox = new HBox();
        Text title = new Text("Sudoku");
        Font titleFont = new Font(boardFontName, 50);
        title.setFont(titleFont);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().add(title);
        titleBox.setPadding(new Insets(20));
        titleBox.setPrefWidth(STARTING_WINDOW_WIDTH);

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

        // Add listener to scale tile size with window width
        puzzleScene.widthProperty().addListener((observableValue, oldValue, newValue) -> {
            tileBackground.setWidth(getMinTileDimension());
            tileBackground.setHeight(getMinTileDimension());
            getTileTintByCoordinates(coordinates).setWidth(getMinTileDimension());
            getTileTintByCoordinates(coordinates).setHeight(getMinTileDimension());
        });

        // Add listener to scale tile size with window height
        puzzleScene.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            tileBackground.setHeight(getMinTileDimension());
            tileBackground.setWidth(getMinTileDimension());
            getTileTintByCoordinates(coordinates).setHeight(getMinTileDimension());
            getTileTintByCoordinates(coordinates).setWidth(getMinTileDimension());
        });

        // Initialize the TileTint (used for shading the tile)
        TileTint tileTint = new TileTint(TILE_WIDTH_AND_HEIGHT, TILE_WIDTH_AND_HEIGHT, coordinates);

        // Add tile text
        Text tileText = new Text();
        Font tileFont = new Font(boardFontName, ((double) TILE_WIDTH_AND_HEIGHT / 2));
        tileText.setFont(tileFont);
        tileText.setFill(Color.rgb(72, 72, 72));

        // Set starting clues' text to bold and color to Black
        if (!sudokuModel.getTileEmpty(coordinates)) {
            tileText.setStyle("-fx-font-weight: bold");
            tileText.setFill(Color.BLACK);
        }

        // Add a listener to scale tile font size with window width
        puzzleScene.widthProperty().addListener((observableValue, oldValue, newValue) -> {
            // Retain bold if it is present
            if (tileText.getStyle().equals("-fx-font-weight: bold")) {
                tileText.setFont(Font.font(boardFontName, FontWeight.BOLD, (getMinTileDimension() / 2)));
            }
            else {
                tileText.setFont(new Font(boardFontName, (getMinTileDimension() / 2)));
            }
        });

        // Add a listener to scale tile font size with window height
        puzzleScene.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            // Retain bold if it is present
            if (tileText.getStyle().equals("-fx-font-weight: bold")) {
                tileText.setFont(Font.font(boardFontName, FontWeight.BOLD, (getMinTileDimension() / 2)));
            }
            else {
                tileText.setFont(new Font(boardFontName, (getMinTileDimension() / 2)));
            }
        });

        // Add a listener that sets tile text to visible only if the value is 1-9
        tileText.textProperty().addListener((observable, oldText, newText) ->
                puzzleController.updateTileText(tileText, newText));

        // Bind the tile's fill color to the tile validity from the model
        if (tileText.getStyle().equals("-fx-font-weight: bold")) {
            puzzleController.bindClueTileTextFill(tileText, coordinates);
        }
        else {
            puzzleController.bindNonClueTileTextFill(tileText, coordinates);
        }

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

        // Add listener to scale line spacing with window width
        puzzleScene.widthProperty().addListener((observableValue, oldValue, newValue) -> {
            verticalGridLines.setSpacing(getMinTileDimension());
            horizontalGridLines.setSpacing(getMinTileDimension());
        });

        // Add listener to scale line spacing with window height
        puzzleScene.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            horizontalGridLines.setSpacing(getMinTileDimension());
            verticalGridLines.setSpacing(getMinTileDimension());
        });

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
                    gridLine.setHeight(BOARD_WIDTH_AND_HEIGHT + (2 * BOX_SPACING));
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

            // Set the additional line length for vertical lines on the edge of the board
            double additionalLength;

            if (isVertical) {
                if (index == 0 || index == 9) {
                    additionalLength = (2 * BOX_SPACING);
                }
                else {
                    additionalLength = 0;
                }
            }
            else {
                additionalLength = 0;
            }

            // Add listener to scale line length with window width
            puzzleScene.widthProperty().addListener((observableValue, oldValue, newValue) -> {
                if (isVertical) {
                    gridLine.setHeight(getMinGridLineLength() + additionalLength);
                }
                else {
                    gridLine.setWidth(getMinGridLineLength());
                }
            });

            // Add listener to scale line length with window height
            puzzleScene.heightProperty().addListener((observableValue, oldValue, newValue) -> {
                if (!isVertical) {
                    gridLine.setWidth(getMinGridLineLength());
                }
                else {
                    gridLine.setHeight(getMinGridLineLength() + additionalLength);
                }
            });

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
    public Coordinates getCoordinatesByTileTint(TileTint tileTint) {
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
    public TileTint getTileTintByCoordinates(Coordinates coordinates) {
        // Get a reference to the tileTint at the given coordinates
        return (TileTint) DFSNodeByUserData(coordinates, puzzleRoot);
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

    /**
     * Gets the minimum stable tile dimension based on the current window size
     * @return the minimum stable tile dimension
     */
    private double getMinTileDimension() {
        double tileTrueWidth = ((puzzleScene.getWidth() - 100 - (2 * BOX_SPACING) - (6 * TILE_SPACING)) / 9);
        double tileTrueHeight = ((puzzleScene.getHeight() - 200 - (2 * BOX_SPACING) - (6 * TILE_SPACING)) / 9);
        double tilePrefWidth = tileTrueWidth - (tileTrueWidth % 2);
        double tilePrefHeight = tileTrueHeight - (tileTrueHeight % 2);

        return Math.min(tilePrefWidth, tilePrefHeight);
    }

    /**
     * Gets the minimum stable grid line length based on the current window size
     * @return the minimum stable grid line length
     */
    private double getMinGridLineLength() {
        double gridLineTrueWidth = (puzzleScene.getWidth() - 100);
        double gridLineTrueHeight = (puzzleScene.getHeight() - 200);
        double gridLinePrefWidth = gridLineTrueWidth - (gridLineTrueWidth % 18);
        double gridLinePrefHeight = gridLineTrueHeight - (gridLineTrueHeight % 18);

        return Math.min(gridLinePrefWidth, gridLinePrefHeight);
    }

    /**
     * Custom Rectangle class for distinguishing TileTint Rectangles from other Objects
     */
    public class TileTint extends Rectangle {
        /**
         * Constructor: Creates a TileTint object as a Rectangle with the coordinates of its corresponding tile as
         * UserData
         * @param width the width of the tile
         * @param height the height of the tile
         * @param coordinates the coordinates of the tile
         */
        public TileTint(int width, int height, Coordinates coordinates) {
            // Initialize the tileTint Rectangle with coordinates in UserData
            super(width, height);
            setUserData(coordinates);

            // Add a listener that updates the tile's hovered property on hover
            hoverProperty().addListener((observable, oldHover, newHover) ->
                    puzzleController.updateTileHovered(coordinates, newHover));

            // Add a listener that updates the tileTint on hover
            hoverProperty().addListener((observable, oldValue, newValue) ->
                    puzzleController.updateTileFill(coordinates));

            // Bind the tile's fillProperty to the color from the model
            puzzleController.bindTileFill(this, coordinates);
        }
    }
}

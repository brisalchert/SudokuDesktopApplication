package Sudoku.UserInterface;

import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

public class PuzzleController {
    private final SudokuModel sudokuModel;
    private final PuzzleView puzzleView;
    private final Stage primaryStage;
    private boolean hasBeenSolved = false;

    /**
     * Constructor: Creates a new PuzzleController with a new puzzle with a certain minimum number of clues
     * @param sudokuModel the SudokuModel with the puzzle data
     * @param primaryStage the stage for displaying GUI information
     * @param minimumClues the minimum number of clues
     */
    public PuzzleController(SudokuModel sudokuModel, Stage primaryStage, int minimumClues) {
        this.sudokuModel = sudokuModel;

        // Generate a new puzzle
        sudokuModel.generateNewPuzzle(minimumClues);

        this.puzzleView = new PuzzleView(sudokuModel, this);
        this.primaryStage = primaryStage;

        primaryStage.setScene(getPuzzleScene());

        puzzleView.setKeyEventHandler(createKeyEventHandler());
        puzzleView.setMouseEventHandler(createMouseEventHandler());
    }

    /**
     * Constructor: Creates a new PuzzleController using a pre-existing puzzle
     * @param sudokuModel the SudokuModel with the puzzle data
     * @param primaryStage the stage for displaying GUI information
     */
    public PuzzleController(SudokuModel sudokuModel, Stage primaryStage) {
        this.sudokuModel = sudokuModel;
        this.puzzleView =  new PuzzleView(sudokuModel, this);
        this.primaryStage = primaryStage;

        primaryStage.setScene(getPuzzleScene());

        puzzleView.setKeyEventHandler(createKeyEventHandler());
        puzzleView.setMouseEventHandler(createMouseEventHandler());
    }

    public PuzzleView getPuzzleView() {
        return puzzleView;
    }

    public Scene getPuzzleScene() {
        return puzzleView.getPuzzleScene();
    }

    /**
     * Gets the starting width of the stage, equal to the scene's starting width plus 16 pixels
     * @return the starting stage width
     */
    public int getStartingStageWidth() {
        return puzzleView.getStartingWindowWidth() + 16;
    }

    /**
     * Gets the starting height of the stage, equal to the scene's starting height plus 39 pixels
     * @return the starting stage height
     */
    public int getStartingStageHeight() {
        return puzzleView.getStartingWindowHeight() + 39;
    }

    public EventHandler<KeyEvent> createKeyEventHandler() {
        EventHandler<KeyEvent> eventHandler = keyEvent -> {
            if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                sudokuModel.setValueLastClickedTile(keyEvent);

                if (sudokuModel.isBoardSolved() && !hasBeenSolved) {
                    Alert gameFinishedAlert = new Alert(Alert.AlertType.INFORMATION);
                    gameFinishedAlert.setTitle("Congratulations!");
                    gameFinishedAlert.setHeaderText(null);
                    gameFinishedAlert.setGraphic(null);
                    gameFinishedAlert.setContentText("Congratulations! You solved the puzzle!");
                    gameFinishedAlert.showAndWait();

                    hasBeenSolved = true;
                }
            }

            keyEvent.consume();
        };

        return eventHandler;
    }

    public EventHandler<MouseEvent> createMouseEventHandler() {
        EventHandler<MouseEvent> eventHandler = mouseEvent -> {
            Object target = mouseEvent.getTarget();

            // If click is not on a tile, unselect the last-clicked tile
            if (!(target instanceof PuzzleView.TileTint)) {
                if (sudokuModel.getLastClickedTile() != null) {
                    sudokuModel.updateLastClickedTile(null);
                }
            }
            else {
                Coordinates tileCoordinates = puzzleView.getCoordinatesByTileTint((PuzzleView.TileTint) target);
                sudokuModel.updateLastClickedTile(tileCoordinates);
            }

            // Update tile fill for all tiles
            sudokuModel.updateFillAllTiles();
        };

        return eventHandler;
    }

    public void updateTileHovered(Coordinates coordinates, boolean hovered) {
        sudokuModel.setTileHovered(coordinates, hovered);
    }

    public void updateTileFill(Coordinates coordinates) {
        sudokuModel.updateFill(coordinates);
    }

    public void updateTileText(Text tileText, String text) {
        tileText.setVisible(!text.equals("0"));
    }

    public void bindTileText(Text tileText, Coordinates coordinates) {
        tileText.textProperty().bindBidirectional(sudokuModel.tileValueProperty(coordinates),
                new NumberStringConverter());
    }

    public void bindTileFill(Rectangle tileTint, Coordinates coordinates) {
        tileTint.fillProperty().bind(sudokuModel.tileColorProperty(coordinates));
    }

    public void bindClueTileTextFill(Text tileText, Coordinates coordinates) {
        tileText.fillProperty().bind(Bindings.when(sudokuModel.tileValidProperty(coordinates))
                .then(Color.BLACK).otherwise(Color.RED));
    }

    public void bindNonClueTileTextFill(Text tileText, Coordinates coordinates) {
        tileText.fillProperty().bind(Bindings.when(sudokuModel.tileValidProperty(coordinates))
                .then(Color.rgb(72, 72, 72)).otherwise(Color.rgb(255, 72, 72)));
    }

    public void initMainMenuButton(Button mainMenuButton) {
        mainMenuButton.setOnAction(e -> {
            MenuController menuController = new MenuController(sudokuModel, primaryStage);
        });
    }
}

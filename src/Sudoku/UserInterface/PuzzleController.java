package Sudoku.UserInterface;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

public class PuzzleController {
    private final SudokuModel sudokuModel;
    private final PuzzleView puzzleView;
    private final Stage primaryStage;

    // Constructor: takes in the model and view as parameters
    public PuzzleController(SudokuModel sudokuModel, Stage primaryStage) {
        this.sudokuModel = sudokuModel;
        this.puzzleView = new PuzzleView(sudokuModel, this);
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

    public void initMainMenuButton(Button mainMenuButton) {
        mainMenuButton.setOnAction(e -> {
            MenuController menuController = new MenuController(sudokuModel, primaryStage);
        });
    }
}

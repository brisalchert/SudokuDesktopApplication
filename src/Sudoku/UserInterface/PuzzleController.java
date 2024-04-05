package Sudoku.UserInterface;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class PuzzleController {
    private final SudokuModel sudokuModel;
    private final PuzzleView puzzleView;

    // Constructor: takes in the model and view as parameters
    public PuzzleController(SudokuModel sudokuModel) {
        this.sudokuModel = sudokuModel;
        this.puzzleView = new PuzzleView(sudokuModel, this);

        puzzleView.setKeyEventHandler(createKeyEventHandler());
        puzzleView.setMouseEventHandler(createMouseEventHandler());
    }

    public PuzzleView getPuzzleView() {
        return puzzleView;
    }

    public Scene getPuzzleScene() {
        return puzzleView.getPuzzleScene();
    }

    public EventHandler<KeyEvent> createKeyEventHandler() {
        EventHandler<KeyEvent> eventHandler = keyEvent -> {
            // Check if there is a tile selected
            if (sudokuModel.getLastClickedTile() != null) {
                if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                    // Check that the input is valid
                    if (keyEvent.getText().matches("[1-9]")) {
                        int value = Integer.parseInt(keyEvent.getText());

                        // Assign the input to the current tile
                        sudokuModel.getLastClickedTile().setValue(value);
                    }
                    else {
                        if (keyEvent.getCode() == KeyCode.BACK_SPACE) {
                            sudokuModel.getLastClickedTile().setValue(null);
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
                if (sudokuModel.getLastClickedTile() != null) {
                    sudokuModel.getLastClickedTile().unselectTile();
                }
            }
        };

        return eventHandler;
    }
}

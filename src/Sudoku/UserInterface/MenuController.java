package Sudoku.UserInterface;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class MenuController {
    private final SudokuModel sudokuModel;
    private final MenuView menuView;
    private final Stage primaryStage;

    // Constructor
    public MenuController(SudokuModel sudokuModel, Stage primaryStage) {
        this.sudokuModel = sudokuModel;
        this.menuView = new MenuView(sudokuModel, this);
        this.primaryStage = primaryStage;

        primaryStage.setScene(getMenuScene());
    }

    public MenuView getMenuView() {
        return menuView;
    }

    public Scene getMenuScene() {
        return menuView.getMenuScene();
    }

    /**
     * Gets the starting width of the stage, equal to the scene's starting width plus 16 pixels
     * @return the starting stage width
     */
    public int getStartingStageWidth() {
        return menuView.getStartingWindowWidth() + 16;
    }

    /**
     * Gets the starting height of the stage, equal to the scene's starting height plus 39 pixels
     * @return the starting stage height
     */
    public int getStartingStageHeight() {
        return menuView.getStartingWindowHeight() + 39;
    }

    public boolean puzzleInstanceExists() {
        return sudokuModel.hasPuzzle();
    }

    public void initNewGameButton(Button newGameButton) {
        newGameButton.setOnAction(e -> {
            if (puzzleInstanceExists()) {
                Alert newGameConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
                newGameConfirmation.setTitle("Start New Game");
                newGameConfirmation.setHeaderText(null);
                newGameConfirmation.setGraphic(null);
                newGameConfirmation.setContentText("Override current game and start new game?");
                newGameConfirmation.showAndWait();

                if (newGameConfirmation.getResult() == ButtonType.OK) {
                    PuzzleController puzzleController = new PuzzleController(sudokuModel, primaryStage, 25);
                }
            }
            else {
                PuzzleController puzzleController = new PuzzleController(sudokuModel, primaryStage, 25);
            }
        });
    }

    public void initResumeGameButton(Button resumeGameButton) {
        resumeGameButton.setOnAction(e -> {
            PuzzleController puzzleController = new PuzzleController(sudokuModel, primaryStage);
        });
    }
}

package Sudoku;

import Sudoku.UserInterface.PuzzleController;
import Sudoku.UserInterface.SudokuModel;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            SudokuModel sudokuModel = new SudokuModel();
            PuzzleController puzzleController = new PuzzleController(sudokuModel);

            primaryStage.setTitle("Sudoku");
            primaryStage.setScene(puzzleController.getPuzzleScene());
            primaryStage.setMinWidth(puzzleController.getStartingStageWidth());
            primaryStage.setMinHeight(puzzleController.getStartingStageHeight());
            primaryStage.show();
        }
        catch (Exception error) {
            error.printStackTrace();
            throw error;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

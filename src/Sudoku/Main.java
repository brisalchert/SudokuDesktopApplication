package Sudoku;

import Sudoku.UserInterface.MenuController;
import Sudoku.UserInterface.SudokuModel;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            SudokuModel sudokuModel = new SudokuModel();
            MenuController menuController = new MenuController(sudokuModel, primaryStage);

            primaryStage.setTitle("Sudoku");
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

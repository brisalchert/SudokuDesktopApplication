package Sudoku;

import Sudoku.UserInterface.UserInterface;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            UserInterface userInterface = new UserInterface(primaryStage);
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

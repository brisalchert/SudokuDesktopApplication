package Sudoku.UserInterface;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class MenuView {
    private final BorderPane menuRoot;
    private final Scene menuScene;

    public MenuView() {
        this.menuRoot = new BorderPane();
        this.menuScene = new Scene(menuRoot);
    }

    public Scene getMenuScene() {
        return menuScene;
    }
}

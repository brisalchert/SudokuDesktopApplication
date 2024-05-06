package Sudoku.UserInterface;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MenuView {
    private final SudokuModel sudokuModel;
    private final MenuController menuController;
    private final AnchorPane menuRoot;
    private final Scene menuScene;
    private final String boardFontName = "Lucida Bright";
    private final int STARTING_WINDOW_WIDTH = 550;
    private final int STARTING_WINDOW_HEIGHT = 650;

    public MenuView(SudokuModel sudokuModel, MenuController menuController) {
        this.sudokuModel = sudokuModel;
        this.menuController = menuController;
        this.menuRoot = new AnchorPane();
        this.menuScene = new Scene(menuRoot);
        menuScene.getStylesheets().add(MenuView.class.getResource("style.css").toExternalForm());
        initializeMenuInterface();
    }

    public Scene getMenuScene() {
        return menuScene;
    }

    public int getStartingWindowWidth() {
        return STARTING_WINDOW_WIDTH;
    }

    public int getStartingWindowHeight() {
        return STARTING_WINDOW_HEIGHT;
    }

    private void initializeMenuInterface() {
        initializeMenuPane(menuRoot);
    }

    private void initializeMenuPane(AnchorPane menuRoot) {
        // Create a BorderPane for menu elements
        BorderPane menuPane = new BorderPane();
        menuPane.setId("menu-pane");

        // Add the menuPane to the root
        menuRoot.getChildren().add(menuPane);
        menuPane.prefWidthProperty().bind(menuScene.widthProperty());
        menuPane.prefHeightProperty().bind(menuScene.heightProperty());

        // Call interface initialization methods
        drawTitle(menuPane);
        drawMenuSelections(menuPane);
    }

    private void drawTitle(BorderPane menuRoot) {
        HBox titleBox = new HBox();
        Text title = new Text("Sudoku");
        Font titleFont = new Font(boardFontName, 50);
        title.setFont(titleFont);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().add(title);
        titleBox.setPadding(new Insets(20));
        titleBox.setPrefWidth(STARTING_WINDOW_WIDTH);

        menuRoot.setTop(titleBox);
    }

    private void drawMenuSelections(BorderPane menuRoot) {
        // Create an AnchorPane for menu elements
        AnchorPane menuPane = new AnchorPane();
        menuRoot.setCenter(menuPane);

        // Create a VBox to store the menu buttons
        VBox menuSelections = new VBox();
        menuPane.getChildren().add(menuSelections);
        menuSelections.setAlignment(Pos.TOP_CENTER);
        menuSelections.prefWidthProperty().bind(menuScene.widthProperty());
        menuSelections.translateYProperty().bind(menuScene.heightProperty().divide(6));
        menuSelections.setSpacing(10);

        // Determine whether to add the resumeGameButton
        if (menuController.puzzleInstanceExists()) {
            // Create the resumeGameButton
            Button resumeGameButton = new Button("Resume Game");
            resumeGameButton.setId("resume-game-button");
            resumeGameButton.setPrefWidth(300);
            resumeGameButton.setPrefHeight(40);
            menuSelections.getChildren().add(resumeGameButton);

            // Add resume game button functionality
            menuController.initResumeGameButton(resumeGameButton);
        }

        // Create the newGameButton
        Button newGameButton = new Button("New Game");
        newGameButton.setId("new-game-button");
        newGameButton.setPrefWidth(300);
        newGameButton.setPrefHeight(40);
        menuSelections.getChildren().add(newGameButton);

        // Add new game button functionality
        menuController.initNewGameButton(newGameButton);
    }
}

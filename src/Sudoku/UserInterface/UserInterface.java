package Sudoku.UserInterface;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;

public class UserInterface {
    private final Stage stage;
    private final Group root;
    private final int BOARD_WIDTH_AND_HEIGHT = 576;
    private final int WINDOW_WIDTH = 676;
    private final int WINDOW_HEIGHT = 776;
    private final Color backgroundColor = Color.rgb(168, 136, 98, 1);

    public UserInterface(Stage stage) {
        this.stage = stage;
        this.root = new Group();
        initializeUserInterface();
    }

    private void initializeUserInterface() {
        drawBackground(root);
        drawTitle(root);
        drawSudokuBoard(root);
        drawGridLines(root);
        stage.show();
    }

    private void drawBackground(Group root) {
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setFill(backgroundColor);
        stage.setTitle("Sudoku");
        stage.setScene(scene);
    }

    private void drawTitle(Group root) {
        HBox titleBox = new HBox();
        Text title = new Text("Sudoku");
        Font titleFont = new Font("Century", 50);
        title.setFont(titleFont);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().add(title);
        titleBox.setPadding(new Insets(20));
        titleBox.setPrefWidth(WINDOW_WIDTH);

        root.getChildren().add(titleBox);
    }

    private void drawSudokuBoard(Group root) {

    }

    private void drawGridLines(Group root) {

    }
}

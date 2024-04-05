package Sudoku.UserInterface;

public class MenuController {
    private final SudokuModel sudokuModel;
    private final MenuView menuView;

    // Constructor
    public MenuController(SudokuModel sudokuModel) {
        this.sudokuModel = sudokuModel;
        this.menuView = new MenuView();
    }

    public MenuView getMenuView() {
        return menuView;
    }
}

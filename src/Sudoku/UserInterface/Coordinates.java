package Sudoku.UserInterface;

public record Coordinates(int row, int column) {
    /**
     * Gets the row coordinate of this Coordinates object
     * @return the row coordinate
     */
    public int row() {
        return row;
    }

    /**
     * Gets the column coordinate of this Coordinates object
     * @return the column coordinate
     */
    public int column() {
        return column;
    }
}

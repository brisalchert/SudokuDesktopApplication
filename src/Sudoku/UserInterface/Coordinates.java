package Sudoku.UserInterface;

public record Coordinates(int x, int y) {
    /**
     * Gets the x coordinate of this Coordinates object
     * @return the x coordinate
     */
    @Override
    public int x() {
        return x;
    }

    /**
     * Gets the y coordinate of this Coordinates object
     * @return the y coordinate
     */
    @Override
    public int y() {
        return y;
    }
}

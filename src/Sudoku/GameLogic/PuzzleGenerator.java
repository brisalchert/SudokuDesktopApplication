package Sudoku.GameLogic;

import Sudoku.UserInterface.SudokuTile;

import java.util.Random;

public class PuzzleGenerator {
    public PuzzleGenerator() {
        setInitialCandidates();
        assignNineRandom();
    }
    public void assignNineRandom() {
        Random generator = new Random();
        int count = 0;

        while (count < 9) {
            int xIndex = generator.nextInt(9);
            int yIndex = generator.nextInt(9);
            SudokuTile randomTile = SudokuTile.getTileGrid()[xIndex][yIndex];

            if (randomTile.isEmpty()) {
                int value = generator.nextInt(9);

                // TODO: Function to update candidates for other tiles based on new placement
                if (randomTile.getCandidates().indexOf(Integer.toString(value)) != -1) {
                    randomTile.setValue(value);
                    randomTile.setCandidates(Integer.toString(value));

                    count++;
                }
            }
        }
    }

    private void setInitialCandidates() {
        for (SudokuTile[] row : SudokuTile.getTileGrid()) {
            for (SudokuTile tile : row) {
                tile.setCandidates("123456789");
            }
        }
    }
}

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
        SudokuTile[][] tileGrid = SudokuTile.getTileGrid();
        int count = 0;

        while (count < 9) {
            int xIndex = generator.nextInt(9);
            int yIndex = generator.nextInt(9);
            SudokuTile randomTile = tileGrid[xIndex][yIndex];

            if (randomTile.isEmpty()) {
                // Generate a random index from the String of valid candidates
                int candidateIndex = generator.nextInt(randomTile.getCandidates().length());
                int value = Integer.parseInt(String.valueOf(randomTile.getCandidates().charAt(candidateIndex)));

                // Check if value is a valid candidate
                if (randomTile.getCandidates().indexOf(Integer.toString(value)) != -1) {
                    // Update the value and candidates for the tile
                    randomTile.setValue(value);
                    randomTile.setCandidates(Integer.toString(value));

                    // Update candidates for tiles in the same row
                    for (SudokuTile tile : randomTile.getRow()) {
                        tile.removeCandidate(value);
                    }

                    // Update candidates for tiles in the same column
                    for (SudokuTile tile : randomTile.getColumn()) {
                        tile.removeCandidate(value);
                    }

                    // Update candidates for tiles in the same box
                    for (SudokuTile tile : randomTile.getBox()) {
                        tile.removeCandidate(value);
                    }

                    count++;
                }
            }
        }
    }

    private void setInitialCandidates() {
        for (SudokuTile[] column : SudokuTile.getTileGrid()) {
            for (SudokuTile tile : column) {
                tile.setCandidates("123456789");
            }
        }
    }
}

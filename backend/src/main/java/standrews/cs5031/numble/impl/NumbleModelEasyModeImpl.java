package standrews.cs5031.numble.impl;

import standrews.cs5031.numble.Cell;
import standrews.cs5031.numble.NumbleModel;

/**
 * Model implementation for the simple Numble game.
 */
public class NumbleModelEasyModeImpl implements NumbleModel {
    private final int numCols;
    private final int numRows;

    /**
     * The number at the right-hand side of the desired equation.
     */
    private final int rhs;
    /**
     * The left-hand side of the desired equation.
     */
    private final String lhs;

    private int numberOfGuessMade;
    private Cell[][] cells;

    private boolean won = false;
    private boolean lost = false;

    public NumbleModelEasyModeImpl(int numRows, int numCols, int rhs, String lhs) {
        this.numCols = numCols;
        this.numRows = numRows;
        this.rhs = rhs;
        this.lhs = lhs;

        numberOfGuessMade = 0;
        cells = new Cell[numRows][numCols];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                cells[row][col] = new Cell(row, col);
            }
        }
    }

    @Override
    public boolean guess(String guess) {
        return false;
    }

    @Override
    public boolean hasLost() {
        return lost;
    }

    @Override
    public boolean hasWon() {
        return won;
    }

    @Override
    public boolean isCorrect(char guessChar, int position) {
        return false;
    }

    @Override
    public boolean exist(char guessChar, int position) {
        return false;
    }

    @Override
    public int getNumCols() {
        return numCols;
    }

    @Override
    public int getNumRows() {
        return numRows;
    }

    @Override
    public int getNumberOfGuessMade() {
        return numberOfGuessMade;
    }

    @Override
    public Cell[][] getCells() {
        return cells;
    }

    @Override
    public Mode getMode() {
        return Mode.EASY;
    }

    public int getRhs() {
        return rhs;
    }

}

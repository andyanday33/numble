package standrews.cs5031.numble.model;

import lombok.Getter;
import standrews.cs5031.numble.exception.MethodNotAvailableException;

@Getter
public abstract class NumbleModel {
    private final int numCols;
    private final int numRows;

    private int numberOfGuessMade;
    private Cell[][] cells;

    private boolean won = false;
    private boolean lost = false;

    public enum Mode {EASY, HARD}

    public NumbleModel(int numRows, int numCols) {
        this.numCols = numCols;
        this.numRows = numRows;
        numberOfGuessMade = 0;
        cells = new Cell[numRows][numCols];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                cells[row][col] = new Cell(row, col);
            }
        }
    }


    /**
     * The first time this is called, the game starts and
     * a right solution is formed.
     * After each guess, the winning condition is checked.
     *
     * @param guess
     * @return true if this is right guess and the player wins, false otherwise
     * @throws IllegalArgumentException    if the guess is not a valid equation
     * @throws MethodNotAvailableException if the game is over - player wins or loses.
     */
    public boolean guess(String guess) {
        if (won|| lost) {
            throw new MethodNotAvailableException("Game is over, no more guess can be made");
        }
        if (isValidGuess(guess)) {
            //Store guess characters in cells
            storeGuess(guess);
            boolean isCorrect = isCorrectSolution(guess);
            numberOfGuessMade++;
            if (isCorrect) {
                won = true;
            } else {
                if (numberOfGuessMade >= numRows) {
                    lost = true;
                }
            }
            return isCorrect;
        } else {
            throw new IllegalArgumentException("Invalid guess input: " + guess);
        }
    }

    protected abstract boolean isCorrectSolution(String guess);

    private void storeGuess(String guess) {
        for (int i = 0; i < numCols; i++) {
            cells[numberOfGuessMade][i].setGuessChar(guess.charAt(i));
        }
    }

    protected abstract boolean isValidGuess(String guess);

    /**
     * Checks if a character is in right place
     *
     * @param guessChar
     * @param position
     * @return
     */
    protected abstract boolean isCorrect(char guessChar, int position);

    /**
     * Checks if a character exists in the given equation.
     * If two same characters in wrong position, while it only has one in solution,
     * then the last one will be marked as not exists.
     *
     * @param guessChar
     * @param comparedWithGuess marks if the character in lhs has been compared with the same character in guess.
     * @return
     */
    protected abstract boolean checkExists(char guessChar, boolean[] comparedWithGuess);


    public abstract Mode getMode();

}

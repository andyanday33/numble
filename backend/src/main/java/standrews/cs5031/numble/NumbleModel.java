package standrews.cs5031.numble;

import com.fasterxml.jackson.annotation.JsonGetter;

public interface NumbleModel {

    enum Mode {EASY, HARD}

    /**
     * The first time this is called, the game starts and
     * a right solution is formed.
     * After each guess, the winning condition is checked.
     * @param guess
     * @throws IllegalArgumentException if the guess is not a valid equation
     * @return true if this is right guess and the player wins, false otherwise
     */
    boolean guess(String guess);

    /**
     * Checks if the player has lost by wrongly guessing equation with last chance.
     */
    @JsonGetter("lost")
    boolean hasLost();

    /**
     * Checks if the player made a right guess.
     */
    @JsonGetter("won")
    boolean hasWon();

    /**
     * Checks if a character is in right place
     * @param guessChar
     * @param position
     * @return
     */
    boolean isCorrect(char guessChar, int position);

    /**
     * Checks if a character exists in the given equation.
     * If two same characters in wrong position, while it only has one in solution,
     * then the last one will be marked as not exists.
     * @param guessChar
     * @param comparedWithGuess marks if the character in lhs has been compared with the same character in guess.
     * @return
     */
    boolean exist(char guessChar, boolean[] comparedWithGuess);

    /**
     * Gets the number of columns on the board.
     */
    int getNumCols();

    /**
     * Gets the number of rows on the board.
     */
    int getNumRows();

    int getNumberOfGuessMade();

    Cell[][] getCells();

    Mode getMode();
}

package standrews.cs5031.numble;

import com.fasterxml.jackson.annotation.JsonGetter;

public interface NumbleModel {

    /**
     * The first time this is called, the game starts and
     * a right solution is formed.
     * After each guess, the winning condition is checked.
     * @param guess
     * @throws IllegalArgumentException if the guess is not a valid equation
     * @return true if this is wrong guess and the player loses, false otherwise
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
     * Checks if a character exists in the given equation
     * @param guessChar
     * @param position
     * @return
     */
    boolean exist(char guessChar, int position);

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
}

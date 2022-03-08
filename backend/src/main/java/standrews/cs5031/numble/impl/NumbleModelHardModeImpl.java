package standrews.cs5031.numble.impl;

import standrews.cs5031.numble.model.Cell;
import standrews.cs5031.numble.model.NumbleModel;
import standrews.cs5031.numble.data.EquationData;
import standrews.cs5031.numble.util.MathUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Model implementation for the hard Numble game.
 */
public class NumbleModelHardModeImpl extends NumbleModel {
    /**
     * The desired solution.
     */
    private final String solution;


    public NumbleModelHardModeImpl(int numRows, int numCols) {
        super(numRows, numCols);
        //Get a random equation from data source.
        this.solution = EquationData.getRandomEquation(Mode.HARD, numCols);
    }

    @Override
    protected boolean isCorrectSolution(String guess) {
        boolean isCorrect = true;
        //Mark if the character in lhs has been compared with the same character in guess.
        boolean[] comparedWithGuess = new boolean[guess.length()];
        //Find all characters in right place.
        for (int i = 0; i < guess.length(); i++) {
            char guessChar = guess.charAt(i);
            if (isCorrect(guessChar, i)) {
                getCells()[getNumberOfGuessMade()][i].setState(Cell.State.CORRECT);
                comparedWithGuess[i] = true;
            }
        }

        for (int i = 0; i < guess.length(); i++) {
            if (getCells()[getNumberOfGuessMade()][i].getState() == Cell.State.CORRECT) {
                continue;
            }
            char guessChar = guess.charAt(i);
            if (checkExists(guessChar, comparedWithGuess)) {
                //Guess character in wrong place
                getCells()[getNumberOfGuessMade()][i].setState(Cell.State.WRONG_POSITION);
            } else {
                //Incorrect guess character
                getCells()[getNumberOfGuessMade()][i].setState(Cell.State.NOT_EXIST);
            }
            isCorrect = false;
        }
        return isCorrect;
    }
    @Override
    protected boolean isValidGuess(String guess) {
        //Check guess is of the correct length
        if (guess.length() != solution.length()) {
            return false;
        }
        if (!usesSolvedChars(guess)) {
            return false;
        }
        //Check guess has no invalid symbols and lhs really equals to rhs
        try {
            int equationMarkIndex = guess.indexOf('=');
            if (equationMarkIndex < 0) {
                return false;
            }
            String lhs = guess.substring(0, equationMarkIndex);
            String rhs = guess.substring(equationMarkIndex + 1);
            return MathUtil.evaluateHard(lhs) == MathUtil.evaluateHard(rhs);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * This checks if guess reused all characters which are the “right character, right place” and “right
     * character, wrong place”.
     *
     * @param guess the String solution player guessed
     * @return true if all right characters in last guess is in current guess expression, false otherwise.
     */
    private boolean usesSolvedChars(String guess) {
        List<Character> lastRightCharsGuessed = getPreviousSolvedChars();
        for (int i = 0; i < guess.length(); i++) {
            if (lastRightCharsGuessed.size() == 0) {
                break;
            }
            char guessChar = guess.charAt(i);
            lastRightCharsGuessed.remove(Character.valueOf(guessChar));
        }
        return lastRightCharsGuessed.size() <= 0;
    }

    /**
     * This extracts all characters in previous row which are marked as "CORRECT" or "WRONG_POSITION".
     *
     * @return a list of characters rightly guessed in previous row.
     */
    private List<Character> getPreviousSolvedChars() {
        List<Character> lastRightCharsGuessed = new LinkedList<>();
        if (getNumberOfGuessMade() - 1 >= 0) {
            for (Cell cell : getCells()[getNumberOfGuessMade() - 1]) {
                if (cell.getState() == Cell.State.CORRECT || cell.getState() == Cell.State.WRONG_POSITION) {
                    lastRightCharsGuessed.add(cell.getGuessChar());
                }
            }
        }
        return lastRightCharsGuessed;
    }

    @Override
    public boolean isCorrect(char guessChar, int position) {
        return guessChar == solution.charAt(position);
    }

    @Override
    public boolean checkExists(char guessChar, boolean[] comparedWithGuess) {
        for (int i = 0; i < solution.length(); i++) {
            if (!comparedWithGuess[i] && guessChar == solution.charAt(i)) {
                comparedWithGuess[i] = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public Mode getMode() {
        return Mode.HARD;
    }
}

package standrews.cs5031.numble.impl;

import standrews.cs5031.numble.model.Cell;
import standrews.cs5031.numble.model.NumbleModel;
import standrews.cs5031.numble.data.EquationData;
import standrews.cs5031.numble.util.MathUtil;

/**
 * Model implementation for the simple Numble game.
 */
public class NumbleModelEasyModeImpl extends NumbleModel {

    /**
     * The number at the right-hand side of the desired equation.
     */
    private final int rhs;
    /**
     * The left-hand side of the desired equation.
     */
    private final String lhs;

    public NumbleModelEasyModeImpl(int numRows, int numCols) {
        super(numRows, numCols);
        //Get a random equation from data source.
        String equation = EquationData.getRandomEquation(Mode.EASY, numCols);
        //Initialise rhs and lhs based on the equation
        int equalMarkIndex = equation.indexOf('=');
        lhs = equation.substring(0, equalMarkIndex);
        rhs = Integer.parseInt(equation.substring(equalMarkIndex + 1));
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
        if(guess.length() != lhs.length()){
            return false;
        }
        //Check guess has no invalid symbols
        try {
            MathUtil.evaluateSimple(guess);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isCorrect(char guessChar, int position) {
        return guessChar == lhs.charAt(position);
    }

    @Override
    public boolean checkExists(char guessChar, boolean[] comparedWithGuess) {
        for (int i = 0; i < lhs.length(); i++) {
            if (!comparedWithGuess[i] && guessChar == lhs.charAt(i)) {
                comparedWithGuess[i] = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public Mode getMode() {
        return Mode.EASY;
    }

    public int getRhs() {
        return rhs;
    }

}

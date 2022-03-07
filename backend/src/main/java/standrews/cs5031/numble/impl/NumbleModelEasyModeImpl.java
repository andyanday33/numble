package standrews.cs5031.numble.impl;

import standrews.cs5031.numble.Cell;
import standrews.cs5031.numble.MethodNotAvailableException;
import standrews.cs5031.numble.NumbleModel;
import standrews.cs5031.numble.data.EquationData;
import javax.script.ScriptException;
import org.json.JSONObject;

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

    public NumbleModelEasyModeImpl(int numRows, int numCols) {
        this.numCols = numCols;
        this.numRows = numRows;
        numberOfGuessMade = 0;
        cells = new Cell[numRows][numCols];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                cells[row][col] = new Cell(row, col);
            }
        }
        //Get a random equation from data source.
        String equation = EquationData.getRandomEquation(Mode.EASY, numCols);
        //Initialise rhs and lhs based on the equation
        int equalMarkIndex = equation.indexOf('=');
        lhs = equation.substring(0, equalMarkIndex);
        rhs = Integer.parseInt(equation.substring(equalMarkIndex + 1));
    }

    /**
     * Checks if the current guess is mathematically equivalent to the rhs.
     * @param guess String from the user
     * @param rhs Target value
     * @return boolean, True if the guess is equivalent to the rhs.
     * @throws IllegalArgumentException
     */

    public boolean evaluate(String guess,int rhs) throws IllegalArgumentException {
        //Splits guess string into each operator and the number its operating on. (as we are evaluating left to right)
        String[] guessParts = guess.split("((?=\\*))|((?=\\/))|((?=\\+))|((?=\\-))");

        int total = 0;
        for (int i = 0; i < guessParts.length; i++) {
            //if operator isnt * or /, just parse string and add to running total
            if (!guessParts[i].contains("*") && !guessParts[i].contains("/")) {

                int temp = Integer.parseInt(guessParts[i]);
                total += temp;
            } else if (guessParts[i].charAt(0) == '*') {

                int temp = Integer.parseInt(guessParts[i].substring(1));
                total = total * temp;
            } else if (guessParts[i].charAt(0) == '/') {

                int temp = Integer.parseInt(guessParts[i].substring(1));
                if(total % temp==0){
                    total = total / temp;
                }else{
                    throw new IllegalArgumentException("No decimal values");
                }

            }

        }
        return total==rhs;
    }

    @Override
    public boolean guess(String guess) {

        if (hasLost() || hasWon()) {
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
            throw new IllegalArgumentException("Invalid guess input");
        }

    }

    private void storeGuess(String guess) {
        for (int i = 0; i < numCols; i++) {
            cells[numberOfGuessMade][i].guessChar = guess.charAt(i);
        }
    }


    private boolean isCorrectSolution(String guess) {
        boolean isCorrect = true;
        //Mark if the character in lhs has been compared with the same character in guess.
        boolean[] comparedWithGuess = new boolean[guess.length()];
        //Find all characters in right place.
        for (int i = 0; i < guess.length(); i++) {
            char guessChar = guess.charAt(i);
            if (isCorrect(guessChar, i)) {
                cells[numberOfGuessMade][i].state = Cell.State.CORRECT;
                comparedWithGuess[i] = true;
            }
        }

        for (int i = 0; i < guess.length(); i++) {
            if (cells[numberOfGuessMade][i].state == Cell.State.CORRECT) {
                continue;
            }
            char guessChar = guess.charAt(i);
            if (exist(guessChar, comparedWithGuess)) {
                //Guess character in wrong place
                cells[numberOfGuessMade][i].state = Cell.State.WRONG_POSITION;
                isCorrect = false;
            } else {
                //Incorrect guess character
                cells[numberOfGuessMade][i].state = Cell.State.NOT_EXIST;
                isCorrect = false;
            }
        }
        return isCorrect;
    }

    private boolean isValidGuess(String guess) {
        //Check guess is of the correct length
        if(guess.length() != lhs.length()){
            return false;
        }
        //Check guess is equal to rhs (and Check guess has no invalid symbols)
        if(!evaluate(guess,rhs)){
            return false;
        }

        return true;
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
        return guessChar == lhs.charAt(position);
    }

    @Override
    public boolean exist(char guessChar, boolean[] comparedWithGuess) {
        for (int i = 0; i < lhs.length(); i++) {
            if (!comparedWithGuess[i] && guessChar == lhs.charAt(i)) {
                comparedWithGuess[i] = true;
                return true;
            }
        }
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

package standrews.cs5031.numble.impl;

import standrews.cs5031.numble.Cell;
import standrews.cs5031.numble.MethodNotAvailableException;
import standrews.cs5031.numble.NumbleModel;
import standrews.cs5031.numble.data.EquationData;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

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
     * Checks if the current guess is valid; i.e is mathematically equal to the target value. Another implementation of this could
     * include Dijkstras twostep algorithm
     * @param guess String from the user
     * @param rhs Target value
     * @return boolean, True if the guess is equivalent to the rhs.
     * @throws ScriptException
     */

    public boolean evaluate(String guess,int rhs) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        Object result = engine.eval(guess);
        String res = String.valueOf(result);
        int fin = Integer.parseInt(res);
        return fin==rhs;
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
                comparedWithGuess[i] = true;
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

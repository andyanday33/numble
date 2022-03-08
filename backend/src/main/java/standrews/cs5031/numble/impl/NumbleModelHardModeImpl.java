package standrews.cs5031.numble.impl;

import standrews.cs5031.numble.Cell;
import standrews.cs5031.numble.MethodNotAvailableException;
import standrews.cs5031.numble.NumbleModel;
import standrews.cs5031.numble.data.EquationData;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Model implementation for the hard Numble game.
 */
public class NumbleModelHardModeImpl implements NumbleModel {

    private final int numCols;
    private final int numRows;

    /**
     * The desired solution.
     */
    private final String solution;

    private int numberOfGuessMade;
    private Cell[][] cells;

    private boolean won = false;
    private boolean lost = false;


    public NumbleModelHardModeImpl(int numRows, int numCols) {
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
        this.solution = EquationData.getRandomEquation(Mode.HARD, numCols);
    }
    /**
     * Evaluates one side of the guess expression to an integer. Splits the expression into number-operator pairs and handles each
     * computation from left to right. Same functionality as evaluate for the EasyMode implementation, but now expressions inside of brackets
     * are evaluated first, then the remaining expression is solved in a left to right fashion.
     * @param expression String from the user
     * @return int.
     * @throws IllegalArgumentException
     */
    public int evaluate(String expression) throws IllegalArgumentException {

        if(expression.contains("(")) {
            if (expression.contains(")")) {
                //Match the outermost parentheses
                Matcher m = Pattern.compile("\\((\\(*(?:[^)(]*|\\([^)]*\\))*\\)*)\\)").matcher(expression);
                while (m.find()) {
                    String sub = m.group(1);    //Matches the inside of the outermost parentheses
                    String subBrackets= "("+sub+")";
                    int sube = evaluate(sub);   //Recursively call this function on the inside of the outermost parentheses
                    String suber = String.valueOf(sube);
                    expression = expression.replace(subBrackets, suber); //Replace the inside of the parentheses (with brackets) with the evaluated version
                }
            }

        }

        //Splits guess string into each operator and the number its operating on. (as we are evaluating left to right)
        String[] guessParts = expression.split("((?=\\*))|((?=\\/))|((?=\\+))|((?=\\-))");

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
                if (total % temp == 0) {
                    total = total / temp;
                } else {
                    throw new IllegalArgumentException("No decimal values");
                }

            }
        }
        return total;
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
            throw new IllegalArgumentException("Invalid guess input: " + guess);
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
            if (checkExists(guessChar, comparedWithGuess)) {
                //Guess character in wrong place
                cells[numberOfGuessMade][i].state = Cell.State.WRONG_POSITION;
            } else {
                //Incorrect guess character
                cells[numberOfGuessMade][i].state = Cell.State.NOT_EXIST;
            }
            isCorrect = false;
        }
        return isCorrect;
    }

    private boolean isValidGuess(String guess) {
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
            return evaluate(lhs) == evaluate(rhs);
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
        if (numberOfGuessMade - 1 >= 0) {
            for (Cell cell : cells[numberOfGuessMade - 1]) {
                if (cell.state == Cell.State.CORRECT || cell.state == Cell.State.WRONG_POSITION) {
                    lastRightCharsGuessed.add(cell.guessChar);
                }
            }
        }
        return lastRightCharsGuessed;
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
        return Mode.HARD;
    }
}

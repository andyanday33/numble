package standrews.cs5031.numble.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import standrews.cs5031.numble.Cell;
import standrews.cs5031.numble.MethodNotAvailableException;
import standrews.cs5031.numble.NumbleModel;
import standrews.cs5031.numble.data.EquationData;

import static org.junit.jupiter.api.Assertions.*;

public class NumbleModelHardModeImplTests {

    NumbleModelHardModeImpl model;
    static MockedStatic<EquationData> equationData;

    @BeforeAll
    public static void allSetup() {
        MockedStatic<EquationData> equationData = Mockito.mockStatic(EquationData.class);
        equationData.when(() -> EquationData.getRandomEquation(NumbleModel.Mode.HARD, 6))
                .thenReturn("3*4=12");
        equationData.when(() -> EquationData.getRandomEquation(NumbleModel.Mode.HARD, 12))
                .thenReturn("4321+11=4332");
        equationData.when(() -> EquationData.getRandomEquation(NumbleModel.Mode.HARD, 7))
                .thenReturn("2+5+2=9");

    }

    @BeforeEach
    public void eachSetup() {
        model = new NumbleModelHardModeImpl(10, 6);
    }

    @AfterAll
    public static void close() {
        try {
            equationData.close();
        } catch (NullPointerException e) {
        }
    }

    @Test
    public void gameCreated() {
        assertEquals(NumbleModel.Mode.HARD, model.getMode());
        assertEquals(10, model.getNumRows());
        assertEquals(6, model.getNumCols());
        for (int i = 0; i < model.getNumRows(); i++) {
            for (int j = 0; j < model.getNumCols(); j++) {
                assertEquals(Cell.State.INIT, model.getCells()[i][j].state);
            }
        }
    }

    @Test
    public void noGuessAfterPlayerWins() {
        //Win the game
        String correctSolution = "3*4=12";
        assertTrue(model.guess(correctSolution));
        assertTrue(model.hasWon());

        //Try another guess
        assertThrows(MethodNotAvailableException.class, () -> model.guess("1+2"));
    }

    @Test
    public void noGuessAfterPlayerLoses() {
        model = new NumbleModelHardModeImpl(2, 6);
        //Lose the game
        String wrongSolution = "5*6=30";
        assertFalse(model.guess(wrongSolution));
        assertFalse(model.guess(wrongSolution));
        assertTrue(model.hasLost());

        //Try another guess
        assertThrows(MethodNotAvailableException.class, () -> model.guess("1+2"));
    }

    /**
     * Check that guesses that do not have the specified length (number of cols) throw an error
     */
    @Test
    public void guessWithInvalidLength() {
        String[] invalidInputs = {"", "1", "+", "1234", "1+2+3", "1+2+3=6", "4*6+1=25"};
        for (String input : invalidInputs) {
            assertThrows(IllegalArgumentException.class, () -> model.guess(input));
        }
        assertEquals(0, model.getNumberOfGuessMade());
    }

    /**
     * Check that guesses that do not reuse right characters throw an error
     */
    @Test
    public void guessNotReuseRightChars() {
        //make a wrong guess
        assertFalse(model.guess("3*5=15"));
        assertEquals(1, model.getNumberOfGuessMade());

        //invalid inputs without all of (3, *, =, 1) four characters.
        String[] invalidInputs = {"2+9-20", "-8+8=0", "10-2=8", "2*7=14"};
        for (String input : invalidInputs) {
            assertThrows(IllegalArgumentException.class, () -> model.guess(input));
        }
        assertEquals(1, model.getNumberOfGuessMade());
    }

    /**
     * Check that guesses that include nonsensical syntax throw an error
     */
    @Test
    public void guessWithInvalidChars() {
        String[] invalidInputs = {"1++1-*", "1+2+32", "1A3=14", "13/2=6", "+--5=5"};
        for (String input : invalidInputs) {
            assertThrows(IllegalArgumentException.class, () -> model.guess(input));
        }
        assertEquals(0, model.getNumberOfGuessMade());
    }


    /**
     * Check that guesses that add up to the incorrect target value throw an error
     */
    @Test
    public void guessWithInvalidValue() {
        String[] invalidInputs = {"2+8=12", "9*1=19", "14-2=8", "123=12"};
        for (String input : invalidInputs) {
            assertThrows(IllegalArgumentException.class, () -> model.guess(input));
        }
        assertEquals(0, model.getNumberOfGuessMade());
    }

    /**
     * Tests case of guesses that represent a decimal value (that would normally be rounded down and treated as an integer) instead throw an error
     */
    @Test
    public void guessWithDecimal() {
        String[] invalidInputs = {"11/2=5", "22/4=5"};
        for (String input : invalidInputs) {
            assertThrows(IllegalArgumentException.class, () -> model.guess(input));
        }
    }

    /**
     * Check that valid equations that add up to target value, but are not the exact correct solution, return false.
     */
    @Test
    public void guessReturnsFalse() {
        String[] invalidInputs = {"3*5=15", "3*6=18", "3*7=21"};
        for (String input : invalidInputs) {
            assertFalse(model.guess(input));
        }
    }
    /**
     * Check that valid equations that add up to target value, but are not the exact correct solution, return false.
     */
    @Test
    public void guessWithBracketsReturnFalse() {
        model = new NumbleModelHardModeImpl(2, 7);
        String[] invalidInputs = {"((9))=9","(4+5)=9"};
        for (String input: invalidInputs) {
            assertFalse(model.guess(input));
        }
    }
    /**
     * Check guesses with unbalanced sets of parentheses are treated as invalid inputs
     */
    @Test
    public void guessWithUnbalancedBrackets() {
        model = new NumbleModelHardModeImpl(2, 7);
        String[] invalidInputs = {"(((9)=9","(10-2=9"};
        for (String input: invalidInputs) {
            assertThrows(IllegalArgumentException.class, () -> model.guess(input));
        }
    }

    /**
     * Ensures that expressions are evaluated in a left to right fashion, not applying the rules of bodmas.
     */
    @Test
    public void checkEvaluatedLeftToRight() {
        model = new NumbleModelHardModeImpl(2, 7);
        //Strings that would equal the target 9 according to bodmas, but not according to left to right evaluation.
        String[] invalidInputs = {"6+9/3=9", "1+2*4=9", "2+1*7=9"};
        for (String input : invalidInputs) {
            assertThrows(IllegalArgumentException.class, () -> model.guess(input));
        }
    }

    @Test
    public void storeCharsInCellAfterWrongGuess() {
        String wrongGuess = "3*5=15";
        assertFalse(model.guess(wrongGuess));
        assertEquals(1, model.getNumberOfGuessMade());
        for (int i = 0; i < model.getNumCols(); i++) {
            assertEquals(wrongGuess.charAt(i), model.getCells()[0][i].guessChar);
        }
    }

    @Test
    public void storeCharsInCellAfterCorrectGuess() {
        String rightGuess = "3*4=12";
        assertTrue(model.guess(rightGuess));
        assertEquals(1, model.getNumberOfGuessMade());
        for (int i = 0; i < model.getNumCols(); i++) {
            assertEquals(rightGuess.charAt(i), model.getCells()[0][i].guessChar);
        }
    }

    @Test
    public void stateUpdateAfterWrongGuessWithCorrectWrongPositionNotExist() {
        String wrongGuess = "5*3=15";
        assertFalse(model.guess(wrongGuess));
        assertEquals(1, model.getNumberOfGuessMade());
        assertEquals(Cell.State.NOT_EXIST, model.getCells()[0][0].state);
        assertEquals(Cell.State.CORRECT, model.getCells()[0][1].state);
        assertEquals(Cell.State.WRONG_POSITION, model.getCells()[0][2].state);
        assertEquals(Cell.State.CORRECT, model.getCells()[0][3].state);
        assertEquals(Cell.State.CORRECT, model.getCells()[0][4].state);
        assertEquals(Cell.State.NOT_EXIST, model.getCells()[0][5].state);
    }

    @Test
    public void stateUpdateAfterCorrectGuess() {
        String rightGuess = "3*4=12";
        assertTrue(model.guess(rightGuess));
        assertEquals(1, model.getNumberOfGuessMade());
        for (Cell cell : model.getCells()[0]) {
            assertEquals(Cell.State.CORRECT, cell.state);
        }
    }

    @Test
    public void stateUpdateAfterWrongGuessWithMultiSameChars() {
        model = new NumbleModelHardModeImpl(2, 12);
        //solution: 4321+11=4332
        String wrongGuess = "1111+22=1133";
        assertFalse(model.guess(wrongGuess));
        assertEquals(1, model.getNumberOfGuessMade());
        //Cell for the first 1
        assertEquals(Cell.State.WRONG_POSITION, model.getCells()[0][0].state);
        //Cell for the second 1
        assertEquals(Cell.State.WRONG_POSITION, model.getCells()[0][1].state);
        //Cell for the third 1
        assertEquals(Cell.State.NOT_EXIST, model.getCells()[0][2].state);
        //Cell for the fourth 1
        assertEquals(Cell.State.CORRECT, model.getCells()[0][3].state);
        //Cell for +
        assertEquals(Cell.State.CORRECT, model.getCells()[0][4].state);
        //Cell for the first 2
        assertEquals(Cell.State.WRONG_POSITION, model.getCells()[0][5].state);
        //Cell for the second 2
        assertEquals(Cell.State.WRONG_POSITION, model.getCells()[0][6].state);
        //Cell for =
        assertEquals(Cell.State.CORRECT, model.getCells()[0][7].state);
        //Cell for the fifth 1
        assertEquals(Cell.State.NOT_EXIST, model.getCells()[0][8].state);
        //Cell for the sixth 1
        assertEquals(Cell.State.NOT_EXIST, model.getCells()[0][9].state);
        //Cell for the first 3
        assertEquals(Cell.State.CORRECT, model.getCells()[0][10].state);
        //Cell for the second 3
        assertEquals(Cell.State.WRONG_POSITION, model.getCells()[0][11].state);
    }

    @Test
    public void playerLosesWhenAllGuessAreWrong() {
        model = new NumbleModelHardModeImpl(2, 6);
        String wrongSolution = "3*5=15";
        assertFalse(model.guess(wrongSolution));
        assertFalse(model.guess(wrongSolution));
        assertEquals(2, model.getNumberOfGuessMade());
        assertTrue(model.hasLost());
        assertFalse(model.hasWon());
    }

    @Test
    public void playerWinsWhenTheyGuessRightSolution() {
        String correctSolution = "3*4=12";
        assertTrue(model.guess(correctSolution));
        assertEquals(1, model.getNumberOfGuessMade());
        assertTrue(model.hasWon());
        assertFalse(model.hasLost());
    }
}

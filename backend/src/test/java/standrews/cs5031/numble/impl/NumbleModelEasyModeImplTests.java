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


public class NumbleModelEasyModeImplTests {
    NumbleModelEasyModeImpl model;
    static MockedStatic<EquationData> equationData;

    @BeforeAll
    public static void allSetup() {
        equationData = Mockito.mockStatic(EquationData.class);
        equationData.when(() -> EquationData.getRandomEquation(NumbleModel.Mode.EASY, 3))
                .thenReturn("3+2=5");
        equationData.when(() -> EquationData.getRandomEquation(NumbleModel.Mode.EASY, 7))
                .thenReturn("4321+11=4332");
        equationData.when(() -> EquationData.getRandomEquation(NumbleModel.Mode.EASY, 5))
                .thenReturn("2+5+2=9");

    }

    @BeforeEach
    public void eachSetup() {
        model = new NumbleModelEasyModeImpl(10, 3);
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
        assertEquals(NumbleModel.Mode.EASY, model.getMode());
        assertEquals(10, model.getNumRows());
        assertEquals(3, model.getNumCols());
        for (int i = 0; i < model.getNumRows(); i++) {
            for (int j = 0; j < model.getNumCols(); j++) {
                assertEquals(Cell.State.INIT, model.getCells()[i][j].getState());
            }
        }
        assertEquals(5, model.getRhs());
    }

    @Test
    public void noGuessAfterPlayerWins() {
        //Win the game
        String correctSolution = "3+2";
        assertTrue(model.guess(correctSolution));
        assertTrue(model.hasWon());

        //Try another guess
        assertThrows(MethodNotAvailableException.class, () -> model.guess("1+2"));
    }

    @Test
    public void noGuessAfterPlayerLoses() {
        model = new NumbleModelEasyModeImpl(2, 3);
        //Lose the game
        String wrongSolution = "4+1";
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
        String[] invalidInputs = {"", "1", "+", "1234", "1+2+3"};
        for (String input : invalidInputs) {
            assertThrows(IllegalArgumentException.class, () -> model.guess(input));
        }
    }

    /**
     * Check that guesses that include nonsensical syntax throw an error
     */
    @Test
    public void guessWithInvalidChars() {
        String[] invalidInputs = {"1++", "+1-", "1A2", "1/2", "--5"};
        for (String input : invalidInputs) {
            assertThrows(IllegalArgumentException.class, () -> model.guess(input));
        }
    }


    /**
     Check that guesses that add up to the incorrect target value throw an error
     */
    /*@Test
    public void guessWithInvalidValue() {
        String[] invalidInputs = {"1+2", "9*1", "4-2", "123"};
        for (String input: invalidInputs) {
            assertThrows(IllegalArgumentException.class, () -> model.guess(input));
        }
    }*/

    /**
     * Tests case of guesses that represent a decimal value (that would normally be rounded down and treated as an integer) instead throw an error
     */
    @Test
    public void guessWithDecimal() {
        String[] invalidInputs = {"11/2", "22/4"};
        for (String input : invalidInputs) {
            assertThrows(IllegalArgumentException.class, () -> model.guess(input));
        }
    }

    /**
     * Check that valid equations that add up to target value, but are not the exact correct solution, return false.
     */
    @Test
    public void guessReturnsFalse() {
        String[] invalidInputs = {"1+4", "4+1", "2+3"};
        for (String input : invalidInputs) {
            assertFalse(model.guess(input));
        }
    }

    /**
     * Ensures that expressions are evaluated in a left to right fashion, not applying the rules of bodmas.
     */
    /*@Test
    public void checkEvaluatedLeftToRight() {
        model = new NumbleModelEasyModeImpl(2, 5);
        //Strings that would equal the target 9 according to bodmas, but not according to left to right evaluation.
        String[] invalidInputs = {"6+9/3", "1+2*4", "2+1*7"};
        for (String input: invalidInputs) {
            assertThrows(IllegalArgumentException.class, () -> model.guess(input));
        }
    }*/
    @Test
    public void storeCharsInCellAfterWrongGuess() {
        String wrongGuess = "1*5";
        assertFalse(model.guess(wrongGuess));
        assertEquals(1, model.getNumberOfGuessMade());
        for (int i = 0; i < model.getNumCols(); i++) {
            assertEquals(wrongGuess.charAt(i), model.getCells()[0][i].getGuessChar());
        }
    }

    @Test
    public void storeCharsInCellAfterCorrectGuess() {
        String rightGuess = "3+2";
        assertTrue(model.guess(rightGuess));
        assertEquals(1, model.getNumberOfGuessMade());
        for (int i = 0; i < model.getNumCols(); i++) {
            assertEquals(rightGuess.charAt(i), model.getCells()[0][i].getGuessChar());
        }
    }

    @Test
    public void stateUpdateAfterWrongGuessWithAllNonExistChars() {
        String wrongGuess = "1*5";
        assertFalse(model.guess(wrongGuess));
        assertEquals(1, model.getNumberOfGuessMade());
        for (Cell cell : model.getCells()[0]) {
            assertEquals(Cell.State.NOT_EXIST, cell.getState());
        }
    }

    @Test
    public void stateUpdateAfterWrongGuessWithCorrectWrongPosition() {
        String wrongGuess = "2+3";
        assertFalse(model.guess(wrongGuess));
        assertEquals(1, model.getNumberOfGuessMade());
        assertEquals(Cell.State.WRONG_POSITION, model.getCells()[0][0].getState());
        assertEquals(Cell.State.CORRECT, model.getCells()[0][1].getState());
        assertEquals(Cell.State.WRONG_POSITION, model.getCells()[0][2].getState());
    }

    @Test
    public void stateUpdateAfterCorrectGuess() {
        String rightGuess = "3+2";
        assertTrue(model.guess(rightGuess));
        assertEquals(1, model.getNumberOfGuessMade());
        for (Cell cell : model.getCells()[0]) {
            assertEquals(Cell.State.CORRECT, cell.getState());
        }
    }

    @Test
    public void stateUpdateAfterWrongGuessWithMultiSameChars() {
        model = new NumbleModelEasyModeImpl(2, 7);
        String wrongGuess = "1111+22";
        assertFalse(model.guess(wrongGuess));
        assertEquals(1, model.getNumberOfGuessMade());
        assertEquals(Cell.State.WRONG_POSITION, model.getCells()[0][0].getState());
        assertEquals(Cell.State.WRONG_POSITION, model.getCells()[0][1].getState());
        assertEquals(Cell.State.NOT_EXIST, model.getCells()[0][2].getState());
        assertEquals(Cell.State.CORRECT, model.getCells()[0][3].getState());
        assertEquals(Cell.State.CORRECT, model.getCells()[0][4].getState());
        assertEquals(Cell.State.WRONG_POSITION, model.getCells()[0][5].getState());
        assertEquals(Cell.State.NOT_EXIST, model.getCells()[0][6].getState());
    }

    @Test
    public void playerLosesWhenAllGuessAreWrong() {
        model = new NumbleModelEasyModeImpl(2, 3);
        String wrongSolution = "4+1";
        assertFalse(model.guess(wrongSolution));
        assertFalse(model.guess(wrongSolution));
        assertEquals(2, model.getNumberOfGuessMade());
        assertTrue(model.hasLost());
        assertFalse(model.hasWon());
    }


    @Test
    public void playerWinsWhenTheyGuessRightSolution() {
        String correctSolution = "3+2";
        assertTrue(model.guess(correctSolution));
        assertEquals(1, model.getNumberOfGuessMade());
        assertTrue(model.hasWon());
        assertFalse(model.hasLost());
    }

}

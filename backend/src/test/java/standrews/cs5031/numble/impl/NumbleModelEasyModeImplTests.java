package standrews.cs5031.numble.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
    public static void setup() {
        MockedStatic<EquationData> equationData = Mockito.mockStatic(EquationData.class);
        equationData.when(() -> EquationData.getRandomEquation(NumbleModel.Mode.EASY, 3))
                .thenReturn("3+2=5");
    }

    @AfterAll
    public static void close(){
        try {
            equationData.close();
        } catch(NullPointerException e) {
        }
    }

    @Test
    public void gameCreated() {
        model = new NumbleModelEasyModeImpl(2, 3);
        assertEquals(NumbleModel.Mode.EASY, model.getMode());
        assertEquals(2, model.getNumRows());
        assertEquals(3, model.getNumCols());
        for (int i = 0; i < model.getNumRows(); i++) {
            for (int j = 0; j < model.getNumCols(); j++) {
                assertEquals(Cell.State.INIT, model.getCells()[i][j].state);
            }
        }
        assertEquals(5, model.getRhs());

        //Try another guess
        assertThrows(MethodNotAvailableException.class, () -> model.guess("1+2"));
    }

    @Test
    public void noGuessAfterPlayerWins() {
        model = new NumbleModelEasyModeImpl(2, 3);
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
        String wrongSolution = "3+1";
        assertFalse(model.guess(wrongSolution));
        assertFalse(model.guess(wrongSolution));
        assertTrue(model.hasLost());

        //Try another guess
        assertThrows(MethodNotAvailableException.class, () -> model.guess("1+2"));
    }


    @Test
    public void guessWithInvalidInputs() {
        model = new NumbleModelEasyModeImpl(2, 3);
        String[] invalidInputs = {"", "1", "+", "1234", "1+2+3",
                                    "1++", "+1-", "1A2", "1/2", "--5",
                                    "1+2", "9*1", "4-2", "123"};
        for (String input: invalidInputs) {
            assertThrows(IllegalArgumentException.class, () -> model.guess(input));
        }
    }

}

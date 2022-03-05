package standrews.cs5031.numble.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import standrews.cs5031.numble.Cell;
import standrews.cs5031.numble.NumbleModel;
import standrews.cs5031.numble.data.EquationData;
import static org.junit.jupiter.api.Assertions.*;

public class NumbleModelEasyModeImplTests {
    NumbleModelEasyModeImpl model;

    @BeforeEach
    public void setup() {
        model = new NumbleModelEasyModeImpl(2, 3);
        MockedStatic<EquationData> equationData = Mockito.mockStatic(EquationData.class);
        equationData.when(() -> EquationData.getRandomEquation(NumbleModel.Mode.EASY, 3))
                .thenReturn("3+2=5");
    }

    @Test
    public void gameCreated() {
        assertEquals(NumbleModel.Mode.EASY, model.getMode());
        assertEquals(2, model.getNumRows());
        assertEquals(3, model.getNumCols());
        assertEquals(Cell.State.INIT, model.getCells()[0][0].state);
        assertEquals(Cell.State.INIT, model.getCells()[1][2].state);
        assertEquals(5, model.getRhs());
    }
}

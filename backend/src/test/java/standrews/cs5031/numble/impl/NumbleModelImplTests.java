package standrews.cs5031.numble.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import standrews.cs5031.numble.NumbleModel;


public class NumbleModelImplTests {

    @Test
    public void checkGame(){

        NumbleModel game = new NumbleModelEasyModeImpl(5,5);

        assertEquals(game.getNumRows(), 5);
        assertEquals(game.getNumCols(),5);

    }
}

package standrews.cs5031.numble.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import standrews.cs5031.numble.NumbleApplication;
import standrews.cs5031.numble.NumbleModel;


public class NumbleModelImplTests {

    @Test
    public void checkGame(){

        NumbleModel game = new NumbleModelImpl(5,5,10,"2+2+6");

        assertTrue(game.getNumRows()==5);
        assertTrue(game.getNumCols()==5);


    }
}
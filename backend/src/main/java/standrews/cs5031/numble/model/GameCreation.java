package standrews.cs5031.numble.model;

import java.io.Serializable;

/**
 * This shows the values needed to creat a game.
 */
public class GameCreation implements Serializable {
    private int numRows;
    private int numCols;
    private NumbleModel.Mode mode;

    public GameCreation() {

    }

    public GameCreation(int numRows, int numCols, NumbleModel.Mode mode) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.mode = mode;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public NumbleModel.Mode getMode() {
        return mode;
    }
}

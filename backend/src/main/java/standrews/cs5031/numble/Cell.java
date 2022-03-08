package standrews.cs5031.numble;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A simple class for cell
 */
@Schema(name = "Cell", description = "Cell for each guess character with different state")
public class Cell {
    /**
     * Possible states that a cell can have.
     */
    public enum State {INIT, CORRECT, WRONG_POSITION, NOT_EXIST}

    public final int col;
    public final int row;
    public char guessChar;
    public State state;

    public Cell(int row, int col, char guessChar, State state) {
        this.col = col;
        this.row = row;
        this.guessChar = guessChar;
        this.state = state;
    }

    public Cell(int row, int col) {
        //default cell with an empty guess char
        this(row, col, ' ', State.INIT);
    }
}

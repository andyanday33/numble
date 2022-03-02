package standrews.cs5031.numble;

/**
 * A simple class for cell
 */
public class Cell {
    /**
     * Possible states that a cell can have.
     */
    enum State {INIT, CORRECT, WRONG_POSITION, NOT_EXIST}

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

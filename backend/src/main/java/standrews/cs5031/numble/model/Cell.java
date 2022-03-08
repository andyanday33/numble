package standrews.cs5031.numble.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * A simple class for cell
 */
@Schema(name = "Cell", description = "Cell for each guess character with different state")
@Data
public class Cell {
    /**
     * Possible states that a cell can have.
     */
    public enum State {INIT, CORRECT, WRONG_POSITION, NOT_EXIST}

    @Schema(description="Column number of this cell", example = "0", required = true)
    @Setter(AccessLevel.NONE)
    private final int col;

    @Schema(description="Row number of this cell", example = "0", required = true)
    @Setter(AccessLevel.NONE)
    private final int row;

    @Schema(description="Character player guessed in this cell", example = "+")
    private char guessChar;


    @Schema(description="State of this cell", example = "CORRECT", required = true, defaultValue = "INIT")
    private State state;

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

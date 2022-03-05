package standrews.cs5031.numble.data;

import standrews.cs5031.numble.NumbleModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This provides lists of equations for the game to select.
 * They are classified based on the game mode and the length.
 */
public class EquationData {
    private static final String[] EASY_MODE_LENGTH_THREE = {"1+2=3", "3*4=12", "8-2=6", "7*5=35", "3*3=9"};
    private static final String[] EASY_MODE_LENGTH_FOUR = {"10+2=12", "31*0=0", "81-2=79", "7*25=175", "36+3=39"};

    private static final String[] HARD_MODE_LENGTH_FIVE = {"1+2=3", "2*4=8", "9-5=4", "6*0=0", "3*3=9"};
    private static final String[] HARD_MODE_LENGTH_SIX = {"3*4=12", "31*0=0", "7*5=35", "10-7=3", "8+7=15"};

    private static final Map<Integer, String[]> EASY_MODE_EQUATIONS = new HashMap<>();
    private static final Map<Integer, String[]> HARD_MODE_EQUATIONS = new HashMap<>();

    //initialise the equations map according to the length.
    static {
        EASY_MODE_EQUATIONS.put(3, EASY_MODE_LENGTH_THREE);
        EASY_MODE_EQUATIONS.put(4, EASY_MODE_LENGTH_FOUR);

        HARD_MODE_EQUATIONS.put(5, HARD_MODE_LENGTH_FIVE);
        HARD_MODE_EQUATIONS.put(6, HARD_MODE_LENGTH_SIX);
    }

    /**
     * Get a random equation from static stored equations from this class.
     * @param mode game mode: easy or hard
     * @param length the length of input users will guess. For easy mode, the length refers to
     *               the left-hand side of an equation, while it refers to the whole length for hard mode.
     * @return an equation represented by a String.
     * @throws IllegalArgumentException if there is no equation list for this length and mode.
     */
    public static String getRandomEquation(NumbleModel.Mode mode, int length) {
        String[] equations = null;
        if (mode == NumbleModel.Mode.EASY) {
            equations = EASY_MODE_EQUATIONS.get(length);
        } else if (mode == NumbleModel.Mode.HARD){
            //hard mode
            equations = HARD_MODE_EQUATIONS.get(length);
        }
        if (equations != null) {
            Random random = new Random();
            return equations[random.nextInt(equations.length)];
        } else {
            throw new IllegalArgumentException("No equation with length " + length + " in " + mode + " mode");
        }
    }
}

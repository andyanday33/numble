package standrews.cs5031.numble.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MathUtil {

    /**
     * Evaluates the guess expression to an integer. Splits the expression into number-operator pairs and handles each
     * computation from left to right.
     * @param expression String from the user
     * @return int.
     * @throws IllegalArgumentException
     */
    public static int evaluateSimple(String expression) throws IllegalArgumentException {
        //Splits guess string into each operator and the number its operating on. (as we are evaluating left to right)
        String[] guessParts = expression.split("((?=\\*))|((?=\\/))|((?=\\+))|((?=\\-))");

        int total = 0;
        for (int i = 0; i < guessParts.length; i++) {
            //if operator isnt * or /, just parse string and add to running total
            if (!guessParts[i].contains("*") && !guessParts[i].contains("/")) {

                int temp = Integer.parseInt(guessParts[i]);
                total += temp;
            } else if (guessParts[i].charAt(0) == '*') {

                int temp = Integer.parseInt(guessParts[i].substring(1));
                total = total * temp;
            } else if (guessParts[i].charAt(0) == '/') {

                int temp = Integer.parseInt(guessParts[i].substring(1));
                if(total % temp==0){
                    total = total / temp;
                }else{
                    throw new IllegalArgumentException("No decimal values");
                }

            }

        }
        return total;
    }

    /**
     * Evaluates one side of the guess expression to an integer. Splits the expression into number-operator pairs and handles each
     * computation from left to right. Same functionality as evaluate for the EasyMode implementation, but now expressions inside of brackets
     * are evaluated first, then the remaining expression is solved in a left to right fashion.
     * @param expression String from the user
     * @return int.
     * @throws IllegalArgumentException
     */
    public static int evaluateHard(String expression) throws IllegalArgumentException {
        if(expression.contains("(")) {
            if (expression.contains(")")) {
                //Match the outermost parentheses
                Matcher m = Pattern.compile("\\((\\(*(?:[^)(]*|\\([^)]*\\))*\\)*)\\)").matcher(expression);
                while (m.find()) {
                    String sub = m.group(1);    //Matches the inside of the outermost parentheses
                    String subBrackets= "("+sub+")";
                    int sube = evaluateHard(sub);   //Recursively call this function on the inside of the outermost parentheses
                    String suber = String.valueOf(sube);
                    expression = expression.replace(subBrackets, suber); //Replace the inside of the parentheses (with brackets) with the evaluated version
                }
            }

        }

        //Splits guess string into each operator and the number its operating on. (as we are evaluating left to right)
        String[] guessParts = expression.split("((?=\\*))|((?=\\/))|((?=\\+))|((?=\\-))");

        int total = 0;
        for (int i = 0; i < guessParts.length; i++) {
            //if operator isnt * or /, just parse string and add to running total
            if (!guessParts[i].contains("*") && !guessParts[i].contains("/")) {

                int temp = Integer.parseInt(guessParts[i]);
                total += temp;
            } else if (guessParts[i].charAt(0) == '*') {

                int temp = Integer.parseInt(guessParts[i].substring(1));
                total = total * temp;
            } else if (guessParts[i].charAt(0) == '/') {

                int temp = Integer.parseInt(guessParts[i].substring(1));
                if (total % temp == 0) {
                    total = total / temp;
                } else {
                    throw new IllegalArgumentException("No decimal values");
                }

            }
        }
        return total;
    }
}

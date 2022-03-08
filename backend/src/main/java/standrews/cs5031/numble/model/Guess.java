package standrews.cs5031.numble.model;

import java.io.Serializable;

public class Guess implements Serializable {
    private String expression;

    public Guess() {

    }

    public Guess(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }
}
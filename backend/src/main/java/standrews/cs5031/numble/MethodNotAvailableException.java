package standrews.cs5031.numble;

/**
 * Thrown to indicate that a method should not be called.
 * @author 200011181
 */
public class MethodNotAvailableException extends RuntimeException {
    /**
     * Constructs a MethodNotAvailableException with no detail message.
     */
    public MethodNotAvailableException() {
        super();
    }

    /**
     * Constructs a MethodNotAvailableException with the specified detail
     * message.
     *
     * @param s the String that contains a detailed message (can be null)
     */
    public MethodNotAvailableException(String s) {
        super(s);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * @param  message the detail message (can be null)
     * @param  cause the cause (can be null)
     */
    public MethodNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    static final long serialVersionUID = -4935462373575602025L;
}

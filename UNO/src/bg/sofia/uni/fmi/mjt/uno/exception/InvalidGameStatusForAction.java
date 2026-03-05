package bg.sofia.uni.fmi.mjt.uno.exception;

public class InvalidGameStatusForAction extends Exception {
    public InvalidGameStatusForAction(String message) {
        super(message);
    }

    public InvalidGameStatusForAction(String message, Throwable cause) {
        super(message, cause);
    }
}

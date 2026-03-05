package bg.sofia.uni.fmi.mjt.uno.exception;

public class MaximumNumberOfPlayersReached extends Exception {
    public MaximumNumberOfPlayersReached(String message) {
        super(message);
    }

    public MaximumNumberOfPlayersReached(String message, Throwable cause) {
        super(message, cause);
    }
}

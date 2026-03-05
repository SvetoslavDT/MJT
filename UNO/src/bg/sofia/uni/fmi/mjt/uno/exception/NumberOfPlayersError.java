package bg.sofia.uni.fmi.mjt.uno.exception;

public class NumberOfPlayersError extends Exception {
    public NumberOfPlayersError(String message) {
        super(message);
    }

    public NumberOfPlayersError(String message, Throwable cause) {
        super(message, cause);
    }
}

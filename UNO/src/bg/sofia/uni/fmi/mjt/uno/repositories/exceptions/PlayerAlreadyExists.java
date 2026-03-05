package bg.sofia.uni.fmi.mjt.uno.repositories.exceptions;

public class PlayerAlreadyExists extends Exception {
    public PlayerAlreadyExists(String message) {
        super(message);
    }

    public PlayerAlreadyExists(String message, Throwable cause) {
        super(message, cause);
    }
}

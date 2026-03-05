package bg.sofia.uni.fmi.mjt.uno.repositories.exceptions;

public class GameAlreadyExists extends Exception {
    public GameAlreadyExists(String message) {
        super(message);
    }

    public GameAlreadyExists(String message, Throwable cause) {
        super(message, cause);
    }
}

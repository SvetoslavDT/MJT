package bg.sofia.uni.fmi.mjt.uno.command.exceptions;

public class PlayerDoesNotExist extends Exception {
    public PlayerDoesNotExist(String message) {
        super(message);
    }

    public PlayerDoesNotExist(String message, Throwable cause) {
        super(message, cause);
    }
}

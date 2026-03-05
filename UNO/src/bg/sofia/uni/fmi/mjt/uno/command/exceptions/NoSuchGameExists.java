package bg.sofia.uni.fmi.mjt.uno.command.exceptions;

public class NoSuchGameExists extends Exception {
    public NoSuchGameExists(String message) {
        super(message);
    }

    public NoSuchGameExists(String message, Throwable cause) {
        super(message, cause);
    }
}

package bg.sofia.uni.fmi.mjt.uno.command.exceptions;

public class InvalidPassword extends Exception {
    public InvalidPassword(String message) {
        super(message);
    }

    public InvalidPassword(String message, Throwable cause) {
        super(message, cause);
    }
}

package bg.sofia.uni.fmi.mjt.uno.command.exceptions;

public class IncorrectCommand extends Exception {
    public IncorrectCommand(String message) {
        super(message);
    }

    public IncorrectCommand(String message, Throwable cause) {
        super(message, cause);
    }
}

package bg.sofia.uni.fmi.mjt.uno.command.exceptions;

public class PlayerAlreadyLogged extends Exception {
    public PlayerAlreadyLogged(String message) {
        super(message);
    }

    public PlayerAlreadyLogged(String message, Throwable cause) {
        super(message, cause);
    }
}

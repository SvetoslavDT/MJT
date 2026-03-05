package bg.sofia.uni.fmi.mjt.uno.exception;

public class WrongInGameAction extends Exception {
    public WrongInGameAction(String message) {
        super(message);
    }

    public WrongInGameAction(String message, Throwable cause) {
        super(message, cause);
    }
}

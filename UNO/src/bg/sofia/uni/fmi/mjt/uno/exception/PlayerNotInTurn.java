package bg.sofia.uni.fmi.mjt.uno.exception;

public class PlayerNotInTurn extends Exception {
    public PlayerNotInTurn(String message) {
        super(message);
    }

    public PlayerNotInTurn(String message, Throwable cause) {
        super(message, cause);
    }
}

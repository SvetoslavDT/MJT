package bg.sofia.uni.fmi.mjt.uno.exception;

public class PlayerNotInGame extends Exception {
    public PlayerNotInGame(String message) {
        super(message);
    }

    public PlayerNotInGame(String message, Throwable cause) {
        super(message, cause);
    }
}

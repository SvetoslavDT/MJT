package bg.sofia.uni.fmi.mjt.uno.exception;

public class GameMakerMissing extends Exception {
    public GameMakerMissing(String message) {
        super(message);
    }

    public GameMakerMissing(String message, Throwable cause) {
        super(message, cause);
    }
}

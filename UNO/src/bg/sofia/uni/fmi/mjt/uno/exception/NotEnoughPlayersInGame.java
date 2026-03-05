package bg.sofia.uni.fmi.mjt.uno.exception;

public class NotEnoughPlayersInGame extends Exception {
    public NotEnoughPlayersInGame(String message) {
        super(message);
    }

    public NotEnoughPlayersInGame(String message, Throwable cause) {
    }
}

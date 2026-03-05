package bg.sofia.uni.fmi.mjt.uno.exception;

public class PlayerWithThatUsernameAlreadyLogged extends Exception {
    public PlayerWithThatUsernameAlreadyLogged(String message) {
        super(message);
    }

    public PlayerWithThatUsernameAlreadyLogged(String message, Throwable cause) {
        super(message, cause);
    }
}

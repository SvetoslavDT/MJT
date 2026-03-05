package bg.sofia.uni.fmi.mjt.uno.nio.exceptions;

public class ClientAlreadyLogged extends Exception {
    public ClientAlreadyLogged(String message) {
        super(message);
    }

    public ClientAlreadyLogged(String message, Throwable cause) {
        super(message, cause);
    }
}

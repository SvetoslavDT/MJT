package bg.sofia.uni.fmi.mjt.uno.nio.exceptions;

public class ClientNotLogged extends Exception {
    public ClientNotLogged(String message) {
        super(message);
    }

    public ClientNotLogged(String message, Throwable cause) {
        super(message, cause);
    }
}

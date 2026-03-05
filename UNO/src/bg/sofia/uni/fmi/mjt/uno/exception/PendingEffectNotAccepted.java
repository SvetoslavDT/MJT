package bg.sofia.uni.fmi.mjt.uno.exception;

public class PendingEffectNotAccepted extends Exception {
    public PendingEffectNotAccepted(String message) {
        super(message);
    }

    public PendingEffectNotAccepted(String message, Throwable cause) {
        super(message, cause);
    }
}

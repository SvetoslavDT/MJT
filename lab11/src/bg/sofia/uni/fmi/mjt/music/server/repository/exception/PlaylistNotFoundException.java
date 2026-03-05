package bg.sofia.uni.fmi.mjt.music.server.repository.exception;

public class PlaylistNotFoundException extends Exception {
    public PlaylistNotFoundException(String message) {
        super(message);
    }

    public PlaylistNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

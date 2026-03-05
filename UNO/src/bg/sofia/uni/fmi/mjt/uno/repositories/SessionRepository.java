package bg.sofia.uni.fmi.mjt.uno.repositories;

import bg.sofia.uni.fmi.mjt.uno.pipeline.Session;
import bg.sofia.uni.fmi.mjt.uno.players.Player;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionRepository {

    private static final SessionRepository INSTANCE = new SessionRepository();

    private final Set<Session> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final ConcurrentHashMap<String, Session> activeByUsername = new ConcurrentHashMap<>();

    private SessionRepository() {
    }

    public static SessionRepository getInstance() {
        return INSTANCE;
    }

    public void addSession(Session s) {
        if (s == null) {
            return;
        }
        sessions.add(s);
    }

    public void removeSession(Session session) {
        if (session == null) {
            return;
        }

        sessions.remove(session);

        String username = session.getUsername();
        if (username != null) {
            activeByUsername.remove(username, session);
        }

        session.setPlayer(null);
    }

    public Session getByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }

        return activeByUsername.get(username);
    }

    public void broadcastToGame(long gameId, String message) throws IOException {
        if (message == null || message.isBlank()) {
            return;
        }

        List<String> gamePlayers = GameRepository.getGame(gameId).getPlayersNames();

        for (String player : gamePlayers) {
            Session session = activeByUsername.get(player);
            if (session == null) {
                continue;
            }

            try {
                session.write(message + System.lineSeparator());
            } catch (IOException e) {
                System.out.println("Failed to write to " + player + ": " + e.getMessage());
                removeSession(session);
            }
        }
    }

    public void registerUsername(Session session, String username) {

        var player = PlayerRepository.getPlayer(username);
        if (player != null) {
            player.setActive(true);
        }
        session.setPlayer(player);

        Session previous = activeByUsername.put(username, session);
        if (previous != null && previous != session) {

            removeSession(previous);
            try {
                var sk = previous.getSelectionKey();
                if (sk != null) {
                    sk.cancel();
                    if (sk.channel() != null) {
                        sk.channel().close();
                    }
                }
            } catch (IOException ignored) {
                // To be ignored
            }
        }
    }

    public void unregisterUsername(Session session) {
        String username = session.getUsername();
        if (username != null) {
            activeByUsername.remove(username, session);
        }

        Player player = session.getPlayer();
        if (player != null) {
            player.setActive(false);
        }

        session.setPlayer(null);
    }
}

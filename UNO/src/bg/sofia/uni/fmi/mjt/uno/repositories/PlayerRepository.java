package bg.sofia.uni.fmi.mjt.uno.repositories;

import bg.sofia.uni.fmi.mjt.uno.players.Player;
import bg.sofia.uni.fmi.mjt.uno.repositories.exceptions.PlayerAlreadyExists;

import java.util.HashMap;
import java.util.Map;

public final class PlayerRepository {

    // private static PlayerRepository instance;

    private static final Map<String, Player> PLAYERS = new HashMap<>();

    private PlayerRepository() {
    }

//    public static PlayerRepository getInstance() {
//        if (instance == null) {
//            instance = new PlayerRepository();
//        }
//        return instance;
//    }

    public static void addPlayer(String username, String password) throws PlayerAlreadyExists {
        if (PLAYERS.containsKey(username)) {
            throw new PlayerAlreadyExists("Player with name " + username + " already exists");
        }
        PLAYERS.put(username, new Player(username, password));
    }

    public static boolean playerExists(String username) {
        return PLAYERS.containsKey(username);
    }

    public static Player getPlayer(String username) {
        return PLAYERS.get(username);
    }
}

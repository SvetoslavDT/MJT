package bg.sofia.uni.fmi.mjt.uno.repositories;

import bg.sofia.uni.fmi.mjt.uno.repositories.exceptions.GameAlreadyExists;
import bg.sofia.uni.fmi.mjt.uno.exception.NumberOfPlayersError;
import bg.sofia.uni.fmi.mjt.uno.game.Game;

import java.util.HashMap;
import java.util.Map;

public final class GameRepository {

    private static final Map<Long, Game> GAMES = new HashMap<>();

    private GameRepository() {
    }

//    public static GameRepository getInstance() {
//        if (instance == null) {
//            instance = new GameRepository();
//        }
//        return instance;
//    }

    public static Game getGame(long id) {
        return GAMES.get(id);
    }

    public static Map<Long, Game> getGames() {
        return GAMES;
    }

    public static void createGame(String username, int maxPlayers, long gameId)
        throws NumberOfPlayersError, GameAlreadyExists {
        if (GAMES.containsKey(gameId)) {
            throw new GameAlreadyExists("Game already exists");
        } else {
            GAMES.put(gameId, new Game(username, maxPlayers, gameId));
        }
    }
}

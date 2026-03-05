package bg.sofia.uni.fmi.mjt.uno.command.commands;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.CommandType;
import bg.sofia.uni.fmi.mjt.uno.command.exceptions.NoSuchGameExists;
import bg.sofia.uni.fmi.mjt.uno.exception.CardIdException;
import bg.sofia.uni.fmi.mjt.uno.exception.GameMakerMissing;
import bg.sofia.uni.fmi.mjt.uno.exception.InvalidGameStatusForAction;
import bg.sofia.uni.fmi.mjt.uno.exception.NotEnoughPlayersInGame;
import bg.sofia.uni.fmi.mjt.uno.exception.PlayerNotInGame;
import bg.sofia.uni.fmi.mjt.uno.exception.PlayerNotInTurn;
import bg.sofia.uni.fmi.mjt.uno.exception.WrongInGameAction;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientNotLogged;
import bg.sofia.uni.fmi.mjt.uno.pipeline.Session;
import bg.sofia.uni.fmi.mjt.uno.repositories.GameRepository;

public final class StartCommand implements Command {

    private static final String START_MESSAGE = "Game has started! Good luck!";

    private final Session session;
    private long gameId;

    public StartCommand(Session session) {
        this.session = session;
    }

    @Override
    public void execute()
        throws ClientNotLogged, GameMakerMissing, PlayerNotInTurn, WrongInGameAction, InvalidGameStatusForAction,
        PlayerNotInGame, CardIdException, NotEnoughPlayersInGame, NoSuchGameExists {
        if (session.getPlayer() == null) {
            throw new ClientNotLogged("Client needs to be logged to use this command");
        } else if (!session.getPlayer().inGame()) {
            throw new ClientNotLogged("Player is not in game");
        }

        gameId = session.getPlayer().getInGame().get();
        if (GameRepository.getGame(gameId) == null) {
            throw new NoSuchGameExists("No such game with id: " + gameId);
        } else if (!GameRepository.getGame(gameId).hasPlayer(session.getUsername())) {
            throw new ClientNotLogged("Player is not in the game with id: " + gameId);
        } else if (!GameRepository.getGame(gameId).getGameMaker().equals(session.getUsername())) {
            throw new GameMakerMissing("Only the game maker can start the game");
        }

        GameRepository.getGame(gameId).startGame();
    }

    @Override
    public CommandType commandType() {
        return CommandType.START;
    }

    public long getGameId() {
        return gameId;
    }

    public static String getStartMessage() {
        return START_MESSAGE;
    }
}

package bg.sofia.uni.fmi.mjt.uno.command.commands;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.CommandType;
import bg.sofia.uni.fmi.mjt.uno.command.exceptions.NoSuchGameExists;
import bg.sofia.uni.fmi.mjt.uno.command.exceptions.PlayerAlreadyLogged;
import bg.sofia.uni.fmi.mjt.uno.exception.InvalidGameStatusForAction;
import bg.sofia.uni.fmi.mjt.uno.exception.MaximumNumberOfPlayersReached;
import bg.sofia.uni.fmi.mjt.uno.exception.PlayerWithThatUsernameAlreadyLogged;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientNotLogged;
import bg.sofia.uni.fmi.mjt.uno.pipeline.Session;
import bg.sofia.uni.fmi.mjt.uno.repositories.GameRepository;

import java.io.IOException;
import java.util.Optional;

public final class JoinCommand implements Command {

    private final long gameId;
    private final String username;
    private final Session session;

    public JoinCommand(long gameId, String username, Session session) {
        this.gameId = gameId;
        if (username == null || username.isBlank()) {
            this.username = null;
        } else {
            this.username = username;
        }
        this.session = session;
    }

    @Override
    public void execute() throws ClientNotLogged, PlayerWithThatUsernameAlreadyLogged, MaximumNumberOfPlayersReached,
        InvalidGameStatusForAction, NoSuchGameExists, PlayerAlreadyLogged, IOException {

        if (session.getPlayer() == null) {
            throw new ClientNotLogged("Client needs to be logged to use this command");
        } else if (session.getPlayer().inGame()) {
            throw new PlayerAlreadyLogged("Client already logged in game");
        } else if (GameRepository.getGame(gameId) == null) {
            throw new NoSuchGameExists("Game with id - " + gameId + " does not exist");
        }

        GameRepository.getGame(gameId).addPlayer(session.getPlayer(), username);
        session.getPlayer().setInGame(Optional.of(gameId));

        session.write("Successfully joined game with id - " + gameId + System.lineSeparator());
    }

    @Override
    public CommandType commandType() {
        return CommandType.JOIN;
    }
}

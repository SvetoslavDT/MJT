package bg.sofia.uni.fmi.mjt.uno.command.commands;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.CommandType;
import bg.sofia.uni.fmi.mjt.uno.exception.NumberOfPlayersError;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientAlreadyLogged;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientNotLogged;
import bg.sofia.uni.fmi.mjt.uno.pipeline.Session;
import bg.sofia.uni.fmi.mjt.uno.repositories.GameRepository;
import bg.sofia.uni.fmi.mjt.uno.repositories.exceptions.GameAlreadyExists;

import java.io.IOException;

public final class CreateGameCommand implements Command {

    private final int numberOfPlayers;
    private final long gameId;
    private final Session session;

    public CreateGameCommand(int numberOfPlayers, long gameId, Session session) {
        this.numberOfPlayers = numberOfPlayers;
        this.gameId = gameId;
        this.session = session;
    }

    @Override
    public void execute()
        throws ClientNotLogged, GameAlreadyExists, NumberOfPlayersError, ClientAlreadyLogged, IOException {
        if (session.getPlayer() == null) {
            throw new ClientNotLogged("Client needs to be logged to use this command");
        } else if (session.getPlayer().inGame()) {
            throw new ClientAlreadyLogged("Player is in game");
        }

        GameRepository.createGame(session.getUsername(), numberOfPlayers, gameId);

        session.write("Successfully created a game with id - " + gameId + System.lineSeparator());
    }

    @Override
    public CommandType commandType() {
        return CommandType.CREATE_GAME;
    }
}

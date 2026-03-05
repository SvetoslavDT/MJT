package bg.sofia.uni.fmi.mjt.uno.command.commands;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.CommandType;
import bg.sofia.uni.fmi.mjt.uno.command.exceptions.NoSuchGameExists;
import bg.sofia.uni.fmi.mjt.uno.command.exceptions.PlayerAlreadyLogged;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientNotLogged;
import bg.sofia.uni.fmi.mjt.uno.pipeline.Session;
import bg.sofia.uni.fmi.mjt.uno.repositories.GameRepository;

import java.io.IOException;

public final class SummaryCommand implements Command {

    private final Session session;
    private final long gameId;

    public SummaryCommand(Session session, long gameId) {
        this.session = session;
        this.gameId = gameId;
    }

    @Override
    public void execute() throws ClientNotLogged, NoSuchGameExists, PlayerAlreadyLogged, IOException {
        if (session.getPlayer() == null) {
            throw new ClientNotLogged("Client needs to be logged to use this command");
        } else if (session.getPlayer().inGame()) {
            throw new PlayerAlreadyLogged("Client already logged in game");
        } else if (GameRepository.getGame(gameId) == null) {
            throw new NoSuchGameExists("Game with id - " + gameId + " does not exist");
        }

        String summary = GameRepository.getGame(gameId).getGameSummary().toString();

        session.write(summary + System.lineSeparator());
    }

    @Override
    public CommandType commandType() {
        return CommandType.SUMMARY;
    }
}

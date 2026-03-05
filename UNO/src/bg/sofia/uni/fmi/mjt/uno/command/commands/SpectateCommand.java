package bg.sofia.uni.fmi.mjt.uno.command.commands;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.CommandType;
import bg.sofia.uni.fmi.mjt.uno.exception.InvalidGameStatusForAction;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientAlreadyLogged;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientNotLogged;
import bg.sofia.uni.fmi.mjt.uno.pipeline.Session;
import bg.sofia.uni.fmi.mjt.uno.repositories.GameRepository;

import java.io.IOException;

public final class SpectateCommand implements Command {

    private final Session session;

    public SpectateCommand(Session session) {
        this.session = session;
    }

    @Override
    public void execute() throws ClientNotLogged, ClientAlreadyLogged, InvalidGameStatusForAction, IOException {
        if (session.getPlayer() == null) {
            throw new ClientNotLogged("Client needs to be logged to use this command");
        } else if (!session.getPlayer().inGame()) {
            throw new ClientAlreadyLogged("Player is not in game");
        }

        Game game = GameRepository.getGame(session.getPlayer().getInGame().get());

        if (!game.isPlayerSpectating(session.getUsername())) {
            throw new InvalidGameStatusForAction("Cannot spectate while in game");
        }

        session.write(
            "You are now spectating. You can use commands for checking the placed cards." + System.lineSeparator());
    }

    @Override
    public CommandType commandType() {
        return CommandType.SPECTATE;
    }
}

package bg.sofia.uni.fmi.mjt.uno.command.commands;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.CommandType;
import bg.sofia.uni.fmi.mjt.uno.exception.PlayerNotInGame;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientAlreadyLogged;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientNotLogged;
import bg.sofia.uni.fmi.mjt.uno.pipeline.Session;
import bg.sofia.uni.fmi.mjt.uno.repositories.GameRepository;

import java.io.IOException;

public final class LeaveCommand implements Command {

    private final Session session;

    public LeaveCommand(Session session) {
        this.session = session;
    }

    @Override
    public void execute() throws ClientNotLogged, ClientAlreadyLogged, PlayerNotInGame, IOException {
        if (session.getPlayer() == null) {
            throw new ClientNotLogged("Client needs to be logged to use this command");
        } else if (!session.getPlayer().inGame()) {
            throw new ClientAlreadyLogged("Player is not in game");
        }

        Game game = GameRepository.getGame(session.getPlayer().getInGame().get());
        game.removePlayer(session.getUsername());

        session.write("You left the game" + System.lineSeparator());
    }

    @Override
    public CommandType commandType() {
        return CommandType.LEAVE;
    }
}

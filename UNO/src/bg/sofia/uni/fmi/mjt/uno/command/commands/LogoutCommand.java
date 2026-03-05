package bg.sofia.uni.fmi.mjt.uno.command.commands;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.CommandType;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientNotLogged;
import bg.sofia.uni.fmi.mjt.uno.pipeline.Session;
import bg.sofia.uni.fmi.mjt.uno.repositories.PlayerRepository;

import java.io.IOException;

public final class LogoutCommand implements Command {

    private final Session session;

    public LogoutCommand(Session session) {
        this.session = session;
    }

    @Override
    public void execute() throws ClientNotLogged, IOException {
        if (session.getPlayer() == null) {
            throw new ClientNotLogged("Client needs to be logged to log out");
        }

        PlayerRepository.getPlayer(session.getUsername()).setActive(false);
        session.setPlayer(null);

        session.write("Successfully logged out" + System.lineSeparator());
    }

    @Override
    public CommandType commandType() {
        return CommandType.LOGOUT;
    }
}

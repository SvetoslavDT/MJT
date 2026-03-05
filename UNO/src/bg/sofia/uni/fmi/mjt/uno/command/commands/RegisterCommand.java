package bg.sofia.uni.fmi.mjt.uno.command.commands;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.CommandType;
import bg.sofia.uni.fmi.mjt.uno.pipeline.Session;
import bg.sofia.uni.fmi.mjt.uno.repositories.PlayerRepository;
import bg.sofia.uni.fmi.mjt.uno.repositories.exceptions.PlayerAlreadyExists;

import java.io.IOException;

public final class RegisterCommand implements Command {

    private final Session session;
    private final String username;
    private final String password;

    public RegisterCommand(String username, String password, Session session) {
        this.username = username;
        this.password = password;
        this.session = session;
    }

    @Override
    public void execute() throws PlayerAlreadyExists, IOException {
        PlayerRepository.addPlayer(username, password);

        session.write("Registered successfully with name " + username + System.lineSeparator());
    }

    @Override
    public CommandType commandType() {
        return CommandType.REGISTER;
    }
}

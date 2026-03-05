package bg.sofia.uni.fmi.mjt.uno.command.commands;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.CommandType;
import bg.sofia.uni.fmi.mjt.uno.command.exceptions.InvalidPassword;
import bg.sofia.uni.fmi.mjt.uno.command.exceptions.PlayerAlreadyLogged;
import bg.sofia.uni.fmi.mjt.uno.command.exceptions.PlayerDoesNotExist;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientAlreadyLogged;
import bg.sofia.uni.fmi.mjt.uno.pipeline.Session;
import bg.sofia.uni.fmi.mjt.uno.repositories.PlayerRepository;

import java.io.IOException;

public final class LoginCommand implements Command {

    private final String username;
    private final String password;
    private final Session session;

    public LoginCommand(String username, String password, Session session) {
        this.username = username;
        this.password = password;
        this.session = session;
    }

    @Override
    public void execute()
        throws PlayerDoesNotExist, InvalidPassword, ClientAlreadyLogged, PlayerAlreadyLogged, IOException {
        if (!PlayerRepository.playerExists(username)) {
            throw new PlayerDoesNotExist("Player " + username + " does not exist");
        } else if (session.getPlayer() != null) {
            throw new ClientAlreadyLogged("Client is already logged in account");
        } else if (!password.equals(PlayerRepository.getPlayer(username).getPassword())) {
            throw new InvalidPassword("Invalid password");
        }

        PlayerRepository.getPlayer(username).setActive(true);
        session.setPlayer(PlayerRepository.getPlayer(username));

        session.write("Successfully logged in as player - " + username + System.lineSeparator());
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public CommandType commandType() {
        return CommandType.LOGIN;
    }
}

package bg.sofia.uni.fmi.mjt.uno.command.commands;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.CommandType;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientNotLogged;
import bg.sofia.uni.fmi.mjt.uno.pipeline.Session;
import bg.sofia.uni.fmi.mjt.uno.repositories.GameRepository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ListGamesCommand implements Command {

    private final String predicate;
    private final Session session;

    public ListGamesCommand(String predicate, Session session) {
        this.predicate = predicate;
        this.session = session;
    }

    @Override
    public CommandType commandType() {
        return CommandType.LIST_GAMES;
    }

    @Override
    public void execute() throws ClientNotLogged, IOException {
        if (session.getPlayer() == null) {
            throw new ClientNotLogged("Client needs to be logged to use this command");
        }

        String predicate = this.predicate.toUpperCase();

        List<Long> ids = GameRepository.getGames().entrySet().stream()
            .filter(e -> predicate.equals("ALL") ||
                e.getValue().getStatus().toString().equals(predicate))
            .map(Map.Entry::getKey)
            .toList();

        String answer;

        if (ids.isEmpty()) {
            answer = "No games found";
        } else {
            answer = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ", "Games: ", System.lineSeparator()));
        }

        session.write(answer + System.lineSeparator());
    }
}

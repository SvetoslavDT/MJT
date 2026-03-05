package bg.sofia.uni.fmi.mjt.uno.command.commands;

import bg.sofia.uni.fmi.mjt.uno.cards.types.Card;
import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.CommandType;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientAlreadyLogged;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientNotLogged;
import bg.sofia.uni.fmi.mjt.uno.pipeline.Session;
import bg.sofia.uni.fmi.mjt.uno.repositories.GameRepository;

import java.io.IOException;
import java.util.List;

public final class ShowPlayedCardsCommand implements Command {

    private static final int CARDS_PER_LINE = 7;

    private final Session session;

    public ShowPlayedCardsCommand(Session session) {
        this.session = session;
    }

    @Override
    public void execute() throws ClientNotLogged, ClientAlreadyLogged, IOException {
        if (session.getPlayer() == null) {
            throw new ClientNotLogged("Client needs to be logged to use this command");
        } else if (!session.getPlayer().inGame()) {
            throw new ClientAlreadyLogged("Player is not in game");
        }

        StringBuilder result = new StringBuilder();
        List<Card> cardHistory = GameRepository.getGame(session.getPlayer().getInGame().get()).getCardHistory();

        for (int i = 0; i < cardHistory.size(); ++i) {
            if (i % CARDS_PER_LINE == 0) {
                result.append(System.lineSeparator());
            }
            result.append(cardHistory.get(i).toString());
        }

        session.write(result.toString() + System.lineSeparator());
    }

    @Override
    public CommandType commandType() {
        return CommandType.SHOW_PLAYED_CARDS;
    }
}

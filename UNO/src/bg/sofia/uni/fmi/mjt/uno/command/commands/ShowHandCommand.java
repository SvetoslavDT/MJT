package bg.sofia.uni.fmi.mjt.uno.command.commands;

import bg.sofia.uni.fmi.mjt.uno.cards.types.Card;
import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.CommandType;
import bg.sofia.uni.fmi.mjt.uno.game.GameStatus;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientAlreadyLogged;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientNotLogged;
import bg.sofia.uni.fmi.mjt.uno.pipeline.Session;
import bg.sofia.uni.fmi.mjt.uno.repositories.GameRepository;

import java.io.IOException;
import java.util.List;

public final class ShowHandCommand implements Command {

    private final Session session;

    public ShowHandCommand(Session session) {
        this.session = session;
    }

    @Override
    public CommandType commandType() {
        return CommandType.SHOW_HAND;
    }

    @Override
    public void execute() throws IOException, ClientNotLogged, ClientAlreadyLogged {
        if (session.getPlayer() == null) {
            throw new ClientNotLogged("Client needs to be logged to use this command");
        } else if (!session.getPlayer().inGame() ||
            GameRepository.getGame(session.getPlayer().getInGame().get()).getStatus() != GameStatus.STARTED) {
            throw new ClientAlreadyLogged("Player is not in game");
        }

        StringBuilder stringBuilder = new StringBuilder();
        List<Card> cards = session.getPlayer().getDeck().getCards();

        for (int i = 0; i < cards.size(); i++) {
            stringBuilder.append(cards.get(i).toString());
            if (i != cards.size() - 1) {
                stringBuilder.append(" ; ");
            }
        }

        String result = stringBuilder.toString();

        session.write(result + System.lineSeparator());
    }
}

package bg.sofia.uni.fmi.mjt.uno.command.commands;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.CommandType;
import bg.sofia.uni.fmi.mjt.uno.exception.CardIdException;
import bg.sofia.uni.fmi.mjt.uno.exception.InvalidGameStatusForAction;
import bg.sofia.uni.fmi.mjt.uno.exception.PendingEffectNotAccepted;
import bg.sofia.uni.fmi.mjt.uno.exception.PlayerNotInGame;
import bg.sofia.uni.fmi.mjt.uno.exception.PlayerNotInTurn;
import bg.sofia.uni.fmi.mjt.uno.exception.WrongInGameAction;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientAlreadyLogged;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientNotLogged;
import bg.sofia.uni.fmi.mjt.uno.pipeline.Session;
import bg.sofia.uni.fmi.mjt.uno.repositories.GameRepository;

import java.io.IOException;
import java.util.Optional;

public final class PlayCommand implements Command {

    private final Session session;
    private final short cardId;

    public PlayCommand(Session session, short cardId) {
        this.session = session;
        this.cardId = cardId;
    }

    @Override
    public void execute()
        throws ClientNotLogged, ClientAlreadyLogged, CardIdException, PlayerNotInTurn, WrongInGameAction,
        InvalidGameStatusForAction, PlayerNotInGame, PendingEffectNotAccepted, IOException {
        if (session.getPlayer() == null) {
            throw new ClientNotLogged("Client needs to be logged to use this command");
        } else if (!session.getPlayer().inGame()) {
            throw new ClientAlreadyLogged("Player is not in game");
        }

        GameRepository.getGame(session.getPlayer().getInGame().get()).playCardAction(session.getUsername(), cardId,
            Optional.empty());

        if (GameRepository.getGame(session.getPlayer().getInGame().get()).isPlayerSpectating(session.getUsername())) {
            String summary = GameRepository.getGame(session.getPlayer().getInGame().get()).getGameSummary().toString();
            session.write(
                "Congratulations, you played your cards" + System.lineSeparator() + summary + System.lineSeparator());
        }
    }

    @Override
    public CommandType commandType() {
        return CommandType.PLAY;
    }
}

package bg.sofia.uni.fmi.mjt.uno.command.commands;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.CommandType;
import bg.sofia.uni.fmi.mjt.uno.exception.InvalidGameStatusForAction;
import bg.sofia.uni.fmi.mjt.uno.exception.PlayerNotInGame;
import bg.sofia.uni.fmi.mjt.uno.exception.PlayerNotInTurn;
import bg.sofia.uni.fmi.mjt.uno.exception.WrongInGameAction;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientAlreadyLogged;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientNotLogged;
import bg.sofia.uni.fmi.mjt.uno.pipeline.Session;
import bg.sofia.uni.fmi.mjt.uno.repositories.GameRepository;

public final class AcceptEffectCommand implements Command {

    private final Session session;

    public AcceptEffectCommand(Session session) {
        this.session = session;
    }

    @Override
    public void execute()
        throws ClientNotLogged, ClientAlreadyLogged, PlayerNotInTurn, WrongInGameAction, InvalidGameStatusForAction,
        PlayerNotInGame {
        if (session.getPlayer() == null) {
            throw new ClientNotLogged("Client needs to be logged to use this command");
        } else if (!session.getPlayer().inGame()) {
            throw new ClientAlreadyLogged("Player is not in game");
        }

        GameRepository.getGame(session.getPlayer().getInGame().get()).acceptEffect(session.getUsername());
    }

    @Override
    public CommandType commandType() {
        return CommandType.ACCEPT_EFFECT;
    }
}

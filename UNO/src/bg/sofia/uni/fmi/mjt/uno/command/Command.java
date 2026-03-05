package bg.sofia.uni.fmi.mjt.uno.command;

import bg.sofia.uni.fmi.mjt.uno.command.exceptions.InvalidPassword;
import bg.sofia.uni.fmi.mjt.uno.command.exceptions.NoSuchGameExists;
import bg.sofia.uni.fmi.mjt.uno.command.exceptions.PlayerAlreadyLogged;
import bg.sofia.uni.fmi.mjt.uno.command.exceptions.PlayerDoesNotExist;
import bg.sofia.uni.fmi.mjt.uno.exception.CardIdException;
import bg.sofia.uni.fmi.mjt.uno.exception.GameMakerMissing;
import bg.sofia.uni.fmi.mjt.uno.exception.InvalidGameStatusForAction;
import bg.sofia.uni.fmi.mjt.uno.exception.MaximumNumberOfPlayersReached;
import bg.sofia.uni.fmi.mjt.uno.exception.NotEnoughPlayersInGame;
import bg.sofia.uni.fmi.mjt.uno.exception.NumberOfPlayersError;
import bg.sofia.uni.fmi.mjt.uno.exception.PendingEffectNotAccepted;
import bg.sofia.uni.fmi.mjt.uno.exception.PlayerNotInGame;
import bg.sofia.uni.fmi.mjt.uno.exception.PlayerNotInTurn;
import bg.sofia.uni.fmi.mjt.uno.exception.PlayerWithThatUsernameAlreadyLogged;
import bg.sofia.uni.fmi.mjt.uno.exception.WrongInGameAction;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientAlreadyLogged;
import bg.sofia.uni.fmi.mjt.uno.nio.exceptions.ClientNotLogged;
import bg.sofia.uni.fmi.mjt.uno.repositories.exceptions.GameAlreadyExists;
import bg.sofia.uni.fmi.mjt.uno.repositories.exceptions.PlayerAlreadyExists;

import java.io.IOException;

public interface Command {

    void execute() throws PlayerAlreadyExists, PlayerDoesNotExist, InvalidPassword, ClientNotLogged, IOException,
        ClientAlreadyLogged, GameAlreadyExists, NumberOfPlayersError, PlayerWithThatUsernameAlreadyLogged,
        MaximumNumberOfPlayersReached, InvalidGameStatusForAction, GameMakerMissing, PlayerNotInTurn, WrongInGameAction,
        PlayerNotInGame, CardIdException, NotEnoughPlayersInGame, NoSuchGameExists, PlayerAlreadyLogged,
        PendingEffectNotAccepted;

    CommandType commandType();
}

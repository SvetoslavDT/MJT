package bg.sofia.uni.fmi.mjt.uno.game;

import bg.sofia.uni.fmi.mjt.uno.cards.Deck;
import bg.sofia.uni.fmi.mjt.uno.cards.enums.ActionType;
import bg.sofia.uni.fmi.mjt.uno.cards.enums.CardType;
import bg.sofia.uni.fmi.mjt.uno.cards.enums.ColorType;
import bg.sofia.uni.fmi.mjt.uno.cards.enums.JokerType;
import bg.sofia.uni.fmi.mjt.uno.cards.types.ActionCard;
import bg.sofia.uni.fmi.mjt.uno.cards.types.Card;
import bg.sofia.uni.fmi.mjt.uno.cards.types.JokerCard;
import bg.sofia.uni.fmi.mjt.uno.exception.InvalidGameStatusForAction;
import bg.sofia.uni.fmi.mjt.uno.exception.MaximumNumberOfPlayersReached;
import bg.sofia.uni.fmi.mjt.uno.exception.CardIdException;
import bg.sofia.uni.fmi.mjt.uno.exception.NotEnoughPlayersInGame;
import bg.sofia.uni.fmi.mjt.uno.exception.NumberOfPlayersError;
import bg.sofia.uni.fmi.mjt.uno.exception.PendingEffectNotAccepted;
import bg.sofia.uni.fmi.mjt.uno.exception.PlayerNotInGame;
import bg.sofia.uni.fmi.mjt.uno.exception.PlayerNotInTurn;
import bg.sofia.uni.fmi.mjt.uno.exception.PlayerWithThatUsernameAlreadyLogged;
import bg.sofia.uni.fmi.mjt.uno.exception.WrongInGameAction;
import bg.sofia.uni.fmi.mjt.uno.players.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class Game {

    private static final Random RANDOM = new Random();
    private static final int MINIMUM_PLAYERS = 2;
    private static final int TWO_CARDS_COUNT = 2;
    private static final int FOUR_CARDS_COUNT = 4;
    private static final short STARTING_CARD_COUNT = 7;
    private static final int MAXIMUM_PLAYERS = 10;

    private final Map<String, Player> players;
    private final List<String> playerTurns;
    private final Set<String> spectatingPlayers;
    private final List<Card> cardHistory;

    private final Deck drawDeck;
    private final Deck putDeck;

    private final String gameMaker;
    private final long gameID;
    private final int maxNumberOfPlayers;
    private GameStatus status;
    private final GameSummary gameSummary;

    private PendingEffect pendingEffect;
    private boolean clockWiseRotation;
    private int currentPlayerIndex;
    private ColorType currentColorType;

    private boolean awaitingInitialColor;

    public Game(String gameMaker, int maxNumberOfPlayers, long gameID) throws NumberOfPlayersError {
        if (maxNumberOfPlayers > MAXIMUM_PLAYERS || maxNumberOfPlayers < MINIMUM_PLAYERS) {
            throw new NumberOfPlayersError("Max number of players is" + MAXIMUM_PLAYERS
                + "and minimum is " + MINIMUM_PLAYERS + ". Prohibited making a game of " + maxNumberOfPlayers);
        }

        this.players = new HashMap<>();
        this.playerTurns = new ArrayList<>();
        this.spectatingPlayers = new HashSet<>();
        this.cardHistory = new ArrayList<>();

        this.gameMaker = gameMaker;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.status = GameStatus.AVAILABLE;
        this.gameSummary = new GameSummary();

        this.drawDeck = new Deck(false);
        this.putDeck = new Deck(true);

        this.gameID = gameID;

        this.pendingEffect = null;
        this.clockWiseRotation = true;
        this.currentPlayerIndex = 0;
        this.currentColorType = null;

        this.awaitingInitialColor = false;
    }

    public void startGame()
        throws NotEnoughPlayersInGame, InvalidGameStatusForAction {
        if (status != GameStatus.AVAILABLE) {
            throw new InvalidGameStatusForAction("Can't start the game because it has" + status.toString());
        } else if (players.size() < MINIMUM_PLAYERS) {
            throw new NotEnoughPlayersInGame("Needed at least " + MINIMUM_PLAYERS + " to start a game");
        }
        status = GameStatus.STARTED;
        for (Player player : players.values()) {
            player.drawCards(drawDeck.getNCards(STARTING_CARD_COUNT));
        }

        Card firstCard = drawDeck.getTopCard();
        while (firstCard.getCardType() == CardType.JOKER
            && ((JokerCard) firstCard).getJokerType() == JokerType.PLUS_FOUR) {
            drawDeck.shuffleCardIn(firstCard, RANDOM);
            firstCard = drawDeck.getTopCard();
        }

        if (firstCard.getCardType() == CardType.JOKER) {
            awaitingInitialColor = true;
            putDeck.fillDeck(firstCard);
        } else {
            playCard(firstCard, Optional.empty());
            putDeck.putCard(firstCard);
        }

        cardHistory.add(firstCard);
    }

    public void playCardAction(String username, short cardId, Optional<ColorType> chosenColor)
        throws InvalidGameStatusForAction, PlayerNotInGame, PlayerNotInTurn, WrongInGameAction, CardIdException,
        PendingEffectNotAccepted {

        playCardActionChecks(username, cardId, chosenColor);

        throwExceptionIfCardShouldChangeColor(username, cardId, chosenColor);
        Card card = players.get(username).getDeck().drawSpesificCard(cardId);

        playCard(card, chosenColor);

        putDeck.putCard(card);
        cardHistory.add(card);

        checkIfPlayerWins(username);

        if (playerTurns.size() < MINIMUM_PLAYERS) {
            endGame();
        }

        movePlayerIndex();
    }

    private void endGame() throws PlayerNotInGame {
        gameSummary.addLoser(playerTurns.getFirst());
        spectatingPlayers.add(playerTurns.getFirst());
        removePlayer(playerTurns.getFirst());

        status = GameStatus.ENDED;
    }

    private void playCard(Card card, Optional<ColorType> chosenColor) {
        if (card.getCardType() == CardType.JOKER) {

            currentColorType = chosenColor.get();
            JokerCard jokerCard = (JokerCard) card;
            if (jokerCard.getJokerType() == JokerType.PLUS_FOUR) {
                pendingEffect = PendingEffect.DRAW_FOUR;
            }

        } else if (card.getCardType() == CardType.ACTION) {

            ActionCard actionCard = (ActionCard) card;
            if (actionCard.getActionType() == ActionType.PLUS_TWO) {
                pendingEffect = PendingEffect.DRAW_TWO;
            } else if (actionCard.getActionType() == ActionType.SKIP) {
                movePlayerIndex();
            } else if (actionCard.getActionType() == ActionType.REVERSE) {
                if (players.size() == MINIMUM_PLAYERS) {
                    movePlayerIndex();
                } else {
                    reverseRotation();
                }
            }
            currentColorType = card.getColor();

        } else if (card.getCardType() == CardType.NUMBER) {
            currentColorType = card.getColor();
        }
    }

    public void acceptEffect(String username)
        throws PlayerNotInTurn, InvalidGameStatusForAction, PlayerNotInGame, WrongInGameAction {
        throwExceptionIfGameHasNotStarted();
        throwExceptionIfPlayerIsSpectating(username);
        throwExceptionIfPlayerNotInGame(username);
        throwExceptionIfNotPlayersTurn(username);

        if (pendingEffect == null) {
            throw new WrongInGameAction("No pending effect to accept");
        } else if (pendingEffect == PendingEffect.DRAW_TWO) {
            drawCards(username, TWO_CARDS_COUNT);
        } else if (pendingEffect == PendingEffect.DRAW_FOUR) {
            drawCards(username, FOUR_CARDS_COUNT);
        }

        pendingEffect = null;
        movePlayerIndex();
    }

    public boolean canPlayerDraw(String username) {
        if (spectatingPlayers.contains(username)) {
            return false;
        }

        Card topCard = putDeck.peekTopCard();

        for (var card : players.get(username).getDeck().getCards()) {
            if (card.canBePlayed(topCard, currentColorType)) {
                return false;
            }
        }

        return true;
    }

    private void drawCard(String username) {
        Card drawnCard = drawDeck.getTopCard();
        players.get(username).drawCard(drawnCard);

        if (drawDeck.getSize() == 0) {
            Card topCard = putDeck.getTopCard();
            for (int i = 0; i < putDeck.getSize(); ++i) {
                drawDeck.putCard(putDeck.getTopCard());
            }
            putDeck.putCard(topCard);
        }
    }

    public void playerDrawCard(String username)
        throws WrongInGameAction, InvalidGameStatusForAction, PlayerNotInGame, PlayerNotInTurn {
        throwExceptionIfGameHasNotStarted();
        throwExceptionIfPlayerIsSpectating(username);
        throwExceptionIfPlayerNotInGame(username);
        throwExceptionIfNotPlayersTurn(username);
        if (!canPlayerDraw(username)) {
            throw new WrongInGameAction("Cannot draw");
        }

        drawCard(username);
        Card drawnCard = players.get(username).getDeck().peekTopCard();
        if (!drawnCard.canBePlayed(putDeck.peekTopCard(), currentColorType)) {
            movePlayerIndex();
        }
    }

    private void drawCards(String username, int count) {
        for (int i = 0; i < count; ++i) {
            drawCard(username);
        }
    }

    public void addPlayer(Player player, String username)
        throws PlayerWithThatUsernameAlreadyLogged, MaximumNumberOfPlayersReached, InvalidGameStatusForAction {
        if (status != GameStatus.AVAILABLE) {
            throw new InvalidGameStatusForAction("Can't join because the game status is " +
                status.toString().toLowerCase());
        } else if (players.size() >= maxNumberOfPlayers) {
            throw new MaximumNumberOfPlayersReached("Maximum number of " + maxNumberOfPlayers + " players reached");
        }

        if (username != null) {
            if (hasPlayer(username)) {
                throw new PlayerWithThatUsernameAlreadyLogged("User \"" + username + "\" already is logged");
            }
            player.setGameName(username);
        } else {
            if (hasPlayer(player.getUsername())) {
                throw new PlayerWithThatUsernameAlreadyLogged("User \"" + player.getUsername()
                    + "\" already is logged");
            }
            player.setGameName(player.getUsername());
        }

        players.put(player.getUsername(), player);
        playerTurns.add(player.getUsername());

        player.setInGame(Optional.of(gameID));
    }

    public boolean hasPlayer(String username) {
        return players.containsKey(username);
    }

    private void shufflePlayerCardsInDrawDeck(Deck playerDeck) {
        List<Card> cards = playerDeck.getCards();

        for (Card card : cards) {
            drawDeck.getCards().add(RANDOM.nextInt(drawDeck.getSize() + 1), card);
        }
    }

    public void removePlayer(String username) throws PlayerNotInGame {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        throwExceptionIfPlayerNotInGame(username);

        int toRemovePlayerIndex = playerTurns.indexOf(username);
        if (status == GameStatus.STARTED && players.get(username).getDeck().getSize() != 0) {
            shufflePlayerCardsInDrawDeck(players.get(username).getDeck());
        }

        players.get(username).setInGame(Optional.empty());
        players.get(username).setGameName(null);

        players.remove(username);
        playerTurns.remove(username);
        spectatingPlayers.remove(username);

        if (toRemovePlayerIndex < currentPlayerIndex) {
            currentPlayerIndex--;
        }

        if (players.size() < MINIMUM_PLAYERS) {
            endGame();
        }
    }

    private void movePlayerIndex() {
        if (clockWiseRotation) {
            currentPlayerIndex = (currentPlayerIndex + 1) % playerTurns.size();
        } else {
            currentPlayerIndex = currentPlayerIndex == 0 ? playerTurns.size() - 1 : currentPlayerIndex - 1;
        }
    }

    private void reverseRotation() {
        clockWiseRotation = !clockWiseRotation;
    }

    private void throwExceptionIfNotPlayersTurn(String username) throws PlayerNotInTurn {
        if (hasPlayer(username)) {
            if (playerTurns.indexOf(username) != currentPlayerIndex) {
                throw new PlayerNotInTurn("Player has to wait for his turn to play");
            }
        }
    }

    private void throwExceptionIfGameHasNotStarted() throws InvalidGameStatusForAction {
        if (status != GameStatus.STARTED) {
            throw new InvalidGameStatusForAction("Game hasn't started");
        }
    }

    private void throwExceptionIfPlayerNotInGame(String username) throws PlayerNotInGame {
        if (!hasPlayer(username)) {
            throw new PlayerNotInGame("Player \"" + username + "\" not present in the game");
        }
    }

    private void throwExceptionIfCardShouldChangeColor(String username, short cardId, Optional<ColorType> chosenColor)
        throws CardIdException {
        if (awaitingInitialColor) {
            awaitingInitialColor = false;
            return;
        }

        if (chosenColor.isPresent()) {
            Card card = players.get(username).getDeck().getSpecificCard(cardId);
            if (card.getCardType() != CardType.JOKER) {
                throw new CardIdException("Card was expected to be a joker to change color");
            }
        }
    }

    private void throwExceptionIfPlayerIsSpectating(String username) throws InvalidGameStatusForAction {
        if (spectatingPlayers.contains(username)) {
            throw new InvalidGameStatusForAction("Cannot do that while spectating");
        }
    }

    private void firstCardChangeColor(Optional<ColorType> chosenColor) {
        if (!awaitingInitialColor) {
            return;
        }

        if (chosenColor.isPresent()) {
            currentColorType = chosenColor.get();
        } else {
            ColorType[] colors = ColorType.values();
            currentColorType = colors[RANDOM.nextInt(colors.length)];
        }
    }

    private void checkIfPlayerWins(String username) throws PlayerNotInGame {
        if (players.get(username).getDeck().getSize() == 0) {
            gameSummary.addWinner(username);
            removePlayer(username);
            spectatingPlayers.add(username);
        }
    }

    private void playCardActionChecks(String username, short cardId, Optional<ColorType> chosenColor)
        throws InvalidGameStatusForAction, PlayerNotInGame, PlayerNotInTurn, PendingEffectNotAccepted,
        WrongInGameAction, CardIdException {
        throwExceptionIfGameHasNotStarted();
        throwExceptionIfPlayerIsSpectating(username);
        throwExceptionIfPlayerNotInGame(username);
        throwExceptionIfNotPlayersTurn(username);
        firstCardChangeColor(chosenColor);

        if (pendingEffect != null) {
            throw new PendingEffectNotAccepted("Pending effect must be accepted first");
        } else if (canPlayerDraw(username)) {
            throw new WrongInGameAction("Player has to draw card");
        } else if (!players.get(username).getDeck().hasCard(cardId)) {
            throw new CardIdException("Card with id " + cardId + " does not exist");
        }

        Card handCard = players.get(username).getDeck().getSpecificCard(cardId);
        if (!handCard.canBePlayed(putDeck.peekTopCard(), currentColorType)) {
            throw new WrongInGameAction("Can not play this specific card");
        }
    }

    public GameStatus getStatus() {
        return status;
    }

    public String getGameMaker() {
        return gameMaker;
    }

    public List<String> getPlayersNames() {
        return playerTurns;
    }

    public String peekTopCardOfPutDeck() throws InvalidGameStatusForAction {
        if (status != GameStatus.STARTED) {
            throw new InvalidGameStatusForAction("Game must be started to peek top card");
        }

        return putDeck.peekTopCard().toString();
    }

    public Player getPlayer(String username) throws PlayerNotInGame {
        throwExceptionIfPlayerNotInGame(username);
        return players.get(username);
    }

    public List<Card> getCardHistory() {
        return cardHistory;
    }

    public boolean isPlayerSpectating(String username) {
        return spectatingPlayers.contains(username);
    }

    public GameSummary getGameSummary() {
        return gameSummary;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Game game)) return false;
        return gameID == game.gameID;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(gameID);
    }
}

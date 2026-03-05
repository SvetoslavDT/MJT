package bg.sofia.uni.fmi.mjt.uno.players;

import bg.sofia.uni.fmi.mjt.uno.cards.Deck;
import bg.sofia.uni.fmi.mjt.uno.cards.types.Card;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Player {

    private final String username;
    private final String password;

    private String gameName;

    private final Deck deck;
    private boolean isActive;
    private Optional<Long> inGame;

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        this.gameName = null;
        deck = new Deck(true);
        isActive = false;
        inGame = Optional.empty();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean inGame() {
        return inGame.isPresent();
    }

    public Optional<Long> getInGame() {
        return inGame;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void drawCard(Card card) {
        deck.fillDeck(card);
    }

    public void drawCards(List<Card> cards) {
        deck.fillDeck(cards);
    }

//    public boolean correctEntry(String username, String password) {
//        if (username == null || password == null) {
//            return false;
//        }
//
//        return this.username.equals(username) && this.password.equals(password);
//    }

    public Deck getDeck() {
        return deck;
    }

    public void setInGame(Optional<Long> inGame) {
        this.inGame = inGame;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Player player)) return false;
        return Objects.equals(username, player.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }
}

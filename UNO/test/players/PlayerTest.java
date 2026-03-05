package players;

import bg.sofia.uni.fmi.mjt.uno.cards.types.Card;
import bg.sofia.uni.fmi.mjt.uno.players.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class PlayerTest {

    private static final String NAME = "Spens";
    private static final String PASSWORD = "leon";

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player(NAME, PASSWORD);
    }

    @Test
    void testConstuctor() {
        assertDoesNotThrow(() -> new Player(NAME, PASSWORD), "Expected correct initialisation");
    }

    @Test
    void testGetUsername() {
        assertEquals(NAME, player.getUsername(), "Expected " + NAME);
    }

    @Test
    void testInGameWhenNotInGame() {
        assertFalse(player.inGame(), "Expected not in game when not in game");
    }

    @Test
    void testGetPassword() {
        assertEquals(PASSWORD, player.getPassword(), "Expected " + PASSWORD);
    }

    @Test
    void testSetInGame() {
        Optional<Long> inGame = Optional.of(123L);
        player.setInGame(inGame);
        assertTrue(player.inGame(), "Expected in game when in game");
    }

    @Test
    void testGetInGame() {
        Optional<Long> inGame = Optional.of(123L);
        player.setInGame(inGame);
        assertTrue(player.inGame(), "Expected in game when in game");
    }

    @Test
    void testSetGameName() {
        player.setGameName("Pesho");
        assertEquals("Pesho", player.getGameName(), "Expected \"Pesho\"");
    }

    @Test
    void testGetGameName() {
        player.setGameName("Pesho");
        assertEquals("Pesho", player.getGameName(), "Expected \"Pesho\"");
    }

    @Test
    void testIsActive() {
        assertFalse(player.isActive(), "Expected not active when player is not active");
    }

    @Test
    void testSetActive() {
        player.setActive(true);
        assertTrue(player.isActive(), "Expected true when player is active");
    }

    @Test
    void testDrawCard() {
        Card cardMock = mock();
        player.drawCard(cardMock);
        assertEquals(1, player.getDeck().getSize(), "Expected size of Deck to be 1");
    }

    @Test
    void testDrawCards() {
        Card card1Mock = mock();
        Card card2Mock = mock();
        List<Card> cardList = new ArrayList<>();
        cardList.add(card1Mock);
        cardList.add(card2Mock);
        player.drawCards(cardList);
        assertEquals(2, player.getDeck().getSize(), "Expected size of Deck to be 2");
    }

    @Test
    void testGetDeck() {
        assertEquals(player.getDeck(), player.getDeck(), "Expected same Deck");
        // Dont know how to do it
    }
}

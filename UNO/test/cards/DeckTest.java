package cards;

import bg.sofia.uni.fmi.mjt.uno.cards.Deck;
import bg.sofia.uni.fmi.mjt.uno.cards.types.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class DeckTest {

    Deck deck;

    @BeforeEach
    void setUp() {
        deck = new Deck(false);
    }

    @Test
    void testConstructorForEmptyDeck() {
        Deck newDeck =  new Deck(true);
        assertEquals(0, newDeck.getCards().size(), "Expected empty deck");
    }

    @Test
    void testConstructorForFullDeck() {
        assertEquals(108, deck.getCards().size(), "Expected full deck of uno cards");
    }

    @Test
    void testFillDeck() {
        assertEquals(108, deck.getCards().size(), "Expected full deck of uno cards");
    }

    @Test
    void testFillDeckWithListOfCards() {
        Card card1 = mock();
        Card card2 = mock();
        List<Card> cards = Arrays.asList(card1, card2);
        int beforeSize = deck.getCards().size();
        deck.fillDeck(cards);
        assertEquals(beforeSize + 2, deck.getCards().size(), "Expected 2 cards to be added");
    }

    @Test
    void testFillDeckWithSingleCard() {
        Card card1 = mock();
        int beforeSize = deck.getCards().size();
        deck.fillDeck(card1);
        assertEquals(beforeSize + 1, deck.getCards().size(), "Expected 1 cards to be added");
    }

    @Test
    void testPeekTopCard() {
        Card lastCard = deck.getCards().getLast();
        assertEquals(lastCard, deck.peekTopCard(), "Expected same card");
    }

    @Test
    void testGetTopCard() {
        Card lastPeekedCard = deck.peekTopCard();
        Card lastCard = deck.getTopCard();
        assertSame(lastCard, lastPeekedCard, "Expected same card");
        assertEquals(108 - 1, deck.getSize(), "Expected one card to be removed");
    }

    @Test
    void testGetNCards() {
        List<Card> cards;
        int oldSize = deck.getCards().size();
        cards = deck.getNCards(3);
        assertEquals(oldSize - 3, deck.getSize(), "Expected 3 cards to be removed");
    }

    @Test
    void testPutCard() {
        Card card1 = mock();
        deck.putCard(card1);
        assertSame(card1, deck.peekTopCard(), "Expected same card");
    }

    @Test
    void testShuffleCardIn() {
        Card card1 = mock();
        int oldSize = deck.getCards().size();
        deck.shuffleCardIn(card1, new Random());
        assertEquals(oldSize + 1, deck.getCards().size(), "Expected 1 cards to be shuffled in");
        assertNotSame(card1, deck.peekTopCard(), "Expected different card");
    }

    @Test
    void testHasCard() {
        Card card1 = mock();
        deck.putCard(card1);
        assertTrue(deck.hasCard(card1.getId()), "Expected card to be found");
    }

    @Test
    void testGetSpecificCard() {
        Card card = deck.getSpecificCard((short) 16);
        assertEquals(16, card.getId(), "Expected card with id 16 to be found");
    }

    @Test
    void testGetSize() {
        assertEquals(108, deck.getCards().size(), "Expected 108 cards in the deck");
    }

    @Test
    void testGetCards() {
        List<Card> cards = deck.getCards();
        assertEquals(108, cards.size(), "Expected 108 cards in the deck meaning the exact deck");
    }
}

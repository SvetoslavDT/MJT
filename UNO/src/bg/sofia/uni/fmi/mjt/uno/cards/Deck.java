package bg.sofia.uni.fmi.mjt.uno.cards;

import bg.sofia.uni.fmi.mjt.uno.cards.enums.ActionType;
import bg.sofia.uni.fmi.mjt.uno.cards.enums.ColorType;
import bg.sofia.uni.fmi.mjt.uno.cards.enums.JokerType;
import bg.sofia.uni.fmi.mjt.uno.cards.types.ActionCard;
import bg.sofia.uni.fmi.mjt.uno.cards.types.Card;
import bg.sofia.uni.fmi.mjt.uno.cards.types.JokerCard;
import bg.sofia.uni.fmi.mjt.uno.cards.types.NumberCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Deck {

    private static final short MAX_CARD_NUMBER = 9;
    private static final short MAX_JOKER_CARDS_FOR_TYPE = 4;

    private List<Card> cards;
    private Map<Short, Card> cardMap;

    public Deck(boolean emptyDeck) {
        cards = new ArrayList<>();
        cardMap = new HashMap<>();

        if (!emptyDeck) {
            fillDeck();
            shuffleDeck();
        }
    }

    private void fillDeck() {
        short idCounter = 1;

        for (ColorType color : ColorType.values()) {

            Card zeroCard = new NumberCard((short) 0, idCounter, color);
            cards.add(zeroCard);
            cardMap.put(idCounter++, zeroCard);

            for (int i = 1; i <= MAX_CARD_NUMBER; i++) {
                idCounter = addNumberCardHelper(i, idCounter, color);
            }

            idCounter = addActionCardHelper(color, idCounter, ActionType.PLUS_TWO);
            idCounter = addActionCardHelper(color, idCounter, ActionType.REVERSE);
            idCounter = addActionCardHelper(color, idCounter, ActionType.SKIP);
        }

        for (int i = 0; i < MAX_JOKER_CARDS_FOR_TYPE; i++) {
            idCounter = addJokerCardsHelper(idCounter);
        }
    }

    private short addJokerCardsHelper(short idCounter) {
        Card colorCard = new JokerCard(null, idCounter, JokerType.COLOR_CHANGE);
        cards.add(colorCard);
        cardMap.put(idCounter++, colorCard);
        Card plusFourCard = new JokerCard(null, idCounter, JokerType.PLUS_FOUR);
        cards.add(plusFourCard);
        cardMap.put(idCounter++, plusFourCard);

        return idCounter;
    }

    private short addNumberCardHelper(int number, short idCounter, ColorType color) {
        Card card1 = new NumberCard((short) number, idCounter, color);
        cards.add(card1);
        cardMap.put(idCounter++, card1);
        Card card2 = new NumberCard((short) number, idCounter, color);
        cards.add(card2);
        cardMap.put(idCounter++, card2);

        return idCounter;
    }

    private short addActionCardHelper(ColorType color, short idCounter, ActionType actionType) {
        Card card1 = new ActionCard(color, idCounter, actionType);
        cards.add(card1);
        cardMap.put(idCounter++, card1);
        Card card2 = new ActionCard(color, idCounter, actionType);
        cards.add(card2);
        cardMap.put(idCounter++, card2);

        return idCounter;
    }

    public void fillDeck(List<Card> cards) {
        for (Card card : cards) {
            this.cards.add(card);
            cardMap.put(card.getId(), card);
        }
    }

    public void fillDeck(Card card) {
        this.cards.add(card);
        cardMap.put(card.getId(), card);
    }

    private void shuffleDeck() {
        Collections.shuffle(cards);
    }

    public Card peekTopCard() {
        return cards.getLast();
    }

    public Card getTopCard() {
        if (cards.isEmpty()) {
            return null;
        }

        cardMap.remove(cards.getLast().getId());
        return cards.removeLast();
    }

    public List<Card> getNCards(int n) {
        List<Card> topNCards = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            topNCards.add(getTopCard());
        }

        return topNCards;
    }

    public void putCard(Card card) {
        cards.add(card);
        cardMap.put(card.getId(), card);
    }

    public void shuffleCardIn(Card card, Random random) {
        int index = random.nextInt(cards.size() + 1);
        cards.add(index, card);
        cardMap.put(card.getId(), card);
    }

    public boolean hasCard(short id) {
        return cardMap.containsKey(id);
    }

    public Card getSpecificCard(short id) {
        return cardMap.get(id);
    }

    public Card drawSpesificCard(short cardId) {
        Card cardToRemove = cardMap.get(cardId);
        cards.remove(cardToRemove);
        cardMap.remove(cardId);

        return cardToRemove;
    }

    public short getSize() {
        return (short) cards.size();
    }

    public List<Card> getCards() {
        return cards;
    }
}

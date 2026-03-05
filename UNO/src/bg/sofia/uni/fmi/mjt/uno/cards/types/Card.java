package bg.sofia.uni.fmi.mjt.uno.cards.types;

import bg.sofia.uni.fmi.mjt.uno.cards.enums.CardType;
import bg.sofia.uni.fmi.mjt.uno.cards.enums.ColorType;

import java.util.Objects;

public abstract class Card {

    private final CardType cardType;
    private final ColorType color;
    private final short id;

    public Card(ColorType color, short id, CardType cardType) {
        this.color = color;
        this.id = id;
        this.cardType = cardType;
    }

    public final CardType getCardType() {
        return cardType;
    }

    public abstract boolean canBePlayed(Card card, ColorType color);

    public ColorType getColor() {
        return color;
    }

    public short getId() {
        return id;
    }

    @Override
    public String toString() {
        String color = this.color == null ? "" : this.color.toString().toLowerCase() + ", ";
        return color + "id = " + id + ", ";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Card card)) return false;
        return id == card.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

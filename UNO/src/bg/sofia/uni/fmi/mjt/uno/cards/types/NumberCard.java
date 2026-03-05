package bg.sofia.uni.fmi.mjt.uno.cards.types;

import bg.sofia.uni.fmi.mjt.uno.cards.enums.CardType;
import bg.sofia.uni.fmi.mjt.uno.cards.enums.ColorType;

import java.util.Objects;

public class NumberCard extends Card {

    private final short number;

    public NumberCard(short number, short id, ColorType color) {
        super(color, id, CardType.NUMBER);
        this.number = number;
    }

    @Override
    public boolean canBePlayed(Card card, ColorType color) {
        if (color != null && color == this.getColor()) {
            return true;
        }

        if (card.getCardType() == this.getCardType()) {
            return ((NumberCard) card).getNumber() == this.number;
        }

        return false;
    }

    public short getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "[" + super.toString() + this.number + "]";
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

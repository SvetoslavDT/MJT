package bg.sofia.uni.fmi.mjt.uno.cards.types;

import bg.sofia.uni.fmi.mjt.uno.cards.enums.CardType;
import bg.sofia.uni.fmi.mjt.uno.cards.enums.ColorType;
import bg.sofia.uni.fmi.mjt.uno.cards.enums.JokerType;

public class JokerCard extends Card {

    private final JokerType jokerType;

    public JokerCard(ColorType color, short id, JokerType jokerType) {
        super(color, id, CardType.JOKER);
        this.jokerType = jokerType;
    }

    @Override
    public boolean canBePlayed(Card card, ColorType color) {
        return true;
    }

    public JokerType getJokerType() {
        return jokerType;
    }

    @Override
    public String toString() {
        return "[" + super.toString() + jokerType.toString().toLowerCase() + "]";
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

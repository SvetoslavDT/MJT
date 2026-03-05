package bg.sofia.uni.fmi.mjt.uno.cards.types;

import bg.sofia.uni.fmi.mjt.uno.cards.enums.ActionType;
import bg.sofia.uni.fmi.mjt.uno.cards.enums.CardType;
import bg.sofia.uni.fmi.mjt.uno.cards.enums.ColorType;

public class ActionCard extends Card {

    private final ActionType actionType;

    public ActionCard(ColorType color, short id, ActionType actionType) {
        super(color, id, CardType.ACTION);
        this.actionType = actionType;
    }

    @Override
    public boolean canBePlayed(Card card, ColorType color) {
        if (color != null && color == this.getColor()) {
            return true;
        }

        if (card.getCardType() == this.getCardType()) {
            return ((ActionCard) card).getActionType() == this.actionType;
        }

        return false;
    }

    public ActionType getActionType() {
        return actionType;
    }

    @Override
    public String toString() {
        return "[" + super.toString() + this.actionType.toString().toLowerCase() + "]";
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

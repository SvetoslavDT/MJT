package cards;

import bg.sofia.uni.fmi.mjt.uno.cards.enums.ActionType;
import bg.sofia.uni.fmi.mjt.uno.cards.enums.ColorType;
import bg.sofia.uni.fmi.mjt.uno.cards.enums.JokerType;
import bg.sofia.uni.fmi.mjt.uno.cards.types.ActionCard;
import bg.sofia.uni.fmi.mjt.uno.cards.types.JokerCard;
import bg.sofia.uni.fmi.mjt.uno.cards.types.NumberCard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JokerCardTest {

    @Test
    void testConstructor() {
        assertDoesNotThrow(() -> new JokerCard(ColorType.BLUE, (short) 10, JokerType.COLOR_CHANGE),
            "Constructor expected to create an instance");
    }

    @Test
    void testCanBePlayed() {
        JokerCard card1 = new JokerCard(ColorType.BLUE, (short) 10, JokerType.COLOR_CHANGE);
        ActionCard card2 = new ActionCard(ColorType.BLUE, (short) 11, ActionType.REVERSE);
        NumberCard card3 = new NumberCard((short)2, (short) 10, ColorType.BLUE);

        assertTrue(card1.canBePlayed(card2, ColorType.BLUE));
        assertTrue(card1.canBePlayed(card3, ColorType.BLUE));
    }

    @Test
    void testCanBePlayedDifferentColorSameAction() {
        ActionCard card1 = new ActionCard(ColorType.BLUE, (short) 10, ActionType.SKIP);
        ActionCard card2 = new ActionCard(ColorType.RED, (short) 11, ActionType.SKIP);

        assertTrue(card1.canBePlayed(card2, ColorType.RED));
    }

    @Test
    void testCanBePlayedDifferentColorDifferentNumber() {
        ActionCard card1 = new ActionCard(ColorType.BLUE, (short) 10, ActionType.SKIP);
        ActionCard card2 = new ActionCard(ColorType.RED, (short) 11, ActionType.REVERSE);

        assertFalse(card1.canBePlayed(card2, ColorType.RED));
    }
}

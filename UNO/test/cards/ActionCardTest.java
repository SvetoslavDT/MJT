package cards;

import bg.sofia.uni.fmi.mjt.uno.cards.enums.ActionType;
import bg.sofia.uni.fmi.mjt.uno.cards.enums.ColorType;
import bg.sofia.uni.fmi.mjt.uno.cards.types.ActionCard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionCardTest {



    @Test
    void testConstructor() {
        assertDoesNotThrow(() -> new ActionCard(ColorType.BLUE, (short) 10, ActionType.SKIP),
            "Constructor expected to create an instance");
    }

    @Test
    void testCanBePlayedSameColor() {
        ActionCard card1 = new ActionCard(ColorType.BLUE, (short) 10, ActionType.SKIP);
        ActionCard card2 = new ActionCard(ColorType.BLUE, (short) 11, ActionType.REVERSE);

        assertTrue(card1.canBePlayed(card2, ColorType.BLUE));
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

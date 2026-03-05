package cards;

import bg.sofia.uni.fmi.mjt.uno.cards.enums.ColorType;
import bg.sofia.uni.fmi.mjt.uno.cards.types.NumberCard;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NumberCardTest {

    @Test
    void testConstructor() {
        assertDoesNotThrow(() -> new NumberCard((short)2, (short) 10, null), "Constructor expected to create an instance");
    }

    @Test
    void testCanBePlayedSameColor() {
        NumberCard card1 = new NumberCard((short)2, (short) 10, ColorType.BLUE);
        NumberCard card2 = new NumberCard((short)3, (short) 11, ColorType.BLUE);

        assertTrue(card1.canBePlayed(card2, ColorType.BLUE));
    }

    @Test
    void testCanBePlayedDifferentColorSameNumber() {
        NumberCard card1 = new NumberCard((short)2, (short) 10, ColorType.BLUE);
        NumberCard card2 = new NumberCard((short)2, (short) 11, ColorType.BLUE);

        assertTrue(card1.canBePlayed(card2, ColorType.RED));
    }

    @Test
    void testCanBePlayedDifferentColorDifferentNumber() {
        NumberCard card1 = new NumberCard((short)2, (short) 10, ColorType.BLUE);
        NumberCard card2 = new NumberCard((short)3, (short) 11, ColorType.GREEN);

        assertFalse(card1.canBePlayed(card2, ColorType.RED));
    }
}

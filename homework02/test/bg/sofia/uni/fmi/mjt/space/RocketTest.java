package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RocketTest {
    private final String line1 = "0,Tsyklon-3,https://en.wikipedia.org/wiki/Tsyklon-3,39.0 m\n";
    private final String line2 = "48,\"Delta IV Medium+ (4,2)\",https://en.wikipedia.org/wiki/Delta_IV,62.5 m";

    private final Rocket rocket1 = Rocket.of(line1);
    private final Rocket rocket2 = Rocket.of(line2);

    @Test
    void testOfWithNullParameterThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Rocket.of(null),
            "Expected IllegalArgumentException");
    }

    @Test
    void testOfWithBlankLineThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Rocket.of(""),
            "Expected IllegalArgumentException");
    }

    @Test
    void testOfWorksCorrectly() {
        assertDoesNotThrow(() -> Rocket.of(line1), "Expected correct initialisation");
    }

    @Test
    void testOfIdReadCorrectly() {
        assertEquals("0", rocket1.id(), "Expected different id");
        assertEquals("48", rocket2.id(), "Expected different id");
    }

    @Test
    void testOfNameReadCorrectly() {
        assertEquals("Tsyklon-3", rocket1.name(), "Expected different name");
        assertEquals("Delta IV Medium+ (4,2)", rocket2.name(), "Expected different name");
    }

    @Test
    void testOfWikiReadCorrectly() {
        assertEquals("https://en.wikipedia.org/wiki/Tsyklon-3", rocket1.wiki().get(),
            "Expected different wiki");
        assertEquals("https://en.wikipedia.org/wiki/Delta_IV", rocket2.wiki().get(),
            "Expected different wiki");
    }

    @Test
    void testOfHeightReadCorrectly() {
        assertEquals(39.0, rocket1.height().get(), "Expected different height");
        assertEquals(62.5, rocket2.height().get(), "Expected different height");
    }
}

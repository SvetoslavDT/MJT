package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.mission.Detail;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DetailTest {

    @Test
    void testConstructor() {
        assertDoesNotThrow(() -> new Detail("Rocket", "Payload"),
            "Expected detail to be constructed");
    }

    @Test
    void testOfWithNullParameterThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Detail.of(null),
            "Expected IllegalArgumentException to be thrown");
    }

    @Test
    void testOfWithBlankParameterThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Detail.of(" "),
            "Expected IllegalArgumentException to be thrown");
    }

    @Test
    void testOfWorksReadsRocketNameCorrectly() {
        String input = "Falcon 9 Block 5 | Starlink V1 L9 & BlackSky";
        Detail detail = Detail.of(input);
           assertEquals("Falcon 9 Block 5", detail.rocketName(),
            "Expected string -> Falcon 9 Block 5");
    }

    @Test
    void testOfWorksReadsPayLoadCorrectly() {
        String input = "Falcon 9 Block 5 | Starlink V1 L9 & BlackSky";
        Detail detail = Detail.of(input);

        assertEquals("Starlink V1 L9 & BlackSky", detail.payload(),
            "Expected string -> Starlink V1 L9 & BlackSky");
    }
}
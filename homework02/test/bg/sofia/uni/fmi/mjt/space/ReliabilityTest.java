package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.reliability.Reliability;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReliabilityTest {

    @Test
    void testConstructorWithNullMissionsThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Reliability(null, "Rocket"),
            "Expected IllegalArgumentException when missions are null");
    }

    @Test
    void testConstructorWithNullRocketThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Reliability(new ArrayList<Mission>(), null),
            "Expected IllegalArgumentException when rockets are null");
    }

    @Test
    void testConstructorWithCorrectParameters() {
        List<Mission> missions = new ArrayList<>();
        String RocketName = "Rocket";

        assertDoesNotThrow(() -> new Reliability(missions, RocketName), "Expected correct initialisation");
    }

    @Test
    void testGetReliabilityWithTotalMissionsZero() {
        List<Mission> missions = new ArrayList<>();
        String RocketName = "Rocket";

        assertEquals(0.0, new Reliability(missions, RocketName).getReliability(), 0.001,
            "Expected 0.0 reliability");
    }

    @Test
    void testGetReliabilityWithNonZeroMissions() {
        String line1 = "0,SpaceX,\"LC-39A, Kennedy Space Center, Florida, USA\",\"Fri Aug 07, 2020\"," +
            "Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,\"50.0 \",Success";
        String line2 = "13,IAI,\"Pad 1, Palmachim Airbase, Israel\",\"Mon Jul 06, 2020\"," +
            "Shavit-2 | Ofek-16,StatusActive,,Success";
        Mission mission1 = Mission.of(line1);
        Mission mission2 = Mission.of(line2);

        List<Mission> missions = new ArrayList<>();
        String RocketName = "Rocket";
        missions.add(mission1);
        missions.add(mission2);

        assertEquals(1.0, new Reliability(missions, RocketName).getReliability(), 0.001,
            "Expected 1.0 reliability");
    }
}

package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MissionTest {

    private final String line1 = "0,SpaceX,\"LC-39A, Kennedy Space Center, Florida, USA\",\"Fri Aug 07, 2020\"," +
        "Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,\"50.0 \",Success";
    private final String line2 = "13,IAI,\"Pad 1, Palmachim Airbase, Israel\",\"Mon Jul 06, 2020\"," +
        "Shavit-2 | Ofek-16,StatusActive,,Success";

    private final Mission mission1 = Mission.of(line1);
    private final Mission mission2 = Mission.of(line2);

    @Test
    void testOfIdSplitCorrectly() {
        assertEquals("0", mission1.id(), "Expected -> 0");
        assertEquals("13", mission2.id(), "Expected -> 13");
    }

    @Test
    void testOfCompanySplitCorrectly() {
        assertEquals("SpaceX", mission1.company(), "Expected -> SpaceX");
        assertEquals("IAI", mission2.company(), "Expected -> IAI");
    }

    @Test
    void testOfLocationSplitCorrectly() {
        assertEquals("LC-39A, Kennedy Space Center, Florida, USA", mission1.location(),
            "Expected different location");
        assertEquals("Pad 1, Palmachim Airbase, Israel", mission2.location(),
            "Expected different location");
    }

    @Test
    void testOfDateSplitCorrectly() {
        assertEquals("2020-08-07", mission1.date().toString(), "Expected different date");
        assertEquals("2020-07-06", mission2.date().toString(), "Expected different date");
    }

    @Test
    void testOfDetailSplitCorrectly() {
        assertEquals("Detail[rocketName=Falcon 9 Block 5, payload=Starlink V1 L9 & BlackSky]",
            mission1.detail().toString(), "Expected different detail");
        assertEquals("Detail[rocketName=Shavit-2, payload=Ofek-16]", mission2.detail().toString(),
            "Expected different detail");
    }

    @Test
    void testOfRocketStatusSplitCorrectly() {
        assertEquals("StatusActive", mission1.rocketStatus().toString(),
            "Expected different status");
        assertEquals("StatusActive", mission2.rocketStatus().toString(),
            "Expected different status");
    }

    @Test
    void testOfCostSplitCorrectly() {
        assertEquals(50.0, mission1.cost().get(), "Expected different cost");
        assertEquals(false, mission2.cost().isPresent(), "Expected different cost");
    }

    @Test
    void testOfMissionStatusSplitCorrectly() {
        assertEquals(MissionStatus.SUCCESS, mission1.missionStatus(), "Expected different status");
        assertEquals(MissionStatus.SUCCESS, mission2.missionStatus(), "Expected different status");
    }

    @Test
    void testOfWithTokenCountLowerThanEight() {
        String newLine = "abra,dabra,\"eehe\"";
        assertThrows(IllegalArgumentException.class, () -> Mission.of(newLine),
            "Expected at least 8 tokens");
    }

    @Test
    void testGetCountryWorksCorrectly() {
        String newLine = "LC-39A, Kennedy Space Center, Florida, USA";
        assertEquals("USA", mission1.getCountry(), "Expected different country");
    }
}
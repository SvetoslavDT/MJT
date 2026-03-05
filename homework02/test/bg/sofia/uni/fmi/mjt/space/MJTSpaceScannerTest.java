package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class MJTSpaceScannerTest {

    private static MJTSpaceScanner mjt;
    private static SecretKey key;

    private static String MISSIONS = """
        Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket," Rocket",Status Mission
        0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,"50.0 ",Success
        1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Thu Aug 06, 2020",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Success
        2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",Starship Prototype | 150 Meter Hop,StatusActive,,Success
        3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Success
        4,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Thu Jul 30, 2020",Atlas V 541 | Perseverance,StatusActive,"145.0 ",Success
        5,CASC,"LC-9, Taiyuan Satellite Launch Center, China","Sat Jul 25, 2020","Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1",StatusActive,"64.68 ",Success
        6,Roscosmos,"Site 31/6, Baikonur Cosmodrome, Kazakhstan","Thu Jul 23, 2020",Soyuz 2.1a | Progress MS-15,StatusActive,"48.5 ",Success
        7,CASC,"LC-101, Wenchang Satellite Launch Center, China","Thu Jul 23, 2020",Long March 5 | Tianwen-1,StatusActive,,Success
        8,SpaceX,"SLC-40, Cape Canaveral AFS, Florida, USA","Mon Jul 20, 2020",Falcon 9 Block 5 | ANASIS-II,StatusActive,"50.0 ",Success
        9,JAXA,"LA-Y1, Tanegashima Space Center, Japan","Sun Jul 19, 2020",H-IIA 202 | Hope Mars Mission,StatusActive,"90.0 ",Success
        """;
    private static String ROCKETS = """
        "",Name,Wiki,Rocket Height
        0,Tsyklon-3,https://en.wikipedia.org/wiki/Tsyklon-3,39.0 m
        1,Tsyklon-4M,https://en.wikipedia.org/wiki/Cyclone-4M,38.7 m
        2,Unha-2,https://en.wikipedia.org/wiki/Unha,28.0 m
        3,Unha-3,https://en.wikipedia.org/wiki/Unha,32.0 m
        4,Vanguard,https://en.wikipedia.org/wiki/Vanguard_(rocket),23.0 m
        5,Vector-H,https://en.wikipedia.org/wiki/Vector-H,18.3 m
        6,Vector-R,https://en.wikipedia.org/wiki/Vector-R,13.0 m
        7,Vega,https://en.wikipedia.org/wiki/Vega_(rocket),29.9 m
        8,Vega C,https://en.wikipedia.org/wiki/Vega_(rocket),35.0 m
        9,Vega E,https://en.wikipedia.org/wiki/Vega_(rocket),35.0 m
        """;

    @BeforeAll
    static void init() throws NoSuchAlgorithmException {
        key = Rijndael.generateSecretKey();

        var rocketReader = new StringReader(ROCKETS);
        var missionReader = new StringReader(MISSIONS);

        mjt = new MJTSpaceScanner(missionReader, rocketReader, key);
    }

    @Test
    void testConstructorWhenRocketsReaderIsNullThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new MJTSpaceScanner(null,
            new StringReader(""), key), "Expected IllegalArgumentException");
    }

    @Test
    void testConstructorWhenMissionsReaderIsNullThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new MJTSpaceScanner(new StringReader(""),
            null, key), "Expected IllegalArgumentException");
    }

    @Test
    void testConstructorWithNullKeyThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new MJTSpaceScanner(new StringReader(""),
            new StringReader(""), null), "Expected IllegalArgumentException");
    }

    @Test
    void testConstructorWithValidParameters() {
        assertDoesNotThrow(() -> new MJTSpaceScanner(new StringReader(""), new StringReader(""),
            key), "Expected initialisation");
    }

    @Test
    void testGetAllMissionsReadsCorrectNumber() {
        Collection<Mission> missions = mjt.getAllMissions();
        for (var mission : missions) {
            System.out.println(mission);
        }

        assertEquals(10, mjt.getAllMissions().size(),
            "Expected different number of missions to be returned");
    }

    @Test
    void testAllMissionsWithNullParameter() {
        assertThrows(IllegalArgumentException.class, () -> mjt.getAllMissions(null),
            "Expected IllegalArgumentException");
    }

    @Test
    void testGetAllMissionsWithMissionStatusSuccess() {
        assertEquals(10, mjt.getAllMissions(MissionStatus.SUCCESS).size(),
            "Expected different number of missions to be returned");
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsWhenFromNull() {
        LocalDate to = LocalDate.now();
        assertThrows(IllegalArgumentException.class,
            () -> mjt.getCompanyWithMostSuccessfulMissions(null, to), "Expected IllegalArgumentException");
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsWhenToNull() {
        LocalDate from = LocalDate.now();
        assertThrows(IllegalArgumentException.class,
            () -> mjt.getCompanyWithMostSuccessfulMissions(from, null),
            "Expected IllegalArgumentException");
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsWhenToIsBeforeFrom() {
        LocalDate to = LocalDate.of(1999, 12, 12);
        LocalDate from = LocalDate.now();

        assertThrows(TimeFrameMismatchException.class,
            () -> mjt.getCompanyWithMostSuccessfulMissions(from, to), "Expected TimeFrameMismatchException");
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsWithCorrectParameters() {
        LocalDate from = LocalDate.of(1959, 12, 12);
        LocalDate to = LocalDate.now();

        assertEquals("CASC", mjt.getCompanyWithMostSuccessfulMissions(from, to),
            "Expected different company name to be returned");
    }

    @Test
    void testGetMissionsPerCountryCorrectNumberOfCountries() {
        assertEquals(4, mjt.getMissionsPerCountry().size(),
            "Expected different number of countries to be returned");
    }

    @Test
    void testGetTopNLeastExpensiveMissionsMissionStatusNull() {
        assertThrows(IllegalArgumentException.class,
            () -> mjt.getTopNLeastExpensiveMissions(10, null, RocketStatus.STATUS_ACTIVE),
            "Expected IllegalArgumentException");
    }

    @Test
    void testGetTopNLeastExpensiveMissionsRocketStatusNull() {
        assertThrows(IllegalArgumentException.class,
            () -> mjt.getTopNLeastExpensiveMissions(10, MissionStatus.SUCCESS, null),
            "Expected IllegalArgumentException");
    }

    @Test
    void testGetTopNLeastExpensiveMissionsNIsZero() {
        assertThrows(IllegalArgumentException.class,
            () -> mjt.getTopNLeastExpensiveMissions(0,
                MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE), "Expected IllegalArgumentException");
    }

    @Test
    void testGetTopNLeastExpensiveMissionsNIsLowerThanZero() {
        assertThrows(IllegalArgumentException.class,
            () -> mjt.getTopNLeastExpensiveMissions(-1,
                MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE), "Expected IllegalArgumentException");
    }

    @Test
    void testGetTopNLeastExpensiveMissions() {
        String[] ids = {"1", "6", "0"};
        List<Mission> result = mjt.getTopNLeastExpensiveMissions(
            3, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);

        String[] expectedIds = {result.get(0).id(), result.get(1).id(), result.get(2).id()};

        assertArrayEquals(ids, expectedIds, "Mission IDs expected to match");
    }

    @Test
    void testGetMostDesiredLocationForMissionsPerCompanyCorrectCountOfCompanies() {
        assertEquals(5, mjt.getMostDesiredLocationForMissionsPerCompany().size(),
            "Expected different number of companies to be returned");
    }

    @Test
    void testGetMostDesiredLocationForMissionsPerCompany() {
        Map<String, String> map = mjt.getMostDesiredLocationForMissionsPerCompany();

        String[] expected = {"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China",
            "LA-Y1, Tanegashima Space Center, Japan",
            "SLC-40, Cape Canaveral AFS, Florida, USA"};

        String[] result = new String[3];
        result[0] = map.get("CASC");
        result[1] = map.get("JAXA");
        result[2] = map.get("SpaceX");

        assertArrayEquals(expected, result, "Mission most desired locations expected to match");
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyFromNull() {
        LocalDate to = LocalDate.now();
        assertThrows(IllegalArgumentException.class,
            () -> mjt.getLocationWithMostSuccessfulMissionsPerCompany(null, to),
            "Expected IllegalArgumentException");
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyToNull() {
        LocalDate from = LocalDate.of(1970, 1, 1);
        assertThrows(IllegalArgumentException.class,
            () -> mjt.getLocationWithMostSuccessfulMissionsPerCompany(from, null),
            "Expected IllegalArgumentException");
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyToBeforeFrom() {
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.of(1970, 1, 1);
        assertThrows(TimeFrameMismatchException.class,
            () -> mjt.getLocationWithMostSuccessfulMissionsPerCompany(from, to),
            "Expected IllegalArgumentException");
    }

    @Test
    void testTestGetLocationWithMostSuccessfulMissionsPerCompanyCountOfCompanies() {
        LocalDate to = LocalDate.now();
        LocalDate from = LocalDate.of(1970, 1, 1);
        assertEquals(5, mjt.getLocationWithMostSuccessfulMissionsPerCompany(from, to).size(),
            "Expected different number of companies to be returned");
    }

    @Test
    void testTestGetLocationWithMostSuccessfulMissionsPerCompany() {
        LocalDate to = LocalDate.now();
        LocalDate from = LocalDate.of(1970, 1, 1);
        Map<String, String> map = mjt.getLocationWithMostSuccessfulMissionsPerCompany(from, to);

        String[] expected = {"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China",
            "LA-Y1, Tanegashima Space Center, Japan",
            "SLC-40, Cape Canaveral AFS, Florida, USA"};

        String[] result = new String[3];
        result[0] = map.get("CASC");
        result[1] = map.get("JAXA");
        result[2] = map.get("SpaceX");

        assertArrayEquals(expected, result, "Mission most desired locations expected to match");
    }

    @Test
    void testGetAllRockets() {
        assertEquals(10, mjt.getAllRockets().size(),
            "Expected different number of rockets to be returned");
    }

    @Test
    void testGetTopNTallestRocketsNIsZero() {
        assertThrows(IllegalArgumentException.class, () -> mjt.getTopNTallestRockets(0),
            "Expected IllegalArgumentException");
    }

    @Test
    void testGetTopNTallestRocketsNIsLowerZero() {
        assertThrows(IllegalArgumentException.class, () -> mjt.getTopNTallestRockets(-1),
            "Expected IllegalArgumentException");
    }

    @Test
    void testGetTopNTallestRocketsPerCompanyTopThree() {
        String[] expected = {"0", "1", "8"};
        List<Rocket> rockets = mjt.getTopNTallestRockets(3);
        String[] result = {rockets.get(0).id(), rockets.get(1).id(), rockets.get(2).id()};

        assertArrayEquals(expected, result, "Expected different hights or rockets");
    }

    @Test
    void testGetWikiPageForRocketCorrectNumberOfRockets() {
        assertEquals(10, mjt.getWikiPageForRocket().size(),
            "Expected different number of rockets to be returned");
    }

    @Test
    void testGetWikiPageForRocket() {
        String[] expected = {"https://en.wikipedia.org/wiki/Vega_(rocket)",
            "https://en.wikipedia.org/wiki/Unha",
            "https://en.wikipedia.org/wiki/Cyclone-4M"};

        Map<String, Optional<String>> map = mjt.getWikiPageForRocket();
        String[] result = new String[3];
        result[0] = map.get("Vega").get();
        result[1] = map.get("Unha-3").get();
        result[2] = map.get("Tsyklon-4M").get();

        assertArrayEquals(expected, result, "Mission most desired locations expected to match");
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsNIsZero() {
        assertThrows(IllegalArgumentException.class,
            () -> mjt.getWikiPagesForRocketsUsedInMostExpensiveMissions(0,
                MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE), "Expected IllegalArgumentException");
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsNIsLowerThanZero() {
        assertThrows(IllegalArgumentException.class,
            () -> mjt.getWikiPagesForRocketsUsedInMostExpensiveMissions(-1,
                MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE), "Expected IllegalArgumentException");
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsMissionStatusNull() {
        assertThrows(IllegalArgumentException.class,
            () -> mjt.getWikiPagesForRocketsUsedInMostExpensiveMissions(3,
                null, RocketStatus.STATUS_ACTIVE), "Expected IllegalArgumentException");
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsRocketStatusNull() {
        assertThrows(IllegalArgumentException.class,
            () -> mjt.getWikiPagesForRocketsUsedInMostExpensiveMissions(3,
                MissionStatus.SUCCESS, null), "Expected IllegalArgumentException");
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsTestFirstThree() {
        // Empty because the small set of missions does not have to rockets referred
        String[] expected = {"", "", ""};

        List<String> list = mjt.getWikiPagesForRocketsUsedInMostExpensiveMissions(
            3, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);

        System.out.println(list);

        String[] result = new String[3];
        result[0] = list.get(0);
        result[1] = list.get(1);
        result[2] = list.get(2);

        assertArrayEquals(expected, result, "Mission most desired locations expected to match");
    }

    @Test
    void testSaveMostReliableRocketOutputStreamNull() {
        LocalDate from = LocalDate.of(1970, 1, 1);
        LocalDate to = LocalDate.now();

        assertThrows(IllegalArgumentException.class,
            () -> mjt.saveMostReliableRocket(null, from, to),
            "Expected IllegalArgumentException");
    }

    @Test
    void testSaveMostReliableRocketFromNull() {
        var stream = new ByteArrayOutputStream();
        LocalDate to = LocalDate.now();

        assertThrows(IllegalArgumentException.class,
            () -> mjt.saveMostReliableRocket(stream, null, to),
            "Expected IllegalArgumentException");
    }

    @Test
    void testSaveMostReliableRocketToNull() {
        LocalDate from = LocalDate.of(1970, 1, 1);
        var stream = new ByteArrayOutputStream();

        assertThrows(IllegalArgumentException.class,
            () -> mjt.saveMostReliableRocket(stream, from, null),
            "Expected IllegalArgumentException");
    }

    @Test
    void testSaveMostReliableRocketToBeforeFrom() {
        LocalDate to = LocalDate.of(1970, 1, 1);
        LocalDate from = LocalDate.now();
        var stream = new ByteArrayOutputStream();

        assertThrows(TimeFrameMismatchException.class,
            () -> mjt.saveMostReliableRocket(stream, from, to),
            "Expected IllegalArgumentException");
    }

    @Test
    void testSaveMostReliableRocketSavesCorrectWiki() {
        LocalDate from = LocalDate.of(1950, 1, 1);
        LocalDate to = LocalDate.now();

        // encrypt to memory
        ByteArrayOutputStream encryptedOut = new ByteArrayOutputStream();
        try {
            mjt.saveMostReliableRocket(encryptedOut, from, to);
        } catch (Exception e) {
            fail(e);
        }

        byte[] cipher = encryptedOut.toByteArray();

        ByteArrayOutputStream decryptedOut = new ByteArrayOutputStream();
        try {
            Rijndael rijndael = new Rijndael(key);
            rijndael.decrypt(new ByteArrayInputStream(cipher), decryptedOut);
        } catch (Exception e) {
            fail(e);
        }

        String decrypted = new String(decryptedOut.toByteArray(), StandardCharsets.UTF_8);
        assertEquals("H-IIA 202", decrypted, "Expected different wiki");
    }
}
package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.reliability.Reliability;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MJTSpaceScanner implements SpaceScannerAPI {

    private final List<Mission> missions;
    private final List<Rocket> rockets;
    private final SecretKey secretKey;

    public MJTSpaceScanner(Reader missionsReader, Reader rocketsReader, SecretKey secretKey) {
        if (missionsReader == null) {
            throw new IllegalArgumentException("MissionsReader cannot be null");
        }
        if (rocketsReader == null) {
            throw new IllegalArgumentException("RocketsReader cannot be null");
        }
        if (secretKey == null) {
            throw new IllegalArgumentException("SecretKey cannot be null");
        }

        missions = readObjects(missionsReader, Mission::of);
        rockets = readObjects(rocketsReader, Rocket::of);
        this.secretKey = secretKey;
    }

    private static <T> List<T> readObjects(Reader reader, Function<String, T> function) {
        BufferedReader bufferedReader = new BufferedReader(reader);

        String line;
        boolean firstLine = true;
        List<T> result = new ArrayList<>();

        try {
            while ((line = bufferedReader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                T object = function.apply(line);
                result.add(object);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Error reading file", e);
        }

        return result;
    }

    @Override
    public Collection<Mission> getAllMissions() {
        return missions.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(missions);
    }

    @Override
    public Collection<Mission> getAllMissions(MissionStatus missionStatus) {
        if (missionStatus == null) {
            throw new IllegalArgumentException("Mission status cannot be null");
        }

        return missions.stream()
            .filter(mission -> mission.missionStatus() == missionStatus)
            .toList();
    }

    @Override
    public String getCompanyWithMostSuccessfulMissions(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("From and to cannot be null");
        }
        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("To cannot be before from");
        }

        return missions.stream()
            .filter(m -> !m.date().isBefore(from) && !m.date().isAfter(to)
                && m.missionStatus() == MissionStatus.SUCCESS)
            .collect(Collectors.groupingBy(Mission::company, Collectors.counting()))
            .entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("");
    }

    @Override
    public Map<String, Collection<Mission>> getMissionsPerCountry() {
        return missions.stream()
            .collect(Collectors.groupingBy(Mission::getCountry, Collectors.toCollection(ArrayList::new)));
    }

    @Override
    public List<Mission> getTopNLeastExpensiveMissions(int n, MissionStatus missionStatus, RocketStatus rocketStatus) {
        if (missionStatus == null || rocketStatus == null) {
            throw new IllegalArgumentException("Missions and/or Rockets cannot be null");
        }
        if (n <= 0) {
            throw new IllegalArgumentException("n must be bigger than 0");
        }

        return missions.stream()
            .filter(m -> m.missionStatus() == missionStatus && m.rocketStatus() == rocketStatus
                && m.cost().isPresent())
            .sorted(Comparator.comparingDouble(m -> m.cost().get()))
            .limit(n)
            .toList();
    }

    @Override
    public Map<String, String> getMostDesiredLocationForMissionsPerCompany() {
        Map<String, Map<String, Long>> companyThenLocationNumber = missions.stream()
            .collect(Collectors.groupingBy(Mission::company,
                Collectors.groupingBy(Mission::location, Collectors.counting())));

        return companyThenLocationNumber.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey,
                entry -> entry.getValue().entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("")));
    }

    @Override
    public Map<String, String> getLocationWithMostSuccessfulMissionsPerCompany(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("From and to cannot be null");
        }
        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("To cannot be before from");
        }

        Map<String, Map<String, Long>> companyThenLocationThenCount = missions.stream()
            .filter(m -> m.missionStatus() == MissionStatus.SUCCESS &&
                !m.date().isBefore(from) && !m.date().isAfter(to))
            .collect(Collectors.groupingBy(Mission::company,
                Collectors.groupingBy(Mission::location, Collectors.counting())));

        return companyThenLocationThenCount.entrySet().stream()
            .filter(entry -> !entry.getValue().isEmpty())
            .collect(Collectors.toMap(Map.Entry::getKey,
                entry -> entry.getValue().entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .get()
                    .getKey()));
    }

    @Override
    public Collection<Rocket> getAllRockets() {
        return rockets.isEmpty() ? Collections.emptyList() : Collections.unmodifiableCollection(rockets);
    }

    @Override
    public List<Rocket> getTopNTallestRockets(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be bigger than 0");
        }

        return rockets.stream()
            .filter(rocket -> rocket.height().isPresent())
            .sorted(Comparator.comparingDouble((Rocket rocket) -> rocket.height().get()).reversed())
            .limit(n)
            .toList();
    }

    @Override
    public Map<String, Optional<String>> getWikiPageForRocket() {
        return rockets.stream()
            .collect(Collectors.toMap(Rocket::name, Rocket::wiki,
                (first, second) -> first));
    }

    @Override
    public List<String> getWikiPagesForRocketsUsedInMostExpensiveMissions(int n, MissionStatus missionStatus,
                                                                          RocketStatus rocketStatus) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be bigger than 0");
        }
        if (missionStatus == null || rocketStatus == null) {
            throw new IllegalArgumentException("missionStatus and/or rocketStatus cannot be null");
        }

        Map<String, Rocket> namesOfRockets = rockets.stream()
            .collect(Collectors.toMap(Rocket::name, rocket -> rocket));

        return missions.stream()
            .filter(m -> m.rocketStatus() == rocketStatus && m.missionStatus() == missionStatus)
            .sorted(Comparator.comparingDouble((Mission m) -> m.cost().orElse(0.0)).reversed())
            .limit(n)
            .map(mission -> {
                String rocketName = mission.detail().rocketName();
                Rocket rocket = namesOfRockets.get(rocketName);
                System.out.println(rocketName);
                System.out.println(namesOfRockets.containsKey(rocketName));
                return rocket == null ? "" : rocket.wiki().orElse("");
            })
            .toList();
    }

    @Override
    public void saveMostReliableRocket(OutputStream outputStream, LocalDate from, LocalDate to) throws CipherException {
        if (outputStream == null || from == null || to == null) {
            throw new IllegalArgumentException("From, to and outputStream cannot be null");
        }
        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("To cannot be before from");
        }

        String mostReliableRocket = missions.stream()
            .filter(m -> !m.date().isBefore(from) && !m.date().isAfter(to))
            .collect(Collectors.groupingBy(m -> m.detail().rocketName()))
            .entrySet().stream()
            .map(entry -> Map.entry(entry.getKey(),
                new Reliability(entry.getValue(), entry.getKey())))
            .max(Comparator.comparingDouble(entry -> entry.getValue().getReliability()))
            .map(Map.Entry::getKey)
            .orElse("");

        byte[] toEncrypt = mostReliableRocket.getBytes(StandardCharsets.UTF_8);
        try (InputStream inputStream = new ByteArrayInputStream(toEncrypt)) {
            try {
                Rijndael rijndael = new Rijndael(secretKey);
                rijndael.encrypt(inputStream, outputStream);
            } catch (Exception e) {
                throw new CipherException("Error encrypting", e);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Exception thrown from creating inputStream", e);
        }
    }
}
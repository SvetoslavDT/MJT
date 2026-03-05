package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public record Mission(String id, String company, String location, LocalDate date, Detail detail,
                      RocketStatus rocketStatus, Optional<Double> cost, MissionStatus missionStatus)
    implements Serializable, Comparable<Mission> {

    private static final int DATE_TOKEN = 3;
    private static final int DETAIL_TOKEN = 4;
    private static final int ROCKET_STATUS_TOKEN = 5;
    private static final int COST_TOKEN = 6;
    private static final int MISSION_STATUS_TOKEN = 7;
    private static final int LEAST_TOKENS = 8;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(
        "EEE MMM dd, yyyy", Locale.ENGLISH);

    public static Mission of(String line) {
        List<String> tokens = splitLineToCorrectTokens(line);

        if (tokens.size() < LEAST_TOKENS) {
            throw new IllegalArgumentException("Tokens must be at least 8");
        }

        String[] unquotedTokens = new String[tokens.size()];
        for (int i = 0; i < unquotedTokens.length; i++) {
            unquotedTokens[i] = unquote(tokens.get(i));
        }

        LocalDate date = LocalDate.parse(unquotedTokens[DATE_TOKEN], FORMATTER);
        Detail detail = Detail.of(unquotedTokens[DETAIL_TOKEN]);
        RocketStatus rocketStatus = RocketStatus.fromString(unquotedTokens[ROCKET_STATUS_TOKEN]);
        Optional<Double> cost = unquotedTokens[COST_TOKEN].isEmpty() ?
            Optional.empty() : Optional.of(getCost(unquotedTokens[COST_TOKEN]));
        MissionStatus missionStatus = MissionStatus.fromString(unquotedTokens[MISSION_STATUS_TOKEN]);

        return new Mission(unquotedTokens[0], unquotedTokens[1], unquotedTokens[2], date, detail,
            rocketStatus, cost, missionStatus);
    }

    private static double getCost(String num) {
        if (num.contains(",")) {
            return Double.parseDouble(num.replace(",", ""));
        }
        return Double.parseDouble(num);
    }

    private static String unquote(String str) {
        String trimmed = str.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }

        return trimmed.trim();
    }

    private static List<String> splitLineToCorrectTokens(String line) {
        List<String> tokens = new ArrayList<>();

        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                inQuotes = !inQuotes;
            } else if (ch == ',' && !inQuotes) {
                tokens.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        tokens.add(current.toString());
        return tokens;
    }

    public String getCountry() {
        List<String> tokens = splitLineToCorrectTokens(this.location);

        String[] unquotedTokens = new String[tokens.size()];
        for (int i = 0; i < unquotedTokens.length; i++) {
            unquotedTokens[i] = unquote(tokens.get(i));
        }

        return unquotedTokens[unquotedTokens.length - 1].trim();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Mission mission)) return false;
        return Objects.equals(id, mission.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public int compareTo(Mission o) {
        if (o == null) {
            throw new NullPointerException("Mission is null");
        }
        return id.compareTo(o.id);
    }
}

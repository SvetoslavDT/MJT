package bg.sofia.uni.fmi.mjt.space.rocket;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public record Rocket(String id, String name, Optional<String> wiki, Optional<Double> height) implements Serializable,
    Comparable<Rocket> {

    private static final int EXPECTED_TOKENS = 4;

    private static final int FIRST_TOKEN = 0;
    private static final int SECOND_TOKEN = 1;
    private static final int THIRD_TOKEN = 2;
    private static final int FOURTH_TOKEN = 3;
    private static final int FIFTH_TOKEN = 4;

    public static Rocket of(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("line cannot be null or blank");
        }
        if (line.contains("\"")) {
            line = line.replaceAll("\"", "");
        }

        String[] tokens = line.split(",", -1);

        if (tokens.length > EXPECTED_TOKENS) {
            tokens = correctTokens(tokens);
        }

        Optional<String> wiki = tokens[THIRD_TOKEN].isBlank() ? Optional.empty() : Optional.of(tokens[2]);
        Optional<Double> height;
        if (tokens[FOURTH_TOKEN].isBlank()) {
            height = Optional.empty();
        } else {
            String value = tokens[FOURTH_TOKEN].replace(" m", "");
            height = Optional.of(Double.parseDouble(value));
        }

        return new Rocket(tokens[FIRST_TOKEN], tokens[SECOND_TOKEN], wiki, height);
    }

    private static String[] correctTokens(String[] tokens) {
        String firstHalf = tokens[SECOND_TOKEN];
        String secondHalf = tokens[THIRD_TOKEN];

        return new String[] {tokens[FIRST_TOKEN], firstHalf + "," + secondHalf,
            tokens[FOURTH_TOKEN], tokens[FIFTH_TOKEN]};
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Rocket rocket)) return false;
        return Objects.equals(id, rocket.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public int compareTo(Rocket o) {
        if (o == null) {
            throw new NullPointerException("Rocket cannot be null");
        }
        return id.compareTo(o.id);
    }
}
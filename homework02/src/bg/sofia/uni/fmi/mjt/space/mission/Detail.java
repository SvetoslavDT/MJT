package bg.sofia.uni.fmi.mjt.space.mission;

import java.io.Serializable;

public record Detail(String rocketName, String payload) implements Serializable {

    private static final int ROCKET_NAME = 0;
    private static final int PAYLOAD = 1;
    private static final int MAX_STRING_SEPARATIONS = 2;

    public static Detail of(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("line is null or blank");
        }

        String[] tokens = line.split("\\|", MAX_STRING_SEPARATIONS);
        for (String token : tokens) {
            token = token.trim();
        }

        return new Detail(tokens[ROCKET_NAME].trim(), tokens[PAYLOAD].trim());
    }
}

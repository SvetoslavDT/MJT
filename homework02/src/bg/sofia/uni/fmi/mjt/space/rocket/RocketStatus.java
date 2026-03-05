package bg.sofia.uni.fmi.mjt.space.rocket;

import java.io.Serializable;

public enum RocketStatus implements Serializable {
    STATUS_RETIRED("StatusRetired"),
    STATUS_ACTIVE("StatusActive");

    private final String value;

    RocketStatus(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public static RocketStatus fromString(String value) {
        for (RocketStatus status : RocketStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }

        return null;
    }
}
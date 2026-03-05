package bg.sofia.uni.fmi.mjt.space.reliability;

import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;

import java.util.List;

public class Reliability {

    private int failedMissions = 0;
    private int successfulMissions = 0;
    private final int totalMissions;

    public Reliability(List<Mission> missions, String rocketName) {
        if (missions == null || rocketName == null) {
            throw new IllegalArgumentException("Missions or Rockets are null");
        }

        for (Mission mission : missions) {
            if (mission.missionStatus() == MissionStatus.SUCCESS) {
                successfulMissions++;
            } else {
                failedMissions++;
            }
        }

        totalMissions = failedMissions + successfulMissions;
    }

    public double getReliability() {
        if (totalMissions == 0) {
            return 0.0;
        }

        return (double) (2 * successfulMissions + failedMissions) / (2 * totalMissions);
    }
}
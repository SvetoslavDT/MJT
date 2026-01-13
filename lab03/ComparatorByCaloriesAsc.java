package bg.sofia.uni.fmi.mjt.fittrack;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

import java.util.Comparator;

public class ComparatorByCaloriesAsc implements Comparator<Workout> {
    public int compare(Workout lhs, Workout rhs) {
        return Integer.compare(rhs.getCaloriesBurned(), lhs.getCaloriesBurned());
    }
}

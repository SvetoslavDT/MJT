package bg.sofia.uni.fmi.mjt.fittrack;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

import java.util.Comparator;

public class ComparatorByDifficultyAsc implements Comparator<Workout> {
    public int compare(Workout lhs, Workout rhs) {
        return Integer.compare(lhs.getDifficulty(), rhs.getDifficulty());
    }
}

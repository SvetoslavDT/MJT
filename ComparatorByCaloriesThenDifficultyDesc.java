package bg.sofia.uni.fmi.mjt.fittrack;

import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;

import java.util.Comparator;

public class ComparatorByCaloriesThenDifficultyDesc implements Comparator<Workout> {

    @Override
    public int compare(Workout lhs, Workout rhs) {

        int result = Integer.compare(rhs.getCaloriesBurned(), lhs.getCaloriesBurned());

        if (result == 0) {
            result = Integer.compare(rhs.getDifficulty(), lhs.getDifficulty());
        }

        return result;
    }
}

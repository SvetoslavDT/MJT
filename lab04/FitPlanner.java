package bg.sofia.uni.fmi.mjt.fittrack;

import bg.sofia.uni.fmi.mjt.fittrack.exception.OptimalPlanImpossibleException;
import bg.sofia.uni.fmi.mjt.fittrack.workout.Workout;
import bg.sofia.uni.fmi.mjt.fittrack.workout.WorkoutType;
import bg.sofia.uni.fmi.mjt.fittrack.workout.filter.WorkoutFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FitPlanner implements FitPlannerAPI {

    private final List<Workout> workouts;

    public FitPlanner(Collection<Workout> availableWorkouts) {
        if (availableWorkouts == null) {
            throw new IllegalArgumentException("availableWorkouts is null");
        }

        workouts = new ArrayList<>();
        workouts.addAll(availableWorkouts);
    }

    @Override
    public List<Workout> findWorkoutsByFilters(List<WorkoutFilter> filters) {
        if (filters == null) {
            throw new IllegalArgumentException("filters is null");
        }

        if (workouts.isEmpty()) {
            return List.of();
        }

        List<Workout> result = new ArrayList<>();

        for (Workout workout : workouts) {
            boolean matchesAll = true;

            for (WorkoutFilter filter : filters) {
                if (!filter.matches(workout)) {
                    matchesAll = false;
                    break;
                }
            }

            if (matchesAll) {
                result.add(workout);
            }
        }

        return Collections.unmodifiableList(result);
    }

    @Override
    public List<Workout> generateOptimalWeeklyPlan(int totalMinutes) throws OptimalPlanImpossibleException {
        if (totalMinutes < 0) {
            throw new IllegalArgumentException("totalMinutes is negative");
        }
        if (totalMinutes == 0 || workouts.isEmpty()) {
            return List.of();
        }

        ensureAtLeastOneFits(totalMinutes);

        int[][] dp = buildDpTable(totalMinutes);
        int maxCalories = dp[workouts.size()][totalMinutes];

        if (maxCalories == 0) {
            throw new OptimalPlanImpossibleException("Could not build an optimal plan (max calories == 0).");
        }

        List<Workout> chosen = reconstructChosenWorkouts(dp, totalMinutes);
        sortByCaloriesThenDifficultyDesc(chosen);

        return Collections.unmodifiableList(chosen);
    }

    private void ensureAtLeastOneFits(int totalMinutes) throws OptimalPlanImpossibleException {

        boolean fitsAtLeastOne = false;
        for (Workout workout : workouts) {

            if (workout.getDuration() <= totalMinutes) {
                fitsAtLeastOne = true;
                break;
            }
        }

        if (!fitsAtLeastOne) {
            throw new OptimalPlanImpossibleException("All workouts exceed the time limit");
        }
    }

    private int[][] buildDpTable(int totalMinutes) {

        int n = workouts.size();
        int[][] dp = new int[n + 1][totalMinutes + 1];

        for (int i = 1; i <= n; i++) {

            Workout w = workouts.get(i - 1);
            int wDuration = w.getDuration();
            int wCaloriesBurned = w.getCaloriesBurned();

            for (int j = 0; j <= totalMinutes; j++) {
                dp[i][j] = dp[i - 1][j];

                if (wDuration <= j) {
                    int candidate = dp[i - 1][j - wDuration] + wCaloriesBurned;
                    if (candidate > dp[i][j]) {
                        dp[i][j] = candidate;
                    }
                }
            }
        }
        return dp;
    }

    private List<Workout> reconstructChosenWorkouts(int[][] dp, int totalMinutes) {
        List<Workout> chosen = new ArrayList<>();
        int total = totalMinutes;
        int n = workouts.size();

        for (int i = n; i >= 1; i--) {

            if (dp[i][total] != dp[i - 1][total]) {
                Workout w = workouts.get(i - 1);
                chosen.add(w);
                total -= w.getDuration();
            }
        }
        return chosen;
    }

    @Override
    public Map<WorkoutType, List<Workout>> getWorkoutsGroupedByType() {
        if (workouts.isEmpty()) {
            return Map.of();
        }

        Map<WorkoutType, List<Workout>> result = new HashMap<>();

        for (Workout workout : workouts) {
            WorkoutType type = workout.getType();

            result.putIfAbsent(type, new ArrayList<>());
            result.get(type).add(workout);
        }

        Map<WorkoutType, List<Workout>> unmodifiableResult = new HashMap<>();
        for (Map.Entry<WorkoutType, List<Workout>> entry : result.entrySet()) {
            unmodifiableResult.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        }

        return Collections.unmodifiableMap(unmodifiableResult);
    }

    @Override
    public List<Workout> getWorkoutsSortedByCalories() {
        if (workouts.isEmpty()) {
            return List.of();
        }

        List<Workout> result = new ArrayList<>(workouts);
        Collections.sort(result, new ComparatorByCaloriesAsc());

        return Collections.unmodifiableList(result);
    }

    @Override
    public List<Workout> getWorkoutsSortedByDifficulty() {
        if (workouts.isEmpty()) {
            return List.of();
        }

        List<Workout> result = new ArrayList<>(workouts);

        Collections.sort(result, new ComparatorByDifficultyAsc());

        return Collections.unmodifiableList(result);
    }

    @Override
    public Set<Workout> getUnmodifiableWorkoutSet() {
        if (workouts.isEmpty()) {
            return Set.of();
        }

        return Collections.unmodifiableSet(new HashSet<>(workouts));
    }

    private void sortByCaloriesThenDifficultyDesc(List<Workout> workouts) {
        Collections.sort(workouts, new ComparatorByCaloriesThenDifficultyDesc());
    }
}

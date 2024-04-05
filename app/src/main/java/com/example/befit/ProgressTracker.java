package com.example.befit;

import java.util.ArrayList;
import java.util.List;

public class ProgressTracker {
    private static ProgressTracker instance;
    private int totalExercisesCompleted;
    private long totalPauseTimeMillis;
    private int totalCaloriesBurned;
    private List<ExerciseEntry> exerciseHistory;

    private ProgressTracker() {
        this.totalExercisesCompleted = 0;
        this.totalPauseTimeMillis = 0;
        this.totalCaloriesBurned = 0;
        this.exerciseHistory = new ArrayList<>();
    }

    public static ProgressTracker getInstance() {
        if (instance == null) {
            instance = new ProgressTracker();
        }
        return instance;
    }

    public int getTotalExercisesCompleted() {
        return totalExercisesCompleted;
    }

    public long getTotalPauseTimeMillis() {
        return totalPauseTimeMillis;
    }

    public int getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public void completeExercise() {
        totalExercisesCompleted++;
        ExerciseEntry entry = new ExerciseEntry(ExerciseType.EXERCISE, System.currentTimeMillis());
        exerciseHistory.add(entry);
    }

    public void addPauseTime(long pauseTimeMillis) {
        // Add pause time only if the exercise was paused
        if (pauseTimeMillis > 0) {
            totalPauseTimeMillis += pauseTimeMillis;
            ExerciseEntry entry = new ExerciseEntry(ExerciseType.PAUSE, System.currentTimeMillis(), pauseTimeMillis);
            exerciseHistory.add(entry);
        }
    }

    public void addCaloriesBurned(long exerciseDurationMillis) {
        // Calculate calories burned based on exercise duration
        double hours = (double) exerciseDurationMillis / (1000 * 60 * 60);
        int caloriesBurned = (int) (hours * 200); // Assuming 200 calories burned per hour
        // Add calories burned only if an exercise was completed
        if (caloriesBurned > 0) {
            totalCaloriesBurned += caloriesBurned;
            ExerciseEntry entry = new ExerciseEntry(ExerciseType.EXERCISE, System.currentTimeMillis(), exerciseDurationMillis);
            exerciseHistory.add(entry);
        }
    }


    public void displayProgress() {
        System.out.println("Total exercises completed: " + totalExercisesCompleted);
        System.out.println("Total pause time (in seconds): " + totalPauseTimeMillis / 1000);
        System.out.println("Total calories burned: " + totalCaloriesBurned);
        System.out.println("Exercise History:");
        for (ExerciseEntry entry : exerciseHistory) {
            System.out.println(entry);
        }
    }

    private static class ExerciseEntry {
        private ExerciseType type;
        private long timestamp;
        private long durationMillis;

        public ExerciseEntry(ExerciseType type, long timestamp) {
            this.type = type;
            this.timestamp = timestamp;
        }

        public ExerciseEntry(ExerciseType type, long timestamp, long durationMillis) {
            this.type = type;
            this.timestamp = timestamp;
            this.durationMillis = durationMillis;
        }

        @Override
        public String toString() {
            if (type == ExerciseType.EXERCISE) {
                return "Exercise at " + timestamp;
            } else {
                return "Pause for " + (durationMillis / 1000) + " seconds at " + timestamp;
            }
        }
    }

    private enum ExerciseType {
        EXERCISE, PAUSE
    }
}

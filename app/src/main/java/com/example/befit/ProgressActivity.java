package com.example.befit;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProgressActivity extends AppCompatActivity {

    private ProgressTracker progressTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_activity);

        // Instantiate ProgressTracker
        progressTracker = ProgressTracker.getInstance();


        // Display initial progress information
        displayProgress();

        // Set click listener for the "Update Progress" button
        Button updateProgressButton = findViewById(R.id.updateProgressButton);
        updateProgressButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Check if any exercise is completed before updating progress
                if (progressTracker.getTotalExercisesCompleted() > 0) {
                    // Example: Simulate completing an exercise
                    //progressTracker.completeExercise();//Dont use it now

                    // Example: Simulate adding pause time
                    long pauseTimeMillis = 5000; // Example pause time: 5 seconds
                    progressTracker.addPauseTime(pauseTimeMillis);

                    // Example: Simulate adding calories burned
                    long exerciseDurationMillis = 300000; // Example exercise duration: 5 minutes
                    progressTracker.addCaloriesBurned(exerciseDurationMillis);

                    // Update and display progress information
                    displayProgress();
                } else {
                    // Inform the user that no exercise has been completed yet
                    Toast.makeText(ProgressActivity.this, "No exercise completed yet.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to display progress information
    private void displayProgress() {
        // Access progress information using getter methods
        int totalExercisesCompleted = progressTracker.getTotalExercisesCompleted();
        long totalPauseTimeMillis = progressTracker.getTotalPauseTimeMillis();
        int totalCaloriesBurned = progressTracker.getTotalCaloriesBurned();

        // Display progress information
        TextView totalExercisesTextView = findViewById(R.id.totalExercisesTextView);
        TextView totalPauseTimeTextView = findViewById(R.id.totalPauseTimeTextView);
        TextView totalCaloriesBurnedTextView = findViewById(R.id.totalCaloriesBurnedTextView);
        totalExercisesTextView.setText("Total Exercises Completed: " + totalExercisesCompleted);
        totalPauseTimeTextView.setText("Total Pause Time (seconds): " + (totalPauseTimeMillis / 1000));
        totalCaloriesBurnedTextView.setText("Total Calories Burned: " + totalCaloriesBurned);
    }
}

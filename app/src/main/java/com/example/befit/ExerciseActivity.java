package com.example.befit;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExerciseActivity extends AppCompatActivity {

    private Button startButton;
    private List<ImageView> exerciseImages = new ArrayList<>();
    private ProgressBar exerciseProgressBar;
    private TextView exerciseDurationTextView;
    private RatingBar exerciseRatingBar;
    private BottomNavigationView bottomNavigationView;

    private int currentExerciseIndex = 0;
    private CountDownTimer exerciseTimer;
    private Map<Integer, Float> exerciseRatings = new HashMap<>();
    private boolean isExercisePaused = false;
    private long remainingTimeMillis;
    private long exerciseDurationMillis;
    private long elapsedMillis;

    private long exerciseStartTime;

    private Button resetButton;

    private ProgressTracker progressTracker;
    private DatabaseHelper databaseHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        // Initialize the DatabaseHelper instance
        databaseHelper = new DatabaseHelper(this);

        // Initialize the ProgressTracker instance
        progressTracker = ProgressTracker.getInstance();

        // Initialize UI elements
        startButton = findViewById(R.id.startButton);
        // Initialize reset button
        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetTimer();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the button and start the exercise
                v.setVisibility(View.GONE);
                startExerciseAfterDelay(2000); // Start after 5 seconds (for demonstration)
            }
        });

        // Add exercise images to the list
        exerciseImages.add(findViewById(R.id.exerciseImage1));
        exerciseImages.add(findViewById(R.id.exerciseImage2));
        exerciseImages.add(findViewById(R.id.exerciseImage3));
        exerciseImages.add(findViewById(R.id.exerciseImage4));
        exerciseImages.add(findViewById(R.id.exerciseImage5));
        exerciseImages.add(findViewById(R.id.exerciseImage6));
        exerciseImages.add(findViewById(R.id.exerciseImage7));

        exerciseProgressBar = findViewById(R.id.exerciseProgressBar);
        exerciseDurationTextView = findViewById(R.id.exerciseDurationTextView);
        exerciseRatingBar = findViewById(R.id.exerciseRatingBar);

        // Set initial progress and rating
        exerciseProgressBar.setProgress(0);
        exerciseRatingBar.setRating(0);

        // Hide all exercise images initially
        hideAllExerciseImages();

        // Initialize bottom navigation view
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            if (item.getItemId() == R.id.navigation_home) {
                // Start HomeActivity
                startActivity(new Intent(ExerciseActivity.this, HomeActivity.class));
                finish(); // Optional: Close the current activity if necessary
                return true;
            } else {
                return false;
            }

        });

        // Set the exercise duration (in milliseconds)
        exerciseDurationMillis = 5000; // Example: 20 seconds
        Button pauseButton = findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExercisePaused) {
                    pauseExercise();
                } else {
                    resumeExercise();
                }
            }
        });


        Button exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the HomeActivity
                Intent intent = new Intent(ExerciseActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Finish the ExerciseActivity
            }
        });
    }

    private void startExerciseAfterDelay(long delayMillis) {
        // Show countdown message with delay
        TextView countdownTextView = findViewById(R.id.countdownTextView);
        countdownTextView.setVisibility(View.VISIBLE);

        new CountDownTimer(delayMillis, 1000) {
            int count = (int) (delayMillis / 1000);

            @Override
            public void onTick(long millisUntilFinished) {
                // Update countdown message
                countdownTextView.setText("The exercise is going to start in " + count + "...");
                count--;
            }

            @Override
            public void onFinish() {
                // Start the exercise after delay
                countdownTextView.setVisibility(View.GONE); // Hide countdown message
                startExercise();
            }
        }.start();
    }

    private void startExercise() {
        // Check if there are more exercises to display
        exerciseStartTime = System.currentTimeMillis();
        if (currentExerciseIndex < exerciseImages.size()) {
            // Shuffle the exercise images to ensure randomness (only shuffle once)
            if (currentExerciseIndex == 0) {
                Collections.shuffle(exerciseImages);
            }

            // Hide all exercise images
            hideAllExerciseImages();

            // Show the next exercise image
            ImageView currentExerciseImage = exerciseImages.get(currentExerciseIndex);
            currentExerciseImage.setVisibility(View.VISIBLE);

            // Reset rating bar to 0 for the current exercise
            exerciseRatingBar.setRating(0);

            // Start countdown timer for the exercise duration
            startTimer(exerciseDurationMillis); // Use exerciseDurationMillis variable for exercise duration
        } else {
            // All exercises have been completed
            exerciseDurationTextView.setText("Exercise Completed for today");
            // Decrease the font size
            exerciseDurationTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15); // Change 16 to your desired font size
        }
    }



    private void startTimer(long durationMillis) {
        exerciseTimer = new CountDownTimer(durationMillis - elapsedMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update countdown timer text
                updateTimerText(millisUntilFinished + elapsedMillis); // Add elapsed time to remaining time

                // Update progress bar
                int progress = (int) ((millisUntilFinished * 100) / durationMillis);
                exerciseProgressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                // Hide the current exercise image
                if (currentExerciseIndex < exerciseImages.size()) {
                    exerciseImages.get(currentExerciseIndex).setVisibility(View.GONE);
                }

                // Move to the next exercise when timer finishes
                currentExerciseIndex++;
                elapsedMillis = 0; // Reset elapsed time for next exercise
                startExercise();
                onExerciseCompleted();
            }
        }.start();
    }


    private void updateTimerText(long millisUntilFinished) {
        // Convert milliseconds to minutes and seconds
        long minutes = millisUntilFinished / 60000;
        long seconds = (millisUntilFinished % 60000) / 1000;

        // Format the time as a string
        String timeString = String.format("%02d:%02d", minutes, seconds);

        // Update the countdown timer text
        exerciseDurationTextView.setText(timeString);
    }

    private void hideAllExerciseImages() {
        for (ImageView exerciseImage : exerciseImages) {
            exerciseImage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
        // Cancel the exercise timer to prevent memory leaks
        if (exerciseTimer != null) {
            exerciseTimer.cancel();
        }
    }

    public void submitRating(View view) {
        // Get the rating provided by the user
        float rating = exerciseRatingBar.getRating();

        // Save the rating for the current exercise
        exerciseRatings.put(currentExerciseIndex, rating);

        // Save the rating to the database
        saveRatingToDatabase(currentExerciseIndex, rating);
    }

    private void saveRatingToDatabase(int exerciseIndex, float rating) {
        // Get the rating provided by the user
        rating = exerciseRatingBar.getRating();

        // Save the rating for the current exercise to the database
        long exerciseId = currentExerciseIndex + 1; // Example: Use the exercise index as exercise ID
        long userId = 1; // Example: Assuming there is a single user with ID 1
        long ratingId = databaseHelper.insertExerciseRating(exerciseId, userId, rating);
        if (ratingId != -1) {
            // Rating inserted successfully
            Toast.makeText(this, "Rating submitted successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Error inserting rating
            Toast.makeText(this, "Failed to submit rating", Toast.LENGTH_SHORT).show();
        }
    }

    private void onExerciseCompleted() {
        // Update the progress tracker when an exercise is completed
        progressTracker.completeExercise(); // Update progress tracker

        // Show a completion message
        Toast.makeText(this, "Exercise completed!", Toast.LENGTH_SHORT).show();

        // Optionally, you can update the UI or take any other actions based on exercise completion.
    }

    private void pauseExercise() {
        // Pause the exercise timer
        if (exerciseTimer != null) {
            exerciseTimer.cancel();
            isExercisePaused = true;

            // Calculate remaining time by subtracting elapsed time from total duration
            remainingTimeMillis = exerciseDurationMillis - (System.currentTimeMillis() - exerciseStartTime);

            // Update button text to indicate resume
            Button pauseButton = findViewById(R.id.pauseButton);
            pauseButton.setText("Resume Exercise");
        }
        // Update progress tracker when exercise is paused
        progressTracker.addPauseTime(exerciseDurationMillis - remainingTimeMillis);
    }


    private void resumeExercise() {
        // Resume the exercise timer
        if (exerciseTimer != null) {
            startTimer(remainingTimeMillis); // Resume timer with remaining time
            isExercisePaused = false;

            // Update button text to indicate pause
            Button pauseButton = findViewById(R.id.pauseButton);
            pauseButton.setText("Pause Exercise");

            // No need to update elapsed time here
        }
    }

    private void resetTimer() {
        // Cancel the current exercise timer if it's running
        if (exerciseTimer != null) {
            exerciseTimer.cancel();
        }

        // Reset timer-related variables for the current exercise
        elapsedMillis = 0;
        remainingTimeMillis = exerciseDurationMillis;

        // Update UI to initial state
        exerciseProgressBar.setProgress(0);
        updateTimerText(exerciseDurationMillis);

        // Update progress tracker when timer is reset
        progressTracker.completeExercise();

        // Hide all exercise images except the current one
        hideAllExerciseImages();
        if (currentExerciseIndex < exerciseImages.size()) {
            ImageView currentExerciseImage = exerciseImages.get(currentExerciseIndex);
            currentExerciseImage.setVisibility(View.VISIBLE);
        }

        // Start the exercise if it's not paused
        if (!isExercisePaused) {
            startTimer(remainingTimeMillis);
        }
    }

}


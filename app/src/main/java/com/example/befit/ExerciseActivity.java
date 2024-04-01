package com.example.befit;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

    private int currentExerciseIndex = 0;
    private CountDownTimer exerciseTimer;
    private Map<Integer, Float> exerciseRatings = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        // Initialize UI elements
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the button and start the exercise
                v.setVisibility(View.GONE);
                startExerciseAfterDelay(5000); // Start after 5 seconds (for demonstration)
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
    }


    private void startExerciseAfterDelay(long delayMillis) {
        new CountDownTimer(delayMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Do nothing
            }

            @Override
            public void onFinish() {
                // Start the exercise
                startExercise();
            }
        }.start();
    }

    private void startExercise() {
        // Check if there are more exercises to display
        if (currentExerciseIndex < exerciseImages.size()) {
            // Shuffle the exercise images to ensure randomness
            Collections.shuffle(exerciseImages);

            // Show the next exercise image
            ImageView currentExerciseImage = exerciseImages.get(currentExerciseIndex);
            currentExerciseImage.setVisibility(View.VISIBLE);

            // Reset rating bar to 0 for the current exercise
            exerciseRatingBar.setRating(0);

            // Start countdown timer for 20 seconds
            startTimer(20000); // 20 seconds in milliseconds
        } else {
            // All exercises have been completed
            exerciseDurationTextView.setText("Exercise Completed for today");
            // Decrease the font size
            exerciseDurationTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15); // Change 16 to your desired font size
        }
    }


    private void startTimer(long durationMillis) {
        exerciseTimer = new CountDownTimer(durationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update countdown timer text
                updateTimerText(millisUntilFinished);

                // Update progress bar
                int progress = (int) ((durationMillis - millisUntilFinished) * 100 / durationMillis);
                exerciseProgressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                // Hide the current exercise image
                exerciseImages.get(currentExerciseIndex).setVisibility(View.GONE);

                // Move to the next exercise when timer finishes
                currentExerciseIndex++;
                startExercise();
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
        // Implement database saving logic here
        // For example, use DatabaseHelper to insert the rating into the database
    }
}

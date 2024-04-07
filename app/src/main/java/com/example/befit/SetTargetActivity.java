package com.example.befit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SetTargetActivity extends AppCompatActivity {

    private EditText dailyTargetEditText;
    private EditText weeklyTargetEditText;
    private TextView dailyTargetProgressTextView;
    private TextView weeklyTargetProgressTextView;
    private SharedPreferences sharedPreferences;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_target);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("targets", MODE_PRIVATE);

        dailyTargetEditText = findViewById(R.id.dailyTargetEditText);
        dailyTargetEditText.setText(""); // Set initial value to empty string
        weeklyTargetEditText = findViewById(R.id.weeklyTargetEditText);
        weeklyTargetEditText.setText(""); // Set initial value to empty string
        dailyTargetProgressTextView = findViewById(R.id.dailyTargetProgressTextView);
        weeklyTargetProgressTextView = findViewById(R.id.weeklyTargetProgressTextView);
        Button saveTargetButton = findViewById(R.id.saveTargetButton);

        // Retrieve previously saved targets, defaulting to 0 if not found
        int savedDailyTarget = sharedPreferences.getInt("dailyTarget", 0);
        int savedWeeklyTarget = sharedPreferences.getInt("weeklyTarget", 0);
        dailyTargetEditText.setText(String.valueOf(savedDailyTarget));
        weeklyTargetEditText.setText(String.valueOf(savedWeeklyTarget));


        // Initialize bottom navigation view
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            // Handle navigation item clicks here
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // Start HomeActivity
                startActivity(new Intent(SetTargetActivity.this, HomeActivity.class));
                finish(); // Close the current activity if necessary
                return true;
            }
            if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(SetTargetActivity.this, ProfileActivity.class));
                finish(); // Close the current activity if necessary
                return true;
            } else {
                return false;
            }
        });



        // Set click listener for the "Save Target" button
        saveTargetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Get the entered daily and weekly targets
                String dailyTargetStr = dailyTargetEditText.getText().toString().trim();
                String weeklyTargetStr = weeklyTargetEditText.getText().toString().trim();

                // Check if both fields are filled
                if (!dailyTargetStr.isEmpty() && !weeklyTargetStr.isEmpty()) {
                    // Parse the targets to integers
                    int dailyTarget = Integer.parseInt(dailyTargetStr);
                    int weeklyTarget = Integer.parseInt(weeklyTargetStr);

                    // Save the targets to SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("dailyTarget", dailyTarget);
                    editor.putInt("weeklyTarget", weeklyTarget);
                    editor.apply();

                    // Update progress TextViews based on current progress
                    updateProgressTextViews(dailyTarget, weeklyTarget);

                    // Pass the targets back to the calling activity (HomeActivity)
                    Intent intent = new Intent();
                    intent.putExtra("dailyTarget", dailyTarget);
                    intent.putExtra("weeklyTarget", weeklyTarget);
                    setResult(RESULT_OK, intent);
                    finish(); // Close this activity
                } else {
                    // Display a toast message if any field is empty
                    Toast.makeText(SetTargetActivity.this, "Please enter both targets", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Update progress TextViews based on saved targets
        updateProgressTextViews(savedDailyTarget, savedWeeklyTarget);
    }

    private void updateProgressTextViews(int dailyTarget, int weeklyTarget) {
        // Retrieve current progress from ProgressTracker
        ProgressTracker progressTracker = ProgressTracker.getInstance(SetTargetActivity.this);
        int exercisesCompletedToday = progressTracker.getExercisesCompletedToday();
        int exercisesCompletedThisWeek = progressTracker.getExercisesCompletedThisWeek();

        // Update TextViews
        dailyTargetProgressTextView.setText("Exercise done today: " + exercisesCompletedToday + "/" + dailyTarget);
        weeklyTargetProgressTextView.setText("Exercise done this week: " + exercisesCompletedThisWeek + "/" + weeklyTarget);
    }
}

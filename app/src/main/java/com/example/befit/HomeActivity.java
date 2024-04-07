package com.example.befit;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.befit.ExerciseActivity;
import com.example.befit.ProgressTracker;
import com.example.befit.SetTargetActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private static final int PROGRESS_ACTIVITY_REQUEST_CODE = 1;
    private int totalExercisesCompleted = 0;
    private ProgressTracker progressTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        ImageView homeButtonImageView = findViewById(R.id.homeButtonImageView);
        Button setTargetButton = findViewById(R.id.setTargetButton);
        Button workoutButton = findViewById(R.id.workoutButton);

        progressTracker = ProgressTracker.getInstance(this);


        // Set click listener for the "Set Target" button
//        Button setTargetButton = findViewById(R.id.setTargetButton);
        setTargetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, SetTargetActivity.class);
                // Pass the totalExercisesCompleted data with the intent
                intent.putExtra("totalExercisesCompleted", totalExercisesCompleted);
                // Start SetTargetActivity
                startActivity(intent);
            }
        });

        // Set click listener for the workout button
        workoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ExerciseActivity
                startActivity(new Intent(HomeActivity.this, ExerciseActivity.class));
            }
        });

        // Set click listener for the "View Progress" button
        Button viewProgressButton = findViewById(R.id.viewProgressButton);
        viewProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
//            public void onClick(View v) {
//                // Start ProgressActivity and wait for result
//                startActivityForResult(new Intent(HomeActivity.this, ProgressActivity.class),PROGRESS_ACTIVITY_REQUEST_CODE);
//            }
//        });
            public void onClick(View v) {
                // Create an intent to navigate to the ProgressActivity
                Intent progressIntent = new Intent(HomeActivity.this, ProgressActivity.class);

                // Start ProgressActivity and wait for result
                startActivityForResult(progressIntent, PROGRESS_ACTIVITY_REQUEST_CODE);
            }
        });
        // Set click listener for the bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            // Handle navigation item clicks here
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // Show a toast message indicating already at the home page
                Toast.makeText(HomeActivity.this, "You are already at the home page", Toast.LENGTH_SHORT).show();
                return true;
            }
            // Add conditions for other navigation items if needed
            if (item.getItemId() == R.id.navigation_profile) {
                // Start profileActivity
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                //finish(); // Optional: Close the current activity if necessary
                return true;
            } else {
                return false;
            }
        });

    }
}
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PROGRESS_ACTIVITY_REQUEST_CODE) {
//            if (resultCode == RESULT_OK && data != null) {
//                // Extract data from the intent and handle it here
//                int totalExercisesCompleted = data.getIntExtra("totalExercisesCompleted", 0);
//                long totalPauseTimeMillis = data.getLongExtra("totalPauseTimeMillis", 0);
//                double totalCaloriesBurned = data.getDoubleExtra("totalCaloriesBurned", 0);
//                long totalExerciseDurationMillis = data.getLongExtra("totalExerciseDurationMillis", 0);
//                // Use the extracted data as needed
//            } else {
//                // Show toast message indicating failure to retrieve progress data
//                Toast.makeText(this, "Failed to retrieve progress data", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//}

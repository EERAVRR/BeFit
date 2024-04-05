package com.example.befit;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.befit.ExerciseActivity;
import com.example.befit.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        ImageView homeButtonImageView = findViewById(R.id.homeButtonImageView);

        // Set the new icon for the home button
        homeButtonImageView.setImageResource(R.drawable.hometop);

        // Set click listener for the workout button
        Button workoutButton = findViewById(R.id.workoutButton);
        workoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Exercise Page
                startActivity(new Intent(HomeActivity.this, ExerciseActivity.class));
            }
        });

        // Inside onCreate() method of HomeActivity.java
        Button viewProgressButton = findViewById(R.id.viewProgressButton);
        viewProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the ProgressActivity when the button is clicked
                Intent intent = new Intent(HomeActivity.this, ProgressActivity.class);
                startActivity(intent);
            }
        });

        // Set click listener for the bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation item clicks here
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    // Show a toast message indicating already at the home page
                    Toast.makeText(HomeActivity.this, "You are already at the home page", Toast.LENGTH_SHORT).show();
                    return true;
                }
                // Add conditions for other navigation items if needed
                return false;
            }


        });

    }
}

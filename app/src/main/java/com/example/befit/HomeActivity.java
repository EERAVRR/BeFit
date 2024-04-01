package com.example.befit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

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
                Intent intent = new Intent(HomeActivity.this, ExerciseActivity.class);
                startActivity(intent);
            }
        });

        // Add similar click listeners for other navigation buttons, if any
    }
}

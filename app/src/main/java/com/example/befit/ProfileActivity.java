package com.example.befit;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    // Views
    private EditText nameEditText;
    private EditText dateOfBirthEditText;
    private EditText countryEditText;
    private EditText genderEditText;
    private EditText phoneNumberEditText;
    // Add other EditText fields for additional profile information if needed

    // DatabaseHelper instance
    private DatabaseHelper databaseHelper;

    // User ID of the currently logged-in user
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilesection);

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        dateOfBirthEditText = findViewById(R.id.dateOfBirthEditText);
        countryEditText = findViewById(R.id.countryEditText);
        genderEditText = findViewById(R.id.genderEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        // Initialize other EditText fields if needed

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Retrieve the user ID of the currently logged-in user from intent or SharedPreferences
        userId = getIntent().getLongExtra("userId", -1); // Example: Retrieve user ID from intent

        // Load the profile data for the currently logged-in user
        loadProfileData(userId);
        // Initialize bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_home) {
                    // Start HomeActivity
                    startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.navigation_profile) {
                    Toast.makeText(ProfileActivity.this, "You are already at the Profile page", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

    }



    private void loadProfileData(long userId) {
        // Retrieve profile data for the given user ID from the database
        Cursor cursor = databaseHelper.getProfile(userId);
        if (cursor != null && cursor.moveToFirst()) {
            // Get the column index for the column name
            int nameIndex = cursor.getColumnIndex(DatabaseHelper.getColumnName());
            // Check if the column index is valid (-1 means the column doesn't exist)
            if (nameIndex != -1) {
                // Extract profile information from the cursor and populate the EditText fields
                String name = cursor.getString(nameIndex);
                // Similarly, handle other columns if needed
                // Populate the EditText fields
                nameEditText.setText(name);
                // Close the cursor
                cursor.close();
                return;
            }
        }
        // Handle the case where the column doesn't exist or other errors occurred
        // Show an error message or log the issue
        Toast.makeText(this, "Error loading profile data", Toast.LENGTH_SHORT).show();
    }


    // Method to save the updated profile information
    private void saveProfile() {
        // Retrieve the updated profile information from the EditText fields
        String name = nameEditText.getText().toString();
        String dateOfBirth = dateOfBirthEditText.getText().toString();
        String country = countryEditText.getText().toString();
        String gender = genderEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();

        // Save the updated profile information to the database
        databaseHelper.updateProfile(userId, name, dateOfBirth, country, gender, phoneNumber);
        // Optionally, show a success message to the user
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }

    // Optional: Add a button click listener to save the profile changes
    public void onSaveProfileButtonClick(View view) {
        saveProfile();
    }
}

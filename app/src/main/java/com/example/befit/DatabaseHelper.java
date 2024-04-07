package com.example.befit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database info
    private static final String DATABASE_NAME = "befit.db";
    private static final int DATABASE_VERSION = 1;

    // Table names and column names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_EXERCISES = "exercises";
    private static final String TABLE_EXERCISE_RATINGS = "exercise_ratings";

    // Common column names
    private static final String COLUMN_ID = "id";

    // Users table column names
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // Exercises table column names
    private static final String COLUMN_EXERCISE_NAME = "name";
    private static final String COLUMN_EXERCISE_IMAGE = "image";
    private static final String COLUMN_EXERCISE_DURATION = "duration";

    // Exercise ratings table column names
    private static final String COLUMN_EXERCISE_ID = "exercise_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_RATING = "rating";
    private static final String COLUMN_TIMESTAMP = "timestamp";


    // Profiles table column names
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DATE_OF_BIRTH = "date_of_birth";
    private static final String COLUMN_COUNTRY = "country";
    private static final String TABLE_PROFILES = "profiles";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_PHONE = "phone_number";

    // Create users table query
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_USERNAME + " TEXT, " +
            COLUMN_PASSWORD + " TEXT)";

    // Create profiles table query
// Create profiles table query
    private static final String CREATE_TABLE_PROFILES = "CREATE TABLE " + TABLE_PROFILES + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_USER_ID + " INTEGER, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_DATE_OF_BIRTH + " TEXT, " +
            COLUMN_COUNTRY + " TEXT, " +
            COLUMN_GENDER + " TEXT, " +  // Add missing column
            COLUMN_PHONE + " TEXT, " +
            "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "))";


    // Create exercises table query
    private static final String CREATE_TABLE_EXERCISES = "CREATE TABLE " + TABLE_EXERCISES + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_EXERCISE_NAME + " TEXT, " +
            COLUMN_EXERCISE_IMAGE + " TEXT, " +
            COLUMN_EXERCISE_DURATION + " INTEGER)";

    // Create exercise ratings table query
    private static final String CREATE_TABLE_EXERCISE_RATINGS = "CREATE TABLE " + TABLE_EXERCISE_RATINGS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_EXERCISE_ID + " INTEGER, " +
            COLUMN_USER_ID + " INTEGER, " +
            COLUMN_RATING + " REAL, " +
            COLUMN_TIMESTAMP + " INTEGER, " +
            "FOREIGN KEY(" + COLUMN_EXERCISE_ID + ") REFERENCES " + TABLE_EXERCISES + "(" + COLUMN_ID + "), " +
            "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_EXERCISES);
        db.execSQL(CREATE_TABLE_EXERCISE_RATINGS);
        db.execSQL(CREATE_TABLE_PROFILES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE_RATINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
        // Create tables again
        onCreate(db);
    }

    // User operations

    public long insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    public boolean isValidLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USERNAME + " = ?" +
                " AND " + COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }
    public long insertProfile(long userId, String name, String dateOfBirth, String country,String gender, String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_DATE_OF_BIRTH, dateOfBirth);
        values.put(COLUMN_COUNTRY, country);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_PHONE, phoneNumber);
        return db.insert(TABLE_PROFILES, null, values);
    }

    public Cursor getProfile(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PROFILES +
                " WHERE " + COLUMN_USER_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }

    public void updateProfile(long userId, String name, String dateOfBirth, String country, String gender, String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_DATE_OF_BIRTH, dateOfBirth);
        values.put(COLUMN_COUNTRY, country);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_PHONE, phoneNumber);
        db.update(TABLE_PROFILES, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    public static String getColumnName() {
        return COLUMN_NAME;
    }

    public static String getColumnDateOfBirth() {
        return COLUMN_DATE_OF_BIRTH;
    }

    public static String getColumnCountry() {
        return COLUMN_COUNTRY;
    }
    public long getUserIdFromAuthenticationSystem(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        long userId = -1;

        // Define the columns you want to retrieve from the database
        String[] projection = {COLUMN_ID};

        // Define the selection criteria
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        // Query the database to find the user with the given username and password
        Cursor cursor = db.query(
                TABLE_USERS,          // The table to query
                projection,           // The columns to return
                selection,            // The columns for the WHERE clause
                selectionArgs,        // The values for the WHERE clause
                null,                 // Don't group the rows
                null,                 // Don't filter by row groups
                null                  // The sort order
        );

        // If a user is found, get their ID
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(COLUMN_ID);
            if (columnIndex != -1) {
                userId = cursor.getLong(columnIndex);
            }
            cursor.close();
        }

        // Return the user ID
        return userId;
    }

    // Exercise operations

    public long insertExercise(String name, String image, long duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXERCISE_NAME, name);
        values.put(COLUMN_EXERCISE_IMAGE, image);
        values.put(COLUMN_EXERCISE_DURATION, duration);
        return db.insert(TABLE_EXERCISES, null, values);
    }

    // Exercise ratings operations

    public long insertExerciseRating(long exerciseId, long userId, float rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXERCISE_ID, exerciseId);
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_RATING, rating);
        return db.insert(TABLE_EXERCISE_RATINGS, null, values);
    }

    // Reset exercise ratings and progress tracking
    public void resetDailyData() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Get the timestamp for the start of the current day
        long startOfDayTimestamp = calculateStartOfDay(System.currentTimeMillis());
        // Delete exercise ratings for the current day
        db.delete(TABLE_EXERCISE_RATINGS, COLUMN_TIMESTAMP + " >= ?", new String[]{String.valueOf(startOfDayTimestamp)});
    }

    // Method to reset data every day at 12:00 AM
    public void resetDataAtMidnight() {
        // Get current time
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        // Calculate time until midnight
        long timeUntilMidnight = calculateTimeUntilMidnight(currentTime);
        // Schedule reset task at midnight
        scheduleResetTask(timeUntilMidnight);
    }

    // Calculate time until midnight
    private long calculateTimeUntilMidnight(long currentTime) {
        // Get calendar instance and set time to midnight
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // Calculate time until midnight
        return calendar.getTimeInMillis() + (24 * 60 * 60 * 1000) - currentTime;
    }

    // Schedule reset task at midnight
    private void scheduleResetTask(long delayMillis) {
        // Create a runnable task to reset data
        Runnable resetTask = new Runnable() {
            @Override
            public void run() {
                resetDailyData(); // Reset daily data at midnight
                resetDataAtMidnight(); // Schedule next reset task
            }
        };
        // Execute reset task after delay
        new android.os.Handler().postDelayed(resetTask, delayMillis);
    }

    // Calculate the start of the day for a given timestamp
    private long calculateStartOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public void resetProgressForDay(long startOfDayInMillis, long endOfDayInMillis) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Perform the reset operation for the current day, e.g., delete or update records within the specified time range
        // For example:
        db.delete(TABLE_EXERCISE_RATINGS, COLUMN_TIMESTAMP + " >= ? AND " + COLUMN_TIMESTAMP + " <= ?", new String[]{String.valueOf(startOfDayInMillis), String.valueOf(endOfDayInMillis)});
        db.close();
    }
}

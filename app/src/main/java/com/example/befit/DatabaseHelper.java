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

    // Create users table query
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_USERNAME + " TEXT, " +
            COLUMN_PASSWORD + " TEXT)";

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE_RATINGS);
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
//        values.put(COLUMN_TIMESTAMP, timestamp);
        return db.insert(TABLE_EXERCISE_RATINGS, null, values);
    }

    // Reset exercise ratings and progress tracking
    public void resetDailyData() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Calculate the timestamp for 7 days ago
        long weekAgoTimestamp = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000;
        // Delete exercise ratings older than 7 days
        db.delete(TABLE_EXERCISE_RATINGS, COLUMN_TIMESTAMP + " < ?", new String[]{String.valueOf(weekAgoTimestamp)});
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
}

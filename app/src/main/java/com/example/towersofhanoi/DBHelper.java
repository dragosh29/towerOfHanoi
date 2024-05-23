package com.example.towersofhanoi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.SimpleDateFormat;
import android.provider.BaseColumns;

import java.util.Date;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tower_of_hanoi.db";
    private static final int DATABASE_VERSION = 2; // Increment the version to trigger onUpgrade
    private SQLiteDatabase database;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase(); // Initialize the database
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LevelEntry.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(LevelEntry.SQL_DELETE_TABLE);
        onCreate(db);
    }

    public void updateLevel(int level, String newBestTime, int newLowestMoves) {
        String selection = LevelEntry.COLUMN_NAME_LEVEL + " = ?";
        String[] selectionArgs = {String.valueOf(level)};

        String[] projection = {
                LevelEntry.COLUMN_NAME_BEST_TIME,
                LevelEntry.COLUMN_NAME_LOWEST_MOVES
        };

        Cursor cursor = database.query(
                LevelEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        ContentValues values = new ContentValues();
        values.put(LevelEntry.COLUMN_NAME_DATE_SET, getCurrentDateTime());

        if (cursor.moveToFirst()) {
            int dbLowestMoves = cursor.getInt(cursor.getColumnIndexOrThrow(LevelEntry.COLUMN_NAME_LOWEST_MOVES));
            String dbBestTime = cursor.getString(cursor.getColumnIndexOrThrow(LevelEntry.COLUMN_NAME_BEST_TIME));

            if (newLowestMoves < dbLowestMoves || dbLowestMoves == 0) {
                values.put(LevelEntry.COLUMN_NAME_LOWEST_MOVES, newLowestMoves);
                values.put(LevelEntry.COLUMN_NAME_BEST_TIME, newBestTime);
            } else if (newLowestMoves == dbLowestMoves) {
                if (compareTimes(newBestTime, dbBestTime)) {
                    values.put(LevelEntry.COLUMN_NAME_BEST_TIME, newBestTime);
                }
            } else if (compareTimes(newBestTime, dbBestTime)) {
                values.put(LevelEntry.COLUMN_NAME_BEST_TIME, newBestTime);
            }

            if (values.size() > 0) {
                database.update(
                        LevelEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
            }
        } else {
            values.put(LevelEntry.COLUMN_NAME_LEVEL, level);
            values.put(LevelEntry.COLUMN_NAME_BEST_TIME, newBestTime);
            values.put(LevelEntry.COLUMN_NAME_LOWEST_MOVES, newLowestMoves);
            database.insert(LevelEntry.TABLE_NAME, null, values);
        }

        cursor.close();
    }

    private boolean compareTimes(String newTime, String dbTime) {
        String[] newTimeParts = newTime.split(":");
        String[] dbTimeParts = dbTime.split(":");

        int newMinutes = Integer.parseInt(newTimeParts[0]);
        int newSeconds = Integer.parseInt(newTimeParts[1]);

        int dbMinutes = Integer.parseInt(dbTimeParts[0]);
        int dbSeconds = Integer.parseInt(dbTimeParts[1]);

        return (newMinutes < dbMinutes) || (newMinutes == dbMinutes && newSeconds < dbSeconds) || dbTime.equals("00:00");
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void closeDatabase() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    public static class LevelEntry implements BaseColumns {
        public static final String TABLE_NAME = "levels";
        public static final String COLUMN_NAME_LEVEL = "level";
        public static final String COLUMN_NAME_BEST_TIME = "best_time";
        public static final String COLUMN_NAME_LOWEST_MOVES = "lowest_moves";
        public static final String COLUMN_NAME_DATE_SET = "date_set"; // New column

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_LEVEL + " INTEGER," +
                        COLUMN_NAME_BEST_TIME + " TEXT," +
                        COLUMN_NAME_LOWEST_MOVES + " INTEGER," +
                        COLUMN_NAME_DATE_SET + " TEXT)"; // New column

        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}


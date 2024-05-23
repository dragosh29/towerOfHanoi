package com.example.towersofhanoi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LevelDataSource {
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public LevelDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertLevel(int level, String bestTime, int lowestMoves) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.LevelEntry.COLUMN_NAME_LEVEL, level);
        values.put(DBHelper.LevelEntry.COLUMN_NAME_BEST_TIME, bestTime);
        values.put(DBHelper.LevelEntry.COLUMN_NAME_LOWEST_MOVES, lowestMoves);
        values.put(DBHelper.LevelEntry.COLUMN_NAME_DATE_SET, getCurrentDateTime());

        return database.insert(DBHelper.LevelEntry.TABLE_NAME, null, values);
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public List<LevelEntry> getAllLevelEntries() {
        List<LevelEntry> levelEntries = new ArrayList<>();

        String[] columns = {
                DBHelper.LevelEntry.COLUMN_NAME_LEVEL,
                DBHelper.LevelEntry.COLUMN_NAME_BEST_TIME,
                DBHelper.LevelEntry.COLUMN_NAME_LOWEST_MOVES,
                DBHelper.LevelEntry.COLUMN_NAME_DATE_SET
        };

        Cursor cursor = database.query(
                DBHelper.LevelEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                DBHelper.LevelEntry.COLUMN_NAME_LEVEL // Sorting by level (disk number)
        );

        while (cursor.moveToNext()) {
            int level = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.LevelEntry.COLUMN_NAME_LEVEL));
            String bestTime = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.LevelEntry.COLUMN_NAME_BEST_TIME));
            int lowestMoves = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.LevelEntry.COLUMN_NAME_LOWEST_MOVES));
            String dateSet = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.LevelEntry.COLUMN_NAME_DATE_SET));

            LevelEntry levelEntry = new LevelEntry(level, bestTime, lowestMoves, dateSet);
            levelEntries.add(levelEntry);
        }

        cursor.close();
        return levelEntries;
    }

    public void deleteLevelEntry(int level) {
        database.delete(DBHelper.LevelEntry.TABLE_NAME,
                DBHelper.LevelEntry.COLUMN_NAME_LEVEL + " = ?", new String[]{String.valueOf(level)});
    }
}

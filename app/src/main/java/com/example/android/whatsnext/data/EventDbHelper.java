package com.example.android.whatsnext.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.whatsnext.data.EventContract.*;


public class EventDbHelper extends SQLiteOpenHelper{
    //name of the table created in sql
    public static final String DATABASE_NAME = "events.db";
    //current version of the database
    private static final int DATABASE_VERSION = 1;

    //EventDbHelper Constructor
    public EventDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //SQL command to create the table
        final String SQL_CREATE_EVENTS_TABLE =
                "CREATE TABLE " + EventEntry.TABLE_NAME + " (" +
                        EventEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        EventEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        EventEntry.COLUMN_DESCRIPTION + " TEXT, " +
                        EventEntry.COLUMN_COMMENTS + " TEXT, " +
                        EventEntry.COLUMN_DATE + " BIGINT NOT NULL, " +
                        EventEntry.COLUMN_PRIORITY + " INT NOT NULL, " +
                        EventEntry.COLUMN_NOTIFY + " TEXT NOT NULL, " +
                        EventEntry.COLUMN_RECURRING + " TEXT NOT NULL, " +
                        EventEntry.COLUMN_PHONE + " TEXT, " +
                        EventEntry.COLUMN_EMAIL + " TEXT);";
        sqLiteDatabase.execSQL(SQL_CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //TODO dbhelper onupgrade method
    }
}

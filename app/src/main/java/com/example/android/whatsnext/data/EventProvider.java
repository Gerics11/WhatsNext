package com.example.android.whatsnext.data;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class EventProvider extends ContentProvider {

    //uri matcher codes
    private static final int CODE_EVENTS = 100;
    private static final int CODE_SINGLE_EVENT = 101;
    private static final int CODE_EMPTY_TABLE = 200;
    //urimatcher instance
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    //eventdbhelper member field
    private EventDbHelper mDbHelper;

    //create urimatcher to solve content uri
    public static UriMatcher buildUriMatcher() {
        //defaults to no match
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String authority = EventContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, EventContract.PATH_EVENTS, CODE_EVENTS);
        matcher.addURI(authority, EventContract.PATH_EVENTS + "/#", CODE_SINGLE_EVENT);


        return matcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDbHelper = new EventDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //projection:   columns to return in the cursor
        //selection:    WHERE clause && passing null returns all
        //selectionArgs:replace ?-s in selection
        //sortorder:    sorting of returned rows

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_EVENTS:
                cursor = database.query(EventContract.EventEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case CODE_SINGLE_EVENT:
                Log.d("EVENTPROVIDER", "URI MATCHING SUCCESS " + selection);

                cursor = database.query(EventContract.EventEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, null);
                break;
            default:
                return null;
        }
        return cursor;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        //get database instance
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsInserted = 0;
        switch (sUriMatcher.match(uri)) {
            case CODE_EVENTS:
                for (ContentValues value : values) {
                    long insertId =
                            database.insert(EventContract.EventEntry.TABLE_NAME, null, value);
                    if (insertId != -1) {
                        rowsInserted++;
                    }
                }
                break;
        }
        return rowsInserted;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues value) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long insertId = -1;
        switch (sUriMatcher.match(uri)) {
            case CODE_EVENTS:
                insertId =
                        database.insert(EventContract.EventEntry.TABLE_NAME, null, value);
                break;
            default:
                Log.d("MAIN", "URI MATCHING FAILED");
        }
        if(insertId == -1){
            Log.d("MAIN", "INSERT ROW FAILED");
        }
        return uri;

    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    public int deleteAll(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        switch (sUriMatcher.match(uri)) {
            case CODE_EVENTS:
                rowsDeleted = database.delete(EventContract.EventEntry.TABLE_NAME,
                        "1", null);
                Log.d("EVENTPROVIDER", "ROWS DELETED SUCCESSFULLY: " + rowsDeleted);
                break;
            default:
                rowsDeleted = -1;
                break;
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        Log.d("MAIN", " UPDATE FROM CONTENTPROVIDER CALLED");
        int rowsUpdated = -1;
        String id = uri.getLastPathSegment();
        Log.d("MAIN", "UPDATE ID FROM PATHSEGMENTS" + id);

        switch (sUriMatcher.match(uri)) {
            case CODE_SINGLE_EVENT:
                rowsUpdated =
//                        database.update(EventContract.EventEntry.TABLE_NAME, null, contentValues);
                        database.update(EventContract.EventEntry.TABLE_NAME, contentValues, "_id=?", new String[]{id});
                break;
            default:
                Log.d("MAIN", " UPDATE METHOD URI MATCHING FAILED");
        }
        if(rowsUpdated != 1){
            Log.d("MAIN", "NUMBER OF UPDATED ROWS IS NOT ONE");
        }
        return rowsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}

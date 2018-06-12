package com.example.android.whatsnext.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Gerics on 2017.12.09..
 */

public class EventContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.whatsnext";
    //base uri to build from
    public static final Uri CONTENT_BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //path to database
    public static final String PATH_EVENTS = "events";

    //class to contain database constants
    public static final class EventEntry implements BaseColumns {
        //uri of the database
        public static final Uri CONTENT_URI = CONTENT_BASE_URI.buildUpon()
                .appendPath(PATH_EVENTS).build();
        //name of table of events
        public static final String TABLE_NAME = "events";
        //all the database columns
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_COMMENTS = "comments";

        public static final String COLUMN_DATE = "dateInMillis";

        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_NOTIFY = "notify";
        public static final String COLUMN_RECURRING = "isRecurring";

        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_EMAIL = "email";
    }
}

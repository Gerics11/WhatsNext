package com.example.android.whatsnext;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.android.whatsnext.data.EventContract.EventEntry;
import com.example.android.whatsnext.utils.InterfaceUtils;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        EventAdapter.EventAdapterOnClickHandler {

    //scroll handler for recyclerview
    //RecyclerView.SmoothScroller smoothScroller;
    InterfaceUtils.ScrollingLinearLayoutManager layoutManager;

    //projection for adapter
    public static final String[] ADAPTER_PROJECTION = {
            EventEntry._ID,
            EventEntry.COLUMN_DATE,
            EventEntry.COLUMN_TITLE,
            EventEntry.COLUMN_DESCRIPTION,
            EventEntry.COLUMN_COMMENTS,
            EventEntry.COLUMN_PRIORITY,
            EventEntry.COLUMN_NOTIFY,
            EventEntry.COLUMN_RECURRING,
            EventEntry.COLUMN_PHONE,
            EventEntry.COLUMN_EMAIL,
    };

    //column number in projection not sure if needed
    public static final int INDEX_ID = 0;
    public static final int INDEX_DATE = 1;
    public static final int INDEX_TITLE = 2;
    public static final int INDEX_DESCRIPTION = 3;
    public static final int INDEX_COMMENT = 4;
    public static final int INDEX_PRIORITY = 5;
    public static final int INDEX_NOTIFY = 6;
    public static final int INDEX_RECURRING = 7;
    public static final int INDEX_PHONE = 8;
    public static final int INDEX_EMAIL = 9;

    private static final int LOADER_ID = 13;

    private EventAdapter mEventAdapter;
    private RecyclerView mRecyclerView;

    public FloatingActionButton mFab;

    View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent newEventIntent = new Intent(MainActivity.this, EditActivity.class);
            startActivity(newEventIntent);
        }
    };






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //TODO TIMEZONE CHECK

        //set floating action button
        mFab = findViewById(R.id.fab_add);
        mFab.setOnClickListener(fabClickListener);

        mRecyclerView = findViewById(R.id.recyclerview_main);
        mRecyclerView.setHasFixedSize(false);



        layoutManager =
                new InterfaceUtils.ScrollingLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        LinearLayoutManager layoutManager =
//                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);


        mRecyclerView.setLayoutManager(layoutManager);

        mEventAdapter = new EventAdapter(this, this);
        mRecyclerView.setAdapter(mEventAdapter);

        getLoaderManager().initLoader(LOADER_ID, null, this );
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //load a cursor with data when loader is created
        Uri eventsUri = EventEntry.CONTENT_URI;
        //sort results by date
        String sortOrder = EventEntry.COLUMN_DATE + " ASC";

        return new CursorLoader(this,
                eventsUri,
                ADAPTER_PROJECTION,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mEventAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mEventAdapter.swapCursor(null);
    }

    @Override
    public void onClick(int id, int adapterPosition) {
        //open edit intent

        Log.d("MAINACTIVITY", "ONCLICK CALLED");

        RecyclerView.State state = new RecyclerView.State();

        layoutManager.smoothScrollToPosition(mRecyclerView, state, adapterPosition);
        //layoutManager.scrollToPosition(adapterPosition);
    }

    @Override
    public void onSwiped(int id, int recyclerViewPosition) {

    }
}

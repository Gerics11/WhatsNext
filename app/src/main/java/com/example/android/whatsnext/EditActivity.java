package com.example.android.whatsnext;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.android.whatsnext.data.EventContract;
import com.example.android.whatsnext.data.EventContract.EventEntry;
import com.example.android.whatsnext.utils.DateUtils;

import org.joda.time.DateTime;

public class EditActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    //todo consider making an event class for object
    private String mEventId;
    private String mPriority;
    private String mNotify;
    private String mRecurring;
    private String mPhoneNumber;
    private String mEmailAddress;

    //data field constants
    public static final String PRIORITY_LOW = "Low";
    public static final String PRIORITY_NORMAL = "Normal";
    public static final String PRIORITY_HIGH = "High";
    public static final String NOTIFY_NONE = "None";
    public static final String NOTIFY_NOTIFY = "Notify";
    public static final String NOTIFY_ALARM = "Alarm";
    public static final String RECURRING_NONE = "None";
    public static final String RECURRING_DAILY = "Daily";
    public static final String RECURRING_WEEKLY = "Weekly";
    public static final String RECURRING_MONTHLY = "Monthly";
    public static final String RECURRING_YEARLY = "Yearly";

    //init fields
    private EditText etTitle;
    private EditText etDesc;
    private EditText etComments;
    private EditText etDateYear;
    private EditText etDateMonth;
    private EditText etDateDay;
    private EditText etDateHour;
    private EditText etDateMinute;
    private ImageView ivPriority;
    private ImageView ivNotify;
    private ImageView ivRecurring;
    private ImageView ivPhone;
    private ImageView ivEmail;

    //dialog views
    private Spinner mDialogSpinner;
    private EditText mDialogEt;
    //to tell if editing or creating event
    private boolean isNewEvent;
    //date edittext focus change listener
    View.OnFocusChangeListener dateFocusChangeListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        //init views
        etTitle = findViewById(R.id.et_title);
        etDesc = findViewById(R.id.et_description);
        etComments = findViewById(R.id.et_comments);
        etDateYear = findViewById(R.id.et_date_year);
        etDateMonth = findViewById(R.id.et_date_month);
        etDateDay = findViewById(R.id.et_date_day);
        etDateHour = findViewById(R.id.et_date_hour);
        etDateMinute = findViewById(R.id.et_date_minute);
        ivPriority = findViewById(R.id.iv_priority);
        ivNotify = findViewById(R.id.iv_notify);
        ivRecurring = findViewById(R.id.iv_recurring);
        ivPhone = findViewById(R.id.iv_phone);
        ivPhone = findViewById(R.id.iv_phone);
        ivEmail = findViewById(R.id.iv_email);
        //set default values if new event
        if (!getIntent().hasExtra("dataId")) {
            mPriority = PRIORITY_NORMAL;
            mNotify = NOTIFY_NOTIFY;
            mRecurring = RECURRING_NONE;
            isNewEvent = true;
        } else { //display data from cursor
            int intentId = getIntent().getExtras().getInt("dataId");
            isNewEvent = false;
            displayEditData(intentId);
        }
        //register listeners
        ivPriority.setOnClickListener(imageClickListener);
        ivNotify.setOnClickListener(imageClickListener);
        ivRecurring.setOnClickListener(imageClickListener);
        ivPhone.setOnClickListener(imageClickListener);
        ivEmail.setOnClickListener(imageClickListener);

        dateFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                Log.d(TAG, "FOCUSCHANGE CALLED");
                EditText et = (EditText) view;
                if (hasFocus) {
                    et.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(view, 0);
                } else {
                    String text = et.getText().toString();
                    if (text.length() == 1) {
                        text = DateUtils.addZero(text);
                        et.setText(text);
                    }
                }
            }
        };

        etDateYear.setOnFocusChangeListener(dateFocusChangeListener);
        etDateMonth.setOnFocusChangeListener(dateFocusChangeListener);
        etDateDay.setOnFocusChangeListener(dateFocusChangeListener);
        etDateHour.setOnFocusChangeListener(dateFocusChangeListener);
        etDateMinute.setOnFocusChangeListener(dateFocusChangeListener);
    }

    public void displayEditData(int dataId) {
        Uri singleEvent = EventEntry.CONTENT_URI.buildUpon().appendPath("").build();
        //select data based on _id of the view
        String[] selectionArgs = new String[]{String.valueOf(dataId)};
        //selection --> _ID + " = " + dataId
        Cursor displayCursor = getContentResolver().query(singleEvent,
                MainActivity.ADAPTER_PROJECTION,
                EventEntry._ID + " = ?",
                selectionArgs,
                null
        );
        if (displayCursor == null) {
            Log.e(TAG, "displayeditdata - empty cursor");
            return;
        }
        displayCursor.moveToFirst();

        mEventId = displayCursor.getString(MainActivity.INDEX_ID);
        //display here
        etTitle.setText(displayCursor.getString(MainActivity.INDEX_TITLE));
        etDesc.setText(displayCursor.getString(MainActivity.INDEX_DESCRIPTION));
        etComments.setText(displayCursor.getString(MainActivity.INDEX_COMMENT));

        DateTime dateTime = DateUtils.getDateTime(displayCursor.getLong(MainActivity.INDEX_DATE));

        etDateYear.setText(String.valueOf(dateTime.getYear()));

        String month = String.valueOf(dateTime.getMonthOfYear());
        if (month.length() == 1) {
            month = DateUtils.addZero(month);
        }
        etDateMonth.setText(month);

        String day = String.valueOf(dateTime.getDayOfMonth());
        if (day.length() == 1) {
            day = DateUtils.addZero(day);
        }
        etDateDay.setText(day);

        String hour = String.valueOf(dateTime.getHourOfDay());
        if (hour.length() == 1) {
            hour = DateUtils.addZero(hour);
        }
        etDateHour.setText(hour);

        String minute = String.valueOf(dateTime.getMinuteOfHour());
        if (minute.length() == 1) {
            minute = DateUtils.addZero(minute);
        }
        etDateMinute.setText(minute);

        mPriority = displayCursor.getString(MainActivity.INDEX_PRIORITY);
        mNotify = displayCursor.getString(MainActivity.INDEX_NOTIFY);
        mRecurring = displayCursor.getString(MainActivity.INDEX_RECURRING);
        setImageButton(R.id.iv_priority);
        setImageButton(R.id.iv_notify);
        setImageButton(R.id.iv_recurring);

        mPhoneNumber = displayCursor.getString(MainActivity.INDEX_PHONE);
        mEmailAddress = displayCursor.getString(MainActivity.INDEX_EMAIL);

        displayCursor.close();
    }

    public void saveInput(View view) {
        //TODO validate input method
        ContentValues values = new ContentValues();
        values.put(EventEntry.COLUMN_TITLE, etTitle.getText().toString());
        values.put(EventEntry.COLUMN_DESCRIPTION, etDesc.getText().toString());
        values.put(EventEntry.COLUMN_COMMENTS, etComments.getText().toString());
        values.put(EventEntry.COLUMN_DATE,
                DateUtils.getUnixTime(Integer.valueOf(etDateYear.getText().toString()),
                        Integer.valueOf(etDateMonth.getText().toString()),
                        Integer.valueOf(etDateDay.getText().toString()),
                        Integer.valueOf(etDateHour.getText().toString()),
                        Integer.valueOf(etDateMinute.getText().toString()), 0));

        values.put(EventEntry.COLUMN_PRIORITY, mPriority);
        values.put(EventEntry.COLUMN_NOTIFY, mNotify);
        values.put(EventEntry.COLUMN_RECURRING, mRecurring);
        values.put(EventEntry.COLUMN_PHONE, mPhoneNumber);
        values.put(EventEntry.COLUMN_EMAIL, mEmailAddress);


        ContentResolver resolver = getContentResolver();

        if (isNewEvent) { //add new event
            resolver.insert(EventEntry.CONTENT_URI, values);
        } else {            //update edited event
            Uri singleEvent = EventContract.CONTENT_BASE_URI.buildUpon()
                    .appendPath(EventContract.PATH_EVENTS)
                    .appendPath(mEventId)
                    .build();

            String whereClause = EventEntry._ID;
            String[] selectionArgs = {mEventId};
            resolver.update(singleEvent, values, whereClause, selectionArgs);

        }
        finish();
    }


    View.OnClickListener imageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EditActivity.this);
            ArrayAdapter<String> adp;
            String[] spinnerArray;

            //BUILD ALERTDIALOG
            //todo build alerdialog method
            final int viewId = view.getId();

            switch (viewId) {
                case R.id.iv_priority:
                    dialogBuilder.setTitle("Set Event Priority"); //todo build dialog method

                    spinnerArray = EditActivity.this.getResources().getStringArray(R.array.edit_priority_spinner);
                    adp = new ArrayAdapter<String>(EditActivity.this,
                            android.R.layout.simple_spinner_item, spinnerArray);


                    mDialogSpinner = new Spinner(EditActivity.this);
                    mDialogSpinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    mDialogSpinner.setAdapter(adp);
                    dialogBuilder.setView(mDialogSpinner);
                    break;

                case R.id.iv_notify:
                    dialogBuilder.setTitle("Set Desired Notification");

                    spinnerArray = EditActivity.this.getResources().getStringArray(R.array.edit_notification_spinner);
                    adp = new ArrayAdapter<String>(EditActivity.this,
                            android.R.layout.simple_spinner_item, spinnerArray);


                    mDialogSpinner = new Spinner(EditActivity.this);
                    mDialogSpinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    mDialogSpinner.setAdapter(adp);
                    dialogBuilder.setView(mDialogSpinner);
                    break;

                case R.id.iv_recurring:
                    dialogBuilder.setTitle("Set Repeat for Event");

                    spinnerArray = EditActivity.this.getResources().getStringArray(R.array.edit_recurring_spinner);
                    adp = new ArrayAdapter<String>(EditActivity.this,
                            android.R.layout.simple_spinner_item, spinnerArray);


                    mDialogSpinner = new Spinner(EditActivity.this);
                    mDialogSpinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    mDialogSpinner.setAdapter(adp);
                    dialogBuilder.setView(mDialogSpinner);
                    break;

                case R.id.iv_phone:
                    dialogBuilder.setTitle("Add Phone Number");
                    mDialogEt = new EditText(EditActivity.this);
                    mDialogEt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    mDialogEt.setInputType(InputType.TYPE_CLASS_NUMBER);
                    dialogBuilder.setView(mDialogEt);

                    break;

                case R.id.iv_email:
                    dialogBuilder.setTitle("Add Email Address");

                    mDialogEt = new EditText(EditActivity.this);
                    mDialogEt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    mDialogEt.setInputType(InputType.TYPE_CLASS_TEXT);
                    dialogBuilder.setView(mDialogEt);

                    break;
            }
            dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {


                    if (mDialogSpinner != null) {
                        String dialogResult = mDialogSpinner.getSelectedItem().toString(); //fixme this fails after localization
                        if (viewId == R.id.iv_priority) {
                            mPriority = dialogResult;
                        } else if (viewId == R.id.iv_notify) {
                            mNotify = dialogResult;
                        } else if (viewId == R.id.iv_recurring) {
                            mRecurring = dialogResult;
                        }
                        mDialogSpinner = null;
                    }
                    if (mDialogEt != null) {
                        if (viewId == R.id.iv_phone) {
                            mPhoneNumber = mDialogEt.getText().toString();
                        } else if (viewId == R.id.iv_email) {
                            mEmailAddress = mDialogEt.getText().toString();
                        }
                    }

                    setImageButton(viewId);
                    mDialogSpinner = null;
                }
            });
            dialogBuilder.create().show();
            if (mDialogSpinner != null) {
                mDialogSpinner.setSelection(setSpinner(viewId));
            }
            if (mPhoneNumber != null && mDialogEt != null && viewId == R.id.iv_phone) {
                mDialogEt.setText(mPhoneNumber);
            }
            if (mPhoneNumber != null && mDialogEt != null && viewId == R.id.iv_email) {
                mDialogEt.setText(mEmailAddress);
            }
        }
    };

    //    private void setImageButton(int viewId, String dialogResult) {
    private void setImageButton(int viewId) {
        switch (viewId) {
            case R.id.iv_priority:
                switch (mPriority) {
                    case "High":
                        ivPriority.setImageResource(R.drawable.ic_priority_high);
                        mPriority = PRIORITY_HIGH;
                        break;
                    case "Normal":
                        ivPriority.setImageResource(R.drawable.ic_priority_normal);
                        mPriority = PRIORITY_NORMAL;
                        break;
                    case "Low":
                        ivPriority.setImageResource(R.drawable.ic_priority_low);
                        mPriority = PRIORITY_LOW;
                        break;
                    default:
                }
                break;
            case R.id.iv_notify:
                switch (mNotify) {
                    case "Alarm":
                        ivNotify.setImageResource(R.drawable.ic_notify_alarm);
                        mNotify = NOTIFY_ALARM;
                        break;
                    case "Notification":
                        ivNotify.setImageResource(R.drawable.ic_notify_normal);
                        mNotify = NOTIFY_NOTIFY;
                        break;
                    case "None":
                        ivNotify.setImageResource(R.drawable.ic_notify_none);
                        mNotify = NOTIFY_NONE;
                        break;
                    default:
                }
                break;
            case R.id.iv_recurring:
                switch (mRecurring) {
                    case "None":
                        ivRecurring.setImageResource(R.drawable.ic_repeat_off);
                        mRecurring = RECURRING_NONE;
                        break;
                    case "Daily":
                        ivRecurring.setImageResource(R.drawable.ic_repeat_on);
                        mRecurring = RECURRING_DAILY;
                        break;
                    case "Weekly":
                        ivRecurring.setImageResource(R.drawable.ic_repeat_on);
                        mRecurring = RECURRING_WEEKLY;
                        break;
                    case "Monthly":
                        ivRecurring.setImageResource(R.drawable.ic_repeat_on);
                        mRecurring = RECURRING_MONTHLY;
                        break;
                    case "Yearly":
                        ivRecurring.setImageResource(R.drawable.ic_repeat_on);
                        mRecurring = RECURRING_YEARLY;
                        break;
                }
        }
    }

    private int setSpinner(int imageId) {
        int position = -1;
        Log.d("EDITACTIVITY", "" + imageId);
        Log.d("EDITACTIVITY", "" + R.id.iv_priority);
        Log.d("EDITACTIVITY", "" + PRIORITY_HIGH);
        switch (imageId) {

            case R.id.iv_priority:
                switch (mPriority) {
                    case PRIORITY_HIGH:
                        position = 0;
                        break;
                    case PRIORITY_NORMAL:
                        position = 1;
                        break;
                    case PRIORITY_LOW:
                        position = 2;
                        break;
                }
                break;
            case R.id.iv_notify:
                switch (mNotify) {
                    case NOTIFY_ALARM:
                        position = 0;
                        break;
                    case NOTIFY_NOTIFY:
                        position = 1;
                        break;
                    case NOTIFY_NONE:
                        position = 2;
                        break;
                }
                break;
            case R.id.iv_recurring:
                switch (mRecurring) {
                    case RECURRING_NONE:
                        position = 0;
                        break;
                    case RECURRING_YEARLY:
                        position = 1;
                        break;
                    case RECURRING_MONTHLY:
                        position = 2;
                        break;
                    case RECURRING_WEEKLY:
                        position = 3;
                        break;
                    case RECURRING_DAILY:
                        position = 4;
                        break;
                }
                break;
        }
        return position;
    }
}

package com.example.android.whatsnext;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.whatsnext.utils.DateUtils;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventAdapterViewHolder>{

    private final String TAG = getClass().getSimpleName();


    private final Context mContext;
    //item click handler
    private EventAdapterOnClickHandler mClickHandler;
    //variable to track current selected item
    private int selectedId = -2;


    //methods we implement with eventadapter
    public interface EventAdapterOnClickHandler {
        void onClick(int id, int recyclerViewPosition); //will open database according to id in editactivity

        void onSwiped(int id, int recyclerViewPosition);
    }


    //cursor holding data to bind holder with data
    private Cursor mCursor;

    //constructor
    public EventAdapter(Context context, EventAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public EventAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        //show larger layout if the element is highlighted
        if (viewType == 0) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_selected, parent, false);
        }
        view.setFocusable(true); //fixme: is this needed
        return new EventAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EventAdapterViewHolder holder, final int position) {
        mCursor.moveToPosition(holder.getAdapterPosition());
        //date
        long date = mCursor.getLong(MainActivity.INDEX_DATE);
        holder.date.setText(DateUtils.getDateString(date));
        //title
        String title = mCursor.getString(MainActivity.INDEX_TITLE);
        holder.title.setText(title);
        //desc
        String description = mCursor.getString(MainActivity.INDEX_DESCRIPTION);
        holder.description.setText(description);
        //icons are visible if there is data
        if (mCursor.getString(MainActivity.INDEX_PHONE) != null &&
                !mCursor.getString(MainActivity.INDEX_PHONE).isEmpty()) { //phone icon
            holder.phone.setVisibility(View.VISIBLE);
        } else {
            holder.phone.setVisibility(View.GONE);
        }
        if (mCursor.getString(MainActivity.INDEX_EMAIL) != null &&
                !mCursor.getString(MainActivity.INDEX_EMAIL).isEmpty()) { //email icon
            holder.email.setVisibility(View.VISIBLE);
        } else {
            holder.email.setVisibility(View.GONE);
        }

        //set extra icons for selected item comments, priority, recurring, onclicklisteners

        String notify = mCursor.getString(MainActivity.INDEX_NOTIFY);  //switch notification icon
        if (notify.equals("None")) {
            holder.notify.setBackgroundResource(R.drawable.ic_notify_none);
        } else if (notify.equals("Alarm")) {
            holder.notify.setBackgroundResource(R.drawable.ic_notify_alarm);
        }

        if (mCursor.getString(MainActivity.INDEX_RECURRING).equals("None")) { //switch repeat icon
            holder.recurring.setBackgroundResource(R.drawable.ic_repeat_off);
        }

        String comment = mCursor.getString(MainActivity.INDEX_COMMENT); //show comment view if !empty
        if (comment.isEmpty()) {
            holder.comment.setVisibility(View.GONE);
        } else {
            holder.comment.setVisibility(View.VISIBLE);
        }
        holder.comment.setText(mCursor.getString(MainActivity.INDEX_COMMENT));

        //open edit activity clicklistener
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCursor.moveToPosition(holder.getAdapterPosition());
                Intent editIntent = new Intent(mContext, EditActivity.class);
                int eventId = mCursor.getInt(MainActivity.INDEX_ID);
                editIntent.putExtra("dataId", eventId);

                holder.editButton.getContext().startActivity(editIntent);
            }
        });

        //open call
        holder.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCursor.moveToPosition(holder.getAdapterPosition());

                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                String uri = "tel:" + mCursor.getInt(MainActivity.INDEX_PHONE);
                phoneIntent.setData(Uri.parse(uri));

                holder.phone.getContext().startActivity(phoneIntent);
            }
        });

        //open mail
        holder.email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCursor.moveToPosition(holder.getAdapterPosition());

                Intent mailIntent = new Intent(Intent.ACTION_SEND);
                mailIntent.setType("*/*");

                mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mCursor.getString(MainActivity.INDEX_EMAIL)});
                mailIntent.putExtra(Intent.EXTRA_SUBJECT, mCursor.getString(MainActivity.INDEX_TITLE));

                holder.email.getContext().startActivity(Intent.createChooser(mailIntent, "Send Email"));
            }
        });

        //priority color
        switch (mCursor.getString(MainActivity.INDEX_PRIORITY)) {
            case "High":
                holder.listItemRoot.setBackgroundColor(Color.parseColor("#ff0000"));
                break;
            case "Normal":
                holder.listItemRoot.setBackgroundColor(Color.parseColor("#808080"));
                break;
            case "Low":
                holder.listItemRoot.setBackgroundColor(Color.parseColor("#33cc33"));
                break;
        }



        // todo add animation
        final boolean isExpanded = position==selectedId;
        holder.imagesLayout.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.comment.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedId = isExpanded ? -1:position;
                notifyDataSetChanged();
                int id = mCursor.getInt(MainActivity.INDEX_ID);
                mClickHandler.onClick(id, position);
            }
        });




//        onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//            mCursor.moveToPosition(viewHolder.getAdapterPosition());
//            String eventId = mCursor.getString(MainActivity.INDEX_ID);
//
//            Uri singleEvent = EventContract.CONTENT_BASE_URI.buildUpon()
//                    .appendPath(EventContract.PATH_EVENTS)
//                    .appendPath(eventId)
//                    .build();
//
//            String whereClause = EventContract.EventEntry._ID;
//            String[] selectionArgs = {eventId};
//            int rowsDeleted = mContext.getContentResolver().delete(singleEvent, whereClause, selectionArgs);
//            Log.d(TAG, "ROWS DELETED: " + rowsDeleted);
//            notifyItemRemoved(viewHolder.getAdapterPosition());
//        }


    }


    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        if (selectedId == position) {
            viewType = 1;
        }
        return viewType;
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    class EventAdapterViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        //views that we refer in the holder
        ConstraintLayout listItemRoot;
        LinearLayout imagesLayout;
        TextView date;
        TextView title;
        TextView description;
        TextView comment;
        ImageView phone;
        ImageView email;
        ImageView notify;
        ImageView recurring;
        Button editButton;

        //constructor for viewholder
        EventAdapterViewHolder(View view) {
            super(view);
            //initialize views
            listItemRoot = view.findViewById(R.id.list_item_root);
            imagesLayout = view.findViewById(R.id.layout_list_images);
            date = view.findViewById(R.id.tv_list_date);
            title = view.findViewById(R.id.tv_list_title);
            description = view.findViewById(R.id.tv_list_description);
            phone = view.findViewById(R.id.iv_list_phone);
            email = view.findViewById(R.id.iv_list_email);
            comment = view.findViewById(R.id.tv_list_comments);
            notify = view.findViewById(R.id.iv_list_notify);
            recurring = view.findViewById(R.id.iv_list_recurring);
            editButton = view.findViewById(R.id.button_edit);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {//
//            int id = mCursor.getInt(MainActivity.INDEX_ID);
//            mClickHandler.onClick(id, getAdapterPosition());
        }


    }
}





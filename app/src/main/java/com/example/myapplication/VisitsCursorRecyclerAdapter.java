package com.example.myapplication;


import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.example.myapplication.R.*;

/**
 * Created by Ира on 16.02.2017.
 */

public class VisitsCursorRecyclerAdapter extends CursorRecyclerAdapter<VisitsCursorRecyclerAdapter.ViewHolder>
        implements ItemTouchHelperAdapter

{
    public DBHelper mDbHelper;
    private final OnStartDragListener mDragStartListener;
    private VisitsListFragment.OnFragmentInteractionListener mClickListener;

    public VisitsCursorRecyclerAdapter(Cursor cursor, DBHelper dbHelper, OnStartDragListener dragStartListener, VisitsListFragment.OnFragmentInteractionListener mClickListener) {
        super(cursor);
        this.mDbHelper = dbHelper;
        this.mDragStartListener = dragStartListener;
        this.mClickListener = mClickListener;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        int visit_id = (int) getItemId(position);
        String date_str = getItemData(position,DBContract.TabVisits.COLUMN_NAME_DATE);
        mDbHelper.deleteVisit(visit_id);

        Cursor new_cursor = mDbHelper.getCursorForVisitList(date_str);
        changeCursorWithoutNotification(new_cursor);
        notifyItemRemoved(position);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        public LinearLayout llParent;

        public TextView lcTime;
        public TextView lcClient;
        public TextView lcPrice;
        public TextView lcNote;

        public ViewHolder(LinearLayout l) {
            super(l);

            llParent = l;
            lcTime = (TextView) l.findViewById(R.id.list_cell_time);
            lcClient = (TextView) l.findViewById(R.id.list_cell_client);
            lcPrice = (TextView) l.findViewById(R.id.list_cell_price);
            lcNote = (TextView) l.findViewById(R.id.list_cell_note);

        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout l = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(layout.visit_list_cell, parent, false);
        ViewHolder vh = new ViewHolder(l);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {
        String time = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.TabVisits.COLUMN_NAME_TIME));
        String client = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.TabClients.COLUMN_NAME_NAME));
        String price = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.TabVisits.COLUMN_NAME_PRICE));
        String note = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.TabVisits.COLUMN_NAME_NOTE));

        holder.lcTime.setText(time);
        holder.lcClient.setText(client);
        holder.lcPrice.setText(price);
        holder.lcNote.setText(note);


        holder.llParent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                       mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });

        holder.llParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onFragmentItemSelected(holder.getItemId());
            }
        });
    }

}


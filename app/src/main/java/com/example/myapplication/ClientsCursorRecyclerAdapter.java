package com.example.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.example.myapplication.R.*;

/**
 * Created by Ира on 16.02.2017.
 */

public class ClientsCursorRecyclerAdapter extends CursorRecyclerAdapter<ClientsCursorRecyclerAdapter.ViewHolder>


{
    public DBHelper mDbHelper;
    private final OnStartDragListener mDragStartListener;

    public ClientsCursorRecyclerAdapter(Cursor cursor, DBHelper dbHelper, OnStartDragListener dragStartListener){
        super(cursor);
        this.mDbHelper = dbHelper;
        this.mDragStartListener = dragStartListener;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        int client_id = (int) getItemId(position);
        mDbHelper.deleteClient(client_id);

        Cursor new_cursor = mDbHelper.getCursorForClientList();
        changeCursorWithoutNotification(new_cursor);
        notifyItemRemoved(position);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder{
        public LinearLayout llParent;

        public TextView tvName;
        public TextView tvPhone;

        public ViewHolder(LinearLayout l) {
            super(l);

            llParent = l;
            tvName = (TextView)(l.findViewById(id.list_cell_client_name));
            tvPhone = (TextView)(l.findViewById(id.list_cell_client_phone));
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
        LinearLayout l=(LinearLayout)LayoutInflater.from(parent.getContext()).inflate(layout.client_list_cell, parent, false);
        ViewHolder vh=new ViewHolder(l);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {
        String strName = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.TabClients.COLUMN_NAME_NAME));
        String strPhone = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.TabClients.COLUMN_NAME_PHONE));
      //  final int client_id = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.TabClients._ID));


        holder.tvName.setText(strName);
        holder.tvPhone.setText(strPhone);
       // holder.llParent.setTag(id.TAG_CLIENT_ID, client_id);

        // Start a drag whenever the handle view it touched
        holder.llParent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }



}

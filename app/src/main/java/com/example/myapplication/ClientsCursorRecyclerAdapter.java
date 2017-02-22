package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Ира on 16.02.2017.
 */

public class ClientsCursorRecyclerAdapter extends CursorRecyclerAdapter<ClientsCursorRecyclerAdapter.ViewHolder>


{
    public DBHelper mDbHelper;

    public ClientsCursorRecyclerAdapter(Cursor cursor, DBHelper dbHelper){
        super(cursor);
        this.mDbHelper = dbHelper;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout llParent;

        public TextView tvName;
        public TextView tvPhone;
        public ImageView ivDelete;


        public ViewHolder(LinearLayout l) {
            super(l);

            llParent = l;
            tvName = (TextView)(l.findViewById(R.id.list_cell_client_name));
            tvPhone = (TextView)(l.findViewById(R.id.list_cell_client_phone));
            ivDelete = (ImageView) (l.findViewById(R.id.list_cell_client_delete));


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout l=(LinearLayout)LayoutInflater.from(parent.getContext()).inflate(R.layout.client_list_cell, parent, false);
        ViewHolder vh=new ViewHolder(l);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final Cursor cursor) {
        String strName = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.TabClients.COLUMN_NAME_NAME));
        String strPhone = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.TabClients.COLUMN_NAME_PHONE));
        final int client_id = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.TabClients._ID));


        holder.tvName.setText(strName);
        holder.tvPhone.setText(strPhone);
        holder.llParent.setTag(R.id.TAG_CLIENT_ID, client_id);
/*
        holder.llParent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float historicX = Float.NaN, historicY = Float.NaN, delX= Float.NaN, delY= Float.NaN;
                LinearLayout childViewUnderv = (LinearLayout)((RecyclerView)v).findChildViewUnder(historicX, historicY);
                ImageView imageViewDelete = (ImageView)(childViewUnderv.findViewById(R.id.list_cell_client_delete));


                switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            historicX = event.getX();
                            historicY = event.getY();

                            delX=imageViewDelete.getX();
                            delY = imageViewDelete.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            imageViewDelete.setVisibility(View.VISIBLE);
                            break;
                        case MotionEvent.ACTION_UP:
                            if (event.getX() >= delX) {
                                deleteFromCursor(client_id, cursor_pos);
                                return true;
                            }
                            else
                            {
                                imageViewDelete.setVisibility(View.INVISIBLE);
                            }
                            break;
                        default:
                            return false;
                    }
                    return false;
                }

            public void deleteFromCursor(int client_id, int cursor_pos){
                mDbHelper.deleteClient(client_id);
                Cursor new_cursor = mDbHelper.getCursorForClientList();
                changeCursorWithoutNotification(new_cursor);
                notifyItemRemoved(cursor_pos);
            }
        });
        */
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int cursor_pos=-1;

                Cursor curent_cursor = getCursor();
                curent_cursor.moveToFirst();
                for (int i=0; i<curent_cursor.getCount(); i++){
                    if (curent_cursor.getInt(curent_cursor.getColumnIndexOrThrow(DBContract.TabClients._ID))==client_id){
                        cursor_pos=curent_cursor.getPosition();
                        break;
                    }
                    else {
                        curent_cursor.moveToNext();
                    }
                }

                mDbHelper.deleteClient(client_id);
                Cursor new_cursor = mDbHelper.getCursorForClientList();
                changeCursorWithoutNotification(new_cursor);
                notifyItemRemoved(cursor_pos);
                //notifyItemRemoved(cursor_pos);
            }
        });

    }



}

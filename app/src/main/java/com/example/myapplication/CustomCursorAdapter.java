package com.example.myapplication;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Ира on 23.12.2016.
 */

public class CustomCursorAdapter extends CursorAdapter {
    public CustomCursorAdapter(MainActivity context, Cursor cursor){
        super(context,cursor,0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_cell, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        LinearLayout lcListLayout = (LinearLayout)view.findViewById(R.id.list_layout);
        Integer pos = (Integer)cursor.getPosition();

        //if (pos%2==0)
        //lcListLayout.setBackground(context.getResources().getDrawable(R.drawable.list_bcg_1));

        //else
        //{lcListLayout.setBackground(context.getResources().getDrawable(R.drawable.list_bcg_2));}

        TextView lcTime = (TextView)view.findViewById(R.id.list_cell_time);
        TextView lcClient = (TextView)view.findViewById(R.id.list_cell_client);
        TextView lcPrice = (TextView) view.findViewById(R.id.list_cell_price);
        TextView lcNote = (TextView) view.findViewById(R.id.list_cell_note);

        String time = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.LittleCalendar.COLUMN_NAME_TIME));
        String client = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.LittleCalendar.COLUMN_NAME_CLIENT));
        String price = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.LittleCalendar.COLUMN_NAME_PRICE));
        String note = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.LittleCalendar.COLUMN_NAME_NOTE));


        lcTime.setText(time);
        lcClient.setText(client);
        lcPrice.setText(price);
        lcNote.setText(note);
    }
}

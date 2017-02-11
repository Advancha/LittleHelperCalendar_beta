package com.example.myapplication;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Ира on 23.12.2016.
 */

public class VisitsCursorAdapter extends CursorAdapter {
    public VisitsCursorAdapter(MainActivity context, Cursor cursor){
        super(context,cursor,0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.visit_list_cell, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView lcTime = (TextView)view.findViewById(R.id.list_cell_time);
        TextView lcClient = (TextView)view.findViewById(R.id.list_cell_client);
        TextView lcPrice = (TextView) view.findViewById(R.id.list_cell_price);
        TextView lcNote = (TextView) view.findViewById(R.id.list_cell_note);

        String time = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.TabVisits.COLUMN_NAME_TIME));
        String client = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.TabClients.COLUMN_NAME_NAME));
        String price = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.TabVisits.COLUMN_NAME_PRICE));
        String note = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.TabVisits.COLUMN_NAME_NOTE));


        lcTime.setText(time);
        lcClient.setText(client);
        lcPrice.setText(price);
        lcNote.setText(note);
    }
}

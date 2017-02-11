package com.example.myapplication;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Ира on 23.12.2016.
 */

public class ClientsCursorAdapter extends CursorAdapter implements Filterable {
    public ClientsCursorAdapter(Context context, Cursor cursor){
        super(context,cursor,0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.client_list_cell, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        LinearLayout lcListLayout = (LinearLayout)view.findViewById(R.id.client_list_cell_layout);

        TextView lcName = (TextView)view.findViewById(R.id.list_cell_client_name);
        TextView lcPhone = (TextView)view.findViewById(R.id.list_cell_client_phone);


        String strName = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.TabClients.COLUMN_NAME_NAME));
        String strPhone = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.TabClients.COLUMN_NAME_PHONE));


        lcName.setText(strName);
        lcPhone.setText(strPhone);
    }


    @Override
    public CharSequence convertToString(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(DBContract.TabClients.COLUMN_NAME_NAME));
    }
}
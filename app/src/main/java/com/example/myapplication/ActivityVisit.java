package com.example.myapplication;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import hirondelle.date4j.DateTime;


public class ActivityVisit extends FragmentActivity {

    private static EditText tvTime;
    private static TextView tvDate;
    private AutoCompleteTextView tvClient;
    private TextView tvPrice;
    private TextView tvNote;

    private static Integer selectedID;
    private static Integer EMPTY_ID=new Integer(-1);
    private DateTime calSelectedDate;

    private  Cursor cursor;
    private ClientsCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit);

        tvDate = (TextView) findViewById(R.id.date);
        tvTime = (EditText) findViewById(R.id.time);
        tvClient = (AutoCompleteTextView) findViewById(R.id.client);
        tvNote = (TextView) findViewById(R.id.note);
        tvPrice = (TextView) findViewById(R.id.price);

        DBHelper dbHelper = new DBHelper(this);
        cursor = dbHelper.getCursorForClientList();
        adapter = new ClientsCursorAdapter(this, cursor);
        tvClient.setAdapter(adapter);

        Intent i = getIntent();
        selectedID = i.getIntExtra(String.valueOf(R.string.selected_id), EMPTY_ID);
        Integer selected_year = i.getIntExtra(String.valueOf(R.string.selected_date_year), 1900);
        Integer selected_month = i.getIntExtra(String.valueOf(R.string.selected_date_month), 1);
        Integer selected_day = i.getIntExtra(String.valueOf(R.string.selected_date_day), 1);

        calSelectedDate = new DateTime(selected_year,selected_month,selected_day,0,0,0,0);
        set_tvDate(calSelectedDate);

        if (!selectedID.equals(EMPTY_ID)) {
              selectDBRecord(selectedID);
        }
    }



    protected void giveBackSelectedDate() {
        Intent i = new Intent();
        i.putExtra(String.valueOf(R.string.selected_date_year), calSelectedDate.getYear());
        i.putExtra(String.valueOf(R.string.selected_date_month), calSelectedDate.getMonth());
        i.putExtra(String.valueOf(R.string.selected_date_day), calSelectedDate.getDay());
        setResult(RESULT_OK, i);
        this.finish();
    }



    public void set_tvDate(DateTime calDate){
        tvDate.setText(String.format("%1$04d-%2$02d-%3$02d",calDate.getYear(),calDate.getMonth(),calDate.getDay()));
    }
    public void addDBRecord_TabVisits(String date, String time, String price, String note){
        DBHelper mDbHelper = new DBHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(DBContract.TabVisits.COLUMN_NAME_ENTRY_ID, 1);
        values.put(DBContract.TabVisits.COLUMN_NAME_CLIENT_ID, cursor.getInt(cursor.getColumnIndex(DBContract.TabClients._ID)));
        //values.put(DBContract.TabVisits.COLUMN_NAME_CLIENT, client);
        values.put(DBContract.TabVisits.COLUMN_NAME_NOTE, note);
        values.put(DBContract.TabVisits.COLUMN_NAME_DATE, date);
        values.put(DBContract.TabVisits.COLUMN_NAME_TIME, time);
        values.put(DBContract.TabVisits.COLUMN_NAME_DATE_TIME, ""+date+"T"+time+"");
        values.put(DBContract.TabVisits.COLUMN_NAME_PRICE, price);

        long newRowId;
        newRowId = db.insert(
                DBContract.TabVisits.TABLE_NAME,
                null,
                values);
    }

    public void updDBRecord_TabVisits(String date, String time, String price, String note){
        DBHelper mDbHelper = new DBHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBContract.TabVisits.COLUMN_NAME_CLIENT_ID, cursor.getInt(cursor.getColumnIndex(DBContract.TabClients._ID)));
        values.put(DBContract.TabVisits.COLUMN_NAME_NOTE, note);
        values.put(DBContract.TabVisits.COLUMN_NAME_DATE, date);
        values.put(DBContract.TabVisits.COLUMN_NAME_TIME, time);
        values.put(DBContract.TabVisits.COLUMN_NAME_DATE_TIME, ""+date+"T"+time+"");
        values.put(DBContract.TabVisits.COLUMN_NAME_PRICE, price);

       db.update(DBContract.TabVisits.TABLE_NAME,values," _id="+selectedID,null);
    }
    public void delDBRecord(){
        DBHelper mDbHelper = new DBHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(DBContract.TabVisits.TABLE_NAME," _id="+selectedID,null);
    }

    public void setFields(Cursor local_cursor){

        if (local_cursor.getCount()>0) {
            local_cursor.moveToPosition(0);

            String strTime = local_cursor.getString(local_cursor.getColumnIndexOrThrow(DBContract.TabVisits.COLUMN_NAME_TIME));
            int clientID = local_cursor.getInt(local_cursor.getColumnIndexOrThrow(DBContract.TabClients._ID));
            String strPrice = local_cursor.getString(local_cursor.getColumnIndexOrThrow(DBContract.TabVisits.COLUMN_NAME_PRICE));
            String strNote = local_cursor.getString(local_cursor.getColumnIndexOrThrow(DBContract.TabVisits.COLUMN_NAME_NOTE));

            tvTime.setText(strTime);
            tvNote.setText(strNote);
            tvPrice.setText(strPrice);


            cursor.moveToFirst();
            for (int i=0; i<cursor.getCount(); i++)
            {
                if (cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.TabClients._ID))==clientID) {
                    tvClient.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBContract.TabClients.COLUMN_NAME_NAME)));
                    break;
                }
                else {
                    cursor.moveToNext();
                }
            }
        }
        else{
            System.out.println("Cursor is empty");
        }

    }

    public void selectDBRecord(Integer selectedID){
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db;
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM "
                + DBContract.TabVisits.TABLE_NAME+" WHERE _id=" + selectedID;
        /*
        String query = "SELECT * FROM "
                + DBContract.TabVisits.TABLE_NAME
                + " LEFT JOIN "+DBContract.TabClients.TABLE_NAME
                + " ON "+DBContract.TabVisits.TABLE_NAME+"."+DBContract.TabVisits.COLUMN_NAME_CLIENT_ID +"="
                + DBContract.TabClients.TABLE_NAME+"._id"
                +" WHERE "+DBContract.TabVisits.TABLE_NAME+"._id=" + selectedID;
                */
        try {
            setFields(db.rawQuery(query,null));
        } catch (SQLiteException e) {
            System.out.println(e.getMessage());

        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void onSave(View view) {
        if (selectedID.equals(EMPTY_ID)) {
            addDBRecord_TabVisits(tvDate.getText().toString(), tvTime.getText().toString(), tvPrice.getText().toString(), tvNote.getText().toString());
        }
        else{
            updDBRecord_TabVisits(tvDate.getText().toString(), tvTime.getText().toString(), tvPrice.getText().toString(), tvNote.getText().toString());
        }
        giveBackSelectedDate();
    }

    public void onBack(View view) {
        finish();
    }

    public void onDel(View view) {
        if (!selectedID.equals(EMPTY_ID)) {
            delDBRecord();
        }
        finish();
    }

    public void onDecrDate(View view)
    {
        calSelectedDate=calSelectedDate.minusDays(1);
        set_tvDate(calSelectedDate);
    }

    public void onIncrDate(View view)
    {
        calSelectedDate=calSelectedDate.plusDays(1);
        set_tvDate(calSelectedDate);
    }


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int hour;
            int minute;
            if (selectedID.equals(EMPTY_ID)) {
                // Use the current time as the default values for the picker
                final Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = 0;
            }
            else{
                hour =Integer.parseInt((tvTime.getText().toString()).substring(0,2));
                minute =Integer.parseInt((tvTime.getText().toString()).substring(3,5));
            }
            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), 1
                    , this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            tvTime.setText(String.format("%1$02d:%2$02d",hourOfDay,minute));
        }


    }
}

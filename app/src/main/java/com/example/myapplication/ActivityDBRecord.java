package com.example.myapplication;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import hirondelle.date4j.DateTime;


public class ActivityDBRecord extends FragmentActivity {

    private static EditText tvTime;
    private static TextView tvDate;
    private TextView tvClient;
    private TextView tvPrice;
    private TextView tvNote;

    private static Integer selectedID;
    private static Integer EMPTY_ID=new Integer(-1);
    private DateTime calSelectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbrecord);

        tvDate = (TextView) findViewById(R.id.date);
        tvTime = (EditText) findViewById(R.id.time);
        tvClient = (TextView) findViewById(R.id.client);
        tvNote = (TextView) findViewById(R.id.note);
        tvPrice = (TextView) findViewById(R.id.price);

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
    public void addDBRecord(String date, String time, String client, String price, String note){
        DBHelper mDbHelper = new DBHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBContract.LittleCalendar.COLUMN_NAME_ENTRY_ID, 1);
        values.put(DBContract.LittleCalendar.COLUMN_NAME_CLIENT, client);
        values.put(DBContract.LittleCalendar.COLUMN_NAME_NOTE, note);
        values.put(DBContract.LittleCalendar.COLUMN_NAME_DATE, date);
        values.put(DBContract.LittleCalendar.COLUMN_NAME_TIME, time);
        values.put(DBContract.LittleCalendar.COLUMN_NAME_DATE_TIME, ""+date+"T"+time+"");
        values.put(DBContract.LittleCalendar.COLUMN_NAME_PRICE, price);

        long newRowId;
        newRowId = db.insert(
                DBContract.LittleCalendar.TABLE_NAME,
                null,
                values);
    }

    public void updDBRecord(String date, String time, String client, String price, String note){
        DBHelper mDbHelper = new DBHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBContract.LittleCalendar.COLUMN_NAME_ENTRY_ID, 1);
        values.put(DBContract.LittleCalendar.COLUMN_NAME_CLIENT, client);
        values.put(DBContract.LittleCalendar.COLUMN_NAME_NOTE, note);
        values.put(DBContract.LittleCalendar.COLUMN_NAME_DATE, date);
        values.put(DBContract.LittleCalendar.COLUMN_NAME_TIME, time);
        values.put(DBContract.LittleCalendar.COLUMN_NAME_DATE_TIME, ""+date+"T"+time+"");
        values.put(DBContract.LittleCalendar.COLUMN_NAME_PRICE, price);

       db.update(DBContract.LittleCalendar.TABLE_NAME,values," _id="+selectedID,null);
    }
    public void delDBRecord(){
        DBHelper mDbHelper = new DBHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(DBContract.LittleCalendar.TABLE_NAME," _id="+selectedID,null);
    }

    public void setFields(Cursor cursor){

        if (cursor.getCount()>0) {
            cursor.moveToPosition(0);

            String strTime = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.LittleCalendar.COLUMN_NAME_TIME));
            String strClient = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.LittleCalendar.COLUMN_NAME_CLIENT));
            String strPrice = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.LittleCalendar.COLUMN_NAME_PRICE));
            String strNote = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.LittleCalendar.COLUMN_NAME_NOTE));

            tvTime.setText(strTime);
            tvClient.setText(strClient);
            tvNote.setText(strNote);
            tvPrice.setText(strPrice);
        }
        else{
            System.out.println("Cursor is empty");
        }

    }

    public void selectDBRecord(Integer selectedID){
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db;
        db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + DBContract.LittleCalendar.TABLE_NAME +" WHERE _id=" + selectedID;
        try {

            Cursor cursor = db.rawQuery(query,null);
            setFields(cursor);
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
            addDBRecord(tvDate.getText().toString(), tvTime.getText().toString(), tvClient.getText().toString(), tvPrice.getText().toString(), tvNote.getText().toString());
        }
        else{
            updDBRecord(tvDate.getText().toString(), tvTime.getText().toString(), tvClient.getText().toString(), tvPrice.getText().toString(), tvNote.getText().toString());
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
            return new TimePickerDialog(getActivity(), 1, this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            tvTime.setText(String.format("%1$02d:%2$02d",hourOfDay,minute));
        }


    }
}

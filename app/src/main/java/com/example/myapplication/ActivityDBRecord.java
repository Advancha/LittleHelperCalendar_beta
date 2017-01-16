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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;


public class ActivityDBRecord extends FragmentActivity {
    private static EditText time;
    private static TextView date;
    private TextView client;
    private TextView price;
    private TextView note;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbrecord);

        date = (TextView) findViewById(R.id.date);
        time = (EditText) findViewById(R.id.time);
        client = (TextView) findViewById(R.id.client);
        note = (TextView) findViewById(R.id.note);
        price = (TextView) findViewById(R.id.price);


        Intent i = getIntent();
        String selectedDate = i.getStringExtra(String.valueOf(R.string.selected_date));
        String selectedID = i.getStringExtra(String.valueOf(R.string.selected_id));

        date.setText(selectedDate);

        if (!selectedID.isEmpty()) {
              selectDBRecord(selectedID);
        }





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


       // Toast.makeText(this,"New record DATE_TIME is "+""+date+"T"+time+"",Toast.LENGTH_LONG).show();

    }

    public void setFields(Cursor cursor){

        if (cursor.getCount()>0) {
            cursor.moveToPosition(0);

            String strTime = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.LittleCalendar.COLUMN_NAME_TIME));
            String strClient = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.LittleCalendar.COLUMN_NAME_CLIENT));
            String strPrice = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.LittleCalendar.COLUMN_NAME_PRICE));
            String strNote = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.LittleCalendar.COLUMN_NAME_NOTE));

            time.setText(strTime);
            client.setText(strClient);
            note.setText(strNote);
            price.setText(strPrice);
        }
        else{
            System.out.println("Cursor is empty");
        }

    }

    public void selectDBRecord(String selectedID){
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db;
        db = dbHelper.getReadableDatabase();
       // String selectedDateString = dateToString(date);

       // String selection = " _id =?";
        String query = "SELECT * FROM " + DBContract.LittleCalendar.TABLE_NAME
               + " WHERE _id=" + Integer.parseInt(selectedID);
       String[] selectionArgs = new String[]{selectedID};

        try {

            Cursor cursor = db.rawQuery(query,null);
            setFields(cursor);
     /*       cursorAdapter = new CustomCursorAdapter(this, cursor);
            clientList.setAdapter(cursorAdapter);
            cursorAdapter.changeCursor(cursor);
*/

        } catch (SQLiteException e) {
            System.out.println(e.getMessage());

        }


    }
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void OnSave(View view) {
        addDBRecord(date.getText().toString(), time.getText().toString(), client.getText().toString(), price.getText().toString(), note.getText().toString());
        this.finish();
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), 1, this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            time.setText(String.format("%1$02d:%2$02d",hourOfDay,minute));
        }
    }
}

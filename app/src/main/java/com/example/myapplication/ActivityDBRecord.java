package com.example.myapplication;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;


public class ActivityDBRecord extends FragmentActivity {
    private static EditText time;
    private static TextView date;
    private TextView client;
    private TextView price;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbrecord);

        Intent i = getIntent();
        date = (TextView) findViewById(R.id.date);
        date.setText(i.getStringExtra(String.valueOf(R.string.selected_date)));

        time = (EditText) findViewById(R.id.time);
        //time.setText("00:00");

        client = (TextView) findViewById(R.id.client);
        price = (TextView) findViewById(R.id.price);
    }


    public void AddDBRecord(String date, String time, String client, String price){
        DBHelper mDbHelper = new DBHelper(this);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();


        ContentValues values = new ContentValues();

        values.put(DBContract.LittleCalendar.COLUMN_NAME_ENTRY_ID, 1);
        values.put(DBContract.LittleCalendar.COLUMN_NAME_CLIENT, client);
        values.put(DBContract.LittleCalendar.COLUMN_NAME_DATE, ""+date+"T"+time+"");
        values.put(DBContract.LittleCalendar.COLUMN_NAME_PRICE, price);


        long newRowId;
        newRowId = db.insert(
                DBContract.LittleCalendar.TABLE_NAME,
                null,
                values);


        Toast.makeText(this,"New record _ID is "+newRowId,Toast.LENGTH_SHORT);

    }
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void OnSave(View view) {
        AddDBRecord(date.getText().toString(), time.getText().toString(), client.getText().toString(), price.getText().toString());
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

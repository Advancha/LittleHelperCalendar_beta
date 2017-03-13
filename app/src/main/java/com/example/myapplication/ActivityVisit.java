package com.example.myapplication;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import hirondelle.date4j.DateTime;


public class ActivityVisit extends FragmentActivity {

    private static EditText tvTime;
    private static EditText tvDate;
    private AutoCompleteTextView tvClient;
    private EditText tvPrice;
    private EditText tvNote;

    private static Integer selectedID;
    private static Integer EMPTY_ID=new Integer(-1);
    private DateTime calSelectedDate;

    private  Cursor cursor;
    private ClientsCursorAdapter adapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab_save_visit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSave(v);
            }
        });
        tvDate = (EditText) findViewById(R.id.date);
        tvTime = (EditText) findViewById(R.id.time);
        tvClient = (AutoCompleteTextView) findViewById(R.id.client);
        tvNote = (EditText) findViewById(R.id.note);
        tvPrice = (EditText) findViewById(R.id.price);

        dbHelper = new DBHelper(this);
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
              setFields();
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
    public void delDBRecord(){
        DBHelper mDbHelper = new DBHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(DBContract.TabVisits.TABLE_NAME," _id="+selectedID,null);
    }

    public void setFields(){
        Cursor local_cursor = dbHelper.getCursorForVisitByID(selectedID);
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
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void onSave(View view) {
        Integer client_id = adapter.getCursor().getInt(cursor.getColumnIndex(DBContract.TabClients._ID));
        Integer visit_id = selectedID;
        String date_text = tvDate.getText().toString();
        String time_text = tvTime.getText().toString();
        String price_text = tvPrice.getText().toString();
        String note_text = tvNote.getText().toString();

        if (selectedID.equals(EMPTY_ID)) {
            dbHelper.addDBRecord_TabVisits(date_text,time_text,price_text,note_text,client_id);
        }
        else{
            dbHelper.updDBRecord_TabVisits(date_text,time_text,price_text,note_text,client_id,visit_id);
        }
        giveBackSelectedDate();
    }
    /*
       public void onBack(View view) {
           finish();
       }


       public void onDel(View view) {
           if (!selectedID.equals(EMPTY_ID)) {
               delDBRecord();
           }
           finish();
       }
   */
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

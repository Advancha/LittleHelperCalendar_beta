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
import android.widget.AdapterView;
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

    private static Long selectedID;
    private static Long EMPTY_ID=new Long(-1);
    private DateTime calSelectedDate;


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
                onSave();
            }
        });
        tvDate = (EditText) findViewById(R.id.date);
        tvTime = (EditText) findViewById(R.id.time);
        tvClient = (AutoCompleteTextView) findViewById(R.id.client);
        tvNote = (EditText) findViewById(R.id.note);
        tvPrice = (EditText) findViewById(R.id.price);


        dbHelper = new DBHelper(this);
        Cursor cursor = dbHelper.getCursorForClientList();
        adapter = new ClientsCursorAdapter(this, cursor);
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return dbHelper.getFilteredCursorForClientList(constraint);
                 }
            });

        /*
        tvClient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvClient.setTag(R.id.TAG_CLIENT_ID,adapter.getItemId(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        */
        tvClient.setAdapter(adapter);

        Intent i = getIntent();
        selectedID = i.getLongExtra(String.valueOf(R.string.selected_id), EMPTY_ID);
        Integer selected_year = i.getIntExtra(String.valueOf(R.string.selected_date_year), 1900);
        Integer selected_month = i.getIntExtra(String.valueOf(R.string.selected_date_month), 1);
        Integer selected_day = i.getIntExtra(String.valueOf(R.string.selected_date_day), 1);

        calSelectedDate = new DateTime(selected_year,selected_month,selected_day,0,0,0,0);
        set_tvDate(calSelectedDate);

        if (!selectedID.equals(EMPTY_ID)) {
              setFields();
        }


    }


    protected void finishWithParams() {
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

    public void setFields(){
        Cursor local_cursor = dbHelper.getCursorForVisitByID(selectedID);
        if (local_cursor.getCount()>0) {
            local_cursor.moveToPosition(0);

            String strTime = local_cursor.getString(local_cursor.getColumnIndexOrThrow(DBContract.TabVisits.COLUMN_NAME_TIME));
            long clientID = local_cursor.getLong(local_cursor.getColumnIndexOrThrow(DBContract.TabVisits.COLUMN_NAME_CLIENT_ID));
            String strPrice = local_cursor.getString(local_cursor.getColumnIndexOrThrow(DBContract.TabVisits.COLUMN_NAME_PRICE));
            String strNote = local_cursor.getString(local_cursor.getColumnIndexOrThrow(DBContract.TabVisits.COLUMN_NAME_NOTE));

            tvTime.setText(strTime);
            tvNote.setText(strNote);
            tvPrice.setText(strPrice);

            adapter.getCursor().moveToFirst();
            for (int i=0; i<adapter.getCursor().getCount(); i++) {
                if (adapter.getCursor().getInt(adapter.getCursor().getColumnIndexOrThrow(DBContract.TabClients._ID))==clientID) {
                    tvClient.setText(adapter.getCursor().getString(adapter.getCursor().getColumnIndexOrThrow(DBContract.TabClients.COLUMN_NAME_NAME)),true);
                    tvClient.setTag(R.id.TAG_CLIENT_ID, clientID);
                    break;
                }
                else {
                    adapter.getCursor().moveToNext();
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



    public void onSave() {
        Long client_id;
        if (adapter.getCursor().getPosition()==-1) {
            client_id =(Long) tvClient.getTag(R.id.TAG_CLIENT_ID);
        }
        else{
            client_id = adapter.getItemId(adapter.getCursor().getPosition());
        }
        Long visit_id = selectedID;
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

        finishWithParams();

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

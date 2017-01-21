package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import hirondelle.date4j.DateTime;

public class MainActivity extends AppCompatActivity {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_SELECTED_DATE = "selected_date";
    private SharedPreferences mSettings;

    private CaldroidFragment caldroidFragment;
    private DateTime selectedDate;
    private Map extraData = new HashMap<String, DateTime>();
    private ListView clientList;
    private TextView tvSelectedDate;
    private Cursor cursor;
    private CustomCursorAdapter cursorAdapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        getSettings();

        clientList = (ListView) findViewById(R.id.client_list);
        clientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // cursor.getColumnIndexOrThrow()
                //Cursor sqlCursor=(Cursor)(clientList.getItemAtPosition(position));

                Integer selected_id=cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.LittleCalendar._ID));
                openDBRecordActivity(selected_id);

             /* String str_time=cursor.getString(cursor.getColumnIndexOrThrow(DBContract.LittleCalendar.COLUMN_NAME_TIME));
                String str_client=cursor.getString(cursor.getColumnIndexOrThrow(DBContract.LittleCalendar.COLUMN_NAME_CLIENT));
                Toast.makeText(getBaseContext(), str_id+": Client="+str_client+"; time="+str_time, Toast.LENGTH_LONG).show();

                Intent i = new Intent();

                Intent intent = new Intent(getBaseContext(), ActivityDBRecord.class);
                intent.putExtra("NEW",false);
                extraData.put("SELECTED_DATE", selectedDate);
                intent.putExtra("SELECTED_ID",str_id);
                startActivity(intent);
                */

            }
        });

        tvSelectedDate = (TextView) findViewById(R.id.selected_date);
        tvSelectedDate.setText(dateToString(selectedDate));

        caldroidFragment = new CaldroidCustomFragment();

        CaldroidListener caldroidListener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                selectedDate = dateToDateTime(date);

                extraData.put("SELECTED_DATE", selectedDate);
                caldroidFragment.setExtraData(extraData);

                caldroidFragment.refreshView();
                onCalDataChange(selectedDate);

                tvSelectedDate.setText(dateToString(selectedDate));
            }

            @Override
            public void onCaldroidViewCreated() {
           /*     Toast.makeText(getApplicationContext(),
                        "Caldroid view is created",
                        Toast.LENGTH_SHORT).show();
            */
            }
        };

        caldroidFragment.setCaldroidListener(caldroidListener);

        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        //args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        //args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.MONTH, selectedDate.getMonth());
        args.putInt(CaldroidFragment.YEAR, selectedDate.getYear());
        args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, false);
        args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        caldroidFragment.setArguments(args);

        extraData.put("SELECTED_DATE", selectedDate);
        caldroidFragment.setExtraData(extraData);


        caldroidFragment.refreshView();
        onCalDataChange(selectedDate);


        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSettings();
        onCalDataChange(selectedDate);
    }

    @Override
    protected void onPause() {

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_SELECTED_DATE, dateToString(selectedDate));
        editor.apply();

        super.onPause();

    }

    public void openDBRecordActivity(Integer selectedId){
        Intent intent = new Intent(this, ActivityDBRecord.class);

        intent.putExtra(String.valueOf(R.string.selected_date), dateToString(selectedDate));
        intent.putExtra(String.valueOf(R.string.selected_id), selectedId);
        startActivity(intent);
    }

    public void OnClickAdd(View view) {
        openDBRecordActivity(new Integer(-1));
    }

    public DateTime dateToDateTime(Date date) {
        GregorianCalendar gDate = new GregorianCalendar();
        gDate.setTime(date);
        DateTime dateTime = new DateTime(gDate.get(Calendar.YEAR), gDate.get(Calendar.MONTH) + 1, gDate.get(Calendar.DAY_OF_MONTH), 0, 0, 0, 0);
        return dateTime;
    }

    public String dateToString(DateTime date) {
        String stringDate = "";
        stringDate = String.format("%1$4d-%2$02d-%3$02d", date.getYear(), date.getMonth(), date.getDay());
        return stringDate;
    }

    public DateTime stringToDate(String str_selected_date) {
        Integer year;
        Integer month;
        Integer day;

        year = Integer.valueOf(str_selected_date.substring(0, 4));
        month = Integer.valueOf(str_selected_date.substring(5, 7));
        day = Integer.valueOf(str_selected_date.substring(8, 10));

        DateTime dateTime = new DateTime(year, month, day, 0, 0, 0, 0);

        return dateTime;
    }


    public void onCalDataChange(DateTime date) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db;
        db = dbHelper.getReadableDatabase();
        String selectedDateString = dateToString(date);

        String selection = DBContract.LittleCalendar.COLUMN_NAME_DATE + "=?";
        String order = DBContract.LittleCalendar.COLUMN_NAME_TIME;
        String query = "SELECT * FROM " + DBContract.LittleCalendar.TABLE_NAME
                + " WHERE " + selection
                + " ORDER BY " + order + " ASC";
        String[] selectionArgs = new String[]{selectedDateString};

        try {
            cursor = db.rawQuery(query, selectionArgs);
            cursorAdapter = new CustomCursorAdapter(this, cursor);
            clientList.setAdapter(cursorAdapter);
            cursorAdapter.changeCursor(cursor);


        } catch (SQLiteException e) {
            System.out.println(e.getMessage());

        }

    }

    protected void getSettings() {
        String selectedDateString;
        String selectedDateStringDef;

        Calendar cal = Calendar.getInstance();
        selectedDateStringDef = dateToString(dateToDateTime(cal.getTime()));

        if (mSettings.contains(APP_PREFERENCES_SELECTED_DATE)) {
            selectedDateString = mSettings.getString(APP_PREFERENCES_SELECTED_DATE, selectedDateStringDef);
            selectedDate = stringToDate(selectedDateString);
        }
        else
        {
            selectedDate = stringToDate(selectedDateStringDef);
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}



package com.example.myapplication;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import hirondelle.date4j.DateTime;

public class MainActivity extends AppCompatActivity {
    static final int PICK_SELECTED_DATE = 1;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_SELECTED_DAY = "selected_day";
    public static final String APP_PREFERENCES_SELECTED_MONTH = "selected_month";
    public static final String APP_PREFERENCES_SELECTED_YEAR = "selected_year";
    private SharedPreferences mSettings;

    private CaldroidFragment caldroidFragment;
    private DateTime selectedDate;
    private Map extraData = new HashMap<String, DateTime>();
    private Bundle args = new Bundle();
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
        tvSelectedDate = (TextView) findViewById(R.id.selected_date);
        clientList = (ListView) findViewById(R.id.client_list);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_new_visit);

        setSelectedDateFromSettings();


        clientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer selected_id=cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.LittleCalendar._ID));
                openDBRecordActivity(selected_id);
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickAdd(view);
                  }
        });


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
            }
        };

        caldroidFragment.setCaldroidListener(caldroidListener);


        Bundle args = new Bundle();
        args.putInt(CaldroidFragment.MONTH, selectedDate.getMonth());
        args.putInt(CaldroidFragment.YEAR, selectedDate.getYear());
        args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
        args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        caldroidFragment.setArguments(args);

        updCaldroidFragment();

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_clients:
                onClickMenuClients();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updCaldroidFragment(){


        extraData.put("SELECTED_DATE", selectedDate);
        caldroidFragment.setExtraData(extraData);
        caldroidFragment.refreshView();

        onCalDataChange(selectedDate);
        tvSelectedDate.setText(dateToString(selectedDate));



    }
    @Override
    protected void onResume() {
        super.onResume();

       // setSelectedDateFromSettings();
        // onCalDataChange(selectedDate);

    }

    @Override
    protected void onPause() {

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_SELECTED_DAY, selectedDate.getDay().toString());
        editor.putString(APP_PREFERENCES_SELECTED_MONTH, selectedDate.getMonth().toString());
        editor.putString(APP_PREFERENCES_SELECTED_YEAR, selectedDate.getYear().toString());
        editor.apply();

        super.onPause();

    }

    public void openDBRecordActivity(Integer selectedId){
        Intent intent = new Intent(this, ActivityDBRecord.class);

        intent.putExtra(String.valueOf(R.string.selected_id), selectedId);
        intent.putExtra(String.valueOf(R.string.selected_date_year),selectedDate.getYear());
        intent.putExtra(String.valueOf(R.string.selected_date_month),selectedDate.getMonth());
        intent.putExtra(String.valueOf(R.string.selected_date_day),selectedDate.getDay());

        startActivityForResult(intent, PICK_SELECTED_DATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_SELECTED_DATE) {
            if (resultCode == RESULT_OK) {
                Integer selected_year = data.getIntExtra(String.valueOf(R.string.selected_date_year), 1900);
                Integer selected_month = data.getIntExtra(String.valueOf(R.string.selected_date_month), 1);
                Integer selected_day = data.getIntExtra(String.valueOf(R.string.selected_date_day), 1);

                DateTime calSelectedDate = new DateTime(selected_year, selected_month, selected_day, 0, 0, 0, 0);
                selectedDate=calSelectedDate;

                SharedPreferences.Editor editor = mSettings.edit();
                editor.apply();

                updCaldroidFragment();
                caldroidFragment.moveToDateTime(selectedDate);

            }
        }
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

    protected void setSelectedDateFromSettings() {
        DateTime currentDate = DateTime.today(TimeZone.getDefault());

        Integer selectedDateDay = Integer.parseInt(mSettings.getString(APP_PREFERENCES_SELECTED_DAY, currentDate.getDay().toString()));
        Integer selectedDateMonth = Integer.parseInt(mSettings.getString(APP_PREFERENCES_SELECTED_MONTH, currentDate.getMonth().toString()));
        Integer selectedDateYear = Integer.parseInt(mSettings.getString(APP_PREFERENCES_SELECTED_YEAR, currentDate.getYear().toString()));
        selectedDate = new DateTime(selectedDateYear,selectedDateMonth,selectedDateDay,0,0,0,0);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public void onClickMenuClients(){
        Intent i = new Intent(this,ActivityClientList.class);
        startActivity(i);
    }

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



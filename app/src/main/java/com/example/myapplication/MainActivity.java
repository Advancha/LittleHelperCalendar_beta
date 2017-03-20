package com.example.myapplication;

import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import hirondelle.date4j.DateTime;

public class MainActivity extends AppCompatActivity implements VisitsListFragment.OnFragmentInteractionListener{
    static final int UPD_VISIT_LIST = 1000;
    static final int PICK_CONTACT=2000;

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_SELECTED_DAY = "selected_day";
    public static final String APP_PREFERENCES_SELECTED_MONTH = "selected_month";
    public static final String APP_PREFERENCES_SELECTED_YEAR = "selected_year";
    private SharedPreferences mSettings;

    private CaldroidFragment caldroidFragment;
    private DateTime selectedDate;
    private Map extraData = new HashMap<String, DateTime>();
    private Bundle args = new Bundle();
//    private ListView lv_visitList;
    private TextView tvSelectedDate;
    private FragmentManager fragmentManager;
    private VisitsListFragment vlFragment;
//    private Cursor cursor;
//    private VisitsCursorAdapter cursorAdapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        tvSelectedDate = (TextView) findViewById(R.id.selected_date);

  //      lv_visitList = (ListView) findViewById(R.id.visit_list);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_new_visit);

        selectedDate=getSelectedDayFromSettings();
        vlFragment = (VisitsListFragment)(getSupportFragmentManager().findFragmentById(R.id.fragment_visit_list));
/*
        lv_visitList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer selected_id=cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.TabVisits._ID));
                openDBRecordActivity(selected_id);
            }
        });

*/
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
            case R.id.action_open_clients:
                onClickMenuOpenClientList();
                return true;
            case R.id.action_import_clients:
                onClickMenuImportClients();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public DateTime getSelectedDate(){
        if (selectedDate==null){
            return getSelectedDayFromSettings();
        }
        else {
            return selectedDate;
        }
    }

    public String getStringSelectedDate(){
            return dateToString(getSelectedDate());
    }

    private void updCaldroidFragment(){


        extraData.put("SELECTED_DATE", selectedDate);
        caldroidFragment.setExtraData(extraData);
     //   caldroidFragment.moveToDateTime(selectedDate);
        caldroidFragment.refreshView();


        onCalDataChange(selectedDate);
        tvSelectedDate.setText(dateToString(selectedDate));



    }
    @Override
    protected void onResume() {
        super.onResume();

        selectedDate=getSelectedDayFromSettings();
        updCaldroidFragment();

        // onCalDataChange(selectedDate);

    }

    @Override
    protected void onPause() {

        super.onPause();

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_SELECTED_DAY, selectedDate.getDay().toString());
        editor.putString(APP_PREFERENCES_SELECTED_MONTH, selectedDate.getMonth().toString());
        editor.putString(APP_PREFERENCES_SELECTED_YEAR, selectedDate.getYear().toString());
        editor.apply();


    }


    public void openVisitActivity(long selectedId){
        Intent intent = new Intent(this, ActivityVisit.class);

        intent.putExtra(String.valueOf(R.string.selected_id), selectedId);
        intent.putExtra(String.valueOf(R.string.selected_date_year),selectedDate.getYear());
        intent.putExtra(String.valueOf(R.string.selected_date_month),selectedDate.getMonth());
        intent.putExtra(String.valueOf(R.string.selected_date_day),selectedDate.getDay());

        startActivityForResult(intent, UPD_VISIT_LIST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (PICK_CONTACT): {
                if (resultCode == RESULT_OK) {
                    contactPicked(data);
                }
            }

            case (UPD_VISIT_LIST): {
                if (resultCode == RESULT_OK) {
                    Integer selected_year = data.getIntExtra(String.valueOf(R.string.selected_date_year), 1900);
                    Integer selected_month = data.getIntExtra(String.valueOf(R.string.selected_date_month), 1);
                    Integer selected_day = data.getIntExtra(String.valueOf(R.string.selected_date_day), 1);

                    DateTime calSelectedDate = new DateTime(selected_year, selected_month, selected_day, 0, 0, 0, 0);
                    selectedDate = calSelectedDate;

                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.apply();

                }
            }
            caldroidFragment.moveToDateTime(selectedDate);
            updCaldroidFragment();

        }
    }

    private void contactPicked(Intent data) {
        DBHelper dbHelper = new DBHelper(this);
        ArrayList<Contact> selectedContacts = data.getParcelableArrayListExtra("SelectedContacts");
        for(int i=0;i<selectedContacts.size();i++) {
            Contact contact = selectedContacts.get(i);
            dbHelper.addDBRecord_TabClients(contact.name, contact.phone);
        }

    }

    public void OnClickAdd(View view) {

        openVisitActivity(new Integer(-1));
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

        String selectedDateString = dateToString(date);
        vlFragment.onCalDateChanged(selectedDateString);


    }

    public DateTime getSelectedDayFromSettings() {
        if (mSettings==null){
            mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        }

        DateTime currentDate = DateTime.today(TimeZone.getDefault());

        Integer selectedDateDay = Integer.parseInt(mSettings.getString(APP_PREFERENCES_SELECTED_DAY, currentDate.getDay().toString()));
        Integer selectedDateMonth = Integer.parseInt(mSettings.getString(APP_PREFERENCES_SELECTED_MONTH, currentDate.getMonth().toString()));
        Integer selectedDateYear = Integer.parseInt(mSettings.getString(APP_PREFERENCES_SELECTED_YEAR, currentDate.getYear().toString()));
        return new DateTime(selectedDateYear,selectedDateMonth,selectedDateDay,0,0,0,0);
    }


    public void onClickMenuOpenClientList(){
        Intent i = new Intent(this,ActivityClientList.class);
        startActivity(i);
    }

    public void onClickMenuImportClients(){

        Intent intentContactPick = new Intent(MainActivity.this,ContactsPickerActivity.class);
        startActivityForResult(intentContactPick,PICK_CONTACT);

    }


    @Override
    public void onFragmentItemSelected(int position) {
        long selected_id=vlFragment.getItemIDByPosition(position);
        if (selected_id>-1) {
            openVisitActivity(selected_id);
        }
    }

    @Override
    public String onFragmentDataRequest() {
        return getStringSelectedDate();
    }

}



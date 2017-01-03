package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import hirondelle.date4j.DateTime;

public class MainActivity extends AppCompatActivity {

    private CaldroidFragment caldroidFragment;
    private DateTime selectedDate;
    private Map extraData = new HashMap<String,DateTime>();
    private ListView clientList;
    private Cursor cursor;
    private CustomCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clientList = (ListView) findViewById(R.id.client_list);

       // caldroidFragment = new CaldroidFragment();
        caldroidFragment = new CaldroidCustomFragment();

        CaldroidListener caldroidListener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                selectedDate= dateToDateTime(date);

                extraData.put("SELECTED_DATE",selectedDate);
                caldroidFragment.setExtraData(extraData);

                caldroidFragment.refreshView();
                onCalDataChange(date);
                }

            @Override
            public void onCaldroidViewCreated() {
                Toast.makeText(getApplicationContext(),
                        "Caldroid view is created",
                        Toast.LENGTH_SHORT).show();
            }
        };

        caldroidFragment.setCaldroidListener(caldroidListener);

        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, false);
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);


        selectedDate = dateToDateTime(cal.getTime());
        extraData.put("SELECTED_DATE", selectedDate);
        caldroidFragment.setArguments(args);

        onCalDataChange(cal.getTime());


/*
        Drawable document = getResources().getDrawable(R.drawable.document);
        Date docDate = new Date();
        docDate.setDate(15);

        caldroidListener=caldroidFragment.getCaldroidListener();

        caldroidFragment.setBackgroundDrawableForDate(document,docDate);
*/
        caldroidFragment.refreshView();

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

    }

    public void OnClickAdd(View view) {
        Intent i = new Intent();

        Intent intent = new Intent(this, ActivityDBRecord.class);
        intent.putExtra(String.valueOf(R.string.selected_date), String.format("%1$4d-%2$02d-%3$02d",selectedDate.getYear(),selectedDate.getMonth(),selectedDate.getDay()));
        startActivity(intent);
    }

    public DateTime dateToDateTime(Date date){
        GregorianCalendar gDate = new GregorianCalendar();
        gDate.setTime(date);
        DateTime dateTime = new DateTime(gDate.get(Calendar.YEAR),gDate.get(Calendar.MONTH)+1,gDate.get(Calendar.DAY_OF_MONTH),0,0,0,0);
        return dateTime;
    }

    public String getStringDate(Date date){
        String stringDate="";
        GregorianCalendar gDate = new GregorianCalendar();
        gDate.setTime(date);
        stringDate=String.format("%1$4d-%2$02d-%3$02d",gDate.get(Calendar.YEAR),gDate.get(Calendar.MONTH)+1,gDate.get(Calendar.DAY_OF_MONTH));
        return stringDate;
    }


    public void onCalDataChange(Date date){
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db;
        db = dbHelper.getReadableDatabase();
        String selectedDate = getStringDate(date);

        String selection = DBContract.LittleCalendar.COLUMN_NAME_DATE+"=?";
        String query = "SELECT * FROM "+DBContract.LittleCalendar.TABLE_NAME+" WHERE "+selection;
        String[] selectionArgs = new String[] {selectedDate};

        try{
            cursor = db.rawQuery(query,selectionArgs);
            cursorAdapter = new CustomCursorAdapter(this, cursor);
            clientList.setAdapter(cursorAdapter);
            cursorAdapter.changeCursor(cursor);
        }
        catch (SQLiteException e){
            System.out.println(e.getMessage());

        }

    }

}



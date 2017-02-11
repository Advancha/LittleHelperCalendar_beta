package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;


/**
 * Created by Ира on 01.02.2017.
 */

public class ActivityClientList extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab_new_client);
        ListView lvClientList = (ListView)findViewById(R.id.client_list);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewClient();
            }
        });


        DBHelper dbHelper = new DBHelper(this);

        Cursor cursor = dbHelper.getCursorForClientList();
        ClientsCursorAdapter cursorAdapter = new ClientsCursorAdapter(this, cursor);
        lvClientList.setAdapter(cursorAdapter);
        cursorAdapter.changeCursor(cursor);

        /*

        //  String selection = DBContract.TabClients.COLUMN_NAME_DATE + "=?";
        String order = DBContract.TabClients.COLUMN_NAME_NAME;
        String query = "SELECT * FROM " + DBContract.TabClients.TABLE_NAME
                + " ORDER BY " + order + " ASC";
       try {
            Cursor cursor = db.rawQuery(query, null);
            ClientsCursorAdapter cursorAdapter = new ClientsCursorAdapter(this, cursor);
            lvClientList.setAdapter(cursorAdapter);
            cursorAdapter.changeCursor(cursor);


        } catch (SQLiteException e) {
            System.out.println(e.getMessage());

        }
        */
    }

    private void addNewClient(){
        Intent i = new Intent(this,ActivityClient.class);
        startActivity(i);
    }
}

package com.example.myapplication;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class ActivityClient extends AppCompatActivity {

    private EditText etClienteName;
    private EditText etClientePhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        etClienteName = (EditText) findViewById(R.id.client_name);
        etClientePhone = (EditText) findViewById(R.id.client_phone);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab_save_client);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSave();
            }
        });
    }

    private void onSave(){
        String clienteName=etClienteName.getText().toString();
        String clientePhone=etClientePhone.getText().toString();
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.addDBRecord_TabClients(clienteName, clientePhone);
        this.finish();
    }
}

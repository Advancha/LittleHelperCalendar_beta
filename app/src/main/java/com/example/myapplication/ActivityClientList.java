package com.example.myapplication;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;


/**
 * Created by Ира on 01.02.2017.
 */

public class ActivityClientList extends AppCompatActivity implements OnStartDragListener{
    public RecyclerView rvClientList;
    public Cursor cursor;
    private RecyclerView.LayoutManager mLayoutManager;
    ClientsCursorRecyclerAdapter cursorAdapter;

    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);


        rvClientList = (RecyclerView) findViewById(R.id.client_list);
        mLayoutManager = new LinearLayoutManager(this);


        rvClientList.setLayoutManager(mLayoutManager);

        rvClientList.setHasFixedSize(true);

     //   ScaleInAnimator itemAnimator = new ScaleInAnimator();
     //   DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
     //   itemAnimator.setRemoveDuration(500);
     //   rvClientList.setItemAnimator(itemAnimator);



        final DBHelper dbHelper = new DBHelper(this);
        final Cursor cursor = dbHelper.getCursorForClientList();

        cursorAdapter = new ClientsCursorRecyclerAdapter(cursor, dbHelper, this);
        rvClientList.setAdapter(cursorAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(cursorAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvClientList);


        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab_new_client);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewClient();
            }
        });

    }

    private void addNewClient(){
        Intent i = new Intent(this,ActivityClient.class);
        startActivity(i);
    }


    @Override
    public void onStartDrag(ClientsCursorRecyclerAdapter.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}

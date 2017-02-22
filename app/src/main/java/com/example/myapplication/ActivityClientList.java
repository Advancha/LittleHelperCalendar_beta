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

public class ActivityClientList extends AppCompatActivity {
    public RecyclerView rvClientList;
    public Cursor cursor;
    private RecyclerView.LayoutManager mLayoutManager;
    ClientsCursorRecyclerAdapter cursorAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.colorBackgroundFloating, typedValue, true);
        final int color_norm = typedValue.data;


        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab_new_client);

        rvClientList = (RecyclerView) findViewById(R.id.client_list);
        mLayoutManager = new LinearLayoutManager(this);


        rvClientList.setLayoutManager(mLayoutManager);

        rvClientList.setHasFixedSize(true);

        ScaleInAnimator itemAnimator = new ScaleInAnimator();
     //   DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setRemoveDuration(500);
        rvClientList.setItemAnimator(itemAnimator);



        final DBHelper dbHelper = new DBHelper(this);
        final Cursor cursor = dbHelper.getCursorForClientList();

        cursorAdapter = new ClientsCursorRecyclerAdapter(cursor, dbHelper);
        rvClientList.setAdapter(cursorAdapter);
/*
        rvClientList.setOnTouchListener(new View.OnTouchListener() {
            float historicX = Float.NaN, historicY = Float.NaN, delX= Float.NaN, delY= Float.NaN;
            LinearLayout childViewUnderv;
            ImageView imageViewDelete;
            int _id=-1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        historicX = event.getX();
                        childViewUnderv = (LinearLayout)((RecyclerView)v).findChildViewUnder(historicX, historicY);
                        imageViewDelete = (ImageView)(childViewUnderv.findViewById(R.id.list_cell_client_delete));

                        childViewUnderv.setBackgroundColor(getResources().getColor(R.color.accent_light));

                        _id=(int)childViewUnderv.getTag(R.id.TAG_CLIENT_ID);
                        delX=imageViewDelete.getX();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        imageViewDelete.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (event.getX() >= delX) {
                            deletefromClientList(_id);
                            return true;
                        }
                        else
                        {
                            imageViewDelete.setVisibility(View.INVISIBLE);
                            childViewUnderv.setBackgroundColor(color_norm);

                        }
                        break;
                    default:
                        return false;
                }
                return false;
            }


            public void deletefromClientList(int _id){
                int _pos=-1;

                Cursor curent_cursor = cursorAdapter.getCursor();
                curent_cursor.moveToFirst();
                for (int i=0; i<curent_cursor.getCount(); i++){
                    if (curent_cursor.getInt(curent_cursor.getColumnIndexOrThrow(DBContract.TabClients._ID))==_id){
                        _pos=curent_cursor.getPosition();
                        break;
                    }
                    else {
                        curent_cursor.moveToNext();
                    }
                }

                dbHelper.deleteClient(_id);
                Cursor new_cursor = dbHelper.getCursorForClientList();
                cursorAdapter.changeCursorWithoutNotification(new_cursor);
                cursorAdapter.notifyItemRemoved(_pos);

            }

        });

*/
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





}

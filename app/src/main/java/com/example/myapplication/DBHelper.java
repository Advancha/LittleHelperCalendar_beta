package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ира on 18.12.2016.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "LittleHelperDB.db";


    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.LittleCalendar.TABLE_NAME + " (" +
                    DBContract.LittleCalendar._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DBContract.LittleCalendar.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    DBContract.LittleCalendar.COLUMN_NAME_CLIENT + TEXT_TYPE + COMMA_SEP +
                    DBContract.LittleCalendar.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                    DBContract.LittleCalendar.COLUMN_NAME_PRICE + REAL_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBContract.LittleCalendar.TABLE_NAME;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //onUpgrade(db, oldVersion, newVersion);
    }
}
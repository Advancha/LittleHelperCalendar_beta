package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ира on 18.12.2016.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "LittleHelperDB.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_VISITS =
            "CREATE TABLE " + DBContract.TabVisits.TABLE_NAME + " (" +
                    DBContract.TabVisits._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DBContract.TabVisits.COLUMN_NAME_NOTE + TEXT_TYPE + COMMA_SEP +
                    DBContract.TabVisits.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                    DBContract.TabVisits.COLUMN_NAME_TIME + TEXT_TYPE + COMMA_SEP +
                    DBContract.TabVisits.COLUMN_NAME_DATE_TIME + TEXT_TYPE + COMMA_SEP +
                    DBContract.TabVisits.COLUMN_NAME_PRICE + REAL_TYPE + COMMA_SEP+
                    DBContract.TabVisits.COLUMN_NAME_CLIENT_ID + REAL_TYPE  + COMMA_SEP+
                    "FOREIGN KEY("+ DBContract.TabVisits.COLUMN_NAME_CLIENT_ID+") REFERENCES "+
                    DBContract.TabClients.TABLE_NAME+"("+ DBContract.TabClients._ID+")"+
                    " )";

    private static final String SQL_CREATE_CLIENTS =
            "CREATE TABLE " + DBContract.TabClients.TABLE_NAME + " (" +
                    DBContract.TabClients._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DBContract.TabClients.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    DBContract.TabClients.COLUMN_NAME_PHONE + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBContract.TabVisits.TABLE_NAME;
    private static final String SQL_DELETE_CLIENTS =
            "DROP TABLE IF EXISTS " + DBContract.TabClients.TABLE_NAME;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CLIENTS);
        db.execSQL(SQL_CREATE_VISITS);

    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_CLIENTS);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //onUpgrade(db, oldVersion, newVersion);
    }

    public Cursor getCursorForClientList(){
        SQLiteDatabase db;
        db = this.getReadableDatabase();

        //  String selection = DBContract.TabClients.COLUMN_NAME_DATE + "=?";
        String order = DBContract.TabClients.COLUMN_NAME_NAME;
        String query = "SELECT * FROM " + DBContract.TabClients.TABLE_NAME
                + " ORDER BY " + order + " ASC";
        try {
            Cursor cursor = db.rawQuery(query, null);
            return cursor;
        } catch (SQLiteException e) {
            System.out.println(e.getMessage());
            return null;

        }

    }
}
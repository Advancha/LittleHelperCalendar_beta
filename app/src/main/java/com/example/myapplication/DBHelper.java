package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import hirondelle.date4j.DateTime;

/**
 * Created by Ира on 18.12.2016.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 9;
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
                    DBContract.TabVisits.COLUMN_NAME_CLIENT_ID + REAL_TYPE  + //COMMA_SEP+
                   // "FOREIGN KEY("+ DBContract.TabVisits.COLUMN_NAME_CLIENT_ID+") REFERENCES "+
                   // DBContract.TabClients.TABLE_NAME+"("+ DBContract.TabClients._ID+")"+
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

    public Cursor getFilteredCursorForClientList(CharSequence str){
        SQLiteDatabase db;
        db = this.getReadableDatabase();

        String order = DBContract.TabClients.COLUMN_NAME_NAME;
        String query = "SELECT * FROM " + DBContract.TabClients.TABLE_NAME
                + " WHERE "+ order + " LIKE '%"+str+"%'"
                + " ORDER BY " + order + " ASC";
        try {
            Cursor cursor = db.rawQuery(query, null);
            return cursor;
        } catch (SQLiteException e) {
            System.out.println(e.getMessage());
            return null;

        }

    }

    public void deleteClient(int _id){
        SQLiteDatabase db;
        db = this.getReadableDatabase();
        db.delete(DBContract.TabClients.TABLE_NAME," _id="+String.valueOf(_id),null);
        db.close();

    }
    public void deleteVisit(int _id){
        SQLiteDatabase db;
        db = this.getWritableDatabase();
        String args = String.valueOf(_id);
        String deleteQuery = "DELETE FROM "+DBContract.TabVisits.TABLE_NAME+" where _id='"+ args +"'";
        db.execSQL(deleteQuery);
        //db.delete(DBContract.TabVisits.TABLE_NAME," _id="+String.valueOf(_id),null);
        //db.close();

    }

    public Cursor getCursorForVisitList(String selectedDateString){
        Cursor cursor;
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = DBContract.TabVisits.COLUMN_NAME_DATE + "=?";
        String order = DBContract.TabVisits.COLUMN_NAME_TIME;
        String query = "SELECT "
                + DBContract.TabVisits.TABLE_NAME+"."+ DBContract.TabVisits._ID+","
                + DBContract.TabVisits.TABLE_NAME+"."+ DBContract.TabVisits.COLUMN_NAME_CLIENT_ID+","
                + DBContract.TabVisits.TABLE_NAME+"."+ DBContract.TabVisits.COLUMN_NAME_DATE+","
                + DBContract.TabVisits.TABLE_NAME+"."+ DBContract.TabVisits.COLUMN_NAME_NOTE+","
                + DBContract.TabVisits.TABLE_NAME+"."+ DBContract.TabVisits.COLUMN_NAME_PRICE+","
                + DBContract.TabVisits.TABLE_NAME+"."+ DBContract.TabVisits.COLUMN_NAME_DATE_TIME+","
                + DBContract.TabVisits.TABLE_NAME+"."+ DBContract.TabVisits.COLUMN_NAME_TIME+","
                + DBContract.TabClients.TABLE_NAME+"."+ DBContract.TabClients.COLUMN_NAME_NAME+","
                + DBContract.TabClients.TABLE_NAME+"."+ DBContract.TabClients.COLUMN_NAME_PHONE
                + " FROM " + DBContract.TabVisits.TABLE_NAME
                + " LEFT JOIN "+DBContract.TabClients.TABLE_NAME
                + " ON "+DBContract.TabVisits.TABLE_NAME+"."+DBContract.TabVisits.COLUMN_NAME_CLIENT_ID +"="+DBContract.TabClients.TABLE_NAME+"."+DBContract.TabClients._ID
                + " WHERE " + selection
                + " ORDER BY " + order + " ASC";
        String[] selectionArgs = new String[]{selectedDateString};

        try {
            cursor = db.rawQuery(query, selectionArgs);
            return cursor;
        } catch (SQLiteException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Cursor getCursorForVisitByID(Integer visit_id){
        Cursor cursor;
        SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM "
                    + DBContract.TabVisits.TABLE_NAME+" WHERE _id=" + visit_id;
        try {
            cursor = db.rawQuery(query,null);
            return cursor;
        } catch (SQLiteException e) {
            System.out.println(e.getMessage());
            return null;
        }

    }
    public void addDBRecord_TabVisits(String date, String time, String price, String note, Integer client_id){
        //DBHelper mDbHelper = new DBHelper(this);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBContract.TabVisits.COLUMN_NAME_CLIENT_ID, client_id);
        values.put(DBContract.TabVisits.COLUMN_NAME_NOTE, note);
        values.put(DBContract.TabVisits.COLUMN_NAME_DATE, date);
        values.put(DBContract.TabVisits.COLUMN_NAME_TIME, time);
        values.put(DBContract.TabVisits.COLUMN_NAME_DATE_TIME, ""+date+"T"+time+"");
        values.put(DBContract.TabVisits.COLUMN_NAME_PRICE, price);

        long newRowId;
        newRowId = db.insert(
                DBContract.TabVisits.TABLE_NAME,
                null,
                values);
    }

    public void updDBRecord_TabVisits(String date, String time, String price, String note, Integer client_id, Integer visit_id){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBContract.TabVisits.COLUMN_NAME_CLIENT_ID, client_id);
        values.put(DBContract.TabVisits.COLUMN_NAME_NOTE, note);
        values.put(DBContract.TabVisits.COLUMN_NAME_DATE, date);
        values.put(DBContract.TabVisits.COLUMN_NAME_TIME, time);
        values.put(DBContract.TabVisits.COLUMN_NAME_DATE_TIME, ""+date+"T"+time+"");
        values.put(DBContract.TabVisits.COLUMN_NAME_PRICE, price);

        db.update(DBContract.TabVisits.TABLE_NAME,values," _id="+visit_id,null);
    }
}
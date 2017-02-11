package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.sql.Date;

/**
 * Created by Ира on 18.12.2016.
 */

public final class DBContract {
    public DBContract(){}

    public static abstract class TabVisits implements BaseColumns {
        public static final String TABLE_NAME = "calendar";
        //public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_CLIENT_ID = "clientid";
        public static final String COLUMN_NAME_CLIENT = "client";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_DATE_TIME = "date_time";
        public static final String COLUMN_NAME_NOTE = "note";

    }

    public static abstract class TabClients implements BaseColumns {
        public static final String TABLE_NAME = "clients";
        public static final String COLUMN_NAME_NAME = "client_name";
        public static final String COLUMN_NAME_PHONE = "client_phone";

    }


}

package com.mmvtcstudent.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by W on 2019/8/15.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "bookmark.db";
    private static final int version = 1;
  /*  String sql = "create table bookmark" +
            "(id integer primary key autoincrement," +
            "time varchar(50) ,readvalue varchar(50),item_name varchar(50),href varchar(50))";*/
    private static final String SQL_CREATE = "create table bookmark(_id integer primary key autoincrement,time varchar(50) ,readvalue varchar(50),item_name varchar(50),href varchar(50))";
    private static final String SQL_DROP = "drop table if exists bookmark";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        db.execSQL(SQL_CREATE);
    }


}

package com.app.phonesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 14501_000 on 2016/8/8.
 */
public class BlackNumberOpenHelper extends SQLiteOpenHelper {
    public BlackNumberOpenHelper(Context context) {
        super(context, "blacknumber.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创见数据库中表
        db.execSQL("create table blacknumber(_id integer primary key autoincrement," +
                "phone varchar(20)," +
                "mode varchar(5));");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

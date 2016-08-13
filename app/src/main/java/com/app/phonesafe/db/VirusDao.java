package com.app.phonesafe.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 14501_000 on 2016/8/12.
 */
public class VirusDao {
        //1.指定数据库路径
        private static String path="data/data/com.app.phonesafe/files/antivirus.db";
        //2.开启数据库
        public static List<String> getVirusList(){
                SQLiteDatabase db=SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READONLY);
                Cursor cursor=db.query("datable",new String[]{"md5"},null,null,null,null,null);
                List<String> virusList=new ArrayList<String>();
                while(cursor.moveToNext()){
                        virusList.add(cursor.getString(0));
                }
                cursor.close();
                db.close();
                return  virusList;
        }
}

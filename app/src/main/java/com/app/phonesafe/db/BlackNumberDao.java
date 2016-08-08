package com.app.phonesafe.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 14501_000 on 2016/8/8.
 */
public class BlackNumberDao {
    BlackNumberOpenHelper blackNumberOpenHelper;
    List<BlackNumberInfo> blackNumberList;

    //BlackNumberDao单例模式
    //1.私有构造方法
    private BlackNumberDao(Context context) {
        //创建数据库及表结构
        blackNumberOpenHelper = new BlackNumberOpenHelper(context);
    }

    //2.声明当前类对象
    private static BlackNumberDao blackNumberDao = null;

    //3.提供一个静态方法，如果当前类的对象为空，创建一个新的
    public static BlackNumberDao getInstance(Context context) {
        if (blackNumberDao == null) {
            blackNumberDao = new BlackNumberDao(context);
        }
        return blackNumberDao;
    }

    /**
     * 增加条目
     *
     * @param phone 拦截的电话号码
     * @param mode  拦截类型（1：短信  2：电话 3：短信及电话）
     */
    public void insert(String phone, String mode) {
        //开启数据库准备写入操作
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("phone", phone);
        value.put("mode", mode);
        db.insert("blacknumber", null, value);
        db.close();
    }

    /**
     * 删除条目
     *
     * @param phone 要删除的电话号码
     */
    public void delete(String phone) {
        //开启数据库准备写入操作
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        db.delete("blacknumber", "phone=?", new String[]{phone});
        db.close();
    }

    /**
     * 修改一个条目
     *
     * @param phone
     * @param mode
     */
    public void update(String phone, String mode) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("mode", mode);
        db.update("blacknumber", value, "phone=?", new String[]{phone});
        db.close();
    }

    /**
     * 查询所有数据
     */
    public List<BlackNumberInfo> findAll() {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"phone", "mode"}, null, null, null, null, "_id desc");
        blackNumberList = new ArrayList<BlackNumberInfo>();
        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setPhone(cursor.getString(0));
            blackNumberInfo.setMode(cursor.getString(1));
            blackNumberList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberList;
    }

    /**
     * 查询20条数据
     * @param index 查询的索引值
     * @return
     */
    public List<BlackNumberInfo> find(int index){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        Cursor cursor=db.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,20",
                new String[]{index+""});
        List<BlackNumberInfo> blackNumberList=new ArrayList<BlackNumberInfo>();
        while(cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo=new BlackNumberInfo();
            blackNumberInfo.setPhone(cursor.getString(0));
            blackNumberInfo.setMode(cursor.getString(1));
            blackNumberList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberList;
    }
    /**
     * @return	数据库中数据的总条目个数,返回0代表没有数据或异常
     */
    public int getCount(){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        int count = 0;
        Cursor cursor = db.rawQuery("select count(*) from blacknumber;", null);
        if(cursor.moveToNext()){
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }



}

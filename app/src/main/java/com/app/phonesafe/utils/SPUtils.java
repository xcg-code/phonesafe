package com.app.phonesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 14501_000 on 2016/8/2.
 */
public class SPUtils  {
    private static SharedPreferences sp;
    /**
     *写入boolean类型变量到文件
     * @param context 上下文环境
     * @param key 存储节点名称
     * @param value 存储节点值
     */
    public static void putBoolean(Context context,String key,boolean value){
        if(sp==null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);//(存储节点文件名称，读写方式)
        }
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean(key,value).commit();
    }
    public static void putString(Context context,String key,String value){
        if(sp==null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);//(存储节点文件名称，读写方式)
        }
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(key,value).commit();
    }

    /**
     *读取boolean类型变量
     * @param context 上下文环境
     * @param key   存储节点名称
     * @param defValue  默认值或读取到的值
     * @return
     */
    public static boolean getBoolean(Context context,String key,boolean defValue){
        if(sp==null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);//(存储节点文件名称，读写方式)
        }
        return sp.getBoolean(key,defValue);
    }
    public static String getString(Context context,String key,String defValue){
        if(sp==null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);//(存储节点文件名称，读写方式)
        }
        return sp.getString(key,defValue);

    }
}

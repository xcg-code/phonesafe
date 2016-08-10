package com.app.phonesafe.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by 14501_000 on 2016/8/8.
 */
public class SmsBackUp {
    private static int index=0;
    //备份短信方法
    public static void backup(Context context, String path, CallBack callBack){
        FileOutputStream fos=null;
        Cursor cursor=null;
        try {
            //1,获取备份短信写入的文件
            File file=new File(path);
            //2,获取内容解析器,获取短信数据库中数据
            cursor = context.getContentResolver().query(Uri.parse("content://sms/"),
                    new String[]{"address", "date", "type", "body"}, null, null, null);
            //3,文件相应的输出流
            fos=new FileOutputStream(file);
            //4,序列化数据库中读取的数据,放置到xml中
            XmlSerializer newSerialzer= Xml.newSerializer();
            //5.给此xml做相应设置
            newSerialzer.setOutput(fos,"utf-8");
            //DTD(xml规范
            newSerialzer.startDocument("utf-8",true);
            newSerialzer.startTag(null,"smss");

            //6,备份短信总数指定
            if(callBack!=null){
                callBack.setMax(cursor.getCount());
            }
            //7,读取数据库中的每一行的数据写入到xml中
            while(cursor.moveToNext()){
                newSerialzer.startTag(null,"smss");

                newSerialzer.startTag(null,"address");
                newSerialzer.text(cursor.getString(0));
                newSerialzer.endTag(null,"address");

                newSerialzer.startTag(null,"date");
                newSerialzer.text(cursor.getString(1));
                newSerialzer.endTag(null,"date");

                newSerialzer.startTag(null,"type");
                newSerialzer.text(cursor.getString(2));
                newSerialzer.endTag(null,"type");

                newSerialzer.startTag(null,"body");
                newSerialzer.text(cursor.getString(3));
                newSerialzer.endTag(null,"body");


                //8,每循环一次就需要去让进度条叠加
                index++;
                Thread.sleep(500);

                //ProgressDialog可以在子线程中更新相应的进度条的改变
                if(callBack!=null) {
                    callBack.setPrograss(index);
                }
            }
            newSerialzer.endTag(null,"smss");
            newSerialzer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
                try {
                    if(cursor!=null && fos!=null) {
                        cursor.close();
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

    }

    //回调
    //1.定义一个接口
    //2,定义接口中未实现的业务逻辑方法(短信总数设置,备份过程中短信百分比更新)
    //3.传递一个实现了此接口的类的对象(至备份短信的工具类中),接口的实现类,一定实现了上诉两个为实现方法(就决定了使用对话框,还是进度条)
    //4.获取传递进来的对象,在合适的地方(设置总数,设置百分比的地方)做方法的调用
    public interface CallBack{
        //短信总数设置为实现方法
        public void setMax(int Max);
        //备份过程中短信百分比更新
        public void setPrograss(int index);
    }
}

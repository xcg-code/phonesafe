package com.app.phonesafe.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.app.phonesafe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 14501_000 on 2016/8/4.
 */
public class ContactListActivity extends Activity {
    private ListView lv_contact;
    MyAdapter adapter;
    private List<HashMap<String,String>> contactList=new ArrayList<HashMap<String, String>>();

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter=new MyAdapter();
            lv_contact.setAdapter(adapter);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initUI();
        initData();
    }

    private void initUI() {
        lv_contact= (ListView) findViewById(R.id.lv_contact);
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(adapter!=null){
                    HashMap<String,String> hashMap=adapter.getItem(position);
                    String phone=hashMap.get("phone");

                    //将电话号码返回
                    Intent intent=new Intent();
                    intent.putExtra("phone",phone);
                    setResult(0,intent);
                    finish();
                }
            }
        });
    }

    /***
     * 获取系统联系人数据
     */
    private void initData() {
        //读取系统联系人为耗时操作，放到子线程处理
        new Thread(){
            @Override
            public void run() {
                super.run();
                //1.获取内容解析器对象
                ContentResolver contentResolover=getContentResolver();
                //2,查询系统联系人数据库（读取联系人权限）
                Cursor cursor=contentResolover.query(
                        Uri.parse("content://com.android.contacts/raw_contacts"),
                        new String[]{"contact_id"},
                        null,null,null);
                contactList.clear();
                //3.循环游标，直到没有数据为止
                while(cursor.moveToNext()){
                    String id=cursor.getString(0);
                    //根据用户唯一id，查询data表和mimetype表生成的视图，获取data及mimetype字段
                    Cursor indexCursor=contentResolover.query(
                            Uri.parse("content://com.android.contacts/data"),
                            new String[]{"data1","mimetype"},
                            "raw_contact_id=?",new String[]{id},null);
                    //循环获取每一个联系人的电话号码及姓名，数据类型
                    HashMap<String,String> hashMap=new HashMap<String,String>();
                    while(indexCursor.moveToNext()){
                        String data=indexCursor.getString(0);
                        String type=indexCursor.getString(1);
//                        Log.i("777","data="+indexCursor.getString(0));
//                        Log.i("777","type="+indexCursor.getString(1));

                        //区分类型给hashMap赋值
                        if(type.equals("vnd.android.cursor.item/phone_v2")){
                            if(!TextUtils.isEmpty(data)){
                                hashMap.put("phone",data);
                            }
                        }else if(type.equals("vnd.android.cursor.item/name")){
                            if(!TextUtils.isEmpty(data)){
                                hashMap.put("name",data);
                            }
                        }
                    }
                    indexCursor.close();
                    contactList.add(hashMap);
                }
                cursor.close();
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return contactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=View.inflate(getApplicationContext(),R.layout.contact_item,null);

            TextView tv_name= (TextView) view.findViewById(R.id.tv_contact_name);
            TextView tv_num= (TextView) view.findViewById(R.id.tv_contact_num);

            tv_name.setText(getItem(position).get("name"));
            tv_num.setText(getItem(position).get("phone"));
            return view;
        }
    }

}

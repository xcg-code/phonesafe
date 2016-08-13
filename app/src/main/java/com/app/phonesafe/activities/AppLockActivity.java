package com.app.phonesafe.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.phonesafe.R;
import com.app.phonesafe.db.AppInfo;
import com.app.phonesafe.db.AppLockDao;
import com.app.phonesafe.engine.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 14501_000 on 2016/8/11.
 */
public class AppLockActivity extends Activity {
    private Button bt_unlock,bt_lock;
    private LinearLayout ll_unlock,ll_lock;
    private TextView tv_unlock,tv_lock;
    private ListView lv_unlock,lv_lock;
    private List<AppInfo> mAppInfoList;
    private List<AppInfo> mLockList;
    private List<AppInfo> mUnLockList;
    private AppLockDao mDao;

    private MyAdapter mLockAdapter;
    private MyAdapter mUnLockAdapter;

    private TranslateAnimation mTranslateAnimation;

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            //6.接收到消息,填充已加锁和未加锁的数据适配器
            mLockAdapter = new MyAdapter(true);
            lv_lock.setAdapter(mLockAdapter);

            mUnLockAdapter = new MyAdapter(false);
            lv_unlock.setAdapter(mUnLockAdapter);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        initUI();
        initData();
    }

    private void initUI() {
        bt_lock = (Button) findViewById(R.id.bt_lock);
        bt_unlock = (Button) findViewById(R.id.bt_unlock);

        ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
        ll_lock = (LinearLayout) findViewById(R.id.ll_lock);

        tv_unlock = (TextView) findViewById(R.id.tv_unlock);
        tv_lock = (TextView) findViewById(R.id.tv_lock);

        lv_unlock = (ListView) findViewById(R.id.lv_unlock);
        lv_lock = (ListView) findViewById(R.id.lv_lock);

        bt_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.已加锁列表隐藏,未加锁列表显示
                ll_lock.setVisibility(View.GONE);
                ll_unlock.setVisibility(View.VISIBLE);
                //2.未加锁变成深色图片,已加锁变成浅色图片
                bt_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
                bt_lock.setBackgroundResource(R.drawable.tab_right_default);
            }
        });

        bt_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.已加锁列表显示,未加锁列表隐藏
                ll_lock.setVisibility(View.VISIBLE);
                ll_unlock.setVisibility(View.GONE);
                //2.未加锁变成浅色图片,已加锁变成深色图片
                bt_unlock.setBackgroundResource(R.drawable.tab_left_default);
                bt_lock.setBackgroundResource(R.drawable.tab_right_pressed);
            }
        });
        lv_unlock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo appInfo = mUnLockAdapter.getItem(position);
                //未加锁------>已加锁过程
                //1.已加锁集合添加一个,未加锁集合移除一个,对象就是getItem方法获取的对象
                mLockList.add(appInfo);
                mUnLockList.remove(appInfo);
                //2.从已加锁的数据库中插入一条数据
                mDao.insert(appInfo.packageName);
                //3.刷新数据适配器
                mUnLockAdapter.notifyDataSetChanged();
            }
        });
       lv_lock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               AppInfo appInfo = mLockAdapter.getItem(position);
               //已加锁------>未加锁过程
               //1.已加锁集合删除一个,未加锁集合添加一个,对象就是getItem方法获取的对象
               mLockList.remove(appInfo);
               mUnLockList.add(appInfo);
               //2.从已加锁的数据库中删除一条数据
               mDao.delete(appInfo.packageName);
               //3.刷新数据适配器
               mLockAdapter.notifyDataSetChanged();
           }
       });
    }

    private void initData() {
        new Thread(){
            public void run() {
                //1.获取所有手机中的应用
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                //2.区分已加锁应用和未加锁应用
                mLockList = new ArrayList<AppInfo>();
                mUnLockList = new ArrayList<AppInfo>();

                //3.获取数据库中已加锁应用包名的的结合
                mDao = AppLockDao.getInstance(getApplicationContext());
                List<String> lockPackageList = mDao.findAll();
                for (AppInfo appInfo : mAppInfoList) {
                    //4,如果循环到的应用的包名,在数据库中,则说明是已加锁应用
                    if(lockPackageList.contains(appInfo.packageName)){
                        mLockList.add(appInfo);
                    }else{
                        mUnLockList.add(appInfo);
                    }
                }
                //5.告知主线程,可以使用维护的数据
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    class MyAdapter extends BaseAdapter {
        private boolean isLock;
        /**
         * @param isLock	用于区分已加锁和未加锁应用的标示	true已加锁数据适配器	false未加锁数据适配器
         */
        public MyAdapter(boolean isLock) {
            this.isLock = isLock;
        }
        @Override
        public int getCount() {
            if(isLock){
                tv_lock.setText("已加锁应用:"+mLockList.size());
                return mLockList.size();
            }else{
                tv_unlock.setText("未加锁应用:"+mUnLockList.size());
                return mUnLockList.size();
            }
        }

        @Override
        public AppInfo getItem(int position) {
            if(isLock){
                return mLockList.get(position);
            }else{
                return mUnLockList.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = View.inflate(getApplicationContext(), R.layout.listview_islock_item, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            final AppInfo appInfo = getItem(position);
            holder.iv_icon.setBackgroundDrawable(appInfo.icon);
            holder.tv_name.setText(appInfo.name);
            return convertView;
        }
    }
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
    }

}

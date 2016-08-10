package com.app.phonesafe.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.app.phonesafe.Config;
import com.app.phonesafe.R;
import com.app.phonesafe.service.AddressService;
import com.app.phonesafe.service.BlackNumberService;
import com.app.phonesafe.service.RocketService;
import com.app.phonesafe.utils.SPUtils;
import com.app.phonesafe.utils.ServiceUtil;
import com.app.phonesafe.view.SettingClickView;
import com.app.phonesafe.view.SettingItemView;

/**
 * Created by 14501_000 on 2016/8/1.
 */
public class SettingActivity extends Activity {
    SettingItemView siv_update;
    SettingItemView siv_location;
    SettingItemView siv_rocket;
    SettingItemView siv_blacknumber;
    SettingClickView scv_toast_style;
    SettingClickView scv_location;
    private String[] mToastStyleDes;
    private int toastStyle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initUpdate();
        initAddress();
        initRocket();
        initToastStyle();
        initLocation();
        initBlackNumber();
    }

    private void initBlackNumber() {
        siv_blacknumber= (SettingItemView) findViewById(R.id.siv_blacknumber);
        //对服务是否开的状态做显示
        boolean isRunning= ServiceUtil.isRunning(this,"com.app.phonesafe.service.BlackNumberService");
        siv_blacknumber.setText(isRunning);
        siv_blacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check=siv_blacknumber.isCheck();
                siv_blacknumber.setText(!check);
                if(!check){
                    //开启服务,管理吐司
                    startService(new Intent(getApplicationContext(), BlackNumberService.class));
                }else{
                    //关闭服务，取消吐司显示
                    stopService(new Intent(getApplicationContext(),BlackNumberService.class));
                }
            }
        });
    }

    private void initRocket() {
        siv_rocket= (SettingItemView) findViewById(R.id.siv_rocket);
        //对服务是否开的状态做显示
        boolean isRunning= ServiceUtil.isRunning(this,"com.app.phonesafe.service.RocketService");
        siv_rocket.setText(isRunning);
        siv_rocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check=siv_rocket.isCheck();
                siv_rocket.setText(!check);
                if(!check){
                    //开启悬浮火箭服务
                    startService(new Intent(getApplicationContext(),RocketService.class));
                }else{
                    //关闭悬浮火箭服务
                    stopService(new Intent(getApplicationContext(),RocketService.class));
                }
            }
        });
    }

    private void initLocation() {
        scv_location= (SettingClickView) findViewById(R.id.scv_location);
        scv_location.setTitle("归属地提示框的位置");
        scv_location.setDes("设置归属地提示框的位置");
        scv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ToastLocationActivity.class));
            }
        });
    }

    private void initToastStyle() {
        scv_toast_style= (SettingClickView) findViewById(R.id.scv_toast_style);
        scv_toast_style.setTitle("设置归属地显示风格");
        mToastStyleDes=new String[]{"透明","橙色","蓝色","灰色","绿色"};
        toastStyle=SPUtils.getInt(getApplicationContext(),Config.TOAST_STYLE,0);
        scv_toast_style.setDes(mToastStyleDes[toastStyle]);
        //监听点击事件
        Log.i("112","位置1");
        scv_toast_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示吐司样式对话框
                showToastStyleDialog();
            }
        });
    }

    private void showToastStyleDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("选择样式");
        //选择单个条目事件监听
		/*
		 * 1:string类型的数组描述颜色文字数组
		 * 2:弹出对画框的时候的选中条目索引值
		 * 3:点击某一个条目后触发的点击事件
		 * */
        builder.setSingleChoiceItems(mToastStyleDes, toastStyle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scv_toast_style.setDes(mToastStyleDes[which]);
                SPUtils.putInt(getApplicationContext(),Config.TOAST_STYLE,which);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 自动更新设置
     */
    private void initUpdate() {
        siv_update= (SettingItemView) findViewById(R.id.siv_update);
        //获取已有的开关状态，用作显示
        boolean open_update=SPUtils.getBoolean(this, Config.OPEN_UPDATE,false);
        //回显过程
        siv_update.setText(open_update);
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check=siv_update.isCheck();
                siv_update.setText(!check);
                SPUtils.putBoolean(getApplicationContext(),Config.OPEN_UPDATE,!check);
            }
        });
    }


    /**
     * 归属地显示设置
     */
    public void initAddress(){
        siv_location= (SettingItemView) findViewById(R.id.siv_findAddr);
        //对服务是否开的状态做显示
        boolean isRunning= ServiceUtil.isRunning(this,"com.app.phonesafe.service.AddressService");
       // boolean open_location=SPUtils.getBoolean(this,Config.OPEN_LOCATION,false);
        siv_location.setText(isRunning);
        siv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check=siv_location.isCheck();
                siv_location.setText(!check);
                //SPUtils.putBoolean(getApplication(),Config.OPEN_LOCATION,!check);
                if(!check){
                    //开启服务,管理吐司
                    startService(new Intent(getApplicationContext(),AddressService.class));
                }else{
                    //关闭服务，取消吐司显示
                    stopService(new Intent(getApplicationContext(),AddressService.class));
                }
            }
        });
    }
}

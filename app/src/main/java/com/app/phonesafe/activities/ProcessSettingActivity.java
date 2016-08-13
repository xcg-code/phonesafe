package com.app.phonesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.app.phonesafe.Config;
import com.app.phonesafe.R;
import com.app.phonesafe.service.LockScreenService;
import com.app.phonesafe.utils.SPUtils;
import com.app.phonesafe.utils.ServiceUtil;

/**
 * Created by 14501_000 on 2016/8/10.
 */
public class ProcessSettingActivity extends Activity{
    private CheckBox cb_show_system,cb_lock_clear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);
        initSystemShow();
        initLockScreenClear();
    }

    private void initSystemShow() {
        cb_show_system= (CheckBox) findViewById(R.id.cb_show_system);
        boolean showSyetem= SPUtils.getBoolean(getApplicationContext(), Config.SHOW_SYSTEM,false);
        cb_show_system.setChecked(showSyetem);
        if(showSyetem){
            cb_show_system.setText("显示系统进程");
        }else{
            cb_show_system.setText("隐藏系统进程");
        }
        cb_show_system.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cb_show_system.setText("显示系统进程");
                }else{
                    cb_show_system.setText("隐藏系统进程");
                }
                SPUtils.putBoolean(ProcessSettingActivity.this,Config.SHOW_SYSTEM, isChecked);
            }
        });
    }


    /**
     * 锁屏清理
     */
    private void initLockScreenClear() {
        cb_lock_clear= (CheckBox) findViewById(R.id.cb_lock_clear);
        boolean isRunning= ServiceUtil.isRunning(this,"com.app.phonesafe.service.LockScreenService");
        cb_lock_clear.setChecked(isRunning);
        if(isRunning){
            cb_lock_clear.setText("锁屏清理已开启");
        }else{
            cb_lock_clear.setText("锁屏清理已关闭");
        }
        cb_lock_clear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cb_lock_clear.setText("锁屏清理已开启");
                    //开启服务
                    startService(new Intent(getApplicationContext(), LockScreenService.class));
                }else{
                    cb_lock_clear.setText("锁屏清理已关闭");
                    //关闭服务
                    stopService(new Intent(getApplicationContext(), LockScreenService.class));
                }
            }
        });
    }
}

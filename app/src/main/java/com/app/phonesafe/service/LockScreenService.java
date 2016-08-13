package com.app.phonesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.app.phonesafe.engine.ProcessInfoProvider;

/**
 * Created by 14501_000 on 2016/8/10.
 */
public class LockScreenService extends Service {
    private IntentFilter intentFilter;
    private  InnerReceiver innerReceiver;
    @Override
    public void onCreate() {
        super.onCreate();
        intentFilter=new IntentFilter(Intent.ACTION_SCREEN_OFF);
        innerReceiver=new InnerReceiver();
        registerReceiver(innerReceiver,intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(innerReceiver!=null){
            unregisterReceiver(innerReceiver);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class InnerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //清理手机正在运行的进程
            ProcessInfoProvider.killAll(getApplicationContext());
        }
    }
}

package com.app.phonesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.app.phonesafe.engine.ProcessInfoProvider;

/**
 * Created by 14501_000 on 2016/8/11.
 */
public class KillProcessReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //杀死进程
        ProcessInfoProvider.killAll(context);
    }
}

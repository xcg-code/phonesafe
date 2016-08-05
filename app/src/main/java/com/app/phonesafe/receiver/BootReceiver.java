package com.app.phonesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.app.phonesafe.Config;
import com.app.phonesafe.utils.SPUtils;


/**
 * Created by 14501_000 on 2016/8/5.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //1,获取开机后手机的sim卡的序列号
        TelephonyManager tm= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber=tm.getSimSerialNumber();
        //2.获取sp存储的sim序列号
        String sp_sim_num= SPUtils.getString(context, Config.SIM_NUMBER,"");
        //3.进行比对
        if(!simSerialNumber.equals(sp_sim_num)){
            //4.发送短信到选中的联系人手机（发送短信权限）
            SmsManager sms=SmsManager.getDefault();
            String phone=SPUtils.getString(context,Config.PHONE_NUMBER,"");
            sms.sendTextMessage(phone,null,"sim changed!",null,null);
        }
    }
}

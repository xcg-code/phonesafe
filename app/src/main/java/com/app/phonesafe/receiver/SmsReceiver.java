package com.app.phonesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.app.phonesafe.Config;
import com.app.phonesafe.R;
import com.app.phonesafe.service.LocationService;
import com.app.phonesafe.utils.SPUtils;

/**
 * Created by 14501_000 on 2016/8/5.
 */
public class SmsReceiver extends BroadcastReceiver {
    DevicePolicyManager mDPM;
    @Override
    public void onReceive(Context context, Intent intent) {
        mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        //1,判断是否开启了防盗保护
        boolean open_security = SPUtils.getBoolean(context, Config.OPEN_SAFE_STATE, false);
        if(open_security){
            //2,获取短信内容
            Object[] objects= (Object[]) intent.getExtras().get("pdus");
            //3,循环遍历短信过程
            for(Object object:objects){
                //4,获取短信对象
                SmsMessage sms=SmsMessage.createFromPdu((byte[])object);
                //5,获取短信对象的基本信息
                String originnatingAddress=sms.getOriginatingAddress();
                String messageBody=sms.getMessageBody();

                //6,判断是否包含播放音乐的关键字
                if(messageBody.contains("#*alarm*#")){
                    //7,播放音乐(准备音乐,MediaPlayer)
                    MediaPlayer mediaPlayer=MediaPlayer.create(context, R.raw.ylzs);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }

                if(messageBody.contains("#*location*#")){
                    //开启获取位置服务
                    context.startService(new Intent(context, LocationService.class));
                }

                if(messageBody.contains("#*lockscreen*#")){
                    //事先已将应用设备管理器激活
                        mDPM.lockNow();
                        //锁屏同时去设置密码
                        mDPM.resetPassword("123", 0);
                }

                if(messageBody.contains("#*wipedata*#")){
                    //mDPM.wipeData(0);//手机数据
                    //mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);//手机sd卡数据
                }


            }
        }
    }
}

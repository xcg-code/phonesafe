package com.app.phonesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by 14501_000 on 2016/8/6.
 */
public class ServiceUtil  {
    /**
     *
     * @param context 上下文环境
     * @param serviceName 服务名称
     * @return
     */
    public static boolean isRunning(Context context,String serviceName){
        //1,获取ActivityMananger管理者对象,可以去获取当前手机正在运行的所有服务
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //2,获取手机中正在运行的服务集合(多少个服务)
        List<ActivityManager.RunningServiceInfo> runningServiceInfos=am.getRunningServices(1000);
        //3,遍历获取的所有的服务集合,拿到每一个服务的类的名称,和传递进来的类的名称作比对,如果一致,说明服务正在运行
        for(ActivityManager.RunningServiceInfo runningServiceInfo:runningServiceInfos){
            //4,获取每一个真正运行服务的名称
            if(serviceName.equals(runningServiceInfo.service.getClassName())){
                return true;
            }
        }
        return  false;
    }
}

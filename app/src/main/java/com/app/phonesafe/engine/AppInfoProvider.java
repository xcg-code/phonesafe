package com.app.phonesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.app.phonesafe.db.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 14501_000 on 2016/8/10.
 */
public class AppInfoProvider {
    /**
     * 返回当前手机所有应用信息(名称,包名,图标,(手机内存,sd卡),(系统,用户));
     * @param context 上下文环境
     * @return 包含手机安装应用相关信息的集合
     */
    public static List<AppInfo> getAppInfoList(Context context){
        //1.包管理者对象
       PackageManager pm= context.getPackageManager();
        //2.获取应用相关信息集合
        List<PackageInfo> packageInfoList=pm.getInstalledPackages(0);
        List<AppInfo> appInfoList=new ArrayList<AppInfo>();
        //3.循环遍历集合
        for(PackageInfo packageInfo:packageInfoList){
            AppInfo appInfo=new AppInfo();
            //4.获取应用包名
            appInfo.packageName=packageInfo.packageName;
            //5.应用名称
            ApplicationInfo applicationInfo=packageInfo.applicationInfo;
            appInfo.name=applicationInfo.loadLabel(pm).toString();
            //6，获取图标
            appInfo.icon=applicationInfo.loadIcon(pm);
            //7.判断是否为系统应用
            if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
                //系统应用
                appInfo.isSystem=true;
            }else{
                //非系统应用
                appInfo.isSystem=false;
            }
            //8.是否为sd卡安装应用
            if((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)==ApplicationInfo.FLAG_EXTERNAL_STORAGE){
                appInfo.isSdCard=true;
            }else{
                appInfo.isSdCard=false;
            }
            appInfoList.add(appInfo);
        }
        return appInfoList;
    }
}

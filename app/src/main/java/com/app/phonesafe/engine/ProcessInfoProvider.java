package com.app.phonesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.app.phonesafe.R;
import com.app.phonesafe.db.ProcessInfo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 14501_000 on 2016/8/10.
 */
public class ProcessInfoProvider {
    //获取进程总数的方法
    public static int getProcessCount(Context context){
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfoList=am.getRunningAppProcesses();
        return processInfoList.size();
    }
    /**
     * @param context
     * @return 返回可用的内存数	bytes
     */
    public static long getAvailSpace(Context context){
        //1,获取activityManager
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //2,构建存储可用内存的对象
        ActivityManager.MemoryInfo memoryInfo=new ActivityManager.MemoryInfo();
        //3,给memoryInfo对象赋(可用内存)值
        am.getMemoryInfo(memoryInfo);
        //4,获取memoryInfo中相应可用内存大小
        return  memoryInfo.availMem;
    }

    /**
     *
     * @param context
     * @return 返回总的内存数	单位为bytes 返回0说明异常
     */
    public static long getTotalSpace(Context context){
        //内存大小写入文件中,读取proc/meminfo文件,读取第一行,获取数字字符,转换成bytes返回
        FileReader fileReader=null;
        BufferedReader bufferedReader=null;
        try {
            fileReader=new FileReader("proc/meminfo");
            bufferedReader=new BufferedReader(fileReader);
            String line=bufferedReader.readLine();
            char[] charArray=line.toCharArray();
            StringBuffer sb=new StringBuffer();
            for(char c:charArray){
                if(c>='0' && c<='9'){
                    sb.append(c);
                }
            }
            return Long.parseLong(sb.toString())*1024;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
                try {
                    if(fileReader!=null && bufferedReader!=null) {
                        fileReader.close();
                        bufferedReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
        return 0;
    }

    /**
     *
     * @param context
     * @return 当前手机正在运行的进程的相关信息
     */
    public static List<ProcessInfo> getProcessInfo(Context context){
        //获取进程相关信息
        List<ProcessInfo> processInfoList = new ArrayList<ProcessInfo>();
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm=context.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcess=am.getRunningAppProcesses();

        for(ActivityManager.RunningAppProcessInfo info:runningAppProcess){
            ProcessInfo processInfo=new ProcessInfo();
            processInfo.packageName=info.processName;
            //获取进程占用的内存大小(传递一个进程对应的pid数组)
            android.os.Debug.MemoryInfo[] processMemoryInfo=am.getProcessMemoryInfo(new int[]{info.pid});
            android.os.Debug.MemoryInfo memoryInfo=processMemoryInfo[0];
            //获取已使用内存的大小
            processInfo.memSize=memoryInfo.getTotalPrivateDirty()*1024;
            try {
                ApplicationInfo applicationInfo=pm.getApplicationInfo(processInfo.packageName,0);
                //获取应用的名称
                processInfo.name=applicationInfo.loadLabel(pm).toString();
                //获取应用的图标
                processInfo.icon=applicationInfo.loadIcon(pm);
                //10,判断是否为系统进程
                if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM){
                    processInfo.isSystem = true;
                }else{
                    processInfo.isSystem = false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                //需要处理
                processInfo.name = info.processName;
                processInfo.icon = context.getResources().getDrawable(R.drawable.ic_launcher);
                processInfo.isSystem = true;
                e.printStackTrace();
            }
            processInfoList.add(processInfo);
        }
        return processInfoList;
    }

    public static void killProcess(Context context,ProcessInfo processInfo){
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //杀死指定包名进程(权限)
        am.killBackgroundProcesses(processInfo.getPackageName());

    }
    /**
     * 杀死所有进程
     * @param ctx	上下文环境
     */
    public static void killAll(Context ctx) {
        //1,获取activityManager
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //2,获取正在运行进程的集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        //3,循环遍历所有的进程,并且杀死
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
            //4,除了手机卫士以外,其他的进程都需要去杀死
            if(info.processName.equals(ctx.getPackageName())){
                //如果匹配上了手机卫士,则需要跳出本次循环,进行下一次寻,继续杀死进程
                continue;
            }
            am.killBackgroundProcesses(info.processName);
        }
    }

}

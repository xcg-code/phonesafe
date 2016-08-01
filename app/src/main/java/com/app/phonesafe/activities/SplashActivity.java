package com.app.phonesafe.activities;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.app.phonesafe.R;
import com.app.phonesafe.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {
    public final static String tag="SplashActivity";
    TextView version;
    int localVersionCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //初始化ui控件
        initUI();
        //初始化数据
        initData();
    }

    private void initData() {
        //1.获取版本名称
        version.setText("版本名称："+getVersionName());
        //2.检测版本更新（通过本地版本号与服务器版本号对比，如果有更新提示用户下载）
        localVersionCode=getVersionCode();
        Log.i("info","--------sajgsyfgasgdys1");
        //3,获取服务器版本号(客户端发请求,服务端给响应,(json,xml))
        //http://www.oxxx.com/update74.json?key=value  返回200 请求成功,流的方式将数据读取下来
        //json中内容包含:
		/* 更新版本的版本名称
		 * 新版本的描述信息
		 * 服务器版本号
		 * 新版本apk下载地址*/
        checkVersion();
    }

    private void checkVersion() {
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url=new URL("http://172.29.141.58:8080/update.json");
                    HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(3000);
                    connection.setReadTimeout(3000);
                    //获取请求成功响应码
                    if(connection.getResponseCode()==200){
                        InputStream in=connection.getInputStream();
                        String respones= StreamUtils.streamToString(in);
                        Log.i("info",respones);
                    }



                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private String getVersionName() {
        //获取包管理者对象
        PackageManager pm=getPackageManager();
        try {
            //获取包中基本信息
           PackageInfo info=pm.getPackageInfo(this.getPackageName(),0);
            //获取版本名称
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    private int getVersionCode() {
        //获取包管理者对象
        PackageManager pm=getPackageManager();
        try {
            //获取包中基本信息
            PackageInfo info=pm.getPackageInfo(this.getPackageName(),0);
            //获取版本号
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void initUI() {
        version= (TextView) findViewById(R.id.tv_version);

    }
}

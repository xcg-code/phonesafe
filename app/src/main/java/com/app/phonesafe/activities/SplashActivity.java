package com.app.phonesafe.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.phonesafe.Config;
import com.app.phonesafe.R;
import com.app.phonesafe.utils.SPUtils;
import com.app.phonesafe.utils.StreamUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {
    public final static String tag="SplashActivity";
    TextView version;
    int localVersionCode;
    RelativeLayout root;
    protected static final int UPDATE_VERSION = 100;//更新新版本的状态码
    protected static final int ENTER_HOME = 101; //进入应用程序主界面状态码
    /**
     * url地址出错状态码
     */
    protected static final int URL_ERROR = 102;
    protected static final int IO_ERROR = 103;
    protected static final int JSON_ERROR = 104;

    String versionDes;
    String downloadURL;

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case UPDATE_VERSION:
                    //弹出对话框
                    showUpdateDialog();break;
                case ENTER_HOME:
                    enterHome();break;
                case URL_ERROR:
                    Toast.makeText(SplashActivity.this,"URL异常",Toast.LENGTH_SHORT).show();
                    enterHome();break;
                case IO_ERROR:
                    Toast.makeText(SplashActivity.this,"IO异常",Toast.LENGTH_SHORT).show();
                    enterHome();break;
                case JSON_ERROR:
                    Toast.makeText(SplashActivity.this,"JSON异常",Toast.LENGTH_SHORT).show();
                    enterHome();break;
                default:
                    enterHome();
                    break;

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        boolean sp=SPUtils.getBoolean(getApplicationContext(),Config.OPEN_UPDATE,false);
        //初始化ui控件
        initUI();
        //初始化数据
        initData();
        //画面淡入淡出
        initAnimation();

        //初始化数据库
        initDatabase();
        if(!SPUtils.getBoolean(getApplicationContext(),Config.HAS_SHORTCUT,false)){
            //生成快捷方式
            initShortCut();
        }
    }

    private void initShortCut() {
        //1.给intent维护图标，数据
        Intent intent=new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
                BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher));
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"手机卫士");
        //2.点击快捷方式跳转
        Intent intent1=new Intent("android.intent.action.HOME");
        intent1.addCategory("android.intent.category.DEFAULT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,intent1);
        //3.发送广播
        sendBroadcast(intent);
        SPUtils.putBoolean(getApplicationContext(),Config.HAS_SHORTCUT,true);
    }

    private void initDatabase() {
        //1.归属地数据库拷贝
        initAddressDB("address.db");
        //2.常用号码数据库
        initAddressDB("commonnum.db");
        initAddressDB("antivirus.db");
    }

    /**
     * 拷贝数据库至file文件夹下
     * @param dbName 数据库名称
     */
    private void initAddressDB(String dbName) {
        //1,在files文件夹下创建同名dbName数据库文件过程
        File files=getFilesDir();//获取/data/data//files目录
        File file=new File(files,dbName);//创建名为dbName的文件
        if(file.exists()){
            return;
        }
        //2.输入流读取第三方资产目录下的文件
        InputStream stream=null;
        FileOutputStream fos=null;
        try {
            stream=getAssets().open(dbName);
            //3,将读取的内容写入到指定文件夹的文件中去
            fos=new FileOutputStream(file);
            byte[] bs=new byte[1024];
            int temp=-1;
            while((temp=stream.read(bs))!=-1){
                fos.write(bs,0,temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(stream!=null&&fos!=null){
                try {
                    stream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initAnimation() {
        AlphaAnimation alpha=new AlphaAnimation(0,1);
        alpha.setDuration(3000);
        root.startAnimation(alpha);
    }

    private void showUpdateDialog() {
        //对话框，是依赖于activity而存在的
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        //设置左上角图标
        builder.setIcon(R.drawable.ic_launcher);
        //设置标题
        builder.setTitle("版本更新");
        //设置内容
        builder.setMessage(versionDes);
        builder.setCancelable(false);
        builder.setPositiveButton("立即更新",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadApk();//下载apk
            }
        });
        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //点击取消，进入主页面
                enterHome();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void enterHome() {
        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void downloadApk() {
        //apk下载链接地址,放置apk的所在路径

        //1,判断sd卡是否可用,是否挂载上
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //2.获取sd卡路径
            String path=Environment.getExternalStorageDirectory().getAbsolutePath()+
                    File.separator+"phoneSafe.apk";
            //3.发送请求，获取apk并放在指定位置
            HttpUtils httpUtils=new HttpUtils();
            //4,发送请求,传递参数(下载地址,下载应用放置位置)
            httpUtils.download(downloadURL, path, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    Log.i(tag, "下载成功");
                    File file = responseInfo.result;//下载成功后的文件位置(apk放置在sd卡中)
                    //提示用户安装,文件作为数据源
                    installApk(file);
                }
                @Override
                public void onFailure(HttpException error, String msg) {
                    Log.i(tag, "下载失败");//下载失败
                    Toast.makeText(SplashActivity.this,"下载安装包失败",Toast.LENGTH_SHORT).show();
                    enterHome();
                }
                //刚刚开始下载方法
                @Override
                public void onStart() {
                    Log.i(tag, "开始下载........");
                    Log.i(tag, "URL:........"+downloadURL);
                    super.onStart();
                }

                //下载过程中的方法(下载apk总大小,当前的下载位置,是否正在下载)
                @Override
                public void onLoading(long total, long current,boolean isUploading) {
                    Log.i(tag, "下载中........");
                    Log.i(tag, "total = "+total);
                    Log.i(tag, "current = "+current);
                    super.onLoading(total, current, isUploading);
                }
            });
        }
    }

    private void installApk(File file) {
        //系统应用界面，源码，安装apk入口
        Intent intent=new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        //设置安装类型
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        startActivityForResult(intent,0);
    }
    //开启一个activity后,返回结果调用的方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();//安装时点击取消，调用方法
        super.onActivityResult(requestCode, resultCode, data);
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
        if(SPUtils.getBoolean(getApplicationContext(), Config.OPEN_UPDATE,false)){
            checkVersion();
        }else{
            mHandler.sendEmptyMessageDelayed(ENTER_HOME,2000);
        }
    }

    private void checkVersion() {
        final Message mes=Message.obtain();
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
                        Log.i(tag,respones);
                        //JSON解析
                        JSONObject jsonObject=new JSONObject(respones);
                        String versionName=jsonObject.getString("versionName");
                        String versionCode=jsonObject.getString("versionCode");
                        versionDes=jsonObject.getString("versionDes");
                        downloadURL=jsonObject.getString("downloadURL");
                        Log.i(tag,versionName);
                        Log.i(tag,versionCode);
                        Log.i(tag,versionDes);
                        Log.i(tag,downloadURL);
                        if(localVersionCode<Integer.parseInt(versionCode)){
                            mes.what=UPDATE_VERSION;
                        }else{
                            mes.what=ENTER_HOME;
                        }
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    mes.what=URL_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    mes.what=IO_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    mes.what=JSON_ERROR;
                }finally {
                    mHandler.sendMessage(mes);
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
        root= (RelativeLayout) findViewById(R.id.rl_root);
    }
}

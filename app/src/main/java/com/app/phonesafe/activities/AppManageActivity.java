package com.app.phonesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.app.phonesafe.R;
import com.app.phonesafe.db.AppInfo;
import com.app.phonesafe.engine.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 14501_000 on 2016/8/9.
 */
public class AppManageActivity  extends Activity implements View.OnClickListener{
    ListView lv_app;
    TextView  tv_des;
    List<AppInfo> mAppInfoList;
    List<AppInfo> mSystemList;
    List<AppInfo> mCustomerList;
    MyAdapter adapter;
    private AppInfo appInfo;
    PopupWindow popupWindow;
    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter=new MyAdapter();
            lv_app.setAdapter(adapter);
            if(tv_des!=null && mCustomerList!=null){
                tv_des.setText("用户应用("+mCustomerList.size()+")");
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manage);
        initTitle();
        intiList();
    }

    private void intiList() {
        lv_app= (ListView) findViewById(R.id.lv_app);
        tv_des= (TextView) findViewById(R.id.tv_des);
        new Thread(){
            @Override
            public void run() {
                super.run();
                mAppInfoList= AppInfoProvider.getAppInfoList(getApplicationContext());
                mSystemList=new ArrayList<AppInfo>();
                mCustomerList=new ArrayList<AppInfo>();
                for(AppInfo appInfo:mAppInfoList){
                    if(appInfo.isSystem){
                        //系统应用
                        mSystemList.add(appInfo);
                    }else{
                        //用户应用
                        mCustomerList.add(appInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();

        lv_app.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //滚动过程中调用方法
                //AbsListView中view就是listView对象
                //firstVisibleItem第一个可见条目索引值
                //visibleItemCount当前一个屏幕的可见条目数
                //总共条目总数
                if(mCustomerList!=null && mSystemList!=null){
                    if(firstVisibleItem>=mCustomerList.size()+1){
                        //滚动到了系统条目
                        tv_des.setText("系统应用("+mSystemList.size()+")");
                    }else{
                        //滚动到了用户应用条目
                        tv_des.setText("用户应用("+mCustomerList.size()+")");
                    }
                }
            }
        });
        lv_app.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0 ||position==mCustomerList.size()+1){
                    return;
                }else{
                    if(position<mCustomerList.size()+1){
                        appInfo=mCustomerList.get(position-1);
                    }else{
                        appInfo=mSystemList.get(position-mCustomerList.size()-2);
                    }
                }
                showPopupWindow(view);
            }
        });
    }

    private void showPopupWindow(View view) {
        View popupView=View.inflate(getApplicationContext(),R.layout.popupwindow,null);
        TextView tv_uninstall= (TextView) popupView.findViewById(R.id.tv_uninstall);
        TextView tv_start= (TextView) popupView.findViewById(R.id.tv_start);
        TextView tv_share= (TextView) popupView.findViewById(R.id.tv_share);

        tv_uninstall.setOnClickListener(this);
        tv_start.setOnClickListener(this);
        tv_share.setOnClickListener(this);

        //透明动画
        AlphaAnimation alphaAnimation=new AlphaAnimation(0,1);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setFillAfter(true);

        //缩放动画
        ScaleAnimation scaleAnimation=new ScaleAnimation(0,1,0,1,
                Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(true);

        //动画集合
        AnimationSet animationSet=new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);

        //1.创建窗体对象，指定宽高
        popupWindow=new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,true);
        //2.设置透明背景
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        //3.指定窗体位置
        popupWindow.showAsDropDown(view,50,-view.getHeight());
        //4.popupView执行动画
        popupView.startAnimation(animationSet);
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tv_uninstall:
                if(appInfo.isSystem){
                    Toast.makeText(getApplicationContext(),"系统应用不能卸载",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent=new Intent("andriod.intent.action.DELETE");
                    intent.addCategory("android.intent.categroy.DELETE");
                    intent.setData(Uri.parse("package:"+appInfo.getPackageName()));
                    startActivity(intent);
                }
                break;
            case R.id.tv_start:
                //通过桌面去开启指定包名应用
                PackageManager pm=getPackageManager();
                //通过Launch开启指定包名的意图,去开启应用
                Intent launchIntent=pm.getLaunchIntentForPackage(appInfo.getPackageName());
                if(launchIntent!=null){
                    startActivity(launchIntent);
                }else{
                    Toast.makeText(getApplicationContext(),"此应用不能开启",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_share:
                //通过短信应用，向外发送短信
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,"分享一个应用，应用名称为:"+appInfo.getName());
                intent.setType("text/plain");
                startActivity(intent);
                break;
        }
       if(popupWindow!=null){
           popupWindow.dismiss();
       }
    }
    private void initTitle() {
        //1.获取内存可用大小,内存路径
        String path=Environment.getDataDirectory().getAbsolutePath();
        String memoryAvaliSpace= Formatter.formatFileSize(this,getAvailSpace(path));
        //2.获取sd卡可用大小，sd卡路径
        String sdPath=Environment.getExternalStorageDirectory().getAbsolutePath();
        String sdAvaliSpace= Formatter.formatFileSize(this,getAvailSpace(sdPath));

        TextView tv_memory= (TextView) findViewById(R.id.tv_memory);
        TextView tv_sd= (TextView) findViewById(R.id.tv_sd);

        tv_memory.setText("内存可用大小："+memoryAvaliSpace);
        tv_sd.setText("sd卡可用大小："+sdAvaliSpace);
    }

    private long getAvailSpace(String path) {
        //获取可用内存大小
        StatFs statfs=new StatFs(path);
        //获取可用区块的个数
        long count=statfs.getAvailableBlocks();
        //获取区块大小
        long size=statfs.getBlockSize();
        //可用空间总大小
        return count*size;
    }


    class MyAdapter extends BaseAdapter{
        //获取数据适配器中条目类型的总数,修改成两种(纯文本,图片+文字)
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount()+1;
        }

        @Override
        public int getItemViewType(int position) {
            if(position==0 || position==mCustomerList.size()+1){
                //返回0,代表纯文本条目的状态码
                return 0;
            }else {
                //返回1,代表图片+文本条目状态码
                return 1;
            }
        }

        //listView中添加两个描述条目
        @Override
        public int getCount() {
            return mCustomerList.size()+mSystemList.size()+2;
        }

        @Override
        public AppInfo getItem(int position) {
            if(position==0 || position==mCustomerList.size()+1){
                return  null;
            }else{
                if(position<mCustomerList.size()+1){
                    return mCustomerList.get(position-1);
                }else{
                    //返回系统应用对应条目的对象
                    return mSystemList.get(position-mCustomerList.size()-2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type=getItemViewType(position);
            if(type==0){ //纯文本条目的状态码
                //展示灰色纯文本条目
                ViewTitleHolder holder=null;
                if(convertView==null){
                    convertView=View.inflate(getApplicationContext(),R.layout.listview_app_item_title,null);
                    holder=new ViewTitleHolder();
                    holder.tv_title= (TextView) convertView.findViewById(R.id.tv_title);
                    convertView.setTag(holder);
                }else{
                    holder= (ViewTitleHolder) convertView.getTag();
                }
                if(position==0){
                    holder.tv_title.setText("用户应用("+mCustomerList.size()+")");
                }else{
                    holder.tv_title.setText("系统应用("+mSystemList.size()+")");
                }
                return convertView;
            }else{
                //展示图片+文字条目
                 ViewHolder holder=null;
                if(convertView==null){
                    holder=new ViewHolder();
                    convertView=View.inflate(getApplicationContext(),R.layout.list_app_item,null);
                    holder.iv_icon= (ImageView) convertView.findViewById(R.id.iv_icon);
                    holder.tv_name= (TextView) convertView.findViewById(R.id.tv_name);
                    holder.tv_path= (TextView) convertView.findViewById(R.id.tv_path);
                    convertView.setTag(holder);
                }else{
                    holder= (ViewHolder) convertView.getTag();
                }

                holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
                holder.tv_name.setText(getItem(position).name);
                if(getItem(position).isSdCard){
                    holder.tv_path.setText("sd卡应用");
                }else{
                    holder.tv_path.setText("手机应用");
                }
                return convertView;
            }
        }
    }


    static class ViewTitleHolder{
        TextView tv_title;
    }
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_path;
    }

}

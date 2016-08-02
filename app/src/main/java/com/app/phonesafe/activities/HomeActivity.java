package com.app.phonesafe.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.phonesafe.Config;
import com.app.phonesafe.R;
import com.app.phonesafe.utils.SPUtils;

import org.w3c.dom.Text;

/**
 * Created by 14501_000 on 2016/8/1.
 */
public class HomeActivity extends Activity {
    private GridView gridView;
    private String[] mTitleStrs;
    private int[] mDrawableIds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //初始化控件
        initUI();
        //初始化控件
        initData();
    }
    private void initUI() {
        gridView= (GridView) findViewById(R.id.gv_home);
    }
    private void initData() {
        mTitleStrs = new String[]{"手机防盗","通信卫士","软件管理",
                "进程管理","流量统计","手机杀毒","缓存清理","高级工具","设置中心"};
        mDrawableIds = new int[]{
                R.drawable.home_safe,R.drawable.home_callmsgsafe,
                R.drawable.home_apps,R.drawable.home_taskmanager,
                R.drawable.home_netmanager,R.drawable.home_trojan,
                R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_settings};
        //九宫格控件设置数据适配器(等同ListView数据适配器)
        gridView.setAdapter(new MyAdapter());

        //注册点击事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0://手机防盗
                        showDialog();
                        break;
                    case 1://通信卫士
                        break;
                    case 2://软件管理
                        break;
                    case 3://进程管理
                        break;
                    case 4://流量统计
                        break;
                    case 5://手机杀毒
                        break;
                    case 6://缓存清理
                        break;
                    case 7://高级工具
                        break;
                    case 8://设置中心
                        Intent intent=new Intent(getApplicationContext(),SettingActivity.class);
                        startActivity(intent);
                        break;
                    default:break;

                }
            }
        });
    }

    private void showDialog() {
        String psd=SPUtils.getString(getApplicationContext(), Config.MOBILE_SAFE_PSD,null);
        if(TextUtils.isEmpty(psd)){
            //1,初始设置密码对话框
            showSetPsdDialog();
        }else{
            //1,确认登陆密码对话框
            showConfirmPsdDialog();
        }
    }

    /**
     * 设置密码对话框
     */
    private void showSetPsdDialog() {
        //因为需要去自己定义对话框的展示样式,所以需要调用dialog.setView(view);
        //view是由自己编写的xml转换成的view对象xml----->view
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final AlertDialog dialog=builder.create();
        final View view=View.inflate(getApplicationContext(),R.layout.dialog_set_psd,null);
        dialog.setView(view);
        dialog.show();
        Button bt_submit= (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel= (Button) view.findViewById(R.id.bt_cancel);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_psd= (EditText) view.findViewById(R.id.et_set_psd);
                EditText et_confirm= (EditText) view.findViewById(R.id.et_confirm_psd);
                String psd=et_psd.getText().toString();
                String confirm=et_confirm.getText().toString();
                if(!TextUtils.isEmpty(psd)&& !TextUtils.isEmpty(confirm)){
                    if(psd.equals(confirm)){
                        //进入应用手机防盗模块,开启一个新的activity
                        Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                        startActivity(intent);
                        //跳转到新的界面以后需要去隐藏对话框
                        dialog.dismiss();

                        SPUtils.putString(getApplicationContext(), Config.MOBILE_SAFE_PSD, psd);
                    }else{
                        Toast.makeText(getApplicationContext(),"密码不一致",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    //提示用户密码输入有为空的情况
                    Toast.makeText(getApplicationContext(),"请输入密码",Toast.LENGTH_SHORT).show();
                }

            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void showConfirmPsdDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final AlertDialog dialog=builder.create();
        final View view=View.inflate(this,R.layout.dialog_confirm_psd,null);
        dialog.setView(view);
        dialog.show();

        Button bt_submit= (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel= (Button) view.findViewById(R.id.bt_cancel);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv_psd= (TextView) view.findViewById(R.id.et_confirm_psd);
                String psd=tv_psd.getText().toString();

                if(!TextUtils.isEmpty(psd)){
                    String sp_psd=SPUtils.getString(getApplicationContext(),Config.MOBILE_SAFE_PSD,"");
                    if(sp_psd.equals(psd)){
                        Intent intent=new Intent(HomeActivity.this,TestActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }else{
                        Toast.makeText(getApplicationContext(),"密码错误",Toast.LENGTH_SHORT).show();
                        tv_psd.setText("");
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"密码不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mTitleStrs.length;
        }

        @Override
        public Object getItem(int position) {
            return mTitleStrs[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=View.inflate(getApplicationContext(),R.layout.gridview_item,null);
            ImageView iv_icon= (ImageView) view.findViewById(R.id.iv_icon);
            TextView tv_title= (TextView) view.findViewById(R.id.tv_title);

            tv_title.setText(mTitleStrs[position]);
            iv_icon.setBackgroundResource(mDrawableIds[position]);
            return view;
        }
    }
}

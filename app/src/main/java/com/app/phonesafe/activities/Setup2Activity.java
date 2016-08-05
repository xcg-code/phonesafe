package com.app.phonesafe.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.phonesafe.Config;
import com.app.phonesafe.R;
import com.app.phonesafe.utils.SPUtils;
import com.app.phonesafe.view.SettingItemView;


/**
 * Created by 14501_000 on 2016/8/3.
 */
public class Setup2Activity extends Activity {
    SettingItemView siv_sim_bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        initUI();
    }

    private void initUI() {
        siv_sim_bound= (SettingItemView) findViewById(R.id.siv_sim_bound);
        //1.回显（读取已有的绑定状态）
        String sim_number=SPUtils.getString(getApplicationContext(), Config.SIM_NUMBER,"");
        //2.判断序列号是否为空
        if(TextUtils.isEmpty(sim_number)){
            siv_sim_bound.setText(false);
        }else{
            siv_sim_bound.setText(true);
        }
        siv_sim_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //3.获取原有状态
                boolean check=siv_sim_bound.isCheck();
                //4.将原有状态取反
                //5.设置状态
                siv_sim_bound.setText(!check);
                SPUtils.putBoolean(getApplicationContext(),Config.OPEN_UPDATE,!check);
                if(!check){
                   // 6.存储（序列号）
                    //获取sim卡序列号（TelephoneManager）
                    TelephonyManager manager= (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String simSerialNumber=manager.getSimSerialNumber();
                    SPUtils.putString(getApplicationContext(),Config.SIM_NUMBER,simSerialNumber);
                }else{
                    SPUtils.putString(getApplicationContext(),Config.SIM_NUMBER,"");
                }

            }
        });
    }
    public void nextPage(View view){
        String sim_number=SPUtils.getString(getApplicationContext(),Config.SIM_NUMBER,"");
        if(!TextUtils.isEmpty(sim_number)) {
            Intent intent = new Intent(Setup2Activity.this, Setup3Activity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        }else{
            Toast.makeText(this,"未绑定sim卡",Toast.LENGTH_SHORT).show();
        }
    }

    public void prePage(View view){
        Intent intent=new Intent(Setup2Activity.this,Setup1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }

}

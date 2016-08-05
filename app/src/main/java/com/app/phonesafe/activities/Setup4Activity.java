package com.app.phonesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.phonesafe.Config;
import com.app.phonesafe.R;
import com.app.phonesafe.utils.SPUtils;

import java.util.Set;


/**
 * Created by 14501_000 on 2016/8/3.
 */
public class Setup4Activity extends Activity {
    private CheckBox cb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        initUI();
}

    private void initUI() {
        cb= (CheckBox) findViewById(R.id.cb_box);

        boolean open_state=SPUtils.getBoolean(getApplicationContext(),Config.OPEN_SAFE_STATE,false);
        cb.setChecked(open_state);
        if(open_state){
            cb.setText("安全设置已开启");
        }else{
            cb.setText("安全设置未开启");
        }

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.putBoolean(getApplicationContext(),Config.OPEN_SAFE_STATE,isChecked);
                if(isChecked){
                    cb.setText("安全设置已开启");
                }else{
                    cb.setText("安全设置未开启");
                }
            }
        });
    }

    public void nextPage(View view){
        boolean state=SPUtils.getBoolean(getApplicationContext(),Config.OPEN_SAFE_STATE,false);
        if(state) {
            Intent intent = new Intent(Setup4Activity.this, SetupOverActivity.class);
            startActivity(intent);
            finish();
            SPUtils.putBoolean(getApplicationContext(),Config.SETUP_OVER,true);
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        }else{
            Toast.makeText(Setup4Activity.this,"请开启防盗保护",Toast.LENGTH_SHORT).show();
        }

    }

    public void prePage(View view){

        Intent intent=new Intent(Setup4Activity.this,Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }
}

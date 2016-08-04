package com.app.phonesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.app.phonesafe.Config;
import com.app.phonesafe.R;
import com.app.phonesafe.utils.SPUtils;


/**
 * Created by 14501_000 on 2016/8/3.
 */
public class Setup4Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
}
    public void nextPage(View view){
        Intent intent=new Intent(Setup4Activity.this,SetupOverActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
    }

    public void prePage(View view){
        SPUtils.putBoolean(getApplicationContext(),Config.SETUP_OVER,true);
        Intent intent=new Intent(Setup4Activity.this,Setup2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }
}

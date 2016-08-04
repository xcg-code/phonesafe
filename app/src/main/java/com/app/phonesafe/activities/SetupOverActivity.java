package com.app.phonesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.app.phonesafe.Config;
import com.app.phonesafe.R;
import com.app.phonesafe.utils.SPUtils;

/**
 * Created by 14501_000 on 2016/8/2.
 */
public class SetupOverActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean setup_over= SPUtils.getBoolean(this, Config.SETUP_OVER,false);
        if(setup_over){
            setContentView(R.layout.activity_setup_over);
        }else{
            Intent intent=new Intent(this,Setup1Activity.class);
            startActivity(intent);
            finish();
        }
    }
}

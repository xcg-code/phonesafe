package com.app.phonesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.phonesafe.Config;
import com.app.phonesafe.R;
import com.app.phonesafe.utils.SPUtils;

/**
 * Created by 14501_000 on 2016/8/2.
 */
public class SetupOverActivity extends Activity {
    TextView tv_phone;
    ImageView iv_lock;
    TextView tv_reset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean setup_over= SPUtils.getBoolean(this, Config.SETUP_OVER,false);
        if(setup_over){
            setContentView(R.layout.activity_setup_over);
            initUI();
        }else{
            Intent intent=new Intent(this,Setup1Activity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initUI() {
        tv_phone= (TextView) findViewById(R.id.tv_phone);
        iv_lock= (ImageView) findViewById(R.id.iv_lock);
        tv_reset= (TextView) findViewById(R.id.tv_reset_setup);
        String phone_num=SPUtils.getString(getApplicationContext(),Config.PHONE_NUMBER,"");
        tv_phone.setText(phone_num);

        tv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SetupOverActivity.this,Setup1Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

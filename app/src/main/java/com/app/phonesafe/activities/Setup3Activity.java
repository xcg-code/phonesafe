package com.app.phonesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.app.phonesafe.Config;
import com.app.phonesafe.R;
import com.app.phonesafe.utils.SPUtils;


/**
 * Created by 14501_000 on 2016/8/3.
 */
public class Setup3Activity extends Activity {
    Button bt_next4;
    Button bt_pre3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        bt_pre3= (Button) findViewById(R.id.bt_pre3);
        bt_next4= (Button) findViewById(R.id.bt_next4);
        bt_pre3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Setup3Activity.this,Setup3Activity.class);
                startActivity(intent);
                finish();
            }
        });
        bt_next4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.putBoolean(getApplicationContext(),Config.SETUP_OVER,true);
                Intent intent=new Intent(Setup3Activity.this,SetupOverActivity.class);
                startActivity(intent);
                finish();
            }
        });


}

}

package com.app.phonesafe.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.app.phonesafe.Config;
import com.app.phonesafe.R;
import com.app.phonesafe.utils.SPUtils;
import com.app.phonesafe.view.SettingItemView;

/**
 * Created by 14501_000 on 2016/8/1.
 */
public class SettingActivity extends Activity {
    SettingItemView siv_update;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initUI();
    }

    private void initUI() {
        siv_update= (SettingItemView) findViewById(R.id.siv_update);
        //获取已有的开关状态，用作显示
        boolean open_update=SPUtils.getBoolean(this, Config.OPEN_UPDATE,false);
        //回显过程
        siv_update.setText(open_update);
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check=siv_update.isCheck();
                siv_update.setText(!check);
                SPUtils.putBoolean(getApplicationContext(),Config.OPEN_UPDATE,!check);
            }
        });
    }
}

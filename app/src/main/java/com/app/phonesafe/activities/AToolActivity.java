package com.app.phonesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.app.phonesafe.R;


/**
 * Created by 14501_000 on 2016/8/5.
 */
public class AToolActivity extends Activity {
    private TextView tv_query_phone_address;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool);
        initPhoneAddress();
    }

    private void initPhoneAddress() {
        tv_query_phone_address= (TextView) findViewById(R.id.tv_query_phone_address);
        tv_query_phone_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AToolActivity.this,QueryAddressActivity.class));
            }
        });
    }
}

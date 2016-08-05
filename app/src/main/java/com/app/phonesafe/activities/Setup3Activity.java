package com.app.phonesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.phonesafe.Config;
import com.app.phonesafe.R;
import com.app.phonesafe.utils.SPUtils;


/**
 * Created by 14501_000 on 2016/8/3.
 */
public class Setup3Activity extends Activity {
    Button bt_select_number;
    EditText et_phone_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        initUI();
}

    private void initUI() {
        bt_select_number= (Button) findViewById(R.id.bt_select_number);
        et_phone_number= (EditText) findViewById(R.id.et_phone_number);

        bt_select_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Setup3Activity.this,ContactListActivity.class);
                startActivityForResult(intent,0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            //1,返回到当前界面的时候,接受结果的方法
            String phone=data.getStringExtra("phone");
            //2,将特殊字符过滤(中划线转换成空字符串)
            phone=phone.replace("-","").replace(" ","").trim();
            et_phone_number.setText(phone);
            SPUtils.putString(getApplicationContext(),Config.PHONE_NUMBER,phone);
        }
    }

    public void nextPage(View view){
        String phone=et_phone_number.getText().toString();
        SPUtils.putString(getApplicationContext(),Config.PHONE_NUMBER,phone);
        if(!TextUtils.isEmpty(phone)) {
            Intent intent = new Intent(Setup3Activity.this, Setup4Activity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        }else{
            Toast.makeText(Setup3Activity.this, "未输入电话号码", Toast.LENGTH_SHORT).show();
        }
    }

    public void prePage(View view){
        SPUtils.putBoolean(getApplicationContext(),Config.SETUP_OVER,true);
        Intent intent=new Intent(Setup3Activity.this,Setup2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }
}

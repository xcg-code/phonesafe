package com.app.phonesafe.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.phonesafe.R;
import com.app.phonesafe.engine.AddressDao;

/**
 * Created by 14501_000 on 2016/8/5.
 */
public class QueryAddressActivity extends Activity {
    private EditText et_phone;
    private Button bt_query;
    private TextView tv_query_result;
    private String mAddress;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //4,控件使用查询结果
            tv_query_result.setText(mAddress);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_address);
        initUI();
    }

    private void initUI() {
        et_phone= (EditText) findViewById(R.id.et_phone);
        bt_query= (Button) findViewById(R.id.bt_query);
        tv_query_result= (TextView) findViewById(R.id.tv_query_result);

        //1.查询功能
        bt_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone=et_phone.getText().toString();
                if(!TextUtils.isEmpty(phone)){
                    //2.查询为耗时操作，在子线程中进行
                    query(phone);
                }else{
                    //抖动
                    //Animation shake= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake);
                    //et_phone.startAnimation(shake);
                }
            }
        });

        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone=et_phone.getText().toString();
                query(phone);
            }
        });
    }

    /**
     * 耗时操作
     * 获取电话号码归属地
     * @param phone 查询电话号码
     */
    private void query(final String phone) {
        new Thread(){
            @Override
            public void run() {
                mAddress= AddressDao.getAddress(phone);
                //3,消息机制,告知主线程查询结束,可以去使用查询结果
                Message mes=new Message();
                mes.obj=mAddress;
                mHandler.sendMessage(mes);
            }
        }.start();
    }
}

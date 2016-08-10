package com.app.phonesafe.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.phonesafe.R;
import com.app.phonesafe.engine.SmsBackUp;

import java.io.File;


/**
 * Created by 14501_000 on 2016/8/5.
 */
public class AToolActivity extends Activity {
    private TextView tv_query_phone_address;
    private TextView tv_sms_bacup;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool);
        initPhoneAddress();
        initSmsBackup();
    }

    private void initSmsBackup() {
        tv_sms_bacup= (TextView) findViewById(R.id.tv_sms_backup);
        tv_sms_bacup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSmsBackupDialog();
            }
        });
    }

    private void showSmsBackupDialog() {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setIcon(R.drawable.ic_launcher);
        pd.setTitle("短信备份");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();

        //直接调用备份短信方法即可
        new Thread(){
            @Override
            public void run() {
                super.run();
                boolean sdCardExist = Environment.getExternalStorageState()
                        .equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
                if (sdCardExist) {
                    //备份路径
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sms74.xml";
                    SmsBackUp.backup(AToolActivity.this, path, new SmsBackUp.CallBack() {
                        @Override
                        public void setMax(int Max) {
                            pd.setMax(Max);
                            Log.i("666","MAX:="+Max);
                        }

                        @Override
                        public void setPrograss(int index) {
                            pd.setProgress(index);
                            Log.i("666","index:="+index);
                        }
                    });
                    pd.dismiss();
                }else {
                    Toast.makeText(AToolActivity.this, "SD卡不存在", Toast.LENGTH_SHORT).show();
                }
            }
        }.start();


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

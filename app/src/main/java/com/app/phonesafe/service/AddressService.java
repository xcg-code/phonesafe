package com.app.phonesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.app.phonesafe.R;
import com.app.phonesafe.engine.AddressDao;

/**
 * Created by 14501_000 on 2016/8/6.
 */
public class AddressService extends Service{
    TelephonyManager tm;
    WindowManager wm;
    MyPhoneStateListener mPhoneStsteListener;
    private final WindowManager.LayoutParams mParams=new WindowManager.LayoutParams();
    View toastView;
    TextView toastText;
    private String mAddress;

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            toastText.setText(mAddress);
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        //第一次开启服务以后,就需要去管理吐司的显示
        //电话状态的监听(服务开启的时候,需要去做监听,关闭的时候电话状态就不需要监听)
        //1,电话管理者对象
        tm= (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneStsteListener=new MyPhoneStateListener();
        tm.listen(mPhoneStsteListener,PhoneStateListener.LISTEN_CALL_STATE);
        wm= (WindowManager) getSystemService(WINDOW_SERVICE);//获取窗体对象
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    class MyPhoneStateListener extends PhoneStateListener{
        //3,手动重写,电话状态发生改变会触发的方法
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch(state){
                case TelephonyManager.CALL_STATE_IDLE://空闲状态，没有任何活动（移除吐司）
                    //挂断电话的时候窗体需要移除吐司
                    if(wm!=null && toastView!=null){
                        wm.removeView(toastView);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机状态，至少有个电话活动。该活动或是拨打（dialing）或是通话
                    break;
                case TelephonyManager.CALL_STATE_RINGING://响铃（显示吐司）
                    showToast(incomingNumber);
                    break;
            }
        }
    }

    private void showToast(String incomingNumber) {
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        //在响铃时显示吐司，和电话类型一致
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
               // | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;没人能被触摸
        params.gravity= Gravity.CENTER_HORIZONTAL;//将吐司指定在左上角

        //吐司展示效果(吐司布局文件),xml-->view(吐司),将吐司挂在到windowManager窗体上
        toastView=View.inflate(this, R.layout.toast_view,null);
        toastText= (TextView) toastView.findViewById(R.id.tv_toast);

        //在窗体上挂在一个view(权限)
        wm.addView(toastView,params);

        //获取到了来电号码以后,需要做来电号码查询
        query(incomingNumber);

    }

    private void query(final String incomingNumber) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                mAddress = AddressDao.getAddress(incomingNumber);
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消对电话状态的监听(开启服务的时候监听电话的对象)
        if(tm!=null && mPhoneStsteListener!=null){
            tm.listen(mPhoneStsteListener,PhoneStateListener.LISTEN_NONE);
        }
    }
}

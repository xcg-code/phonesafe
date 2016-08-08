package com.app.phonesafe.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.app.phonesafe.R;

/**
 * Created by 14501_000 on 2016/8/6.
 */
public class RocketBackgroundActivity extends Activity{
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_rocket);

        ImageView iv_top= (ImageView) findViewById(R.id.iv_top);
        ImageView iv_bottom= (ImageView) findViewById(R.id.iv_bottom);

        //尾气淡入淡出效果,动画是异步操作,并不会去阻塞主线程
        AlphaAnimation alphaAnimation=new AlphaAnimation(0,1);
        alphaAnimation.setDuration(500);
        iv_top.startAnimation(alphaAnimation);
        iv_bottom.startAnimation(alphaAnimation);

        //将尾气消失，发送一个延时消息,1000毫秒后结束此activity
        handler.sendEmptyMessageDelayed(0,1000);
    }
}

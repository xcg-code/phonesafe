package com.app.phonesafe.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.app.phonesafe.Config;
import com.app.phonesafe.R;
import com.app.phonesafe.activities.RocketBackgroundActivity;
import com.app.phonesafe.utils.SPUtils;

/**
 * Created by 14501_000 on 2016/8/6.
 */
public class RocketService  extends Service{
    WindowManager wm;
    int screenWidth;
    int screenHeight;
    View rocketView;
    private final WindowManager.LayoutParams mParams=new WindowManager.LayoutParams();
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mParams.y= (int) msg.obj;
            //更新到火箭上(窗体)
            wm.updateViewLayout(rocketView,mParams);
        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        wm= (WindowManager) getSystemService(WINDOW_SERVICE);//获取窗体
        screenWidth=wm.getDefaultDisplay().getWidth();
        screenHeight=wm.getDefaultDisplay().getHeight();

        //初始化小火箭
        showRocket();

    }

    private void showRocket() {
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;//不在和吐司类型相互绑定,通话的类型相互绑定
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;不能被触摸
        params.gravity= Gravity.LEFT+Gravity.TOP;//将吐司指定在左上角

        //吐司展示效果(吐司布局文件),xml-->view(吐司),将吐司挂在到windowManager窗体上
        rocketView= View.inflate(this, R.layout.rocket_view,null);
        ImageView rocket= (ImageView) rocketView.findViewById(R.id.iv_rocket);
        //获取设置了动画的背景，然后让此背景执行
        AnimationDrawable drawable= (AnimationDrawable) rocket.getBackground();
        //获取背景图片后，让其动起来
        drawable.start();

        rocketView.setOnTouchListener(new View.OnTouchListener() {
            private int startX;
            private int startY;
            //对不同的事件做不同的逻辑处理
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int nowX = (int) event.getRawX();
                        int nowY = (int) event.getRawY();

                        int disX = nowX - startX;
                        int disY = nowY - startY;

                        params.x = params.x+disX;
                        params.y = params.y+disY;

                        //容错处理
                        if(params.x<0){
                            params.x = 0;
                        }

                        if(params.y<0){
                            params.y=0;
                        }

                        if(params.x>screenWidth-rocketView.getWidth()){
                            params.x = screenWidth-rocketView.getWidth();
                        }

                        if(params.y>screenHeight-rocketView.getHeight()-22){
                            params.y = screenHeight-rocketView.getHeight()-22;
                        }
                        ////告知窗体吐司需要按照手势的移动,去做位置的更新
                        wm.updateViewLayout(rocketView,params);

                        //重置一次其实坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        //放手时，如果在指定区域则播放动画效果
                        if(params.x>(screenWidth/3)&&params.x<(screenWidth*2/3)&&params.y>(screenHeight*2/3)){
                            //火箭发射
                            sendRocket();
                            //在开启火箭过程中,去开启一个新的activity,activity透明,在此activity中放置两张图片(淡入淡出效果)
                            Intent intent=new Intent(getApplicationContext(),RocketBackgroundActivity.class);
                            //指定开启新的activity任务栈
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        break;
                }
                return true;
            }
        });
        //在窗体上挂在一个view(权限)
        wm.addView(rocketView,params);
    }

    private void sendRocket() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                for(int i=0;i<11;i++){
                    int y=350-i*25;
                    //睡眠
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //通过消息机制,将y轴坐标作为主线程火箭竖直方向上的显示位置
                    Message msg=Message.obtain();
                    msg.obj=y;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(wm!=null&&rocketView!=null){
            wm.removeView(rocketView);
        }
    }
}

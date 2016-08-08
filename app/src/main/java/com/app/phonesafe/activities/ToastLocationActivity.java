package com.app.phonesafe.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.app.phonesafe.Config;
import com.app.phonesafe.R;
import com.app.phonesafe.utils.SPUtils;

/**
 * Created by 14501_000 on 2016/8/6.
 */
public class ToastLocationActivity  extends Activity{
    Button bt_top,bt_bottom;
    ImageView iv_drag;
    WindowManager wm;
    int screenHeight;
    int screenWidth;
    private long[] mHits=new long[2];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_location);
        initUI();
    }

    private void initUI() {
        //可拖拽双击居中的图片控件
        bt_top= (Button) findViewById(R.id.bt_top);
        bt_bottom= (Button) findViewById(R.id.bt_bottom);
        iv_drag= (ImageView) findViewById(R.id.iv_drag);

        wm= (WindowManager) getSystemService(WINDOW_SERVICE);
        screenHeight=wm.getDefaultDisplay().getHeight();
        screenWidth=wm.getDefaultDisplay().getWidth();

        int locationX= SPUtils.getInt(getApplicationContext(), Config.LOCATION_X,0);
        int locationY=SPUtils.getInt(getApplicationContext(),Config.LOCATION_Y,0);

        //指定宽高都为WRAP_CONTENT
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        //将左上角的坐标作用在iv_drag对应规则参数上
        layoutParams.leftMargin = locationX;
        layoutParams.topMargin = locationY;
        //将以上规则作用在iv_drag上
        iv_drag.setLayoutParams(layoutParams);

        if(locationY>screenHeight/2){
            bt_bottom.setVisibility(View.INVISIBLE);
            bt_top.setVisibility(View.VISIBLE);
        }else{
            bt_bottom.setVisibility(View.VISIBLE);
            bt_top.setVisibility(View.INVISIBLE);
        }

        iv_drag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits,1,mHits,0,mHits.length-1);
                mHits[mHits.length-1]= SystemClock.uptimeMillis();
                if(mHits[mHits.length-1]-mHits[0]<500){
                    //满足双击条件
                    int left=screenWidth/2-iv_drag.getWidth()/2;
                    int top=screenHeight/2-iv_drag.getHeight()/2;
                    int right=screenWidth/2+iv_drag.getWidth()/2;
                    int bottom=screenHeight/2+iv_drag.getHeight()/2;
                    iv_drag.layout(left,top,right,bottom);
                    //存储位置
                    SPUtils.putInt(getApplicationContext(),Config.LOCATION_X,iv_drag.getLeft());
                    SPUtils.putInt(getApplicationContext(),Config.LOCATION_Y,iv_drag.getTop());
                }
            }
        });
        //监听某一个控件的拖拽过程(按下(1),移动(多次),抬起(1))
        iv_drag.setOnTouchListener(new View.OnTouchListener() {
            private int startX;
            private int startY;
            //对不同的事件做不同的逻辑处理
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX= (int) event.getRawX();
                        startY= (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int nowX= (int) event.getRawX();
                        int nowY= (int) event.getRawY();

                        int disX=nowX-startX;
                        int disY=nowY-startY;

                        //当前控件所在屏幕的（左，上）角
                        int left=iv_drag.getLeft()+disX;//左侧坐标
                        int top=iv_drag.getTop()+disY;//顶端坐标
                        int right=iv_drag.getRight()+disX;
                        int bottom=iv_drag.getBottom()+disY;

                        //容错处理(iv_drag不能拖拽出手机屏幕)
                        //左边缘不能超出屏幕
                        if(left<0){
                            return true;
                        }
                        //右边边缘不能超出屏幕
                        if(right>screenWidth){
                            return true;
                        }
                        //上边缘不能超出屏幕可现实区域
                        if(top<0){
                            return true;
                        }
                        //下边缘(屏幕的高度-22 = 底边缘显示最大值)
                        if(bottom>screenHeight - 22){
                            return true;
                        }

                        if(top>screenHeight/2){
                            bt_bottom.setVisibility(View.INVISIBLE);
                            bt_top.setVisibility(View.VISIBLE);
                        }else{
                            bt_bottom.setVisibility(View.VISIBLE);
                            bt_top.setVisibility(View.INVISIBLE);
                        }
                        //2,告知移动的控件,按计算出来的坐标去做展示
                        iv_drag.layout(left,top,right,bottom);

                        //3,重置一次其实坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        //4,存储移动到的位置
                        SPUtils.putInt(getApplicationContext(),Config.LOCATION_X,iv_drag.getLeft());
                        SPUtils.putInt(getApplicationContext(),Config.LOCATION_Y,iv_drag.getTop());
                        break;
                }
                //在当前的情况下返回false不响应事件,返回true才会响应事件
                //既要响应点击事件又要响应拖拽事件则返回false
                return false;
            }
        });

    }
}

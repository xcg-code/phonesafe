package com.app.phonesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.phonesafe.R;

/**
 * Created by 14501_000 on 2016/8/6.
 */
public class SettingClickView extends RelativeLayout {
    TextView tv_title;
    TextView tv_des;
    public SettingClickView(Context context) {
        this(context,null);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view=View.inflate(context, R.layout.setting_click_view,this);
        tv_title= (TextView) view.findViewById(R.id.tv_title);
        tv_des= (TextView) view.findViewById(R.id.tv_des);
    }
    /**
     * @param title	设置标题内容
     */
    public void setTitle(String title){
        tv_title.setText(title);
    }

    /**
     * @param des	设置描述内容
     */
    public void setDes(String des){
        tv_des.setText(des);
    }

}

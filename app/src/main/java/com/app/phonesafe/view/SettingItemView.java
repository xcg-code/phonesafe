package com.app.phonesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.app.phonesafe.R;


/**
 自定义组合控件
 1.将已经编写好的布局文件,抽取到一个类中去做管理,下次还需要使用此布局结构的时候,
 直接使用组合控件对应的对象.

 2.将组合控件的布局,抽取到单独的一个xml中

 3.通过一个单独的类,去加载此段布局文件.

 4.checkBox是否选中,决定SettingItemView是否开启,isCheck(){return checkbox.isCheck()}方法

 5.提供一个SettingItemView,切换选中状态的方法setCheck(boolean isCheck)
 */
public class SettingItemView extends RelativeLayout {
    private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.app.phonesafe";
    private String mDestitle;
    private String mDesoff;
    private String mDeson;
    TextView tv_text1;
    TextView tv_text2;
    CheckBox cb;

    public SettingItemView(Context context) {
        this(context,null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //xml->View 将xml布局转化为View对象
        View view=View.inflate(context, R.layout.setting_item_view,this);
        tv_text1= (TextView) view.findViewById(R.id.tv_text1);
        tv_text2= (TextView) view.findViewById(R.id.tv_text2);
        cb= (CheckBox) view.findViewById(R.id.cb_check);
        //setText(isCheck());
        initAttrs(attrs);
        tv_text1.setText(mDestitle);
    }

    private void initAttrs(AttributeSet attrs) {
        //通过名空间+属性名称获取属性值

        mDestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
        mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
        mDeson = attrs.getAttributeValue(NAMESPACE, "deson");
    }

    public boolean isCheck(){
        return cb.isChecked();
    }
    public void setText(boolean isCheck){
        cb.setChecked(isCheck);
        if(isCheck){
            tv_text2.setText(mDeson);
        }else{
            tv_text2.setText(mDesoff);
        }

    }
}

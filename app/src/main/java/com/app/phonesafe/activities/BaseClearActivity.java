package com.app.phonesafe.activities;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.app.phonesafe.R;

/**
 * Created by 14501_000 on 2016/8/12.
 */
public class BaseClearActivity extends TabActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_clear_cache);

        //1.生成选项卡1
        TabHost.TabSpec tab1 = getTabHost().newTabSpec("clear_cache").setIndicator("缓存清理");
        //2.生成选项卡2
        TabHost.TabSpec tab2 = getTabHost().newTabSpec("sd_cache_clear").setIndicator("sd卡清理");

        //3.告知点中选项卡后续操作
        tab1.setContent(new Intent(this,ClearCacheActivity.class));
        tab2.setContent(new Intent(this,SDCacheClearActivity.class));

        //4.将此两个选项卡维护host(选项卡宿主)中去
        getTabHost().addTab(tab1);
        getTabHost().addTab(tab2);
    }

}

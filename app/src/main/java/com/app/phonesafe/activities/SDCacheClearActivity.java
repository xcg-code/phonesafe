package com.app.phonesafe.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by 14501_000 on 2016/8/12.
 */
public class SDCacheClearActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(getApplicationContext());
        textView.setText("SDCacheClearActivity");
        setContentView(textView);
    }
}

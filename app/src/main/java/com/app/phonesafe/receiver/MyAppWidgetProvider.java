package com.app.phonesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.app.phonesafe.service.UpdateWidgetService;

/**
 * Created by 14501_000 on 2016/8/11.
 */
public class MyAppWidgetProvider extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    //创建第一个窗体小部件的方法
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        //开启服务(onCreate)
        context.startService(new Intent(context, UpdateWidgetService.class));
    }

    //创建多一个窗体小部件调用方法
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        //开启服务
        context.startService(new Intent(context, UpdateWidgetService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        //当窗体小部件宽高发生改变的时候调用方法,创建小部件的时候,也调用此方法
        //开启服务
        context.startService(new Intent(context, UpdateWidgetService.class));
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    //删除最后一个窗体小部件调用方法
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        //关闭服务
        context.stopService(new Intent(context, UpdateWidgetService.class));
    }
}

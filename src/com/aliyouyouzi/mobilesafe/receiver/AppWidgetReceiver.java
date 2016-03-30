package com.aliyouyouzi.mobilesafe.receiver;

import com.aliyouyouzi.mobilesafe.service.UpdateWidgetService;
import com.aliyouyouzi.mobilesafe.utils.ProgressManagerUtils;
import com.aliyouyouzi.mobilesafe.R;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

public class AppWidgetReceiver extends AppWidgetProvider {

	private static final String TAG = "AppWidgetReceiver";

	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive");
		super.onReceive(context, intent);
	}

	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.d(TAG, "onDeleted");
		super.onDeleted(context, appWidgetIds);
	}

	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// 开启一个服务进行不停的查询数据
		context.startService(new Intent(context, UpdateWidgetService.class));
	}

	public void onDisabled(Context context) {
		// 关闭一个服务进行不停的查询数据
		context.stopService(new Intent(context, UpdateWidgetService.class));
		super.onDisabled(context);
	}

	public void onEnabled(Context context) {
		Log.d(TAG, "onEnabled");
		super.onEnabled(context);
	}
}

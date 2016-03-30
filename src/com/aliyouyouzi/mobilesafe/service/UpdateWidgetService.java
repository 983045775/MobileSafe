package com.aliyouyouzi.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.aliyouyouzi.mobilesafe.receiver.AppWidgetReceiver;
import com.aliyouyouzi.mobilesafe.utils.ProgressManagerUtils;
import com.aliyouyouzi.mobilesafe.R;

public class UpdateWidgetService extends Service {

	private AppWidgetManager mAwm;

	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onCreate() {
		mAwm = AppWidgetManager.getInstance(this);
		new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
					// 查询运行的进程
					int count = ProgressManagerUtils
							.queryRunningProgress(UpdateWidgetService.this);
					// 查询占用的内存
					long memory = ProgressManagerUtils
							.queryCanUserMemory(UpdateWidgetService.this);
					// 进行应用初始化数据显示
					ComponentName provider = new ComponentName(
							UpdateWidgetService.this, AppWidgetReceiver.class);
					RemoteViews views = new RemoteViews(getPackageName(),
							R.layout.process_widget);

					views.setTextViewText(R.id.process_count, "正在运行的进程: "
							+ count);
					views.setTextViewText(
							R.id.process_memory,
							"可用内存: "
									+ Formatter.formatFileSize(
											UpdateWidgetService.this, memory));
					Intent intent = new Intent();
					intent.setAction("org.lc.clear");
					PendingIntent pendingIntent = PendingIntent.getBroadcast(
							UpdateWidgetService.this, 100, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
					mAwm.updateAppWidget(provider, views);
				}
			};
		}.start();
	}

	public void onDestroy() {
		super.onDestroy();
	}

}

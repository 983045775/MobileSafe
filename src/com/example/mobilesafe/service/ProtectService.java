package com.example.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.example.mobilesafe.R;
import com.example.mobilesafe.utils.Constants;
import com.example.mobilesafe.utils.PreferencesUtils;
import com.example.mobilesafe.utils.ServiceUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RemoteViews;

public class ProtectService extends Service {
	private static final int ID = 100;
	protected static final String TAG = "ProtectService";
	private NotificationManager nm;

	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onCreate() {
		// 获取通知管理器
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// 创建通知栏对象,设置小图标
		Notification noti = new Notification.Builder(getApplicationContext())
				.setSmallIcon(R.drawable.icon).build();
		// 设置内容样式
		noti.contentView = new RemoteViews(getPackageName(),
				R.layout.notification_layout);
		//意图
		Intent intent = new Intent();
		intent.setAction("org.lc.home");
		noti.contentIntent = PendingIntent.getActivity(getApplicationContext(),
				ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		//发通知
		nm.notify(0, noti);
		super.onCreate();

		// 设置一个定时器任务
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			public void run() {
				// 进行查看
				boolean autoClear = PreferencesUtils.getBoolean(
						ProtectService.this, Constants.AUTO_CLEAR, false);
				if (autoClear) {
					// 需要自动清理,查看进程
					boolean running = ServiceUtils.isServiceRunning(
							getApplicationContext(),
							LockSreenAutoClearService.class);
					if (!running) {
						// 打开这个进程
						Log.d(TAG, "居然有人杀死我要保护的进程,我重新打开");
						Intent intent = new Intent(ProtectService.this,
								LockSreenAutoClearService.class);
						startService(intent);
					}
				}
			}
		}, 3000, 15000);
	}
}

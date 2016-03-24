package com.example.mobilesafe.service;

import java.util.List;

import com.example.mobilesafe.activity.LockActivity;
import com.example.mobilesafe.db.AppLockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class WatchDog1Service extends Service {
	protected static final String TAG = "WatchDog1Service";
	private String extra;
	private boolean flag = true;
	private AppLockDao mDao;
	private List<String> allLock;
	private ActivityManager mAm;

	private ContentObserver observer = new ContentObserver(new Handler()) {

		public void onChange(boolean selfChange) {
			allLock = mDao.queryAllLock();
		};
	};

	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onCreate() {
		super.onCreate();
		mDao = new AppLockDao(WatchDog1Service.this);
		allLock = mDao.queryAllLock();
		mAm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		// 注册一个广播
		IntentFilter filter = new IntentFilter();
		filter.addAction("org.lc.close.lock");
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(receiver, filter);

		ContentResolver resolver = getContentResolver();
		resolver.registerContentObserver(
				Uri.parse("content://org.mobile.lock"), false, observer);

		watch();

	}

	private void watch() {
		flag = true;
		new Thread() {

			public void run() {
				while (flag) {

					// 获取当前页面栈顶的包名
					RunningTaskInfo taskInfo = mAm.getRunningTasks(1).get(0);
					String packageName = taskInfo.topActivity.getPackageName();
					if (extra != null) {
						if (extra.equals(packageName)) {
							continue;
						}
					}
					if (allLock.contains(packageName)) {
						// 说明是存在的,需要显示一个我的Acitivty
						Intent intent = new Intent(WatchDog1Service.this,
								LockActivity.class);
						intent.putExtra("name", packageName);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		getContentResolver().unregisterContentObserver(observer);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_SCREEN_OFF)) {
				flag = false;
				allLock.clear();
			} else if (action.equals(Intent.ACTION_SCREEN_ON)) {
				flag = true;
				watch();
			} else if (action.equals("org.lc.close.lock")) {

			}
			extra = intent.getStringExtra("name");
		}
	};
}

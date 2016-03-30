package com.aliyouyouzi.mobilesafe.service;

import java.util.ArrayList;
import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;

import com.aliyouyouzi.mobilesafe.activity.LockActivity;
import com.aliyouyouzi.mobilesafe.db.AppLockDao;

public class WatchDog2Service extends AccessibilityService {

	private static final String TAG = "MyAccessibilityService";
	private List<String> mDatas = null;
	private String extra;
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			extra = intent.getStringExtra("name");
		}
	};
	private ContentObserver observer = new ContentObserver(new Handler()) {
		public void onChange(boolean selfChange) {
			mDatas = mDao.queryAllLock();
		}
	};
	private AppLockDao mDao;

	public void onAccessibilityEvent(AccessibilityEvent event) {

		if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
			// 每当状态改变了,然后我们再进行获取top
			ActivityManager mAm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			String packageName = mAm.getRunningTasks(1).get(0).topActivity
					.getPackageName();

			if (extra != null) {
				if (extra.equals(packageName)) {
					return;
				}
			}
			if (mDatas.contains(packageName)) {
				// 上锁了,弹出来intent
				Intent intent = new Intent(this, LockActivity.class);
				intent.putExtra("name", packageName);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}

		}
	}

	public void onInterrupt() {

	}

	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 注册一个广播
		IntentFilter filter = new IntentFilter();
		filter.addAction("org.lc.close.lock");
		registerReceiver(receiver, filter);
		mDao = new AppLockDao(this);
		mDatas = mDao.queryAllLock();
		// 注册一个数据库监听
		getContentResolver().registerContentObserver(
				Uri.parse("content://org.mobile.lock"), true, observer);
	}

}

package com.example.mobilesafe.service;

import java.util.List;
import java.util.ListIterator;

import com.example.mobilesafe.domain.ProgressManagerInfo;
import com.example.mobilesafe.engine.ProgressDatasProvide;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class LockSreenAutoClearService extends Service {

	private static final String TAG = "LockSreenAutoClearService";
	private LockScreenReceiver receiver = new LockScreenReceiver();

	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onCreate() {
		Log.d(TAG, "开启锁屏清理");
		// 注册一个锁屏广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(receiver, filter);
		super.onCreate();
	}

	public void onDestroy() {
		Log.d(TAG, "关闭锁屏清理");
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	private class LockScreenReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			// 清理的代码,查询所有的进程
			List<ProgressManagerInfo> list = ProgressDatasProvide
					.getProgressMessage(context);
			ListIterator<ProgressManagerInfo> iterator = list.listIterator();
			// 获取任务管理器
			ActivityManager am = (ActivityManager) context
					.getSystemService(context.ACTIVITY_SERVICE);
			while (iterator.hasNext()) {
				ProgressManagerInfo info = iterator.next();
				am.killBackgroundProcesses(info.getPackagerName());
			}
		}
	}
}

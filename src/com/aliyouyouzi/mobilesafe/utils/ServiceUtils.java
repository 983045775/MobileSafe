package com.aliyouyouzi.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

public class ServiceUtils {
	private static final String TAG = "ServiceUtils";

	/**
	 * 判断骚扰拦截服务是否开启
	 * @param context 上下文
	 * @param clazz 拦截服务类
	 * @return 是否开启
	 */
	public static boolean isServiceRunning(Context context,
			Class<? extends Service> clazz) {
		// 获取任务管理器
		ActivityManager manager = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		// 获取正在运行的所有服务
		List<RunningServiceInfo> runningServices = manager
				.getRunningServices(Integer.MAX_VALUE);
		for (RunningServiceInfo runningServiceItem : runningServices) {
			// 每个服务
			ComponentName service = runningServiceItem.service;
			String className = service.getClassName();
			if (className.equals(clazz.getName())) {
				// 说明是开启的
				return true;
			}
		}
		return false;
	}
}

package com.aliyouyouzi.mobilesafe.utils;

import java.io.DataOutputStream;
import java.util.HashSet;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.text.TextUtils;
import android.util.Log;

public class ProgressManagerUtils {

	private static final String TAG = "ProgressManagerUtils";

	/**
	 * 查询正在运行的进程
	 * 
	 * @param context
	 * @return
	 */
	public static int queryRunningProgress(Context context) {
		// boolean root = upgradeRootPermission(context.getPackageCodePath());
		// if (!root) {
		// return 0;
		// }
		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		if (list != null) {
			return list.size();
		}
		return 0;
	}

	/**
	 * 查询所有进程
	 * 
	 * @param context
	 * @return
	 */
	public static int queryAllProgress(Context context) {
		// 设置一个集合用来存储进程名字
		HashSet<String> name = new HashSet<String>();
		// 获取包管理器
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
		for (PackageInfo packageInfo : installedPackages) {
			// 每个应用程序
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			if (!TextUtils.isEmpty(applicationInfo.processName)) {
				name.add(applicationInfo.processName);
			}
			// 每个应用的activity
			ActivityInfo[] activities = packageInfo.activities;
			if (activities != null) {
				for (ActivityInfo activityInfo : activities) {
					if (!TextUtils.isEmpty(activityInfo.processName)) {
						name.add(activityInfo.processName);
					}
				}
			}
			// 每个应用的receiver
			ActivityInfo[] receivers = packageInfo.receivers;
			if (receivers != null) {

				for (ActivityInfo receiver : receivers) {
					if (!TextUtils.isEmpty(receiver.processName)) {
						name.add(receiver.processName);
					}
				}
			}
			// 每个应用的service
			ServiceInfo[] services = packageInfo.services;
			if (services != null) {

				for (ServiceInfo serviceInfo : services) {
					if (!TextUtils.isEmpty(serviceInfo.processName)) {
						name.add(serviceInfo.processName);
					}
				}
			}

			// 每个应用的providers
			ProviderInfo[] providers = packageInfo.providers;
			if (providers != null) {
				for (ProviderInfo providerInfo : providers) {
					if (!TextUtils.isEmpty(providerInfo.processName)) {
						name.add(providerInfo.processName);
					}
				}
			}
		}
		return name.size();
	}

	/**
	 * 返回已用内存
	 * 
	 * @param context
	 * @return
	 */
	public static long queryUserMemory(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		// 创建内存接收器
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		long availMem = outInfo.availMem;
		long totalMem = outInfo.totalMem;
		Log.d(TAG, availMem + "   " + totalMem);
		return totalMem - availMem;
	}

	/**
	 * 查询可用内存
	 * 
	 * @param context
	 * @return
	 */
	public static long queryCanUserMemory(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		// 创建内存接收器
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		long availMem = outInfo.availMem;
		return availMem;
	}

	// /**
	// * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
	// *
	// * @return 应用程序是/否获取Root权限
	// */
	// public static boolean upgradeRootPermission(String pkgCodePath) {
	// Process process = null;
	// DataOutputStream os = null;
	// try {
	// String cmd = "chmod 777 " + pkgCodePath;
	// process = Runtime.getRuntime().exec("su"); // 切换到root帐号
	// os = new DataOutputStream(process.getOutputStream());
	// os.writeBytes(cmd + "\n");
	// os.writeBytes("exit\n");
	// os.flush();
	// process.waitFor();
	// } catch (Exception e) {
	// return false;
	// } finally {
	// try {
	// if (os != null) {
	// os.close();
	// }
	// process.destroy();
	// } catch (Exception e) {
	// }
	// }
	// return true;
	// }
}

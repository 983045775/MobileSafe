package com.example.mobilesafe.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class PackageUtils {

	public static String getVersionName(Context context) {
		PackageManager packageManager = context.getPackageManager();
		// 查询info(信息)
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					"com.example.mobilesafe", 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getVersionCode(Context context) {
		PackageManager packageManager = context.getPackageManager();
		// 查询info(信息)
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					"com.example.mobilesafe", 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}
}

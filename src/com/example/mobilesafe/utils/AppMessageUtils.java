package com.example.mobilesafe.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.example.mobilesafe.db.AppLockDao;
import com.example.mobilesafe.domain.AppMessageInfo;

public class AppMessageUtils {

	private static AppLockDao mDao;

	/**  
	 * 获取所有应用APP信息
	 * 
	 * @param context
	 * @return
	 */
	public static List<AppMessageInfo> getAllAppMessage(Context context) {
		mDao = new AppLockDao(context);
		PackageManager manager = context.getPackageManager();
		List<ApplicationInfo> list = manager.getInstalledApplications(0);
		List<AppMessageInfo> listDatas = new ArrayList<AppMessageInfo>();
		for (ApplicationInfo info : list) {
			AppMessageInfo message = new AppMessageInfo();
			message.setPackageName(info.packageName);// 包名
			message.setIcon(info.loadIcon(manager));// 图标
			message.setAppName(info.loadLabel(manager).toString());// 应用名字
			if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				// 系统应用
				message.setSystem(true);
			} else {
				// 不是系统应用
				message.setSystem(false);
			}
			if (mDao.query(info.packageName)) {
				message.setLock(true);
			} else {
				message.setLock(false);
			}

			listDatas.add(message);
		}
		return listDatas;
	}

	/**
	 * 根据包名进行查询应用信息
	 * 
	 * @param packageName
	 *            包名
	 * @return
	 */

	public static AppMessageInfo getAppMessage(Context context,
			String packageName) {
		PackageManager mPm = context.getPackageManager();
		List<ApplicationInfo> list = mPm.getInstalledApplications(0);
		for (ApplicationInfo info : list) {
			if (info.packageName.equals(packageName)) {
				AppMessageInfo appMessageInfo = new AppMessageInfo();
				appMessageInfo.setAppName(info.loadLabel(mPm).toString());
				appMessageInfo.setIcon(info.loadIcon(mPm));
				appMessageInfo.setLock(false);
				appMessageInfo.setPackageName(packageName);
				if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
					appMessageInfo.setSystem(true);
				} else {
					appMessageInfo.setSystem(false);
				}
				return appMessageInfo;
			}
		}
		return null;
	}
}

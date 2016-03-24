package com.example.mobilesafe.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.format.Formatter;

import com.example.mobilesafe.domain.AppManagerInfo;

public class AppManagerDatasProvide {

	public static List<AppManagerInfo> getAllAppMessage(Context context) {
		List<AppManagerInfo> list = new ArrayList<AppManagerInfo>();
		// 获取包管理器
		PackageManager pm = context.getPackageManager();
		// 查看所有安装的程序
		List<ApplicationInfo> lists = pm.getInstalledApplications(0);
		for (ApplicationInfo info : lists) {
			AppManagerInfo appinfo = new AppManagerInfo();

			appinfo.setPackageName(info.packageName);// 包名
			appinfo.setAppName(info.loadLabel(pm).toString());// 应用名字
			appinfo.setIcon(info.loadIcon(pm));// 图标
			appinfo.setSize(Formatter.formatFileSize(context, new File(
					info.sourceDir).length())); // 文件大小
			if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				// 是系统应用
				appinfo.setAndroidApp(true);// 系统应用
			} else {
				appinfo.setAndroidApp(false);// 系统应用
			}

			if ((info.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
				appinfo.setSdcard(true);// 在sd卡安装的
			} else {
				appinfo.setSdcard(false);// 在手机安装的
			}
			list.add(appinfo);
		}
		return list;
	}
}

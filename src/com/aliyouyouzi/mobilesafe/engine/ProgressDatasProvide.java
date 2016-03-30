package com.aliyouyouzi.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.aliyouyouzi.mobilesafe.domain.ProgressManagerInfo;
import com.aliyouyouzi.mobilesafe.R;

public class ProgressDatasProvide {
	/**
	 * 查询所有的进程信息
	 * 
	 * @param context
	 *            上下文
	 * @return 集合装有ProgressManagerInfo对象
	 */
	public static List<ProgressManagerInfo> getProgressMessage(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();

		List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();

		List<ProgressManagerInfo> list = new ArrayList<ProgressManagerInfo>();
		if (processes != null) {
			for (int i = 0; i < processes.size(); i++) {
				RunningAppProcessInfo process = processes.get(i);

				// process.pid;//进程id
				String packageName = process.processName;// 进程id包名

				Drawable icon = null;// 图标
				String name = null;// 应用的名称
				long memory = 0;
				boolean isSystem = false;
				try {
					
					ApplicationInfo applicationInfo = pm.getApplicationInfo(
							packageName, 0);
					// 应用的图标
					icon = applicationInfo.loadIcon(pm);
					name = applicationInfo.loadLabel(pm).toString();

					int flags = applicationInfo.flags;

					if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
						isSystem = true;
					} else {
						isSystem = false;
					}

				} catch (NameNotFoundException e) {
					icon = context.getResources().getDrawable(
							R.drawable.ic_launcher);
					name = packageName;
					isSystem = true;
				}

				// 内存信息
				android.os.Debug.MemoryInfo memoryInfo = am
						.getProcessMemoryInfo(new int[] { process.pid })[0];
				memory = memoryInfo.getTotalPss() * 1024;

				ProgressManagerInfo info = new ProgressManagerInfo();
				info.setIcon(icon);
				info.setAppName(name);
				info.setPackagerName(packageName);
				info.setUserMemory(memory);// 占用的memory
				info.setSystem(isSystem);

				list.add(info);
			}
		}
		return list;
	}
}

package com.aliyouyouzi.mobilesafe.receiver;

import java.util.List;
import java.util.ListIterator;

import com.aliyouyouzi.mobilesafe.domain.ProgressManagerInfo;
import com.aliyouyouzi.mobilesafe.engine.ProgressDatasProvide;
import com.aliyouyouzi.mobilesafe.utils.ProgressManagerUtils;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

public class ClearProgressReceiver extends BroadcastReceiver {

	private static final String TAG = "ClearProgressReceiver";
	private ActivityManager ma;

	public void onReceive(Context context, Intent intent) {
		ma = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);

		int count = 0;
		long memory = 0;
		// 获取所有的进程信息
		List<ProgressManagerInfo> list = ProgressDatasProvide
				.getProgressMessage(context);
		ListIterator<ProgressManagerInfo> iterator = list.listIterator();
		while (iterator.hasNext()) {
			count++;
			ProgressManagerInfo info = iterator.next();
			ma.killBackgroundProcesses(info.getPackagerName());
			memory += info.getUserMemory();
		}
		Toast.makeText(
				context,
				"清理了" + count + "条进程,和"
						+ Formatter.formatFileSize(context, memory) + "的垃圾",
				Toast.LENGTH_SHORT).show();
	}
}

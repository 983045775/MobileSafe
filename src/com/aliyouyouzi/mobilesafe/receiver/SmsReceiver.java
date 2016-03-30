package com.aliyouyouzi.mobilesafe.receiver;

import com.aliyouyouzi.mobilesafe.service.GPSService;
import com.aliyouyouzi.mobilesafe.utils.Constants;
import com.aliyouyouzi.mobilesafe.utils.PreferencesUtils;
import com.aliyouyouzi.mobilesafe.R;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * 用于短信过滤,查看安全号码发过来的短信,进行远程操作,接收广播的类
 * 
 * @author liu
 * 
 */
public class SmsReceiver extends BroadcastReceiver {

	private static final String TAG = "SmsReceiver";

	public void onReceive(Context context, Intent intent) {
		// 获取设备管理器对象
		DevicePolicyManager manager = (DevicePolicyManager) context
				.getSystemService(context.DEVICE_POLICY_SERVICE);
		// 设置哪一个是进行设备管理监听的对象
		ComponentName who = new ComponentName(context, SafeAdminReceiver.class);
		// 首先查看是否受保护
		boolean flag = PreferencesUtils.getBoolean(context,
				Constants.SJFD_PROTECTION);
		Log.d(TAG, "#*location*#" + flag);
		if (!flag) {
			// 说明为false不受到保护
			return;
		}
		Log.d(TAG, "接收到了短信了");
		// 获取短信内容
		Object[] objects = (Object[]) intent.getExtras().get("pdus");
		Log.d(TAG, objects.length + "");
		for (Object obj : objects) {
			SmsMessage pdu = SmsMessage.createFromPdu((byte[]) obj);
			String body = pdu.getMessageBody();
			if (body.equals("#*location*#")) {
				// 说明是GPS追踪
				Log.d(TAG, "GPS追踪");
				// 开启服务
				Intent GPSService = new Intent(context, GPSService.class);
				context.startService(GPSService);
				abortBroadcast();
			} else if (body.equals("#*alarm*#")) {
				// 播放报警音乐
				Log.d(TAG, "播放报警");
				MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
				player.setLooping(true);
				player.start();
				abortBroadcast();
			} else if (body.equals("#*wipedata*#")) {
				// 远程数据删除
				Log.d(TAG, "远程数据删除");
				if (manager.isAdminActive(who)) {
					manager.wipeData(0);
				}
				abortBroadcast();
			} else if (body.equals("#*lockscreen*#")) {
				// 远程锁屏
				Log.d(TAG, "远程锁屏");
				if (manager.isAdminActive(who)) {
					manager.resetPassword("123", 0);
					// 设置锁屏
					manager.lockNow();
				}
				abortBroadcast();
			}
		}
	}
}

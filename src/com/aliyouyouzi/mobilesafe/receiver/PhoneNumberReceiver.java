package com.aliyouyouzi.mobilesafe.receiver;

import com.aliyouyouzi.mobilesafe.utils.Constants;
import com.aliyouyouzi.mobilesafe.utils.PreferencesUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 用于监听开机自动检测手机号码是否是当前手机号,如果不是将进行给安全号码发送被盗短信
 * 
 * @author liu
 * 
 */
public class PhoneNumberReceiver extends BroadcastReceiver {

	private static final String TAG = "PhoneNumberReceiver";

	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "读取到了开机了");
		// 首先获取是否开启保护
		boolean flag = PreferencesUtils.getBoolean(context,
				Constants.SJFD_PROTECTION);
		if (!flag) {
			// 不需要保护
			return;
		}
		// 一旦开机就执行
		TelephonyManager teleManager = (TelephonyManager) context
				.getSystemService(context.TELEPHONY_SERVICE);
		// 获取当前号码
		String currentNumber = teleManager.getSimSerialNumber();
		// 获取保存的本机号码
		String saveNumber = PreferencesUtils.getString(context,
				Constants.SJFD_SMS);
		if (!saveNumber.equals(currentNumber)) {
			// 说明手机被盗啦......
			Log.d(TAG, "手机被盗了.......");
			// 进行短信的发送
			SmsManager sms = SmsManager.getDefault();
			// 获取安全号码
			String surenessNumber = PreferencesUtils.getString(context,
					Constants.SJFD_NUMBER);
			sms.sendTextMessage(surenessNumber, null, "shouji ni bei dao la",
					null, null);
		}
	}
}

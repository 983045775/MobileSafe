package com.example.mobilesafe.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.example.mobilesafe.activity.BlackSmsPhoneSafe;
import com.example.mobilesafe.db.BlackSafeDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallSmsService extends Service {

	private static final String TAG = "CallSmsService";
	private BlackSafeDao dao = null;

	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onCreate() {
		super.onCreate();
		dao = new BlackSafeDao(getApplicationContext());
		Log.d(TAG, "骚扰拦截服务开启了");
		manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		// 电话拦截
		manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		// 短信拦截
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		smsReceiver = new SmsReceiver();
		registerReceiver(smsReceiver, filter);
	}

	private class SmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			Object[] objs = (Object[]) extras.get("pdus");
			for (Object obj : objs) {
				SmsMessage pdu = SmsMessage.createFromPdu((byte[]) obj);
				String address = pdu.getOriginatingAddress();
				// 开始黑名单的判断
				int find = dao.find(address);
				if (find == BlackSmsPhoneSafe.BLACK_ALL
						|| find == BlackSmsPhoneSafe.BLACK_SMS) {
					// 说明需要拦截
					Log.d(TAG, "需要短信拦截");
					abortBroadcast();
				}
			}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 取消电话拦截
		unregisterReceiver(smsReceiver);
		manager.listen(listener, PhoneStateListener.LISTEN_NONE);
		Log.d(TAG, "骚扰拦截服务关闭了");
	}

	private PhoneStateListener listener = new PhoneStateListener() {
		public void onCallStateChanged(int state, final String incomingNumber) {
			// #CALL_STATE_IDLE 空闲
			// #CALL_STATE_RINGING 响铃
			// #CALL_STATE_OFFHOOK 接听

			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				// 空闲
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				// 响铃
				int find = dao.find(incomingNumber);
				if (find == BlackSmsPhoneSafe.BLACK_ALL
						|| find == BlackSmsPhoneSafe.BLACK_CALL) {
					// ServiceManager.getService(Context.TELEPHONY_SERVICE)
					// android.os.ServiceManager
					try {
						Class<?> clazz = Class
								.forName("android.os.ServiceManager");
						Method method = clazz.getDeclaredMethod("getService",
								String.class);
						IBinder binder = (IBinder) method.invoke(null,
								Context.TELEPHONY_SERVICE);
						ITelephony telephony = ITelephony.Stub
								.asInterface(binder);
						boolean endCall = telephony.endCall();
						Log.d(TAG, endCall + "");
						// 进行通话记录的删除
						final Uri url = Uri.parse("content://call_log/calls");
						final ContentResolver resolver = getContentResolver();
						// 进行注册一个监听
						resolver.registerContentObserver(url, true,
								new ContentObserver(new Handler()) {
									@Override
									public void onChange(boolean selfChange) {
										super.onChange(selfChange);
										// 一旦改变了,就删除
										String where = "number=?";
										String[] selectionArgs = new String[] { incomingNumber };
										int delete = resolver.delete(url,
												where, selectionArgs);
										Log.d(TAG, delete + "");
									}
								});

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				// 接听
				break;

			}
		};
	};
	private TelephonyManager manager;
	private SmsReceiver smsReceiver;
}

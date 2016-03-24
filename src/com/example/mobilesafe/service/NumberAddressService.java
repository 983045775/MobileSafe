package com.example.mobilesafe.service;

import com.example.mobilesafe.db.NumberAddressDao;
import com.example.mobilesafe.utils.MyToast;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

public class NumberAddressService extends Service {

	private static final String TAG = "NumberAddressService";
	private TelephonyManager manager;
	private MyToast mToast;

	public IBinder onBind(Intent intent) {
		return null;
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			String address = NumberAddressDao.find(NumberAddressService.this,
					number);
			mToast.show(address);
		}
	};

	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "号码归属地服务开启了");
		manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		// 监听手机
		manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		mToast = new MyToast(this);
		// 设置一个监听拨打电话的
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(receiver, filter);
	}

	private PhoneStateListener listener = new PhoneStateListener() {

		// 监听拨打电话状态
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			// CALL_STATE_IDLE 空闲
			// CALL_STATE_RINGING 响铃
			// CALL_STATE_OFFHOOK 接听
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				mToast.hide();
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				// 查询号码
				String address = NumberAddressDao.find(
						NumberAddressService.this, incomingNumber);
				mToast.show(address);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				break;
			}
		}
	};

	public void onDestroy() {
		super.onDestroy();
		manager.listen(listener, PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(receiver);
		Log.d(TAG, "号码归属地服务关闭了");
	}
}

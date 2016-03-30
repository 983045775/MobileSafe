package com.aliyouyouzi.mobilesafe.activity;

import com.aliyouyouzi.mobilesafe.engine.SmsProvide;
import com.aliyouyouzi.mobilesafe.engine.SmsProvide.OnBackupsListener;
import com.aliyouyouzi.mobilesafe.service.WatchDog1Service;
import com.aliyouyouzi.mobilesafe.service.WatchDog2Service;
import com.aliyouyouzi.mobilesafe.utils.ServiceUtils;
import com.aliyouyouzi.mobilesafe.view.Setting_view_item;
import com.aliyouyouzi.mobilesafe.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * 常用工具页面
 * 
 * @author liu
 * 
 */
public class CommonToolsActivity extends Activity implements OnClickListener {

	private Setting_view_item mToolnumber;
	private Setting_view_item mCommonNumber;
	private Setting_view_item mCommonBackup;
	private Setting_view_item mCommonRecover;
	private Setting_view_item mAppLock;
	private Setting_view_item mWatchDog1;
	private Setting_view_item mWatchDog2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_tool);

		mToolnumber = (Setting_view_item) findViewById(R.id.common_querynumber);
		mCommonNumber = (Setting_view_item) findViewById(R.id.common_common_number);
		mCommonBackup = (Setting_view_item) findViewById(R.id.common_sms_backups);
		mCommonRecover = (Setting_view_item) findViewById(R.id.common_sms_recover);
		mAppLock = (Setting_view_item) findViewById(R.id.common_app_lock);
		mWatchDog1 = (Setting_view_item) findViewById(R.id.common_app_dog1);
		mWatchDog2 = (Setting_view_item) findViewById(R.id.common_app_dog2);

		mToolnumber.setOnClickListener(this);
		mCommonNumber.setOnClickListener(this);
		mCommonRecover.setOnClickListener(this);
		mCommonBackup.setOnClickListener(this);
		mAppLock.setOnClickListener(this);
		mWatchDog1.setOnClickListener(this);
		mWatchDog2.setOnClickListener(this);
	}

	protected void onResume() {
		// 检测按钮,UI改变
		if (ServiceUtils.isServiceRunning(getApplicationContext(),
				WatchDog1Service.class)) {
			mWatchDog1.clickOnOff(true);
		} else {
			mWatchDog1.clickOnOff(false);
		}

		if (ServiceUtils.isServiceRunning(getApplicationContext(),
				WatchDog2Service.class)) {
			mWatchDog2.clickOnOff(true);
		} else {
			mWatchDog2.clickOnOff(false);
		}
		super.onRestart();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.common_common_number:
			// 常用号码查询
			Intent commIntent = new Intent(this, CommonNumberActivity.class);
			startActivity(commIntent);
			break;
		case R.id.common_querynumber:
			// 号码归属地
			Intent intent = new Intent(this, ToolQueryNumberActivity.class);
			startActivity(intent);
			break;

		case R.id.common_sms_backups:
			// 开启进度提示框
			final ProgressDialog dialog = new ProgressDialog(
					CommonToolsActivity.this);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.show();
			// 短信备份
			SmsProvide.smsBackups(CommonToolsActivity.this,
					new OnBackupsListener() {

						public void success() {
							Toast.makeText(CommonToolsActivity.this, "备份成功~",
									Toast.LENGTH_SHORT).show();
							dialog.dismiss();
						}

						public void progress(int progress) {
							dialog.setProgress(progress);
						}

						public void fail() {
							Toast.makeText(CommonToolsActivity.this, "备份失败 ~",
									Toast.LENGTH_SHORT).show();
							dialog.dismiss();
						}

						public void allCount(int count) {
							dialog.setMax(count);
						}
					});
			break;

		case R.id.common_sms_recover:
			// 短信恢复
			final ProgressDialog recoverDialog = new ProgressDialog(
					CommonToolsActivity.this);
			recoverDialog.setCanceledOnTouchOutside(false);
			recoverDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			recoverDialog.show();
			SmsProvide.smsRecover(CommonToolsActivity.this,
					new OnBackupsListener() {
						public void success() {
							Toast.makeText(CommonToolsActivity.this, "恢复成功~",
									Toast.LENGTH_SHORT).show();
							recoverDialog.dismiss();
						}

						public void progress(int progress) {
							recoverDialog.setProgress(progress);
						}

						public void fail() {
							Toast.makeText(CommonToolsActivity.this, "恢复失败~",
									Toast.LENGTH_SHORT).show();
							recoverDialog.dismiss();
						}

						public void allCount(int count) {
							recoverDialog.setMax(count);
						}
					});
			break;
		case R.id.common_app_lock:
			// 跳转到程序锁界面
			Intent AppLockIntent = new Intent(CommonToolsActivity.this,
					AppLockActivity.class);
			startActivity(AppLockIntent);
			break;
		case R.id.common_app_dog1:
			// 检测电子狗服务是否开启
			if (ServiceUtils.isServiceRunning(getApplicationContext(),
					WatchDog1Service.class)) {
				// 开启的,咱关闭它
				Intent dogIntent = new Intent(CommonToolsActivity.this,
						WatchDog1Service.class);
				stopService(dogIntent);
				// UI改变
				mWatchDog1.clickOnOff(false);
			} else {
				// 关闭的,咱开启它
				Intent dogIntent = new Intent(CommonToolsActivity.this,
						WatchDog1Service.class);
				startService(dogIntent);
				// Ui改变
				mWatchDog1.clickOnOff(true);
			}
			break;
		case R.id.common_app_dog2:
			// <intent-filter>
			// <action android:name="android.intent.action.MAIN" />
			// <action android:name="android.settings.ACCESSIBILITY_SETTINGS" />
			// <!-- Wtf... this action is bogus! Can we remove it? -->
			// <action android:name="ACCESSIBILITY_FEEDBACK_SETTINGS" />
			// <category android:name="android.intent.category.DEFAULT" />
			// <category android:name="android.intent.category.VOICE_LAUNCH" />
			// </intent-filter>
			Intent AccessibilityIntent = new Intent();
			AccessibilityIntent
					.setAction("android.settings.ACCESSIBILITY_SETTINGS");
			AccessibilityIntent
					.addCategory("android.intent.category.VOICE_LAUNCH");
			AccessibilityIntent.addCategory("android.intent.category.DEFAULT");
			startActivity(AccessibilityIntent);
			break;
		}

	}
}

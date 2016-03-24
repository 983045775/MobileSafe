package com.example.mobilesafe.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.mobilesafe.R;
import com.example.mobilesafe.receiver.SafeAdminReceiver;

public class SjfdSetupFourActivity extends BaseSetupActivity {

	protected static final int REQUEST_CODE_ENABLE_ADMIN = 100;
	private RelativeLayout rl_admin;
	private ImageView sjfd_setup4_iv;
	private ComponentName who;
	private DevicePolicyManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.sjfd_setup_four);

		rl_admin = (RelativeLayout) findViewById(R.id.sjfd_setup4_rl_admin);
		sjfd_setup4_iv = (ImageView) findViewById(R.id.sjfd_setup4_iv);
		// 获取设备管理接收器
		manager = (DevicePolicyManager) getSystemService(this.DEVICE_POLICY_SERVICE);
		who = new ComponentName(this, SafeAdminReceiver.class);
		// 在这里初始化图标
		sjfd_setup4_iv
				.setImageResource(manager.isAdminActive(who) ? R.drawable.admin_activated
						: R.drawable.admin_inactivated);
		rl_admin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 进行检查是否激活管理员
				if (manager.isAdminActive(who)) {
					// 说明是已经激活了,需要取消激活
					manager.resetPassword("", 0);
					manager.removeActiveAdmin(who);
					sjfd_setup4_iv
							.setImageResource(R.drawable.admin_inactivated);
				} else {
					// 说明是没有激活需要激活
					Intent intent = new Intent(
							DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, who);
					intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
							"手机卫士");
					startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_ENABLE_ADMIN:
			// 说明是激活的页面返回的
			if (manager.isAdminActive(who)) {
				// 说明是激活的
				sjfd_setup4_iv.setImageResource(R.drawable.admin_activated);
			}
			break;

		default:
			break;
		}
	}

	protected boolean setupNext() {
		if (!manager.isAdminActive(who)) {
			Toast.makeText(this, "必须激活设备管理员才能够开启手机防盗功能", Toast.LENGTH_SHORT)
					.show();
			return true;
		}
		Intent intent = new Intent(this, SjfdSetupFiveActivity.class);
		startActivity(intent);
		return false;
	}

	protected boolean setuppre() {
		Intent intent = new Intent(this, SjfdSetupThreeActivity.class);
		startActivity(intent);
		return false;
	}
}

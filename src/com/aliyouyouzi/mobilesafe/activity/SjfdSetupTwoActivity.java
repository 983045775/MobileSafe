package com.aliyouyouzi.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aliyouyouzi.mobilesafe.utils.Constants;
import com.aliyouyouzi.mobilesafe.utils.PreferencesUtils;
import com.aliyouyouzi.mobilesafe.R;

public class SjfdSetupTwoActivity extends BaseSetupActivity implements
		OnClickListener {

	private ImageView iv_lock;
	private RelativeLayout rv_sim;
	// 手机串号
	private String sms_number;

	@SuppressWarnings("static-access")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sjfd_setup_two);
		rv_sim = (RelativeLayout) findViewById(R.id.sjfd_setup_rv_sim);
		rv_sim.setOnClickListener(this);
		iv_lock = (ImageView) findViewById(R.id.sjfd_setup_iv_lock);
		TelephonyManager man = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
		sms_number = man.getSimSerialNumber();
		if (TextUtils.isEmpty(sms_number)) {
			sms_number = "haha";
		}
		String sms = PreferencesUtils.getString(this, Constants.SJFD_SMS);
		if (TextUtils.isEmpty(sms)) {
			iv_lock.setImageResource(R.drawable.unlock);
		} else {
			iv_lock.setImageResource(R.drawable.lock);
		}
	}

	protected boolean setupNext() {
		String sms = PreferencesUtils.getString(this, Constants.SJFD_SMS);
		if (TextUtils.isEmpty(sms)) {
			Toast.makeText(this, "要使用手机防盗功能必须绑定SIM卡", Toast.LENGTH_SHORT)
					.show();
			return true;
		}
		Intent intent = new Intent(this, SjfdSetupThreeActivity.class);
		startActivity(intent);
		return false;
	}

	protected boolean setuppre() {
		Intent intent = new Intent(this, SjfdSetupOneActivity.class);
		startActivity(intent);
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sjfd_setup_rv_sim:
			// 说明点击了按钮,进行绑定
			String sms = PreferencesUtils.getString(this, Constants.SJFD_SMS);
			if (TextUtils.isEmpty(sms)) {
				// 说明这个不存在
				PreferencesUtils
						.putString(this, Constants.SJFD_SMS, sms_number);
				iv_lock.setImageResource(R.drawable.lock);
			} else {
				// 说明存在
				PreferencesUtils.putString(this, Constants.SJFD_SMS, null);
				iv_lock.setImageResource(R.drawable.unlock);
			}
			break;
		default:
			break;
		}
	}
}

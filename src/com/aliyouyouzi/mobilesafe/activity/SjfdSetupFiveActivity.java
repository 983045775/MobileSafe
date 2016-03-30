package com.aliyouyouzi.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.aliyouyouzi.mobilesafe.utils.Constants;
import com.aliyouyouzi.mobilesafe.utils.PreferencesUtils;
import com.aliyouyouzi.mobilesafe.R;

public class SjfdSetupFiveActivity extends BaseSetupActivity {

	private CheckBox cb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sjfd_setup_five);
		cb = (CheckBox) findViewById(R.id.sjfd_checkbox);
		cb.setChecked(PreferencesUtils.getBoolean(this,
				Constants.SJFD_PROTECTION));
		// 添加改变事件
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// 发生改变进行记录
				PreferencesUtils.putBoolean(SjfdSetupFiveActivity.this,
						Constants.SJFD_PROTECTION, isChecked);
			}
		});
	}

	protected boolean setupNext() {
		if (!cb.isChecked()) {
			Toast.makeText(this, "必须开启防盗保护才能进入手机防盗功能", Toast.LENGTH_SHORT)
					.show();
			return true;
		}
		// 说明勾选了
		PreferencesUtils.putBoolean(this, Constants.SJFD_SETUP, true);
		Intent intent = new Intent(this, SjfdSetupEndActivity.class);
		startActivity(intent);
		return false;
	}

	protected boolean setuppre() {
		Intent intent = new Intent(this, SjfdSetupFourActivity.class);
		startActivity(intent);
		return false;
	}
}

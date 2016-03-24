package com.example.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobilesafe.R;
import com.example.mobilesafe.utils.Constants;
import com.example.mobilesafe.utils.PreferencesUtils;

public class SjfdSetupThreeActivity extends BaseSetupActivity {

	private static final int CONTACTS_NUMBER = 100;
	private EditText ev_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sjfd_setup_three);
		ev_number = (EditText) findViewById(R.id.sjfd_setup3_ev_number);
		// 进行安全号码回显
		String number = PreferencesUtils.getString(this, Constants.SJFD_NUMBER);
		ev_number.setText(number);
		if (number != null) {
			ev_number.setSelection(number.length());
		}
	}

	public void addContacts(View view) {
		Intent intent = new Intent(this, ContactsItemActivity2.class);
		startActivityForResult(intent, CONTACTS_NUMBER);
	}

	protected boolean setupNext() {
		String number = ev_number.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			// 为空说明没有设置安全号码
			Toast.makeText(this, "使用手机防盗功能必须设置安全号码", Toast.LENGTH_SHORT).show();
			return true;
		}
		// 说明有文字显示
		PreferencesUtils.putString(this, Constants.SJFD_NUMBER, number);
		Intent intent = new Intent(this, SjfdSetupFourActivity.class);
		startActivity(intent);
		return false;
	}

	protected boolean setuppre() {
		Intent intent = new Intent(this, SjfdSetupTwoActivity.class);
		startActivity(intent);
		return false;
	}

	/**
	 * 一旦有forResult的界面finsh掉,就调用这个方法
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CONTACTS_NUMBER:
			// 说明是item它finsh回来的
			if (data != null) {
				Bundle bundle = data.getExtras();
				String result = bundle.getString(Constants.NUMBER);
				// 将数据显示到edittext上
				ev_number.setText(result);
				// 设置光标到最后一位
				if (result != null)
					ev_number.setSelection(result.length());
				break;
			}
		}
	}

}

package com.example.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.R;
import com.example.mobilesafe.domain.AppMessageInfo;
import com.example.mobilesafe.utils.AppMessageUtils;

public class LockActivity extends Activity {

	private ImageView mIvIcon;
	private EditText mTvEdit;
	private TextView mTvName;
	private String packageName;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock);
		mTvName = (TextView) findViewById(R.id.lock_et_name);
		mTvEdit = (EditText) findViewById(R.id.lock_et_pwd);
		mIvIcon = (ImageView) findViewById(R.id.lock_iv_icon);

		Intent intent = getIntent();
		packageName = intent.getStringExtra("name");
		// 根据包名查询应用信息,进行赋值
		AppMessageInfo info = AppMessageUtils.getAppMessage(this, packageName);
		if (info.getAppName() != null) {
			mTvName.setText(info.getAppName());
		} else {
			mTvName.setText(packageName);
		}
		mIvIcon.setImageDrawable(info.getIcon());

	}

	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);

		finish();
	}

	public void clickok(View view) {
		// 进行非空校验
		String pwd = mTvEdit.getText().toString().trim();
		if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}

		if (!pwd.equals("123")) {
			return;
		}
		// 发送一个需求关闭的广播
		Intent intent = new Intent();
		// 获取包名
		intent.putExtra("name", packageName);
		intent.setAction("org.lc.close.lock");
		sendBroadcast(intent);
		finish();
	}
}

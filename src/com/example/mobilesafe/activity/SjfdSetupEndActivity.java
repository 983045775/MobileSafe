package com.example.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobilesafe.R;
import com.example.mobilesafe.utils.Constants;
import com.example.mobilesafe.utils.PreferencesUtils;

public class SjfdSetupEndActivity extends Activity {

	private static final String TAG = "SjfdSetupEndActivity";
	private ImageView iv_lock;
	private TextView sjfd_end_number;
	private RelativeLayout rl_proPhone;
	private RelativeLayout rl_openSetup;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sjfd_setup_end);
		iv_lock = (ImageView) findViewById(R.id.setup_end_ivlock);
		sjfd_end_number = (TextView) findViewById(R.id.sjfd_end_number);
		rl_proPhone = (RelativeLayout) findViewById(R.id.rl_proPhone);
		rl_openSetup = (RelativeLayout) findViewById(R.id.rl_openSetup);
		// 添加图片
		iv_lock.setImageResource(PreferencesUtils.getBoolean(this,
				Constants.SJFD_PROTECTION) ? R.drawable.lock
				: R.drawable.unlock);
		Log.i(TAG, PreferencesUtils.getBoolean(this, Constants.SJFD_PROTECTION)
				+ "");
		// 添加号码
		sjfd_end_number.setText(PreferencesUtils.getString(this,
				Constants.SJFD_NUMBER));

		rl_proPhone.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 获取当前是否保护
				boolean pro = PreferencesUtils.getBoolean(
						SjfdSetupEndActivity.this, Constants.SJFD_PROTECTION);
				if (pro) {
					// 受到保护
					PreferencesUtils.putBoolean(SjfdSetupEndActivity.this,
							Constants.SJFD_PROTECTION, false);
					// 更换图片
					iv_lock.setImageResource(R.drawable.unlock);
				} else {
					// 不受到保护
					PreferencesUtils.putBoolean(SjfdSetupEndActivity.this,
							Constants.SJFD_PROTECTION, true);
					// 更换图片
					iv_lock.setImageResource(R.drawable.lock);
				}
			}
		});
		rl_openSetup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 进行跳转到第一个界面
				Intent intent = new Intent(SjfdSetupEndActivity.this,
						SjfdSetupOneActivity.class);
				startActivity(intent);
				// 来一个动画效果,第一个是来的效果,第二个参数是出去的效果
				overridePendingTransition(R.anim.next_int, R.anim.next_exit);
				finish();
			}
		});
	}
}

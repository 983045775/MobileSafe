package com.example.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.R;
import com.example.mobilesafe.db.BlackSafeDao;
import com.example.mobilesafe.domain.BlackInfo;
/**
 * 黑名单添加
 * @author liu
 *
 */
public class BlackAddActivity extends Activity implements OnClickListener {

	private static final String TAG = "BlackAddActivity";
	public static final String ACTION = "add";
	public static final String UPDATA = "updata";
	public static final String ADD = "add";
	public static final String TYPE = "type";
	public static final String POSITION = "position";
	public static final String NUMBER = "number";
	private Button save;
	private Button unsave;
	private TextView black_tv_title;
	private RadioGroup group;
	private RadioButton all;
	private RadioButton sms;
	private RadioButton call;
	private EditText black_et_number;
	private int flag = -1;
	private BlackSafeDao dao;
	private boolean mUpdata = false;
	private int position = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_add);

		dao = new BlackSafeDao(this);
		black_tv_title = (TextView) findViewById(R.id.black_tv_title);
		all = (RadioButton) findViewById(R.id.black_rbt_all);
		call = (RadioButton) findViewById(R.id.black_rbt_call);
		sms = (RadioButton) findViewById(R.id.black_rbt_sms);
		group = (RadioGroup) findViewById(R.id.black_rg);
		save = (Button) findViewById(R.id.black_save);
		unsave = (Button) findViewById(R.id.black_unsave);
		black_et_number = (EditText) findViewById(R.id.black_et_number);
		// 添加监听
		save.setOnClickListener(this);
		unsave.setOnClickListener(this);
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				flag = checkedId;
			}
		});
		// 进行动作的判断
		Intent intent = getIntent();
		String data = intent.getAction();
		if (data.equals(UPDATA)) {
			position = intent.getIntExtra(POSITION, -1);
			mUpdata = true;
			// title的更新
			black_tv_title.setText("更新黑名单");
			// 号码锁定
			black_et_number.setEnabled(false);
			black_et_number.setFocusable(false);
			// 号码重现
			String number = intent.getStringExtra(NUMBER);
			black_et_number.setText(number);
			// 拦截类型显示
			int type = Integer.parseInt(intent.getStringExtra(TYPE));
			switch (type) {
			case BlackSmsPhoneSafe.BLACK_CALL:
				// 设置哪个radiobutton进行选定
				call.setChecked(true);
				break;
			case BlackSmsPhoneSafe.BLACK_SMS:
				sms.setChecked(true);
				break;
			case BlackSmsPhoneSafe.BLACK_ALL:
				all.setChecked(true);
				break;

			}
			// btn改变text
			save.setText("更新");
		}
	}

	/**
	 * 按钮点击监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.black_save:
			// 保存
			preSave();
			break;

		case R.id.black_unsave:
			// 取消
			preUnsave();
			break;
		}
	}

	/**
	 * 不保存
	 * 
	 */
	private void preUnsave() {
		finish();
	}

	/**
	 * 不保存
	 */
	private void preSave() {
		// 判断号码是否为空
		String number = black_et_number.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			Toast.makeText(BlackAddActivity.this, "号码不能为空", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		if (flag == -1) {
			Toast.makeText(BlackAddActivity.this, "必须选择拦截类型",
					Toast.LENGTH_SHORT).show();
			return;
		}
		int type = -1;
		switch (flag) {
		case R.id.black_rbt_call: // 0
			type = BlackSmsPhoneSafe.BLACK_CALL;
			break;
		case R.id.black_rbt_sms: // 1
			type = BlackSmsPhoneSafe.BLACK_SMS;
			break;
		case R.id.black_rbt_all: // 2
			type = BlackSmsPhoneSafe.BLACK_ALL;
			break;
		}
		if (mUpdata) {
			dao.updata(number, type + "");
			Intent data = new Intent();
			data.putExtra(TYPE, type);
			data.putExtra(POSITION, position);
			setResult(Activity.RESULT_CANCELED, data);
		} else {
			dao.add(number, type + "");
			// 之前把数据存起来
			Intent data = new Intent();
			BlackInfo info = new BlackInfo();
			info.setNumber(number);
			info.setType(type + "");
			data.putExtra("info", info);
			setResult(Activity.RESULT_OK, data);
		}
		finish();
	}
}

package com.aliyouyouzi.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyouyouzi.mobilesafe.db.NumberAddressDao;
import com.aliyouyouzi.mobilesafe.R;

public class ToolQueryNumberActivity extends Activity implements
		OnClickListener, TextWatcher {
	private static final String TAG = "ToolQueryNumberActivity";
	private TextView mTv_Result;
	private EditText mEv_number;
	private Button mBt_query;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tool_querynumber);
		// 查找控件
		mBt_query = (Button) findViewById(R.id.tool_query);
		mEv_number = (EditText) findViewById(R.id.tool_query_number);
		mTv_Result = (TextView) findViewById(R.id.tool_query_result);
		mBt_query.setOnClickListener(this);
		mEv_number.addTextChangedListener(this);
	}

	public void onClick(View v) {
		// 非空判断
		String number = mEv_number.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			mEv_number.startAnimation(shake);
			Toast.makeText(this, "查询号码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		String result = NumberAddressDao.find(this, number);
		mTv_Result.setText("归属地 : " + result);
		Log.d(TAG, "进行数据库号码的查询");
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		//当文字改变的时候
		String result = NumberAddressDao.find(this, s.toString());
		mTv_Result.setText("归属地 : " + result);
	}

	@Override
	public void afterTextChanged(Editable s) {
		
	}
}

package com.example.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mobilesafe.R;

public class SegmentedControl extends LinearLayout implements OnClickListener {

	private TextView mLock;
	private TextView mUnLock;
	private OnSelectClickListener select;
	private boolean isUnLock = false;

	public SegmentedControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		View view = View.inflate(context, R.layout.view_segmentedcontrol, this);
		mLock = (TextView) view.findViewById(R.id.app_tv_lock);
		mUnLock = (TextView) view.findViewById(R.id.app_tv_unlock);

		mLock.setOnClickListener(this);
		mUnLock.setOnClickListener(this);

		// 默认选中未加锁
		mUnLock.setSelected(true);
		isUnLock = true;
	}

	public SegmentedControl(Context context) {
		this(context, null);
	}

	public void setOnSelectClickListener(OnSelectClickListener select) {
		if (select != null) {
			this.select = select;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.app_tv_lock:
			// 点击了加锁
			if (isUnLock) {
				select.OnSelect(false);
				mLock.setSelected(true);
				mUnLock.setSelected(false);
				isUnLock = false;
			}
			break;
		case R.id.app_tv_unlock:
			// 点击了未加锁
			if (!isUnLock) {
				mLock.setSelected(false);
				select.OnSelect(true);
				mUnLock.setSelected(true);
				isUnLock = true;
			}
			break;
		}
	}

	public interface OnSelectClickListener {

		public void OnSelect(boolean isUnlock);
	}

}

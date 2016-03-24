package com.example.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mobilesafe.R;

public class ProgressDesView extends LinearLayout {

	private TextView mTvLeft;
	private TextView mTvRight;
	private TextView mTvTitle;
	private ProgressBar mPbProgress;

	public ProgressDesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		View view = View.inflate(context, R.layout.view_progress_des, this);
		mTvLeft = (TextView) view.findViewById(R.id.view_progress_tv_left);
		mTvRight = (TextView) view.findViewById(R.id.view_progress_tv_right);
		mTvTitle = (TextView) view.findViewById(R.id.view_progress_tv_title);
		mPbProgress = (ProgressBar) view
				.findViewById(R.id.view_progress_pb_progress);
	}

	public ProgressDesView(Context context) {
		this(context, null);
	}

	public void setTitle(String title) {
		mTvTitle.setText(title);
	}

	public void setLeft(String leftDes) {
		mTvLeft.setText(leftDes);
	}

	public void setRight(String rightDes) {
		mTvRight.setText(rightDes);
	}

	public void setProgress(int progress) {
		mPbProgress.setProgress(progress);
	}
}

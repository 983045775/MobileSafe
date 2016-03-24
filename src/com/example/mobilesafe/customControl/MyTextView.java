package com.example.mobilesafe.customControl;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView {

	public MyTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 跑马灯设置
		setEllipsize(TruncateAt.MARQUEE);
		setFocusable(true);
		setMarqueeRepeatLimit(-1);
		setFocusableInTouchMode(true);
	}

	// 防止两个textview一个不出现跑马灯
	public boolean isFocused() {
		return true;
	}

	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		if (focused) {
			super.onFocusChanged(focused, direction, previouslyFocusedRect);
		}
	}

	// 防止alertdialog阻止跑马灯
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if (hasWindowFocus) {
			super.onWindowFocusChanged(hasWindowFocus);
		}
	}
}

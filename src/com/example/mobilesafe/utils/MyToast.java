package com.example.mobilesafe.utils;

import com.example.mobilesafe.R;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

public class MyToast implements OnTouchListener {

	private static final String TAG = "MyToast";
	private View mView;
	private Context context;
	private WindowManager mWM;
	private WindowManager.LayoutParams mParams;
	private float x;
	private float y;

	public MyToast(Context context) {
		this.context = context;
	}

	public void show(String address) {
		mView = View.inflate(context, R.layout.toast_address, null);
		mView.setOnTouchListener(this);
		TextView mAddress = (TextView) mView
				.findViewById(R.id.toast_tv_address);
		mAddress.setText(address);
		// 查询存储的颜色
		int color = PreferencesUtils.getInt(context, Constants.ADDRESS_STYLE,
				R.drawable.toast_address_shape);
		mAddress.setBackgroundResource(color);
		mParams = new WindowManager.LayoutParams();
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		mParams.format = PixelFormat.TRANSLUCENT;
		mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		mParams.setTitle("Toast");
		mWM = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
		mWM.addView(mView, mParams);
	}

	public void hide() {
		if (mView != null) {
			if (mView.getParent() != null) {
				mWM.removeView(mView);
			}
			mView = null;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			x = event.getRawX();
			y = event.getRawY();
			Log.d(TAG, x + "按下去了" + y);
			break;
		case MotionEvent.ACTION_UP:
			Log.d(TAG, "按上来了");
			break;
		case MotionEvent.ACTION_MOVE:
			// 记录移动的x y
			float moveX = event.getRawX();
			float moveY = event.getRawY();
			float needX = -(x - moveX);
			float needY = -(y - moveY);

			mParams.x += (int) (needX + 0.5f);
			mParams.y += (int) (needY + 0.5f);
			x = moveX;
			y = moveY;
			mWM.updateViewLayout(mView, mParams);
			Log.d(TAG, moveX + "移动" + moveY);
			x = moveX;
			y = moveY;
			break;

		}
		return false;
	}
}

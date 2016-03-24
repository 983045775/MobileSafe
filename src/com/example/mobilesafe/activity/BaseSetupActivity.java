package com.example.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View;

import com.example.mobilesafe.R;

/**
 * 页面跳转的提取
 * 
 * @author liu
 * 
 */
public abstract class BaseSetupActivity extends Activity {

	protected static final String TAG = "BaseSetupActivity";
	public GestureDetector detector;

	// 在每个的创建页面都添加一个手势触摸事件
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		detector = new GestureDetector(this, new SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// e1 是落下去的位置
				// e2 是起来的位置
				float e1x = e1.getRawX();
				float e1y = e1.getRawY();
				float e2x = e2.getRawX();
				float e2y = e2.getRawY();
				if ((Math.abs(e1y - e2y) > Math.abs(e1x - e2x))) {
					// 说明向上移动了而已
					Log.i(TAG, "向上移动了而已");
					return false;
				}
				if (e1x > e2x + 80) {
					// 说明移动了
					Log.i(TAG, "移动了,前进");
					donext();
					return true;
				}
				if (e2x > e1x + 80) {
					// 说明移动了
					Log.i(TAG, "移动了,后退");
					dopre();
					return true;
				}
				Log.i(TAG, "没移动了");
				return false;
			}
		});
	}

	public void donext() {
		if (setupNext()) {
			return;
		}
		// 动画效果,第一个是进来的,第二个是离开的
		overridePendingTransition(R.anim.next_int, R.anim.next_exit);
		finish();
	}

	public void dopre() {

		if (setuppre()) {
			return;
		}
		// 动画效果,第一个是进来的,第二个是离开的
		overridePendingTransition(R.anim.pre_int, R.anim.pre_exit);
		finish();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	/**
	 * 页面跳转上一个
	 * 
	 * @param view
	 */
	public void clickpre(View view) {
		dopre();
	}

	/**
	 * 页面跳转下一个
	 * 
	 * @param view
	 */
	public void clicknext(View view) {
		donext();
	}

	/**
	 * 跳转下一个的intent实现
	 */
	protected abstract boolean setupNext();

	/**
	 * 跳转上一个的intent实现
	 */

	protected abstract boolean setuppre();
}

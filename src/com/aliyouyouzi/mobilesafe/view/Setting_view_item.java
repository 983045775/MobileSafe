package com.aliyouyouzi.mobilesafe.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyouyouzi.mobilesafe.R;

public class Setting_view_item extends RelativeLayout {

	private final int FRIST = 0;
	private final int SECOND = 1;
	private final int END = 2;
	private TextView setting_item_tv_title;
	private ImageView setting_item_iv_on_off;
	private boolean flag = false;
	private View view;

	public Setting_view_item(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public Setting_view_item(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 绑定layout跟这个页面
		view = View.inflate(context, R.layout.view_setting_item, this);
		// 获取控件
		setting_item_tv_title = (TextView) findViewById(R.id.setting_item_tv_title);
		setting_item_iv_on_off = (ImageView) findViewById(R.id.setting_item_iv_on_off);
		// 获取自定义的控件属性
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.Setting_view_item);
		// 获取文字,和图片
		String title = typedArray.getString(R.styleable.Setting_view_item_text);
		int background = typedArray.getInt(
				R.styleable.Setting_view_item_mybackground, FRIST);
		// 设置是否显示开关
		boolean flag = typedArray.getBoolean(
				R.styleable.Setting_view_item_switchs, true);
		if (flag) {
			setting_item_iv_on_off.setVisibility(View.VISIBLE);
		} else {
			setting_item_iv_on_off.setVisibility(View.GONE);
		}
		// 设置文字,背景
		setting_item_tv_title.setText(title);
		switch (background) {
		case FRIST:
			view.setBackgroundResource(R.drawable.setting_item_first_background);
			break;
		case SECOND:
			view.setBackgroundResource(R.drawable.setting_item_seconde_background);
			break;
		case END:
			view.setBackgroundResource(R.drawable.setting_item_last_background);
			break;
		}
		// 调用开关
		clickOnOff(flag);
		typedArray.recycle();
	}

	public Setting_view_item(Context context) {
		this(context, null);
	}

	/**
	 * 进行打开或者关闭开关
	 * 
	 * @param on
	 */
	public void clickOnOff(boolean on) {
		this.flag = on;
		if (on) {
			setting_item_iv_on_off.setImageResource(R.drawable.on);
			// flag = false;
		} else {
			setting_item_iv_on_off.setImageResource(R.drawable.off);
			// flag = true;
		}
	}

	/**
	 * 打开则关闭,关闭则打开
	 */
	public void openon_off() {
		clickOnOff(!flag);
	}

	/**
	 * 获取当前开关的状态的
	 */
	public boolean getOn_Off() {
		return flag;
	}

}

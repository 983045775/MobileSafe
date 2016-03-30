package com.aliyouyouzi.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyouyouzi.mobilesafe.service.CallSmsService;
import com.aliyouyouzi.mobilesafe.service.NumberAddressService;
import com.aliyouyouzi.mobilesafe.utils.Constants;
import com.aliyouyouzi.mobilesafe.utils.PreferencesUtils;
import com.aliyouyouzi.mobilesafe.utils.ServiceUtils;
import com.aliyouyouzi.mobilesafe.view.AddresssDialog;
import com.aliyouyouzi.mobilesafe.view.Setting_view_item;
import com.aliyouyouzi.mobilesafe.R;

public class SettingActivity extends Activity implements OnClickListener {

	private Setting_view_item settingUpdate;
	private Setting_view_item mSetting_callsmssafe;
	private Setting_view_item mNumberaddress; // 号码归属地
	private Setting_view_item mAddressStyle;

	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activitiy_setting);
		super.onCreate(savedInstanceState);
		settingUpdate = (Setting_view_item) findViewById(R.id.setting_auto_update);
		mSetting_callsmssafe = (Setting_view_item) findViewById(R.id.setting_callsmssafe);
		mNumberaddress = (Setting_view_item) findViewById(R.id.setting_numberaddress);
		mAddressStyle = (Setting_view_item) findViewById(R.id.setting_address_style);
		// 设置默认的开关
		boolean date = PreferencesUtils.getBoolean(this, Constants.AUTO_UPDATE,
				true);
		settingUpdate.clickOnOff(date);
		// 给控件添加点击事件
		settingUpdate.setOnClickListener(this);
		// 设置点击事件
		mSetting_callsmssafe.setOnClickListener(this);
		mNumberaddress.setOnClickListener(this);
		mAddressStyle.setOnClickListener(this);
	}

	// 切换页面监听
	protected void onResume() {
		super.onResume();
		if (ServiceUtils.isServiceRunning(SettingActivity.this,
				CallSmsService.class)) {
			mSetting_callsmssafe.clickOnOff(true);
		} else {
			mSetting_callsmssafe.clickOnOff(false);
		}
		if (ServiceUtils.isServiceRunning(SettingActivity.this,
				NumberAddressService.class)) {
			mNumberaddress.clickOnOff(true);
		} else {
			mNumberaddress.clickOnOff(false);

		}
	}

	// 点击事件
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting_auto_update:
			// 自动更新设置
			// 点击开关
			settingUpdate.openon_off();
			// 获取当前的状态
			boolean on_Off = settingUpdate.getOn_Off();
			// 进行存储当前的状态
			PreferencesUtils.putBoolean(SettingActivity.this,
					Constants.AUTO_UPDATE, on_Off);
			break;
		case R.id.setting_callsmssafe:
			// 骚扰拦截设置
			// 判断服务是否在运行
			if (ServiceUtils.isServiceRunning(SettingActivity.this,
					CallSmsService.class)) {
				// 说明开启了,关闭他
				Intent intent = new Intent(SettingActivity.this,
						CallSmsService.class);
				stopService(intent);
				// UI更新
				mSetting_callsmssafe.clickOnOff(false);
			} else {
				// 说明关闭了 ,开启他
				Intent intent = new Intent(SettingActivity.this,
						CallSmsService.class);
				startService(intent);
				// UI更新
				mSetting_callsmssafe.clickOnOff(true);
			}
			break;
		case R.id.setting_numberaddress:
			// 归属地显示设置
			if (ServiceUtils.isServiceRunning(this, NumberAddressService.class)) {
				// 说明需要关闭的服务
				Intent intent = new Intent(this, NumberAddressService.class);
				stopService(intent);
				// 开关的变化
				mNumberaddress.clickOnOff(false);
			} else {
				// 说明需要开启的服务
				Intent intent = new Intent(this, NumberAddressService.class);
				startService(intent);
				mNumberaddress.clickOnOff(true);
			}
			break;
		case R.id.setting_address_style:
			// 归属地风格设置 TODO:
			AddresssDialog dialog = new AddresssDialog(this);
			dialog.setAdapter(new AddressAdapterStyle());
			dialog.show();
			break;
		}
	}

	private String[] titles = new String[] { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };
	private int[] icons = new int[] { R.drawable.toast_address_shape,
			R.drawable.toast_address_orange, R.drawable.toast_address_blue,
			R.drawable.toast_address_gray, R.drawable.toast_address_green };

	private class AddressAdapterStyle extends BaseAdapter {

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(SettingActivity.this,
						R.layout.item_address_style, null);
				holder.mAddressDec = (TextView) convertView
						.findViewById(R.id.item_address_dec);
				holder.mAddressSelected = (ImageView) convertView
						.findViewById(R.id.item_address_selected);
				holder.mAddressStyle = (ImageView) convertView
						.findViewById(R.id.item_address_style);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.mAddressDec.setText(titles[position]);
			holder.mAddressStyle.setBackgroundResource(icons[position]);
			holder.mAddressSelected.setVisibility(View.GONE);
			// 取出记录点击的样式
			int color = PreferencesUtils.getInt(getApplicationContext(),
					Constants.ADDRESS_STYLE, icons[0]);
			if (color == icons[position]) {
				holder.mAddressSelected.setVisibility(View.VISIBLE);
			}
			return convertView;
		}
	}

	private static class ViewHolder {
		ImageView mAddressStyle;
		TextView mAddressDec;
		ImageView mAddressSelected;
	}
}
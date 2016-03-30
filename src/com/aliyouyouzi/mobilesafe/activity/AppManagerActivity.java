package com.aliyouyouzi.mobilesafe.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.provider.Settings;

import com.aliyouyouzi.mobilesafe.domain.AppManagerInfo;
import com.aliyouyouzi.mobilesafe.engine.AppManagerDatasProvide;
import com.aliyouyouzi.mobilesafe.view.ProgressDesView;
import com.aliyouyouzi.mobilesafe.R;
/** 
 * 这是软件管理页面
 * @author liu
 *
 */
public class AppManagerActivity extends Activity implements OnScrollListener,
		OnItemClickListener {
	private static final String TAG = "AppManagerActivity";
	private ProgressDesView mPvRom;
	private ProgressDesView mPvSd;
	private ListView mLvDatas;
	private List<AppManagerInfo> mDatas;
	private List<AppManagerInfo> mUserDatas = new ArrayList<AppManagerInfo>();
	private List<AppManagerInfo> mSystemDatas = new ArrayList<AppManagerInfo>();
	private List<AppManagerInfo> mOrderDatas = new ArrayList<AppManagerInfo>();
	private LinearLayout mLoading;
	private TextView mTvTitle;
	private UninstallAppBoast boast;
	private AppManagerDatas appAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		mPvSd = (ProgressDesView) findViewById(R.id.app_manager_pv_sd);
		mPvRom = (ProgressDesView) findViewById(R.id.app_manager_pv_rom);
		mLvDatas = (ListView) findViewById(R.id.app_manager_lv_datas);
		mLoading = (LinearLayout) findViewById(R.id.black_ll_loading);
		mTvTitle = (TextView) findViewById(R.id.app_manager_tv_title);
		// 进行rom的数据设置
		romSetting();
		// 进行sd卡的数据设置
		sdSetting();
		mTvTitle.setVisibility(View.GONE);
		new Thread() {
			public void run() {
				// 查询数据 ->List<AppManagerInfo>
				mDatas = AppManagerDatasProvide
						.getAllAppMessage(AppManagerActivity.this);
				// 设置用户程序和系统程序
				for (AppManagerInfo mData : mDatas) {
					if (mData.isAndroidApp()) {
						// 系统应用
						mSystemDatas.add(mData);
					} else {
						// 用户应用
						mUserDatas.add(mData);
					}
				}
				mOrderDatas.addAll(mUserDatas);
				mOrderDatas.addAll(mSystemDatas);
				Log.d(TAG, mUserDatas.size() + " " + mSystemDatas.size());

				runOnUiThread(new Runnable() {

					public void run() {
						mTvTitle.setVisibility(View.VISIBLE);
						mLoading.setVisibility(View.GONE);
						appAdapter = new AppManagerDatas();
						mLvDatas.setAdapter(appAdapter);
					}
				});
			};
		}.start();
		// 添加一个滑动事件,用来监听修改title文子
		mLvDatas.setOnScrollListener(this);
		// 给每个添加一个点击事件
		mLvDatas.setOnItemClickListener(this);
		boast = new UninstallAppBoast();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.PACKAGE_REMOVED");
		filter.addDataScheme("package");
		registerReceiver(boast, filter);
	}

	private class UninstallAppBoast extends BroadcastReceiver {

		public void onReceive(Context context, final Intent intent) {
			new Thread() {
				public void run() {
					String appName = intent.getData().toString()
							.replace("package:", "");
					ListIterator<AppManagerInfo> listIterator = mOrderDatas
							.listIterator();
					while (listIterator.hasNext()) {
						AppManagerInfo info = listIterator.next();
						if (info.getPackageName().equals(appName)) {
							listIterator.remove();
						}
					}
					runOnUiThread(new Runnable() {
						public void run() {
							// 完事了 UI更新
							appAdapter.notifyDataSetChanged();
						}
					});
				};
			}.start();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(boast);
	}

	private void sdSetting() {
		File SDdirectory = Environment.getExternalStorageDirectory();
		long SDfreeSpace = SDdirectory.getFreeSpace();// 可用空间
		long SDtotalSpace = SDdirectory.getTotalSpace();// 总空间
		long SDuserSpace = SDtotalSpace - SDfreeSpace;// 使用空间

		mPvSd.setTitle("SD卡:");
		mPvSd.setRight(Formatter.formatFileSize(this, SDfreeSpace) + "可用");
		mPvSd.setLeft(Formatter.formatFileSize(this, SDuserSpace) + "已用");
		int SDprogress = (int) ((SDuserSpace * 100f / SDtotalSpace) + 0.5f);
		mPvSd.setProgress(SDprogress);
	}

	private void romSetting() {
		File directory = Environment.getDataDirectory();
		long freeSpace = directory.getFreeSpace();// 可用空间0-
		long totalSpace = directory.getTotalSpace();// 总空间
		long userSpace = totalSpace - freeSpace;// 使用空间

		mPvRom.setTitle("内存:");
		mPvRom.setRight(Formatter.formatFileSize(this, freeSpace) + "可用");
		mPvRom.setLeft(Formatter.formatFileSize(this, userSpace) + "已用");
		int progress = (int) ((userSpace * 100f / totalSpace) + 0.5f);
		mPvRom.setProgress(progress);
	}

	private class AppManagerDatas extends BaseAdapter {

		public int getCount() {
			if (mDatas != null) {
				return mDatas.size() + 2;
			}
			return 0;
		}

		public Object getItem(int position) {
			if (mDatas != null) {
				return mDatas.get(position);
			}
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			if (position == 0) {
				TextView view = new TextView(AppManagerActivity.this);
				view.setPadding(10, 10, 10, 10);
				view.setBackgroundColor(Color.parseColor("#D4D4D4"));
				view.setTextSize(18);
				view.setText("用户程序(" + mUserDatas.size() + "个)");
				return view;
			}

			if (position == mUserDatas.size() + 1) {
				TextView view = new TextView(AppManagerActivity.this);
				view.setPadding(10, 10, 10, 10);
				view.setBackgroundColor(Color.parseColor("#D4D4D4"));
				view.setTextSize(18);
				view.setText("系统程序(" + mSystemDatas.size() + "个)");
				return view;
			}

			Log.d(TAG, position + "");
			if (position <= mUserDatas.size()) {
				position -= 1;
			} else {
				position -= 2;
			}
			ViewHolder holder = null;
			if (convertView == null || convertView instanceof TextView) {
				convertView = View.inflate(AppManagerActivity.this,
						R.layout.item_appmanager_listview, null);
				holder = new ViewHolder();
				convertView.setTag(holder);
				holder.mIvIcon = (ImageView) convertView
						.findViewById(R.id.item_app_manager_iv_icon);
				holder.mTvName = (TextView) convertView
						.findViewById(R.id.item_app_manager_tv_name);
				holder.mTvType = (TextView) convertView
						.findViewById(R.id.item_app_manager_tv_type);
				holder.mTvSize = (TextView) convertView
						.findViewById(R.id.item_app_manager_tv_size);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// 设置数据
			holder.mIvIcon
					.setImageDrawable(mOrderDatas.get(position).getIcon());
			if (mOrderDatas.get(position).isAndroidApp()) {
				// 是自带程序
				holder.mTvName.setText(mOrderDatas.get(position)
						.getPackageName());
			} else {
				holder.mTvName.setText(mOrderDatas.get(position).getAppName());
			}
			if (mOrderDatas.get(position).isSdcard())
				holder.mTvType.setText("SD卡内存");
			else
				holder.mTvType.setText("手机内存");
			holder.mTvSize.setText(mOrderDatas.get(position).getSize());
			return convertView;
		}
	}

	private static class ViewHolder {
		ImageView mIvIcon;
		TextView mTvName;
		TextView mTvType;
		TextView mTvSize;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	// 监听滑动事件
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mUserDatas == null || mSystemDatas == null) {
			return;
		}
		if (firstVisibleItem <= mUserDatas.size()) {
			mTvTitle.setText("用户程序(" + mUserDatas.size() + "个)");
		} else if (firstVisibleItem > mUserDatas.size()) {
			mTvTitle.setText("系统程序(" + mSystemDatas.size() + "个)");
		}
	}

	// 点击item监听回调
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// 设置一个弹出框
		View contentView = View.inflate(this, R.layout.popup_app_manager_extra,
				null);

		if (position <= mUserDatas.size()) {
			position -= 1;
		} else {
			position -= 2;
		}
		int width = LayoutParams.WRAP_CONTENT;
		int height = LayoutParams.WRAP_CONTENT;
		final PopupWindow window = new PopupWindow(contentView, width, height);
		// 获取焦点
		window.setFocusable(true);
		// 设置其他的地方可以触摸
		window.setOutsideTouchable(true);
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		// 添加样式
		window.setAnimationStyle(R.style.PopupAnimation);
		window.showAsDropDown(view, 300, -view.getHeight());
		// 寻找四个控件
		LinearLayout mLLInfo = (LinearLayout) contentView
				.findViewById(R.id.popup_app_manager_ll_info);
		LinearLayout mLLOpen = (LinearLayout) contentView
				.findViewById(R.id.popup_app_manager_ll_open);
		LinearLayout mLLShare = (LinearLayout) contentView
				.findViewById(R.id.popup_app_manager_ll_share);
		LinearLayout mLLUninstall = (LinearLayout) contentView
				.findViewById(R.id.popup_app_manager_ll_uninstall);

		// 添加点击事件
		final int index = position;
		mLLInfo.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 应用详情
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				Uri uri = Uri.fromParts("package", mOrderDatas.get(index)
						.getPackageName(), null);
				intent.setData(uri);
				startActivity(intent);
				// 让其消失
				window.dismiss();
			}
		});
		mLLOpen.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 打开应用
				PackageManager packageManager = getPackageManager();
				Intent intent = new Intent();
				intent = packageManager.getLaunchIntentForPackage(mOrderDatas
						.get(index).getPackageName());
				if (intent != null) {
					startActivity(intent);
				}
				window.dismiss();
			}
		});
		mLLShare.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 分享
			}
		});
		mLLUninstall.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 卸载应用
				Uri packageURI = Uri.parse("package:"
						+ mOrderDatas.get(index).getPackageName());
				Intent intent = new Intent(Intent.ACTION_DELETE);
				intent.setData(packageURI);
				startActivity(intent);
				window.dismiss();
			}
		});
	}
}

package com.aliyouyouzi.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyouyouzi.mobilesafe.domain.ProgressManagerInfo;
import com.aliyouyouzi.mobilesafe.engine.ProgressDatasProvide;
import com.aliyouyouzi.mobilesafe.service.LockSreenAutoClearService;
import com.aliyouyouzi.mobilesafe.utils.Constants;
import com.aliyouyouzi.mobilesafe.utils.PreferencesUtils;
import com.aliyouyouzi.mobilesafe.utils.ProgressManagerUtils;
import com.aliyouyouzi.mobilesafe.utils.ServiceUtils;
import com.aliyouyouzi.mobilesafe.view.ProgressDesView;
import com.aliyouyouzi.mobilesafe.view.Setting_view_item;
import com.aliyouyouzi.mobilesafe.R;

public class ProgressManagerActivity extends Activity implements
		OnItemClickListener, OnClickListener {
        
	private ProgressDesView mPvProgress;
	private ProgressDesView mPvMemory;
	private StickyListHeadersListView mListview;
	private List<ProgressManagerInfo> list;
	private List<ProgressManagerInfo> userlist;
	private List<ProgressManagerInfo> systemlist;
	private List<ProgressManagerInfo> orderlist;
	private LinearLayout mLoading;
	private boolean isloading = false;
	private MyAdapter myAdapter;
	private ImageView mIvClean;
	private long memoryUser = 0;
	private int progressCount = 0;
	private static final String TAG = "ProgressManagerActivity";
	private ImageView mIvUp1;
	private ImageView mIvUp2;
	private SlidingDrawer mSdDrawer;
	private Setting_view_item mDisplaySystem;
	private Setting_view_item mLockScreen;
 
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress_manager);
		mPvProgress = (ProgressDesView) findViewById(R.id.progress_pv_progress);
		mPvMemory = (ProgressDesView) findViewById(R.id.progress_pv_memory);
		mListview = (StickyListHeadersListView) findViewById(R.id.progress_slv_list);
		mLoading = (LinearLayout) findViewById(R.id.black_ll_loading);
		mIvClean = (ImageView) findViewById(R.id.progress_iv_clean);
		mIvUp1 = (ImageView) findViewById(R.id.progress_manager_iv_up1);
		mIvUp2 = (ImageView) findViewById(R.id.progress_manager_iv_up2);
		mSdDrawer = (SlidingDrawer) findViewById(R.id.progress_manager_sd_drawer);
		mDisplaySystem = (Setting_view_item) findViewById(R.id.progress_manager_set_display_system);
		mLockScreen = (Setting_view_item) findViewById(R.id.progress_manager_set_lock_screen);
		// 设置进程信息
		progressCount = ProgressManagerUtils
				.queryRunningProgress(getApplicationContext());
		setProgressMessage();

		// 设置内存信息
		memoryUser = ProgressManagerUtils.queryUserMemory(this);
		setMemoryMessage();
		// 开线程
		mLoading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				// 添加listview数据
				list = ProgressDatasProvide
						.getProgressMessage(ProgressManagerActivity.this);
				// 进行排序,用户在前,系统在后
				systemlist = new ArrayList<ProgressManagerInfo>();
				userlist = new ArrayList<ProgressManagerInfo>();
				orderlist = new ArrayList<ProgressManagerInfo>();
				for (ProgressManagerInfo info : list) {
					if (info.isSystem()) {
						systemlist.add(info);
					} else {
						userlist.add(info);
					}
				}
				orderlist.addAll(userlist);
				orderlist.addAll(systemlist);
				// 更新UI
				runOnUiThread(new Runnable() {
					public void run() {
						mLoading.setVisibility(View.GONE);
						//TODO
						myAdapter = new MyAdapter();
						mListview.setAdapter(myAdapter);
						isloading = true;
					}
				});
			};
		}.start();
		///
		// 添加item点击事件
		mListview.setOnItemClickListener(this);
		// 添加垃圾清理事件
		mIvClean.setOnClickListener(this);
		// 给箭头设置动画
		setUpAnimation();
		// 监听拉上去更改图片的动作
		mSdDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
				mIvUp1.clearAnimation();
				mIvUp2.clearAnimation();

				mIvUp1.setImageResource(R.drawable.drawer_arrow_down);
				mIvUp2.setImageResource(R.drawable.drawer_arrow_down);
			}
		});
		// 监听拉下去更改图片
		mSdDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {
				mIvUp1.setImageResource(R.drawable.drawer_arrow_up);
				mIvUp2.setImageResource(R.drawable.drawer_arrow_up);

				setUpAnimation();
			}
		});
		// UI更新,首先设置显示系统进程按钮
		boolean displaySystem = PreferencesUtils.getBoolean(this,
				Constants.DISPLAY_SYSTEM, true);
		mDisplaySystem.clickOnOff(displaySystem);

		// 自动清理
		boolean autoClear = PreferencesUtils.getBoolean(this,
				Constants.AUTO_CLEAR, false);
		mLockScreen.clickOnOff(autoClear);

		// displaySystem添加点击事件
		mDisplaySystem.setOnClickListener(this);
		// 添加一个锁屏自动清理点击事件
		mLockScreen.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 自动清理
		boolean autoClear = ServiceUtils.isServiceRunning(
				getApplicationContext(), LockSreenAutoClearService.class);
		Log.d(TAG, autoClear + "");
		PreferencesUtils.putBoolean(getApplicationContext(),
				Constants.AUTO_CLEAR, autoClear);
		mLockScreen.clickOnOff(autoClear);
	}

	private void setUpAnimation() {
		AlphaAnimation animation = new AlphaAnimation(0.15f, 1f);
		animation.setDuration(700);
		animation.setRepeatCount(AlphaAnimation.INFINITE);
		animation.setRepeatMode(AlphaAnimation.REVERSE);
		mIvUp1.startAnimation(animation);

		AlphaAnimation animation_2 = new AlphaAnimation(0.15f, 1f);
		animation_2.setDuration(700);
		animation_2.setRepeatCount(AlphaAnimation.INFINITE);
		animation_2.setRepeatMode(AlphaAnimation.REVERSE);
		mIvUp2.startAnimation(animation_2);
	}

	private void setMemoryMessage() {
		ProgressManagerUtils.queryUserMemory(this);
		mPvMemory.setTitle("内存:");
		mPvMemory
				.setLeft("占有内存:"
						+ Formatter.formatFileSize(getApplicationContext(),
								memoryUser));
		long canUserMemory = ProgressManagerUtils.queryCanUserMemory(this);
		mPvMemory.setRight("可用内存:"
				+ Formatter.formatFileSize(getApplicationContext(),
						canUserMemory));
		long allMemory = memoryUser + canUserMemory;
		int progressMemory = (int) (memoryUser * 100f / allMemory + 0.5f);
		mPvMemory.setProgress(progressMemory);
	}

	private void setProgressMessage() {
		mPvProgress.setTitle("进程数:");

		mPvProgress.setLeft("正在运行" + progressCount + "个");
		int queryAllProgress = ProgressManagerUtils
				.queryAllProgress(getApplicationContext());
		mPvProgress.setRight("总共应用" + queryAllProgress + "个");

		int progress = (int) (progressCount * 100f / queryAllProgress + 0.5f);
		mPvProgress.setProgress(progress);// 百分比
	}

	private class MyAdapter extends BaseAdapter implements
			StickyListHeadersAdapter {

		@Override
		public int getCount() {
			boolean isDisplay = PreferencesUtils.getBoolean(
					getApplicationContext(), Constants.DISPLAY_SYSTEM, true);
			if (isDisplay) {
				if (orderlist != null) {
					return orderlist.size();
				}
			} else {
				if (userlist != null) {
					return userlist.size();
				}
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			boolean isDisplay = PreferencesUtils.getBoolean(
					getApplicationContext(), Constants.DISPLAY_SYSTEM, true);
			if (isDisplay) {
				if (orderlist != null) {
					return orderlist.get(position);
				}
			} else {
				if (userlist != null) {
					return userlist.get(position);
				}
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();

				convertView = View.inflate(ProgressManagerActivity.this,
						R.layout.item_progress_listview, null);
				holder.mCbSelect = (CheckBox) convertView
						.findViewById(R.id.item_progress_cb_select);
				holder.mIvicon = (ImageView) convertView
						.findViewById(R.id.item_progress_iv_icon);
				holder.mTvAppName = (TextView) convertView
						.findViewById(R.id.item_progress_tv_appName);
				holder.mTvUsermemory = (TextView) convertView
						.findViewById(R.id.item_progress_tv_usermemory);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ProgressManagerInfo info = null;
			try {
				info = orderlist.get(position);
			} catch (Exception e) {
			}

			// 更新样式
			holder.mIvicon.setImageDrawable(info.getIcon());
			if (info.getAppName() != null) {
				holder.mTvAppName.setText(info.getAppName());
			} else {
				holder.mTvAppName.setText(info.getPackagerName());
			}
			holder.mTvUsermemory.setText("占有内存: "
					+ Formatter.formatFileSize(getApplicationContext(),
							info.getUserMemory()));

			holder.mCbSelect.setChecked(info.isCheck());
			if (info.getPackagerName().equals(
					ProgressManagerActivity.this.getPackageName())) {
				Log.d(TAG, "说明是一个名字");
				holder.mCbSelect.setVisibility(View.GONE);
			} else {
				holder.mCbSelect.setVisibility(View.VISIBLE);
			}
			return convertView;
		}

		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = new TextView(ProgressManagerActivity.this);
			}
			if (orderlist.get(position).isSystem()) {
				// #D1D1D1
				((TextView) convertView).setText("系统进程");
			} else {
				((TextView) convertView).setText("用户进程");
			}
			((TextView) convertView).setBackgroundColor(Color
					.parseColor("#D1D1D1"));
			((TextView) convertView).setPadding(3, 3, 3, 3);
			return convertView;
		}

		public long getHeaderId(int position) {
			return orderlist.get(position).isSystem() ? 0 : 1;
		}

	}

	private static class ViewHolder {
		ImageView mIvicon;
		TextView mTvAppName;
		TextView mTvUsermemory;
		CheckBox mCbSelect;
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		orderlist.get(position).setCheck(!orderlist.get(position).isCheck());
		// Ui更新
		myAdapter.notifyDataSetChanged();
	}

	public void clickAll(View view) {
		if (isloading) {// 加载好了
			boolean isDisplay = PreferencesUtils.getBoolean(
					getApplicationContext(), Constants.DISPLAY_SYSTEM, true);
			if (isDisplay) {
				// 说明显示系统进程
				for (int x = 0; x < orderlist.size(); x++) {
					if (orderlist.get(x).getPackagerName()
							.equals(this.getPackageName())) {
						continue;
					}
					orderlist.get(x).setCheck(true);
				}
			} else {
				for (int x = 0; x < userlist.size(); x++) {
					if (userlist.get(x).getPackagerName()
							.equals(this.getPackageName())) {
						continue;
					}
					userlist.get(x).setCheck(true);
				}
			}
			// 更新UI
			myAdapter.notifyDataSetChanged();
		}
	}

	public void clickReverse(View view) {
		if (isloading) { // 加载好了
			boolean isDisplay = PreferencesUtils.getBoolean(
					getApplicationContext(), Constants.DISPLAY_SYSTEM, true);
			if (isDisplay) {
				for (int x = 0; x < orderlist.size(); x++) {
					if (orderlist.get(x).getPackagerName()
							.equals(this.getPackageName())) {
						continue;
					}
					orderlist.get(x).setCheck(!orderlist.get(x).isCheck());
				}
			} else {
				for (int x = 0; x < userlist.size(); x++) {
					if (userlist.get(x).getPackagerName()
							.equals(this.getPackageName())) {
						continue;
					}
					userlist.get(x).setCheck(!userlist.get(x).isCheck());
				}
			}
			// 更新UI
			myAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 点击事件
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.progress_iv_clean:
			long memory = 0;
			int progressValue = 0;
			// 进行遍历

			ListIterator<ProgressManagerInfo> listIterator = orderlist
					.listIterator();

			while (listIterator.hasNext()) {
				ProgressManagerInfo info = listIterator.next();
				if (info.isCheck()) {

					// 需要清理的
					ActivityManager am = (ActivityManager) ProgressManagerActivity.this
							.getSystemService(getApplicationContext().ACTIVITY_SERVICE);
					am.killBackgroundProcesses(info.getPackagerName());
					progressValue++;
					memory += info.getUserMemory();
					listIterator.remove();
				}
			}
			myAdapter.notifyDataSetChanged();

			// 更新下数据
			progressCount = progressCount - progressValue;
			memoryUser = memoryUser - memory;
			setMemoryMessage();
			setProgressMessage();
			// Toast
			Toast.makeText(
					ProgressManagerActivity.this,
					"清理了"
							+ progressValue
							+ "个应用,占用"
							+ Formatter.formatShortFileSize(
									getApplicationContext(), memory),
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.progress_manager_set_display_system:
			if(!isloading){
				break;
			}
			// 是否显示系统进程
			boolean isDisplay = PreferencesUtils.getBoolean(
					getApplicationContext(), Constants.DISPLAY_SYSTEM, true);
			mDisplaySystem.clickOnOff(!isDisplay);
			PreferencesUtils.putBoolean(getApplicationContext(),
					Constants.DISPLAY_SYSTEM, !isDisplay);
			// ui更新
			myAdapter.notifyDataSetChanged();
			break;
		case R.id.progress_manager_set_lock_screen:
			// 锁屏自动清理
			boolean autoClear = PreferencesUtils.getBoolean(this,
					Constants.AUTO_CLEAR, false);
			// 是否开启服务
			if (ServiceUtils.isServiceRunning(getApplicationContext(),
					LockSreenAutoClearService.class)) {
				// 运行的
				stopService(new Intent(ProgressManagerActivity.this,
						LockSreenAutoClearService.class));
				mLockScreen.clickOnOff(false);
				PreferencesUtils.putBoolean(getApplicationContext(),
						Constants.AUTO_CLEAR, false);
			} else {
				startService(new Intent(ProgressManagerActivity.this,
						LockSreenAutoClearService.class));
				mLockScreen.clickOnOff(true);
				PreferencesUtils.putBoolean(getApplicationContext(),
						Constants.AUTO_CLEAR, true);
			}
			break;
		}
	}
}

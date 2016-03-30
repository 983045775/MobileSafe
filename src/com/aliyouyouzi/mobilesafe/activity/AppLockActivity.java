package com.aliyouyouzi.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyouyouzi.mobilesafe.db.AppLockDao;
import com.aliyouyouzi.mobilesafe.domain.AppMessageInfo;
import com.aliyouyouzi.mobilesafe.utils.AppMessageUtils;
import com.aliyouyouzi.mobilesafe.view.SegmentedControl;
import com.aliyouyouzi.mobilesafe.view.SegmentedControl.OnSelectClickListener;
import com.aliyouyouzi.mobilesafe.R;

public class AppLockActivity extends Activity {

	protected static final String TAG = "AppLockActivity";
	private SegmentedControl mAppLock;
	private List<AppMessageInfo> mAlllockList;
	private List<AppMessageInfo> mUnlockList = new ArrayList<AppMessageInfo>();
	private List<AppMessageInfo> mlockList = new ArrayList<AppMessageInfo>();
	private TextView mTvDesc;
	private ListView mLvDatas;
	private DatasAdapter adapter;
	private boolean flag = false;
	private AppLockDao dao;             
	private LinearLayout mLoading;
	private boolean isAnimation = false;
//是
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_applock);
		mAppLock = (SegmentedControl) findViewById(R.id.applock);
		mLvDatas = (ListView) findViewById(R.id.applock_lv_datas);
		mTvDesc = (TextView) findViewById(R.id.applock_tv_desc);
		mLoading = (LinearLayout) findViewById(R.id.black_ll_loading);
		dao = new AppLockDao(this);
		// 先获取数据
		new AsyncTask<Void, Void, Void>() {
			protected void onPreExecute() {
				mLoading.setVisibility(View.VISIBLE);
			};

			protected Void doInBackground(Void... params) {
				mAlllockList = AppMessageUtils
						.getAllAppMessage(AppLockActivity.this);
				Log.d(TAG, "加载数据");

				return null;
			}

			protected void onPostExecute(Void result) {
				mLoading.setVisibility(View.GONE);
				// 初始化
				mUnlockList.clear();
				mUnlockList.addAll(mAlllockList);
				ListIterator<AppMessageInfo> iterator = mUnlockList
						.listIterator();
				while (iterator.hasNext()) {
					AppMessageInfo info = iterator.next();
					if (info.isLock()) {
						iterator.remove();
					}
				}
				mTvDesc.setText("未加锁的应用(" + mUnlockList.size() + ")个");
				adapter = new DatasAdapter();
				mLvDatas.setAdapter(adapter);
				// 添加点击事件
				mAppLock.setOnSelectClickListener(new OnSelectClickListener() {
					public void OnSelect(boolean isUnlock) {
						if (isUnlock) {
							// 进行UI的数据加载
							flag = false;
							Log.d(TAG, "未加锁");
							mUnlockList.clear();
							mUnlockList.addAll(mAlllockList);
							ListIterator<AppMessageInfo> iterator = mUnlockList
									.listIterator();
							while (iterator.hasNext()) {
								AppMessageInfo info = iterator.next();
								if (info.isLock()) {
									iterator.remove();
								}
							}
							mTvDesc.setText("未加锁的应用(" + mUnlockList.size()
									+ ")个");
							mLvDatas.setAdapter(adapter);
						} else {
							flag = true;
							Log.d(TAG, "加锁");
							mlockList.clear();
							mlockList.addAll(mAlllockList);
							ListIterator<AppMessageInfo> iterator = mlockList
									.listIterator();
							while (iterator.hasNext()) {
								AppMessageInfo info = iterator.next();
								if (!info.isLock()) {
									iterator.remove();
								}
							}
							mTvDesc.setText("已加锁的应用(" + mlockList.size() + ")个");
							mLvDatas.setAdapter(adapter);
						}
					}
				});
			};
		}.execute();

	}

	private class DatasAdapter extends BaseAdapter {

		public int getCount() {
			if (flag) {
				if (mlockList != null) {
					return mlockList.size();
				}
			} else {
				if (mUnlockList != null) {
					return mUnlockList.size();
				}
			}
			return 0;
		}

		public Object getItem(int position) {
			if (flag) {
				if (mlockList != null) {
					return mlockList.get(position);
				}
			} else {
				if (mUnlockList != null) {
					return mUnlockList.get(position);
				}
			}
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(AppLockActivity.this,
						R.layout.item_applock_listview, null);

				convertView.setTag(holder);

				holder.mIcon = (ImageView) convertView
						.findViewById(R.id.item_applock_icon);
				holder.mName = (TextView) convertView
						.findViewById(R.id.item_applock_name);
				holder.mLock = (ImageView) convertView
						.findViewById(R.id.item_applock_lock);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			AppMessageInfo info = null;
			if (flag) {
				info = mlockList.get(position);
			} else {
				info = mUnlockList.get(position);
			}

			if (info.getAppName() == null) {
				holder.mName.setText(info.getPackageName());
			} else {
				holder.mName.setText(info.getAppName());
			}
			holder.mIcon.setImageDrawable(info.getIcon());
			if (info.isLock()) {
				// 锁住的,需要解锁
				holder.mLock.setImageResource(R.drawable.btn_unlock_selector);
			} else {
				// 解锁的,需要锁住
				holder.mLock.setImageResource(R.drawable.btn_lock_selector);
			}
			final AppMessageInfo appinfo = info;
			final View view = convertView;
			holder.mLock.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					if (isAnimation) {
						return;
					}
					if (!appinfo.isLock()) {
						// 添加一个位移动画
						TranslateAnimation animation = new TranslateAnimation(
								Animation.RELATIVE_TO_PARENT, 0,
								Animation.RELATIVE_TO_PARENT, 1,
								Animation.RELATIVE_TO_PARENT, 0,
								Animation.RELATIVE_TO_PARENT, 0);
						animation.setDuration(200);
						view.startAnimation(animation);
						animation.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								isAnimation = true;
							}

							@Override
							public void onAnimationRepeat(Animation animation) {

							}

							@Override
							public void onAnimationEnd(Animation animation) {
								// 加锁 --> 解锁 删除加锁,添加进解锁 islock设置成false
								boolean add = dao.add(appinfo.getPackageName());
								Log.d(TAG, "" + add);
								if (add) {
									mUnlockList.remove(appinfo);
									// mlockList.add(appinfo);
									appinfo.setLock(true);
									// UI更新
									mTvDesc.setText("未加锁的应用("
											+ mUnlockList.size() + ")个");
									adapter.notifyDataSetChanged();
								} else {
									Toast.makeText(AppLockActivity.this,
											"加锁失败", Toast.LENGTH_SHORT).show();
								}
								isAnimation = false;
							}
						});
					} else {
						// 未锁
						TranslateAnimation animation = new TranslateAnimation(
								Animation.RELATIVE_TO_PARENT, 0,
								Animation.RELATIVE_TO_PARENT, -1,
								Animation.RELATIVE_TO_PARENT, 0,
								Animation.RELATIVE_TO_PARENT, 0);
						animation.setDuration(200);
						view.startAnimation(animation);
						animation.setAnimationListener(new AnimationListener() {

							public void onAnimationStart(Animation animation) {
								isAnimation = true;
							}

							public void onAnimationRepeat(Animation animation) {

							}

							public void onAnimationEnd(Animation animation) {
								if (dao.query(appinfo.getPackageName())) {
									mlockList.remove(appinfo);
									// mUnlockList.add(appinfo);
									dao.delete(appinfo.getPackageName());
									appinfo.setLock(false);
									// UI刷新
									mTvDesc.setText("已加锁的应用("
											+ mlockList.size() + ")个");
									adapter.notifyDataSetChanged();
								} else {
									Toast.makeText(AppLockActivity.this,
											"解锁失败", Toast.LENGTH_SHORT).show();
								}
								isAnimation = false;
							}
						});

					}
				}
			});
			return convertView;
		}
	}

	private class ViewHolder {
		ImageView mIcon;
		TextView mName;
		ImageView mLock;
	}
}

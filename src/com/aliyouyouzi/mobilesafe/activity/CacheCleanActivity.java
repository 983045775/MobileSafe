package com.aliyouyouzi.mobilesafe.activity;

import java.io.DataOutputStream;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyouyouzi.mobilesafe.domain.CacheInfo;
import com.aliyouyouzi.mobilesafe.R;

public class CacheCleanActivity extends Activity {

	protected static final String TAG = "CacheCleanActivity";
	private TextView mTvName;// 应用名字
	private TextView mTvSize;// 应用大小
	private ImageView mIvLine;// 线
	private ImageView mIvIcon;// 图标
	private ProgressBar mPbProgress;// 进度
	private ListView mListView;
	private LinearLayout mLlScan;
	private LinearLayout mLlresult;
	private TextView mTvDesc;
	private Button mBtnClear;
	private LinearLayout mLlLoading;

	private int progressCount = 0;

	private int count;
	private long allSize;
	private TranslateAnimation mLineAnimation;// 线的动画
	private List<CacheInfo> mDatas = new ArrayList<CacheInfo>();
	private PackageManager mPm;
	private MyAsyncTask task;
	private MyDatasAdapter mAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_clean);
		// 初始化控件
		mIvIcon = (ImageView) findViewById(R.id.cache_iv_icon);
		mIvLine = (ImageView) findViewById(R.id.cache_iv_line);
		mTvName = (TextView) findViewById(R.id.cache_tv_name);
		mTvSize = (TextView) findViewById(R.id.cache_tv_size);
		mPbProgress = (ProgressBar) findViewById(R.id.cache_pb_progress);
		mListView = (ListView) findViewById(R.id.cache_lv_listview);
		mLlresult = (LinearLayout) findViewById(R.id.cache_ll_result);
		mLlScan = (LinearLayout) findViewById(R.id.cache_ll_scan);
		mTvDesc = (TextView) findViewById(R.id.cache_tv_desc);
		mBtnClear = (Button) findViewById(R.id.cache_btn_clear);
		mLlLoading = (LinearLayout) findViewById(R.id.black_ll_loading);

		mLlLoading.setVisibility(View.GONE);
		mAdapter = new MyDatasAdapter();
		// 获取包管理器
		mPm = getPackageManager();

		task = new MyAsyncTask();
		task.execute();
		mBtnClear.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mLlLoading.setVisibility(View.VISIBLE);
				if (allSize <= 0) {
					mLlLoading.setVisibility(View.GONE);
					Toast.makeText(CacheCleanActivity.this, "不需要清理",
							Toast.LENGTH_SHORT).show();
					return;
				}
				try {
					Method method = mPm.getClass().getMethod(
							"freeStorageAndNotify", Long.TYPE,
							IPackageDataObserver.class);
					method.invoke(mPm, Long.MAX_VALUE,
							new IPackageDataObserver.Stub() {

								public void onRemoveCompleted(
										String packageName, boolean succeeded) {
									// 进度
									runOnUiThread(new Runnable() {
										public void run() {
											mLlLoading.setVisibility(View.GONE);
											allSize = 0;
											count = 0;
											mTvDesc.setText("总共有"
													+ count
													+ "个软件缓存,共"
													+ Formatter
															.formatFileSize(
																	getApplicationContext(),
																	allSize));
											Toast.makeText(
													CacheCleanActivity.this,
													"清理成功", Toast.LENGTH_SHORT)
													.show();
											Log.d(TAG, "清理成功");

										}
									});
								}
							});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void onDestroy() {
		super.onDestroy();
		task.cancel(true);
	}

	private class MyAsyncTask extends AsyncTask<Void, CacheInfo, Void> {

		@Override
		protected void onProgressUpdate(CacheInfo... values) {
			CacheInfo info = values[0];
			// 设置数据
			mIvIcon.setImageDrawable(info.getIcon());
			mTvName.setText(info.getName());
			mTvSize.setText(Formatter.formatFileSize(getApplicationContext(),
					info.getCacheSize()));
			mPbProgress.setProgress(progressCount);

			if (mDatas.size() == 1) {
				mListView.setAdapter(mAdapter);
			} else {
				mListView.setVisibility(View.GONE);
				mAdapter.notifyDataSetChanged();
				mListView.setVisibility(View.VISIBLE);
			}
			// 滚到底部

			mListView.smoothScrollToPosition(mAdapter.getCount());
			Log.d(TAG, Thread.currentThread().getName() + "");
			Log.d(TAG, "滚回底部");
		}

		public void UpdateProgress(CacheInfo... values) {
			publishProgress(values);
		}

		protected void onPreExecute() {
			// 隐藏按钮
			mBtnClear.setVisibility(View.GONE);
			// 动画加载
			mLineAnimation = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, 0,
					Animation.RELATIVE_TO_PARENT, 0,
					Animation.RELATIVE_TO_PARENT, -0.4f,
					Animation.RELATIVE_TO_PARENT, 0.4f);
			mLineAnimation.setDuration(700);
			mLineAnimation.setRepeatCount(Animation.INFINITE);
			mLineAnimation.setRepeatMode(Animation.REVERSE);
			mIvLine.startAnimation(mLineAnimation);

			mLlresult.setVisibility(View.GONE);
			mLlScan.setVisibility(View.VISIBLE);
		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// mIvLine.clearAnimation();
			// 滚动到顶部
			new Thread() {
				public void run() {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					runOnUiThread(new Runnable() {
						public void run() {
							mTvDesc.setText("总共有"
									+ count
									+ "个软件缓存,共"
									+ Formatter.formatFileSize(
											getApplicationContext(), allSize));
							mLlresult.setVisibility(View.VISIBLE);
							mLlScan.setVisibility(View.GONE);
							mListView.smoothScrollToPosition(0);
						}
					});
				};
			}.start();
			Log.d(TAG, Thread.currentThread().getName() + "");
			Log.d(TAG, "滚回顶部");
			// 显示按钮
			mBtnClear.setVisibility(View.VISIBLE);
		};

		protected Void doInBackground(Void... params) {
			try {
				List<PackageInfo> packages = mPm.getInstalledPackages(0);
				// 设置最大进度
				mPbProgress.setMax(packages.size());
				for (PackageInfo packageInfo : packages) {
					Thread.sleep(100);
					progressCount++;
					// 从这里根据包名进行计算缓存大小,利用反射
					Method method = mPm.getClass().getMethod(
							"getPackageSizeInfo", String.class,
							IPackageStatsObserver.class);
					method.invoke(mPm, packageInfo.packageName, mStatsObserver);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	};

	final IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
		public void onGetStatsCompleted(final PackageStats stats,
				boolean succeeded) {
			// 获取包管理器,根据包名
			try {
				ApplicationInfo applicationInfo = mPm.getApplicationInfo(
						stats.packageName, 0);
				// 遍历
				final CacheInfo info = new CacheInfo();
				// 设置数据
				info.setName(applicationInfo.loadLabel(mPm).toString());
				info.setIcon(applicationInfo.loadIcon(mPm));
				info.setPackageName(stats.packageName);
				info.setCacheSize(stats.cacheSize);

				runOnUiThread(new Runnable() {
					public void run() {
						if (stats.cacheSize > 0) {
							mDatas.add(0, info);
							count++;
							allSize += stats.cacheSize;
						} else {
							mDatas.add(info);
						}
					}
				});

				// 进行publish一下
				task.UpdateProgress(info);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private class MyDatasAdapter extends BaseAdapter {

		public int getCount() {
			if (mDatas != null) {
				return mDatas.size();
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
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_cache_listview, null);
				holder = new ViewHolder();
				convertView.setTag(holder);

				// 初始化item的控件
				holder.mIvItemclean = (ImageView) convertView
						.findViewById(R.id.item_cache_iv_clean);
				holder.mIvItemIcon = (ImageView) convertView
						.findViewById(R.id.item_cache_iv_icon);
				holder.mTvItemCacheSize = (TextView) convertView
						.findViewById(R.id.item_cache_tv_cachesize);
				holder.mTvItemName = (TextView) convertView
						.findViewById(R.id.item_cache_tv_name);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// 进行控件的数据加载
			final CacheInfo info = mDatas.get(position);
			if (info.getCacheSize() > 0) {
				holder.mIvItemclean.setVisibility(View.VISIBLE);
			} else {
				holder.mIvItemclean.setVisibility(View.GONE);
			}
			final View conView = convertView;
			holder.mIvItemclean.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// 虚假清理最后还是靠,一键位清理
					TranslateAnimation animation = new TranslateAnimation(
							Animation.RELATIVE_TO_PARENT, 0,
							Animation.RELATIVE_TO_PARENT, 1,
							Animation.RELATIVE_TO_PARENT, 0,
							Animation.RELATIVE_TO_PARENT, 0);
					animation.setDuration(300);
					conView.startAnimation(animation);
					animation.setAnimationListener(new AnimationListener() {

						public void onAnimationStart(Animation animation) {

						}

						public void onAnimationRepeat(Animation animation) {

						}

						public void onAnimationEnd(Animation animation) {
							count--;
							allSize -= info.getCacheSize();
							// 标题的UI更新下
							mTvDesc.setText("总共有"
									+ count
									+ "个软件缓存,共"
									+ Formatter.formatFileSize(
											getApplicationContext(), allSize));
							mDatas.remove(info);
							mAdapter.notifyDataSetChanged();
						}
					});
				}
			});
			holder.mIvItemIcon.setImageDrawable(info.getIcon());
			holder.mTvItemCacheSize.setText("缓存大小: "
					+ Formatter.formatFileSize(getApplicationContext(),
							info.getCacheSize()));
			holder.mTvItemName.setText(info.getName());
			return convertView;
		}
	}

	private static class ViewHolder {
		ImageView mIvItemIcon;
		ImageView mIvItemclean;
		TextView mTvItemName;
		TextView mTvItemCacheSize;
	}

	public void fastScan(View view) {
		// 快速扫描
		mDatas.clear();
		task = new MyAsyncTask();
		task.execute();
		// 初始化数据
		allSize = 0;
		progressCount = 0;
		count = 0;
	}
}

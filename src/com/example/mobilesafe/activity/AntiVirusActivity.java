package com.example.mobilesafe.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobilesafe.R;
import com.example.mobilesafe.db.AntiVirusDao;
import com.example.mobilesafe.domain.AntiVirusInfo;
import com.example.mobilesafe.utils.MD5Utils;
import com.github.lzyzsd.circleprogress.ArcProgress;

public class AntiVirusActivity extends Activity implements OnClickListener {
	private PackageManager mPm;
	private AntiVirusDao mDao;
	private List<AntiVirusInfo> mDatas;
	private ListView mListView;
	private TextView mTvPackageName;
	private ArcProgress mCpProgress;// 进度
	private RelativeLayout mRlProgressContainer; // 进度的容器
	private LinearLayout mLlResultContainer; // 结果的容器
	private LinearLayout mLlAnimationSet;// 动画的容器
	private ImageView mIvLeft;
	private ImageView mIvRight;

	private TextView mTvDesc;
	private Button mBtnQuick;
	private boolean antivirusFlag = false;

	private DatasAdapter adapter = null;
	private MyTask task;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_antivirus);
		// 初始化控件
		mListView = (ListView) findViewById(R.id.antivirus_lv_listview);
		mTvPackageName = (TextView) findViewById(R.id.antivirus_tv_packagename);
		mCpProgress = (ArcProgress) findViewById(R.id.antivirus_ap_progress);
		mRlProgressContainer = (RelativeLayout) findViewById(R.id.antivirus_rl_progress_container);
		mLlResultContainer = (LinearLayout) findViewById(R.id.antivirus_ll_result_container);
		mTvDesc = (TextView) findViewById(R.id.antivirus_tv_desc);
		mBtnQuick = (Button) findViewById(R.id.antivirus_btn_quick);
		mLlAnimationSet = (LinearLayout) findViewById(R.id.antivirus_ll_animation_container);
		mIvLeft = (ImageView) findViewById(R.id.antivirus_iv_left);
		mIvRight = (ImageView) findViewById(R.id.antivirus_iv_right);

		mBtnQuick.setOnClickListener(this);

		mDao = new AntiVirusDao();
		mDatas = new ArrayList<AntiVirusInfo>();
		start();
	}

	public void start() {
		task = new MyTask();
		task.execute();
	}

	private class MyTask extends AsyncTask<Void, AntiVirusInfo, Void> {
		private int progressCount = 0;
		private int max = 0;

		protected void onProgressUpdate(AntiVirusInfo... values) {
			if (isCancelled())
				return;
			AntiVirusInfo info = values[0];
			mTvPackageName.setText(info.getPackageName());
			// 获取进度百分比
			int a = (int) ((progressCount * 100f) / max + 0.5f);
			mCpProgress.setProgress(a);
			// 添加listview
			if (mDatas.size() == 1) {
				adapter = new DatasAdapter();
				mListView.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
			// 移动到最后一个
			mListView.smoothScrollToPosition(mDatas.size());
		}

		protected Void doInBackground(Void... params) {

			mPm = getPackageManager();
			List<PackageInfo> packages = mPm
					.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
			max = packages.size();
			for (PackageInfo packageInfo : packages) {
				if (isCancelled())
					return null;
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// 进度
				progressCount++;
				final AntiVirusInfo anti = new AntiVirusInfo();
				// 获取所有的安装信息
				ApplicationInfo info = packageInfo.applicationInfo;
				String name = info.loadLabel(mPm).toString();// 名字
				String packageName = info.packageName;// 包名
				Drawable icon = info.loadIcon(mPm); // 图标
				// 获取这个应用的md5值
				String dir = info.sourceDir;
				File file = new File(dir);
				String md5 = MD5Utils.encode(file);
				final boolean isAntiVirus = mDao.find(getApplicationContext(),
						md5); // 是不是病毒
				// 设置数据
				anti.setAntiVirus(isAntiVirus);
				anti.setIcon(icon);
				anti.setName(name);
				anti.setPackageName(packageName);

				// 恢复主线程
				runOnUiThread(new Runnable() {
					public void run() {
						if (isAntiVirus) {
							mDatas.add(0, anti);
						} else {
							mDatas.add(anti);
						}
					}
				});

				// 更新下进度
				publishProgress(anti);
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			mBtnQuick.setEnabled(false);
			mDatas.clear();
			mRlProgressContainer.setVisibility(View.VISIBLE);
			mLlResultContainer.setVisibility(View.GONE);
		}

		protected void onPostExecute(Void result) {
			mRlProgressContainer.setVisibility(View.GONE);
			mLlResultContainer.setVisibility(View.VISIBLE);
			if (antivirusFlag) {
				mTvDesc.setText("你当前的手机极度不安全");
				mTvDesc.setTextColor(Color.RED);
			} else {
				mTvDesc.setText("你当前的手机很安全");
				mTvDesc.setTextColor(Color.GREEN);
			}
			// 移动到第一个
			mListView.smoothScrollToPosition(0);
			// 添加一个动画
			mIvLeft.setImageBitmap(setLeftBitmap());
			mIvRight.setImageBitmap(setRightBitmap());
			mLlAnimationSet.setVisibility(View.VISIBLE);
			// 开启动画集合
			showAnimationSet();
			mRlProgressContainer.setVisibility(View.GONE);
			mLlResultContainer.setVisibility(View.VISIBLE);
		}

	}

	private void showAnimationSet() {
		AnimatorSet set = new AnimatorSet();
		mIvLeft.measure(0, 0);
		mIvRight.measure(0, 0);
		set.playTogether(
				ObjectAnimator.ofFloat(mIvLeft, "translationX", 0,
						-mIvLeft.getMeasuredWidth()),
				ObjectAnimator.ofFloat(mIvRight, "translationX", 0,
						mIvRight.getMeasuredWidth()),
				ObjectAnimator.ofFloat(mIvLeft, "alpha", 1, 0),
				ObjectAnimator.ofFloat(mIvRight, "alpha", 1, 0));
		set.setDuration(1000);
		set.start();
		set.addListener(new AnimatorListener() {

			public void onAnimationStart(Animator animation) {
			}

			public void onAnimationRepeat(Animator animation) {

			}

			public void onAnimationEnd(Animator animation) {
				mLlAnimationSet.setVisibility(View.GONE);
				mBtnQuick.setEnabled(true);
			}

			public void onAnimationCancel(Animator animation) {

			}
		});
	}

	private Bitmap setLeftBitmap() {
		// 设置原图
		mRlProgressContainer.setDrawingCacheEnabled(true);
		mRlProgressContainer
				.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

		Bitmap oldBit = mRlProgressContainer.getDrawingCache();
		int width = (int) (oldBit.getWidth() / 2f + 0.5f);
		int height = oldBit.getHeight();
		// 设置画布
		Bitmap newBit = Bitmap.createBitmap(width, height, oldBit.getConfig());
		// 设置画板
		Canvas canvas = new Canvas(newBit);
		// 设置画笔
		Paint paint = new Paint();
		Matrix matrix = new Matrix();
		// 开始画
		canvas.drawBitmap(oldBit, matrix, paint);
		return newBit;
	}

	private Bitmap setRightBitmap() {
		// 设置原图
		mRlProgressContainer.setDrawingCacheEnabled(true);
		mRlProgressContainer
				.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

		Bitmap oldBit = mRlProgressContainer.getDrawingCache();
		int width = (int) (oldBit.getWidth() / 2f + 0.5f);
		int height = oldBit.getHeight();
		// 设置画布
		Bitmap newBit = Bitmap.createBitmap(width, height, oldBit.getConfig());
		// 设置画板
		Canvas canvas = new Canvas(newBit);
		// 设置画笔
		Paint paint = new Paint();
		Matrix matrix = new Matrix();
		matrix.preTranslate(-width, 0);
		// 开始画
		canvas.drawBitmap(oldBit, matrix, paint);
		return newBit;
	}

	private class DatasAdapter extends BaseAdapter {

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
				holder = new ViewHolder();
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_antivirus_listview, null);
				convertView.setTag(holder);
				holder.mIvClean = (ImageView) convertView
						.findViewById(R.id.item_antivirus_iv_clean);
				holder.mIvIcon = (ImageView) convertView
						.findViewById(R.id.item_antivirus_iv_icon);
				holder.mTvIsAntivirus = (TextView) convertView
						.findViewById(R.id.item_antivirus_tv_isantivirus);
				holder.mTvName = (TextView) convertView
						.findViewById(R.id.item_antivirus_tv_name);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			AntiVirusInfo info = mDatas.get(position);
			if (info.isAntiVirus()) {
				// 是病毒
				antivirusFlag = true;
				holder.mIvClean.setVisibility(View.VISIBLE);
				holder.mTvIsAntivirus.setTextColor(Color.RED);
				holder.mTvIsAntivirus.setText("病毒");
			} else {
				holder.mIvClean.setVisibility(View.GONE);
				holder.mTvIsAntivirus.setTextColor(Color.GREEN);
				holder.mTvIsAntivirus.setText("安全");
			}
			holder.mIvIcon.setImageDrawable(info.getIcon());
			holder.mTvName.setText(info.getName());
			return convertView;
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		task.cancel(true);
	}

	private class ViewHolder {
		ImageView mIvIcon;
		ImageView mIvClean;
		TextView mTvIsAntivirus;
		TextView mTvName;
	}

	@Override
	public void onClick(View v) {
		if (v.equals(mBtnQuick)) {
			mBtnQuick.setEnabled(false);
			mLlAnimationSet.setVisibility(View.VISIBLE);

			AnimatorSet set = new AnimatorSet();
			mIvLeft.measure(0, 0);
			mIvRight.measure(0, 0);
			set.playTogether(
					ObjectAnimator.ofFloat(mIvLeft, "translationX",
							-mIvLeft.getMeasuredWidth(), 0),
					ObjectAnimator.ofFloat(mIvRight, "translationX",
							mIvRight.getMeasuredWidth(), 0),
					ObjectAnimator.ofFloat(mIvLeft, "alpha", 1, 0),
					ObjectAnimator.ofFloat(mIvRight, "alpha", 1, 0));
			set.setDuration(1000);
			set.start();
			set.addListener(new AnimatorListener() {

				public void onAnimationStart(Animator animation) {
				}

				public void onAnimationRepeat(Animator animation) {

				}

				public void onAnimationEnd(Animator animation) {
					mLlAnimationSet.setVisibility(View.GONE);
					start();
				}

				public void onAnimationCancel(Animator animation) {

				}
			});
		}
	}
}

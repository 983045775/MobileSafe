package com.example.mobilesafe.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobilesafe.R;
import com.example.mobilesafe.db.AntiVirusDao;
import com.example.mobilesafe.domain.AntiVirusInfo;
import com.example.mobilesafe.utils.MD5Utils;

public class AntiVirusActivity extends Activity {
	private PackageManager mPm;
	private AntiVirusDao mDao;
	private List<AntiVirusInfo> mDatas;
	private ListView mListView;
	private TextView mTvPackageName;
	private TextView mTvPercentage;
	private DatasAdapter adapter = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_antivirus);
		// 初始化控件
		mListView = (ListView) findViewById(R.id.antivirus_lv_listview);
		mTvPackageName = (TextView) findViewById(R.id.antivirus_tv_packagename);
		mTvPercentage = (TextView) findViewById(R.id.antivirus_tv_percentage);

		mDao = new AntiVirusDao();
		mDatas = new ArrayList<AntiVirusInfo>();
		// 加载所有的包数据
		new AsyncTask<Void, AntiVirusInfo, Void>() {

			private int progressCount = 0;
			private int max = 0;

			protected Void doInBackground(Void... params) {

				mPm = getPackageManager();
				List<PackageInfo> packages = mPm
						.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
				max = packages.size();
				for (PackageInfo packageInfo : packages) {
					try {
						Thread.sleep(100);
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
					final boolean isAntiVirus = mDao.find(
							getApplicationContext(), md5); // 是不是病毒
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

			protected void onProgressUpdate(AntiVirusInfo... values) {
				AntiVirusInfo info = values[0];
				mTvPackageName.setText(info.getPackageName());
				// 获取进度百分比
				int a = (int) (progressCount * 100f) / max;
				mTvPercentage.setText(a + "%");
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
			
			protected void onPostExecute(Void result) {
				// 移动到第一个
				mListView.smoothScrollToPosition(0);
			};

		}.execute();
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

	private class ViewHolder {
		ImageView mIvIcon;
		ImageView mIvClean;
		TextView mTvIsAntivirus;
		TextView mTvName;
	}
}

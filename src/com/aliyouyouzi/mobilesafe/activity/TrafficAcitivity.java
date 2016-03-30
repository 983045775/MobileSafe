package com.aliyouyouzi.mobilesafe.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aliyouyouzi.mobilesafe.domain.TrafficInfo;
import com.aliyouyouzi.mobilesafe.R;

public class TrafficAcitivity extends Activity {

	private ListView mListview;
	private PackageManager mPm;
	private List<TrafficInfo> mDatas;
	private LinearLayout mLoading;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic);
		mListview = (ListView) findViewById(R.id.traffic_lv_listview);
		mLoading = (LinearLayout) findViewById(R.id.black_ll_loading);

		mDatas = new ArrayList<TrafficInfo>();
		// 查找需要的流量统计的数据信息,每个应用的uid
		mPm = getPackageManager();

		// 开启一个异步任务
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				mLoading.setVisibility(View.VISIBLE);
			}

			protected Void doInBackground(Void... params) {
				List<PackageInfo> packages = mPm.getInstalledPackages(0);
				TrafficInfo traInfo = null;
				for (PackageInfo packageInfo : packages) {

					ApplicationInfo info = packageInfo.applicationInfo;
					int uid = info.uid;// uid
					String name = info.loadLabel(mPm).toString();// 应用名字
					String packageName = info.packageName;// 包名
					Drawable icon = info.loadIcon(mPm);// 图标
					long rcv = getRcvTraffic(uid);// 接收的流量
					long send = getSendTraffic(uid);// 发送的流量

					// 设置数据
					if (rcv > 0 || send > 0) {
						traInfo = new TrafficInfo();
						traInfo.setIcon(icon);
						traInfo.setName(name);
						traInfo.setPackageName(packageName);
						traInfo.setRcv(rcv);
						traInfo.setSend(send);
						mDatas.add(traInfo);
					}
				}
				return null;
			}

			protected void onPostExecute(Void result) {
				mLoading.setVisibility(View.GONE);
				// 设置UI
				mListview.setAdapter(new TrafficAdapter());
			}

		}.execute();

	}

	private class TrafficAdapter extends BaseAdapter {

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
				// 需要创建
				convertView = View.inflate(TrafficAcitivity.this,
						R.layout.item_traffic_listview, null);
				holder = new ViewHolder();
				convertView.setTag(holder);
				holder.icon = (ImageView) convertView
						.findViewById(R.id.item_traffic_iv_icon);
				holder.name = (TextView) convertView
						.findViewById(R.id.item_traffic_tv_name);
				holder.rcv = (TextView) convertView
						.findViewById(R.id.item_traffic_tv_rcv);
				holder.send = (TextView) convertView
						.findViewById(R.id.item_traffic_tv_send);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.icon.setImageDrawable(mDatas.get(position).getIcon());
			holder.rcv.setText("接收: "
					+ Formatter.formatFileSize(getApplicationContext(), mDatas
							.get(position).getRcv()));
			holder.send.setText("发送: "
					+ Formatter.formatFileSize(getApplicationContext(), mDatas
							.get(position).getSend()));
			holder.name.setText(mDatas.get(position).getName());
			return convertView;
		}
	}

	private static class ViewHolder {
		ImageView icon;
		TextView name;
		TextView rcv;
		TextView send;
	}

	private long getSendTraffic(int uid) {
		String url = "/proc/uid_stat/" + uid + "/tcp_snd";
		BufferedReader reader = null;
		try {
			File file = new File(url);
			reader = new BufferedReader(new FileReader(file));
			Long send = Long.valueOf(reader.readLine());
			return send;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
					reader = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	private long getRcvTraffic(int uid) {
		String url = "/proc/uid_stat/" + uid + "/tcp_rcv";
		BufferedReader reader = null;
		try {
			File file = new File(url);
			reader = new BufferedReader(new FileReader(file));
			Long rcv = Long.valueOf(reader.readLine());
			return rcv;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
					reader = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
}

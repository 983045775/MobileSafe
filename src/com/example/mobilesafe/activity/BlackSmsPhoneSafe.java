package com.example.mobilesafe.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.R;
import com.example.mobilesafe.db.BlackSafeDao;
import com.example.mobilesafe.domain.BlackInfo;

/**
 * 黑名单短信和号码的页面
 * 
 * @author liu
 * 
 */
public class BlackSmsPhoneSafe extends Activity implements OnItemClickListener {

	public static final int BLACK_CALL = 0;
	public static final int BLACK_SMS = 1;
	public static final int BLACK_ALL = 2;
	private static final int REQUESTCODE_OK = 100;
	private static final int REQUESTCODE_UPDATA = 101;
	private static final String TAG = "BlackSmsPhoneSafe";
	private ListView black_lv;
	private List<BlackInfo> list;
	private BlackMyAdapter myAdapter;
	private BlackSafeDao dao;
	private LinearLayout mLlLoading;
	private ImageView mIv_empty;
	private int SelectNum = 20;// 查询的个数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_sms_phone_safe);

		dao = new BlackSafeDao(this);
		black_lv = (ListView) findViewById(R.id.black_lv);
		mLlLoading = (LinearLayout) findViewById(R.id.black_ll_loading);
		mIv_empty = (ImageView) findViewById(R.id.black_iv_empty);
		myAdapter = new BlackMyAdapter();
		// 开启一个线程
		new Thread() {
			public void run() {
				try {
					Thread.sleep(1300);
				} catch (Exception e) {
					e.printStackTrace();
				}
				list = dao.findpart(SelectNum, 0);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mLlLoading.setVisibility(View.GONE);
						black_lv.setAdapter(myAdapter);
						black_lv.setEmptyView(mIv_empty);
						// 给black_lv添加一个点击事件
						black_lv.setOnItemClickListener(BlackSmsPhoneSafe.this);
					}
				});
			};
		}.start();
		// 添加一个下拉的监听
		black_lv.setOnScrollListener(new OnScrollListener() {

			@Override
			// 监听状态改变
			// SCROLL_STATE_IDLE 状态空闲
			// SCROLL_STATE_TOUCH_SCROLL 触摸滑动不松开
			// SCROLL_STATE_FLING 状态惯性
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 获取最后一个指针
				int end = black_lv.getLastVisiblePosition();
				// 空闲并且到底部
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& end == list.size() - 1) {
					Log.d(TAG, "到底部了");
					// 进行重新加载
					new Thread() {
						public void run() {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									mLlLoading.setVisibility(View.VISIBLE);
								}
							});
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							final List<BlackInfo> partList = dao.findpart(
									SelectNum, list.size());
							list.addAll(partList);
							// 消失进度\
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if (partList.size() == 0) {
										Toast.makeText(BlackSmsPhoneSafe.this,
												"已经到底了", Toast.LENGTH_SHORT)
												.show();
									}
									mLlLoading.setVisibility(View.GONE);
								}
							});
							// UI更新
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									myAdapter.notifyDataSetChanged();
								}
							});
						};
					}.start();
				}
			}

			@Override
			// 监听firstVisibleItem 第一个是几
			// visibleItemCount 当前显示
			// totalItemCount 总数
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
	}

	/**
	 * listview界面的添加
	 * 
	 * @author liu
	 * 
	 */
	public class BlackMyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (list != null) {
				return list.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (list != null) {
				return list.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(BlackSmsPhoneSafe.this,
						R.layout.item_black_listview, null);
				holder = new ViewHolder();
				holder.iv_delete = (ImageView) convertView
						.findViewById(R.id.item_black_iv_delete);
				holder.tv_number = (TextView) convertView
						.findViewById(R.id.item_black_tv_number);
				holder.tv_type = (TextView) convertView
						.findViewById(R.id.item_black_tv_type);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.tv_number.setText(list.get(position).getNumber());
			int type = Integer.parseInt(list.get(position).getType());
			switch (type) {
			case BLACK_CALL:
				holder.tv_type.setText("电话拦截");
				break;
			case BLACK_SMS:
				holder.tv_type.setText("短信拦截");

				break;
			case BLACK_ALL:
				holder.tv_type.setText("电话+短信拦截");

				break;
			}
			holder.iv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 进行删除数据库对应的这条
					boolean delete = dao.delete(list.get(position).getNumber());
					if (delete) {
						// 删除成功
						list.remove(list.get(position));
						Toast.makeText(BlackSmsPhoneSafe.this, "删除成功",
								Toast.LENGTH_SHORT).show();
						myAdapter.notifyDataSetChanged();
					} else {
						// 删除失败
						Toast.makeText(BlackSmsPhoneSafe.this, "删除失败",
								Toast.LENGTH_SHORT).show();
					}
				}
			});
			return convertView;
		}

	}

	private static class ViewHolder {
		TextView tv_number;
		TextView tv_type;
		ImageView iv_delete;

	}

	/**
	 * 页面返回,判断做什么
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (resultCode) {
		case Activity.RESULT_OK:
			if (data != null) {
				BlackInfo info = (BlackInfo) data.getSerializableExtra("info");
				list.add(info);
				myAdapter.notifyDataSetChanged();
			}
			break;
		case Activity.RESULT_CANCELED:
			if (data != null) {
				int position = data.getIntExtra(BlackAddActivity.POSITION, -1);
				int type = data.getIntExtra(BlackAddActivity.TYPE, -1);
				list.get(position).setType(type + "");
				myAdapter.notifyDataSetChanged();
			}
			break;

		}
	}

	/**
	 * 添加按钮点击的效果
	 * 
	 * @param view
	 */
	public void addBlack(View view) {
		Intent intent = new Intent(this, BlackAddActivity.class);
		intent.setAction(BlackAddActivity.ADD);
		startActivityForResult(intent, REQUESTCODE_OK);
	}

	/**
	 * 每个item添加点击效果
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, BlackAddActivity.class);
		intent.putExtra(BlackAddActivity.NUMBER, list.get(position).getNumber());
		intent.putExtra(BlackAddActivity.TYPE, list.get(position).getType());
		intent.putExtra(BlackAddActivity.POSITION, position);
		intent.setAction(BlackAddActivity.UPDATA);
		startActivityForResult(intent, REQUESTCODE_UPDATA);
		Log.d(TAG, position + "");
	}
}

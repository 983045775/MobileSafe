package com.example.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.R;
import com.example.mobilesafe.domain.HomeItem;
import com.example.mobilesafe.utils.Constants;
import com.example.mobilesafe.utils.PreferencesUtils;

public class HomeActivity extends Activity implements OnItemClickListener {

	private static final String[] ITEMNAME = new String[] { "手机防盗", "骚扰拦截",
			"软件管家", "进程管理", "流量统计", "手机杀毒", "缓存清理", "常用工具" };
	private static final String[] DESCS = new String[] { "远程定位手机", "全面拦截骚扰",
			"管理您的软件", "管理运行进程", "流量一目了然", "病毒无法藏身", "系统快如火箭", "常用工具大全" };
	private static final int[] ITEMID = new int[] { R.drawable.sjfd,
			R.drawable.srlj, R.drawable.rjgj, R.drawable.jcgl, R.drawable.lltj,
			R.drawable.sjsd, R.drawable.hcql, R.drawable.cygj };
	private static final String TAG = "HomeActivity";
	private ImageView mivlogo;

	private GridView mgridview;
	private List<HomeItem> home_item;
	private int photo_width;
	private int photo_height;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化代码
		info();
		// 添加旋转
		// mivlogo.setRotationY(rotationY)
		ObjectAnimator animator = ObjectAnimator.ofFloat(mivlogo, "rotationY",
				0, 45, 90, 180, 270, 360);
		animator.setDuration(3000);
		animator.setRepeatCount(ObjectAnimator.INFINITE);
		animator.setRepeatMode(ObjectAnimator.RESTART);
		animator.start();
		add_GridView();
	}

	public void add_GridView() {
		// 添加到list
		home_item = new ArrayList<HomeItem>();
		for (int x = 0; x < ITEMID.length; x++) {
			HomeItem item = new HomeItem();
			item.setDesc(DESCS[x]);
			item.setPhotoid(ITEMID[x]);
			item.setItemname(ITEMNAME[x]);
			// 添加
			home_item.add(item);
		}
		// 给gridview添加适配器
		mgridview.setAdapter(new MyGridAdapter());
		// 给每个item添加点击事件
		mgridview.setOnItemClickListener(this);
	}

	// 自定义适配器
	private class MyGridAdapter extends BaseAdapter {

		public int getCount() {
			return home_item.size();
		}

		@Override
		public Object getItem(int position) {
			return home_item.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TextView textView = new TextView(HomeActivity.this);
			View item_home = View.inflate(HomeActivity.this,
					R.layout.item_home_gridview, null);
			// 获取图片,名字,介绍
			ImageView item_home_iv = (ImageView) item_home
					.findViewById(R.id.item_home_iv_photo);
			TextView item_home_name = (TextView) item_home
					.findViewById(R.id.item_home_tv_name);
			TextView item_home_desc = (TextView) item_home
					.findViewById(R.id.item_home_tv_desc);
			// 添加
			// 获取图片宽高器
			android.view.ViewGroup.LayoutParams para = item_home_iv
					.getLayoutParams();
			// 设置图片宽高
			para.width = (int) (photo_width / 6.7f + 0.5f);
			para.height = (int) (photo_width / 6.7f + 0.5f);
			item_home_iv.setLayoutParams(para);
			item_home_iv.setImageResource(home_item.get(position).getPhotoid());
			item_home_name.setText(home_item.get(position).getItemname());
			item_home_desc.setText(home_item.get(position).getDesc());
			return item_home;
		}
	}

	@SuppressWarnings("deprecation")
	private void info() {
		setContentView(R.layout.activity_home);
		mivlogo = (ImageView) findViewById(R.id.home_logo);
		mgridview = (GridView) findViewById(R.id.home_grid);
		// 获取屏幕宽高
		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display windowdisplay = windowManager.getDefaultDisplay();
		// 获取手机宽高
		photo_width = windowdisplay.getWidth();
		photo_height = windowdisplay.getHeight();
		Log.i(TAG, "宽和高是:" + photo_width + "--" + photo_height);
	}

	/**
	 * 菜单设置
	 * 
	 * @param view
	 */
	public void home_setting(View view) {
		Intent intent = new Intent(this, SettingActivity.class);
		startActivity(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0:
			// 说明点击了手机防盗
			performsjfd();
			break;
		case 1:
			// 点击了骚扰拦截
			performcss();
			break;
		case 2:
			// 点击了软件管家
			performappmanager();
			break;
		case 3:
			// 点击了进程管理
			performProgressManager();
			break;
		case 6:
			// 点击了常用工具
			perforCacheClena();
			break;
		case 7:
			// 点击了常用工具
			commonTools();
			break;
		}
	}

	private void perforCacheClena() {
		Intent intent = new Intent(this, CacheCleanActivity.class);
		startActivity(intent);
	}

	private void performProgressManager() {
		Intent intent = new Intent(this, ProgressManagerActivity.class);
		startActivity(intent);
	}

	private void performappmanager() {
		Intent intent = new Intent(this, AppManagerActivity.class);
		startActivity(intent);

	}

	private void commonTools() {
		Intent intent = new Intent(this, CommonToolsActivity.class);
		startActivity(intent);
	}

	private void performcss() {
		// 说明点击了
		Intent intent = new Intent(this, BlackSmsPhoneSafe.class);
		startActivity(intent);
	}

	private void performsjfd() {
		String password = PreferencesUtils.getString(this,
				Constants.SJFD_PASSWORD, null);
		// 如果有数据说明不是第一次点
		if (password == null) {
			Log.d(TAG, "以前没点过");
			// 说明是第一次点
			showinitPwdDialog();
		} else {
			Log.d(TAG, "以前点过");
			// 没有数据说明是第一次点
			showEnterPwdDialog();
		}
	}

	private void showEnterPwdDialog() {
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.diglog_sjfd_end, null);
		dialog.setView(view);
		// 查找控件
		final EditText et_pwd = (EditText) view
				.findViewById(R.id.dialog_tv_pwd);
		Button bt_cancel = (Button) view.findViewById(R.id.dialog_bt_cancel);
		Button bt_confirm = (Button) view.findViewById(R.id.dialog_bt_confirm);
		bt_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String pwd = et_pwd.getText().toString().trim();
				String pre_pwd = PreferencesUtils.getString(HomeActivity.this,
						Constants.SJFD_PASSWORD);
				if (TextUtils.isEmpty(pwd)) {
					// 不能为空
					Toast.makeText(HomeActivity.this, "密码不能空",
							Toast.LENGTH_SHORT).show();
					et_pwd.requestFocus();
					return;
				}
				if (!pwd.equals(pre_pwd)) {
					Toast.makeText(HomeActivity.this, "密码不不正确",
							Toast.LENGTH_SHORT).show();
					et_pwd.requestFocus();
					return;
				}
				// 到这里说明正确了
				dialog.dismiss();
				Log.d(TAG, "去手机防盗界面了");
				// 去手机防盗界面
				gotosjfd();
			}

		});
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	// 去手机防盗界面
	private void gotosjfd() {
		// 获取标记,
		boolean flag = PreferencesUtils.getBoolean(this, Constants.SJFD_SETUP);
		if (flag) {
			// 如果true说明以前设置过,就跳到最后一个页面
			Intent intent = new Intent(this, SjfdSetupEndActivity.class);
			startActivity(intent);
			Log.d(TAG, "跳到最后一个界面");
		} else {
			Log.d(TAG, "从第一个界面开始");
			Intent intent = new Intent(this, SjfdSetupOneActivity.class);
			startActivity(intent);
		}
	}

	private void showinitPwdDialog() {
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.diglog_sjfd_one, null);
		builder.setView(view);
		final AlertDialog dialog = builder.create();
		// 首先获取控件
		Button bt_cancel = (Button) view.findViewById(R.id.dialog_bt_cancel);
		Button bt_confirm = (Button) view.findViewById(R.id.dialog_bt_confirm);
		final EditText tv_confirm = (EditText) view
				.findViewById(R.id.dialog_tv_confirm);
		final EditText tv_pwd = (EditText) view
				.findViewById(R.id.dialog_tv_pwd);
		// 进行校验数据
		// 确定按钮
		bt_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String pwd = tv_pwd.getText().toString().trim();
				Log.d(TAG, pwd);
				String confirm_pwd = tv_confirm.getText().toString().trim();
				if (TextUtils.isEmpty(pwd)) {
					// 说明密码为空 toast进行提示
					Toast.makeText(HomeActivity.this, "密码不能为空",
							Toast.LENGTH_SHORT).show();
					// 获取焦点
					tv_pwd.requestFocus();
					return;
				}
				if (TextUtils.isEmpty(confirm_pwd)) {
					// 说明密码为空 toast进行提示
					Toast.makeText(HomeActivity.this, "确认密码不能为空",
							Toast.LENGTH_SHORT).show();
					// 获取焦点
					tv_confirm.requestFocus();
					return;
				}
				// 校验两次密码不一致
				if (!pwd.equals(confirm_pwd)) {
					Toast.makeText(HomeActivity.this, "两次密码不一致",
							Toast.LENGTH_SHORT).show();
					return;
				}
				// 进行密码的存储
				PreferencesUtils.putString(HomeActivity.this,
						Constants.SJFD_PASSWORD, pwd);
				dialog.dismiss();
			}
		});
		// 取消按钮
		bt_cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 进行取消dialog
				dialog.dismiss();
			}
		});
		dialog.show();
	}
}

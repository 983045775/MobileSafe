package com.example.mobilesafe.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.R;
import com.example.mobilesafe.service.ProtectService;
import com.example.mobilesafe.utils.Constants;
import com.example.mobilesafe.utils.GzipUtils;
import com.example.mobilesafe.utils.PackageUtils;
import com.example.mobilesafe.utils.PreferencesUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class SplashActivity extends Activity {

	protected static final String TAG = "SplashActivity";
	protected static final int SHOW_ERROR = 1;
	protected static final int UPDATE = 2;
	protected static final int INSERT_PACKAGE = 3;
	private TextView splash_tv_version;
	private String update_desc;// 更新描述信息
	private String update_url;// 更新地址
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_ERROR:
				Toast.makeText(SplashActivity.this, "检查更新失败",
						Toast.LENGTH_SHORT).show();
				toHome();
				break;
			case UPDATE:
				// 需要更新创建出一个dialog
				createDialog();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		// 获取版本号
		splash_tv_version = (TextView) findViewById(R.id.splash_tv_version);
		// 获取版本
		splash_tv_version.setText("版本号 : " + PackageUtils.getVersionName(this));
		// 获取当前的更新标记
		boolean updateDate = PreferencesUtils.getBoolean(this,
				Constants.AUTO_UPDATE, true);
		// 进行网络连接访问,校验版本
		if (updateDate) {
			Log.i(TAG, "需要更新");
			getVersion();
		} else {
			Log.i(TAG, "不需要更新");
			toHome();
		}
		// 进行解压拷贝数据库操作
		unGzipDataBase();
		// 进行拷贝常用数据库操作
		copyCommonNumber();
		// 进行拷贝常用数据库操作
		copyAntiVirusDB();
		// 开启保护进程
		Intent intent = new Intent(this, ProtectService.class);
		startService(intent);
	}

	private void copyAntiVirusDB() {
		new Thread() {
			public void run() {
				File file = new File(getFilesDir(), "antivirus.db");
				// 进行判断是否存在这个文件
				if (file.exists()) {
					// 存在了,
					Log.d(TAG, "病毒数据库已经存在了.");
				} else {
					InputStream in = null;
					FileOutputStream out = null;
					try {
						in = getAssets().open("antivirus.db");
						int len = -1;
						byte[] buffer = new byte[1024];
						out = new FileOutputStream(file);
						while ((len = in.read(buffer)) != -1) {
							out.write(buffer, 0, len);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							out.close();
							in.close();
							out = null;
							in = null;
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				}
			}
		}.start();
	}

	private void copyCommonNumber() {
		new Thread() {
			public void run() {
				File file = new File(getFilesDir(), "commonnum.db");
				if (!file.exists()) {
					// 需要解压
					Log.d(TAG, "commonnum需要解压");
					AssetManager manager = getAssets();
					InputStream input = null;
					OutputStream output = null;
					try {
						input = manager.open("commonnum.db");
						output = new FileOutputStream(file);

						byte[] buffer = new byte[1024];
						int len = -1;
						while ((len = input.read(buffer)) != -1) {
							output.write(buffer, 0, len);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							input.close();
							output.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else {
					// 不需要解压
					Log.d(TAG, "commonnum不需要解压");
				}
			};
		}.start();
	}

	private void unGzipDataBase() {
		final File file = new File(getFilesDir(), "address.db");
		if (file.exists()) {
			Log.d(TAG, "有数据库了,不需要解压");
			return;
		}
		new Thread() {
			public void run() {
				AssetManager manager = getAssets();
				try {
					InputStream in = manager.open("address.zip");
					// 写到哪里位置的文件
					OutputStream out = new FileOutputStream(file);
					// 进行解压
					GzipUtils.unZip(in, out);
					Log.d(TAG, "没有数据库了,需要解压");
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	public void toHome() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// 去主页
				Intent intent = new Intent(SplashActivity.this,
						HomeActivity.class);
				startActivity(intent);
				finish();
			}
		}, 1300);
	}

	public void createDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("需要更新");
		builder.setMessage(update_desc);
		// 不能让对话框消失
		builder.setCancelable(false);
		builder.setPositiveButton("立即更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 更新代码 TODO
				// 下载url的代码
				update_download();
			}
		});
		builder.setNegativeButton("稍后再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				toHome();
			}
		});
		builder.show();
	}

	private void update_download() {
		// 下载的对话框
		final ProgressDialog load_dialog = new ProgressDialog(
				SplashActivity.this);
		// 设置无法消失
		load_dialog.setCancelable(false);
		load_dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		load_dialog.show();
		// 下载
		HttpUtils httpUtils = new HttpUtils();
		final String target = new File(
				Environment.getExternalStorageDirectory(),
				System.currentTimeMillis() + ".apk").getPath();
		httpUtils.download(update_url, target, new RequestCallBack<File>() {

			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				load_dialog.dismiss();
				Log.i(TAG, "下载成功");
				// 下载成功了转向打开安装界面
				/*
				 * <intent-filter> <action
				 * android:name="android.intent.action.VIEW"/> <category
				 * android:name="android.intent.category.DEFAULT"/> <data
				 * android:scheme="content"/> <data android:scheme="file"/>
				 * <data
				 * android:mimeType="application/vnd.android.package-archive"/>
				 * </intent-filter>
				 */
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setDataAndType(Uri.parse("file://" + target),
						"application/vnd.android.package-archive");
				startActivityForResult(intent, INSERT_PACKAGE);
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				load_dialog.setMax((int) total);
				load_dialog.setProgress((int) current);
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Log.i(TAG, "下载失败");

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == INSERT_PACKAGE) {
			// 说明是我安装的请求码
			switch (resultCode) {
			case Activity.RESULT_OK:
				// 说明成功了..什么都不用管
				break;
			case Activity.RESULT_CANCELED:
				// 用户点了取消了
				toHome();
				break;
			}
		}
	}

	private void getVersion() {
		// 进行耗时的操作
		new Thread() {
			public void run() {
				AndroidHttpClient httpclient = AndroidHttpClient.newInstance(
						null, SplashActivity.this);
				// 设置超时

				HttpParams params = httpclient.getParams();
				HttpConnectionParams.setConnectionTimeout(params, 5000);
				HttpConnectionParams.setSoTimeout(params, 5000);
				// 获取连接
				String url = SplashActivity.this.getResources().getString(
						R.string.URL);
				// 用get提交方式
				HttpGet get = new HttpGet(url);
				try {
					HttpResponse response = httpclient.execute(get);
					// 获取请求码
					int code = response.getStatusLine().getStatusCode();
					if (code == 200) {
						// 请求成功
						// 获取输入流
						String json = EntityUtils.toString(
								response.getEntity(), "utf-8");
						Log.i(TAG, json);
						// 解析Json
						JSONObject jsonObject = new JSONObject(json);
						int netcode = jsonObject.getInt("version");
						// 获取本地的版本
						int locacode = PackageUtils
								.getVersionCode(SplashActivity.this);
						if (netcode > locacode) {
							// 需要更新
							Log.i(TAG, "需要更新");
							// 获取更新描述,已经更新url
							update_url = jsonObject.getString("url");
							update_desc = jsonObject.getString("desc");
							// 创建出一个dialog
							Message message = Message.obtain();
							message.what = UPDATE;
							handler.sendMessage(message);
						} else {
							// 不需要更新
							Log.i(TAG, "不需要更新");
							toHome();
						}
					} else {
						Message msg = new Message();
						msg.what = SHOW_ERROR;
						handler.sendMessage(msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessage(msg);
				} finally {
					httpclient.close();
				}
			};
		}.start();
	}
}

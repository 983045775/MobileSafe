package com.example.mobilesafe.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

public class SmsProvide {

	private static final int ALL_COUNT = 0;
	private static final int PROGRESS = 1;
	private static final String TAG = "SmsProvide";

	public static void smsBackups(final Context context,
			final OnBackupsListener listener) {
		new AsyncTask<Void, Integer, Boolean>() {
			protected Boolean doInBackground(Void... params) {
				// 获取短信内容
				ContentResolver resolver = context.getContentResolver();
				Uri uri = Uri.parse("content://sms/");
				String[] projection = new String[] { "address", "date", "type",
						"body" };
				FileOutputStream out = null;
				try {
					Cursor cursor = resolver.query(uri, projection, null, null,
							null);
					// 将读到的数据写入到SD卡吧
					XmlSerializer serializer = Xml.newSerializer();

					out = new FileOutputStream(new File(
							Environment.getExternalStorageDirectory(),
							"smsProvide.xml"));
					serializer.setOutput(out, "utf-8");
					serializer.startDocument("utf-8", true);
					serializer.startTag(null, "root");
					int count = 0;
					// 总数
					publishProgress(ALL_COUNT, cursor.getCount());
					while (cursor.moveToNext()) {
						count++;
						Thread.sleep(200);
						serializer.startTag(null, "sms");
						// address
						serializer.startTag(null, "address");
						String address = cursor.getString(0);
						serializer.text(address);
						serializer.endTag(null, "address");
						// date
						serializer.startTag(null, "date");
						String date = cursor.getString(1);
						serializer.text(date);
						serializer.endTag(null, "date");
						// type
						serializer.startTag(null, "type");
						String type = cursor.getString(2);
						serializer.text(type);
						serializer.endTag(null, "type");
						// body
						serializer.startTag(null, "body");
						String body = cursor.getString(3);
						serializer.text(body);
						serializer.endTag(null, "body");

						serializer.endTag(null, "sms");

						publishProgress(PROGRESS, count);
					}
					serializer.endTag(null, "root");
					serializer.endDocument();
					cursor.close();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						out = null;
					}
				}
			}

			protected void onProgressUpdate(Integer... values) {
				if (listener != null) {
					if (values[0] == 1) {
						listener.progress(values[1]);
					} else {
						// 总数
						listener.allCount(values[1]);
					}
				}
			}

			protected void onPostExecute(Boolean result) {
				if (result) {
					listener.success();
				} else {
					listener.fail();
				}
			};
		}.execute();

	}

	/**
	 * 短信恢复
	 * 
	 * @param context
	 * @param listener
	 */
	public static void smsRecover(final Context context,
			final OnBackupsListener listener) {
		new AsyncTask<Void, Integer, Boolean>() {
			protected Boolean doInBackground(Void... params) {
				FileInputStream in = null;
				// 获取xml解析器
				XmlPullParser pullParser = Xml.newPullParser();
				List<Smsinfo> list = new ArrayList<Smsinfo>();
				try {
					in = new FileInputStream(new File(
							Environment.getExternalStorageDirectory(),
							"smsProvide.xml"));
					pullParser.setInput(in, "utf-8");

					int type = pullParser.getEventType();

					Smsinfo sms = null;
					while (type != XmlPullParser.END_DOCUMENT) {
						// 没到底部
						switch (type) {
						case XmlPullParser.START_TAG:
							if (pullParser.getName().equals("address")) {
								sms.address = pullParser.nextText();
							} else if (pullParser.getName().equals("type")) {
								sms.type = pullParser.nextText();
							} else if (pullParser.getName().equals("date")) {
								sms.date = pullParser.nextText();
							} else if (pullParser.getName().equals("body")) {
								sms.body = pullParser.nextText();
							} else if (pullParser.getName().equals("sms")) {
								sms = new Smsinfo();
							}
							break;
						case XmlPullParser.END_TAG:
							list.add(sms);
							break;
						}
						type = pullParser.next();
					}
					if (list.size() > 0) {

						// 进行插入数据库短信
						ContentResolver resolver = context.getContentResolver();
						Uri url = Uri.parse("content://sms/");
						publishProgress(ALL_COUNT, list.size() - 1);
						int count = 0;
						for (int x = 0; x < list.size() - 1; x++) {
							Smsinfo info = list.get(x);
							try {
								Thread.sleep(300);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							ContentValues values = new ContentValues();
							values.put("address", info.address);
							values.put("date", info.date);
							values.put("body", info.body);
							values.put("type", info.type);
							count++;

							publishProgress(PROGRESS, count);
							Uri uri = resolver.insert(url, values);
							Log.d(TAG, uri.toString());
						}

					}
					return true;
				} catch (Exception e) {
					return false;
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						in = null;
					}
				}
			}

			protected void onProgressUpdate(Integer[] values) {
				if (listener != null) {
					if (values[0] == ALL_COUNT) {
						listener.allCount(values[1]);
					} else {
						listener.progress(values[1]);
					}

				}
			};

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					listener.success();
				} else {
					listener.fail();
				}
			}
		}.execute();
	}

	public static interface OnBackupsListener {
		// 进度 总数 成功 失败
		void progress(int progress);

		void allCount(int count);

		void success();

		void fail();
	}

	public static class Smsinfo {
		public String type;
		public String address;
		public String date;
		public String body;
	}
}

package com.aliyouyouzi.mobilesafe.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class AppLockDao {

	private AppLockOpenHelper helper = null;
	private Context context;

	public AppLockDao(Context context) {
		this.context = context;
		helper = new AppLockOpenHelper(context);
	}

	/**
	 * 根据包名增加一条数据
	 * 
	 * @param packageName
	 * @return
	 */
	public boolean add(String packageName) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(AppLockConstant.APPLOCK_PACKAGENAME, packageName);
		long insert = db.insert(AppLockConstant.DATA_NAME, null, values);
		db.close();
		// 添加一个内容提供者检测
		ContentResolver resolver = context.getContentResolver();
		resolver.notifyChange(Uri.parse("content://org.mobile.lock"), null);
		return insert != -1;
	}

	/**
	 * 根据包名删除一条数据
	 * 
	 * @param packageName
	 * @return
	 */
	public boolean delete(String packageName) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String whereClause = AppLockConstant.APPLOCK_PACKAGENAME + "=?";
		int delete = db.delete(AppLockConstant.DATA_NAME, whereClause,
				new String[] { packageName });
		db.close();
		// 添加一个内容提供者检测
		ContentResolver resolver = context.getContentResolver();
		resolver.notifyChange(Uri.parse("content://org.mobile.lock"), null);
		return delete > 0;
	}

	/**
	 * 根据包名查询是否上锁
	 * 
	 * @param packageName
	 * @return
	 */
	public boolean query(String packageName) {
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql = "select * from " + AppLockConstant.DATA_NAME + " where "
				+ AppLockConstant.APPLOCK_PACKAGENAME + "=?";
		Cursor cursor = db.rawQuery(sql, new String[] { packageName });
		String name = null;
		if (cursor.moveToNext()) {
			name = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return name != null;
	}

	/**
	 * 查询所有已经上锁的
	 * 
	 * @return
	 */
	public List<String> queryAllLock() {
		String sql = "select " + AppLockConstant.APPLOCK_PACKAGENAME + " from "
				+ AppLockConstant.DATA_NAME;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		List<String> list = new ArrayList<String>();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				list.add(cursor.getString(0));
			}
		}
		cursor.close();
		db.close();
		return list;
	}
}

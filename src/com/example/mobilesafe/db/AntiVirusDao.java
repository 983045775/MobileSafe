package com.example.mobilesafe.db;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AntiVirusDao {
	/**
	 * 根据md5进行查询,是否存在
	 * 
	 * @param context
	 *            上下文
	 * @param md5
	 *            md5值
	 * @return 是否存在
	 */
	public static boolean find(Context context, String md5) {
		String path = new File(context.getFilesDir(), "antivirus.db")
				.getAbsolutePath();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);

		String sql = "select count(*) from datable where md5=?;";
		Cursor cursor = db.rawQuery(sql, new String[] { md5 });
		int index = 0;
		if (cursor != null) {
			if (cursor.moveToNext()) {
				index = Integer.valueOf(cursor.getString(0));
				cursor.close();
			}
		}
		db.close();
		return index > 0;
	}

	public static boolean add(Context context, String md5) {
		String path = new File(context.getFilesDir(), "antivirus.db")
				.getAbsolutePath();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		ContentValues values = new ContentValues();
		values.put("md5", md5);
		values.put("name", "Android.Adware.AirAD.a");
		values.put("desc", "恶意后台扣费,病毒木马程序");
		values.put("type", 6);
		long insert = db.insert("datable", null, values);
		db.close();
		return insert > 0;
	}
}

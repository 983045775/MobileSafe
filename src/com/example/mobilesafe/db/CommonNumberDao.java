package com.example.mobilesafe.db;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommonNumberDao {
	/**
	 * 返回查询classlist的数目
	 * 
	 * @param context
	 *            上下文
	 * @return 数目
	 */
	public static int SelectCount(Context context) {
		File file = new File(context.getFilesDir(), "commonnum.db");
		SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getPath(), null,
				SQLiteDatabase.OPEN_READONLY);
		String sql = "select count(*) from classlist";
		Cursor cursor = db.rawQuery(sql, null);
		int count = 0;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				count = cursor.getInt(0);
			}
		}
		cursor.close();
		db.close();
		return count;
	}

	/**
	 * 查询对应table表的数目
	 * 
	 * @param context
	 *            上下文
	 * @param groupPosition
	 *            第几个表-1的值
	 * @return 数目
	 */
	public static int SelectChildCount(Context context, int groupPosition) {
		File file = new File(context.getFilesDir(), "commonnum.db");
		SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getPath(), null,
				SQLiteDatabase.OPEN_READONLY);
		String sql = "select count(*) from table" + (groupPosition + 1);
		Cursor cursor = db.rawQuery(sql, null);
		int count = 0;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				count = cursor.getInt(0);
			}
		}
		cursor.close();
		db.close();
		return count;
	}

	/**
	 * 在classlist表中查询指定列的名字,
	 * 
	 * @param context
	 *            上下文
	 * @param groupPosition
	 *            指定列
	 * @return 名字
	 */
	public static String SelectItmeContent(Context context, int groupPosition) {
		File file = new File(context.getFilesDir(), "commonnum.db");
		SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getPath(), null,
				SQLiteDatabase.OPEN_READONLY);
		String sql = "select name from classlist where idx = ?";
		String name = null;
		Cursor cursor = db.rawQuery(sql, new String[] { (groupPosition + 1)
				+ "" });
		if (cursor != null) {
			while (cursor.moveToNext()) {
				name = cursor.getString(0);
			}
		}
		cursor.close();
		db.close();
		return name;
	}

	/**
	 * 查询子item的内容
	 * 
	 * @param context
	 *            上下文
	 * @param groupPosition
	 *            第几个item
	 * @param childPosition
	 *            第几个子节点
	 * @return 名字+号码 数组
	 */
	public static String[] SelectChildItemContent(Context context,
			int groupPosition, int childPosition) {
		File file = new File(context.getFilesDir(), "commonnum.db");
		SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getPath(), null,
				SQLiteDatabase.OPEN_READONLY);
		String sql = "select number,name from table" + (groupPosition + 1)
				+ " where _id = ?;";
		String name = "";
		String number = "";
		Cursor cursor = db.rawQuery(sql, new String[] { (childPosition + 1)
				+ "" });
		if (cursor != null) {
			while (cursor.moveToNext()) {
				number = cursor.getString(0);
				name = cursor.getString(1);
			}
		}
		cursor.close();
		db.close();
		return new String[] { name, number };
	}
}

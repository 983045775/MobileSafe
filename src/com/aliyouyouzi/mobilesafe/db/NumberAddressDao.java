package com.aliyouyouzi.mobilesafe.db;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class NumberAddressDao {

	public static String find(Context context, String number) {
		String path = new File(context.getFilesDir(), "address.db")
				.getAbsolutePath();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		boolean isPhone = number
				.matches("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		String address = null;
		if (isPhone) {
			// 手机号码的查询
			String sql = "select cardtype from info where mobileprefix=?";

			String prefix = number.substring(0, 7);
			Cursor cursor = db.rawQuery(sql, new String[] { prefix });
			if (cursor != null) {
				if (cursor.moveToNext()) {
					address = cursor.getString(0);
				}
				cursor.close();
			}
		} else {
			// 非手机
			int length = number.length();

			switch (length) {
			case 3:
				address = "紧急电话";
				break;
			case 4:
				address = "模拟器";
				break;
			case 5:
				address = "服务号码";
				break;
			case 7:
			case 8:
				address = "本地座机";
				break;
			case 10:
			case 11:
			case 12:
				// 查询
				String prefix = number.substring(0, 3);

				String sql = "select city from info where area=?";
				Cursor cursor = db.rawQuery(sql, new String[] { prefix });
				if (cursor != null) {
					if (cursor.moveToNext()) {
						address = cursor.getString(0);
					}
					cursor.close();
				}

				if (TextUtils.isEmpty(address)) {
					// 没有

					prefix = number.substring(0, 4);
					cursor = db.rawQuery(sql, new String[] { prefix });
					if (cursor != null) {
						if (cursor.moveToNext()) {
							address = cursor.getString(0);
						}
						cursor.close();
					}
				}

				if (TextUtils.isEmpty(address)) {
					address = "未知";
				}

				break;
			default:
				address = "未知";
				break;
			}
		}
		db.close();
		return address;
	}
}

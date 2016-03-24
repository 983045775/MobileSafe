package com.example.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppLockOpenHelper extends SQLiteOpenHelper {

	public AppLockOpenHelper(Context context) {
		super(context, AppLockConstant.DATA_NAME, null,
				AppLockConstant.APPLOCK_VERSION);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(AppLockConstant.SQL.CREATE_SQL);
	}

}

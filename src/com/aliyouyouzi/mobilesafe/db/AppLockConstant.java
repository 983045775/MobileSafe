package com.aliyouyouzi.mobilesafe.db;

public interface AppLockConstant {

	int APPLOCK_VERSION = 1;
	String DATA_NAME = "applock";
	String APPLOCK_PACKAGENAME = "packagename";
	String APPLOCK_ID = "_id";

	interface SQL {
		String CREATE_SQL = "create table " + DATA_NAME + "(" + APPLOCK_ID
				+ " integer primary key autoincrement," + APPLOCK_PACKAGENAME
				+ " text unique);";
	}
}

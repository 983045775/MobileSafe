package com.example.mobilesafe.test;

import com.example.mobilesafe.db.AppLockDao;

import android.test.AndroidTestCase;

public class AppLockTest extends AndroidTestCase {

	private static final String TAG = "AppLockTest";

	public void addTest() {
		AppLockDao dao = new AppLockDao(getContext());
		boolean add = dao.add("com.heima.hashaha");
		assertEquals(true, add);
	}

	public void deleteTest() {
		AppLockDao dao = new AppLockDao(getContext());
		boolean add = dao.delete("com.heima.hahaha");
		assertEquals(true, add);
	}

	public void queryTest() {
		AppLockDao dao = new AppLockDao(getContext());
		boolean add = dao.query("com.heima.hahaha");
		assertEquals(true, add);
	}
}

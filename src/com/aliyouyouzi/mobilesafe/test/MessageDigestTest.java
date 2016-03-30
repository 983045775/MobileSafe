package com.aliyouyouzi.mobilesafe.test;

import java.io.File;

import android.test.AndroidTestCase;
import android.util.Log;

import com.aliyouyouzi.mobilesafe.db.AntiVirusDao;
import com.aliyouyouzi.mobilesafe.utils.Base64Utils;
import com.aliyouyouzi.mobilesafe.utils.MD5Utils;

public class MessageDigestTest extends AndroidTestCase {

	private static final String TAG = "MD5Utils";

	public void MD5Test() {
		String encode = MD5Utils.encode("123456");
	}

	public void Base64Test() {
		String encode = Base64Utils.encode("123456");
		Log.d(TAG, encode);
		encode = Base64Utils.decode(Base64Utils.encode("123456"));
		Log.d(TAG, encode);
	}

	public void findFilemd5() {
		boolean find = AntiVirusDao.find(getContext(), "f0ac206417721f24a4708aaca2ea5063");
		assertEquals(true, find);
	}

	public void addmd5() {
		String md5 = "f0ac206417721f24a4708aaca2ea5063";
		boolean add = AntiVirusDao.add(getContext(),md5);
		assertEquals(true, add);
	}
}

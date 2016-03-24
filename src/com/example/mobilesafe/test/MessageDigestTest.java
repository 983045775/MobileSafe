package com.example.mobilesafe.test;

import com.example.mobilesafe.utils.Base64Utils;
import com.example.mobilesafe.utils.MD5Utils;

import android.test.AndroidTestCase;
import android.util.Log;

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
}

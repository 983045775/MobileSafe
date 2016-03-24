package com.example.mobilesafe.utils;

import android.util.Base64;

public class Base64Utils {

	public static String encode(String text) {

		byte[] bytes = Base64.encode(text.getBytes(), Base64.DEFAULT);

		return new String(bytes);
	}

	public static String decode(String text) {
		byte[] bytes = Base64.decode(text.getBytes(), Base64.DEFAULT);
		return new String(bytes);
	}

}

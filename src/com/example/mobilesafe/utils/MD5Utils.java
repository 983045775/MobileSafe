package com.example.mobilesafe.utils;

import java.security.MessageDigest;

import android.util.Log;

public class MD5Utils {

	private static final String TAG = "MD5Utils";

	public static String encode(String text) {

		try {
			MessageDigest digester = MessageDigest.getInstance("MD5");
			byte[] bytes = digester.digest(text.getBytes());
			StringBuilder sb = new StringBuilder();
			for (int x = 0; x < bytes.length; x++) {
				Log.d(TAG, "" + bytes[x]);
				int a = bytes[x] & 0xff;
				Log.d(TAG, "" + a);
				String hex = Integer.toHexString(a);

				if (hex.length() == 1) {
					hex = 0 + hex;
				}
				sb.append(hex);
			}
			Log.d(TAG, new String(bytes));
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

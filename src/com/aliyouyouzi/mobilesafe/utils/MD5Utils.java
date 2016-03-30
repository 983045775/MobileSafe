package com.aliyouyouzi.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

import android.util.Log;

public class MD5Utils {

	public static String encode(File file) {
		FileInputStream in = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");

			in = new FileInputStream(file);
			int len = -1;
			byte[] buffer = new byte[8000];

			while ((len = in.read(buffer)) != -1) {
				digest.update(buffer, 0, len);
			}

			byte[] bs = digest.digest();
			StringBuilder sb = new StringBuilder();
			for (int x = 0; x < bs.length; x++) {
				int a = bs[x] & 0xff;
				String hex = Integer.toHexString(a);

				if (hex.length() == 1) {
					hex = 0 + hex;
				}
				sb.append(hex);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
			}
		}
		return null;
	}

	public static String encode(String text) {

		try {
			MessageDigest digester = MessageDigest.getInstance("MD5");
			byte[] bytes = digester.digest(text.getBytes());
			StringBuilder sb = new StringBuilder();
			for (int x = 0; x < bytes.length; x++) {
				int a = bytes[x] & 0xff;
				String hex = Integer.toHexString(a);

				if (hex.length() == 1) {
					hex = 0 + hex;
				}
				sb.append(hex);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

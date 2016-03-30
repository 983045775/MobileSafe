package com.aliyouyouzi.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesUtils {
	private static SharedPreferences sp;

	@SuppressWarnings("static-access")
	private static SharedPreferences getNeedPreferences(Context context) {
		if (sp == null) {
			sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		}
		return sp;
	}

	/**
	 * 进行配置信息的存储,更新或者不更新标记的存储
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putBoolean(Context context, String key, boolean value) {
		SharedPreferences sp = getNeedPreferences(context);
		Editor edit = sp.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}

	/**
	 * 根据指定的key进行读取配置信息
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 * @return 如果不存在,返回默认值false
	 */
	public static boolean getBoolean(Context context, String key) {
		SharedPreferences sp = getNeedPreferences(context);
		return sp.getBoolean(key, false);
	}

	/**
	 * 根据指定的key进行读取配置文件信息
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 *            指定键
	 * @param defaultValue
	 *            如果不存在的默认值
	 * @return 如果不存在,返回默认值
	 */
	public static boolean getBoolean(Context context, String key,
			boolean defaultValue) {
		SharedPreferences sp = getNeedPreferences(context);
		return sp.getBoolean(key, defaultValue);
	}
	
	/**
	 * 进行配置信息的存储,更新或者不更新标记的存储
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putString(Context context, String key, String value) {
		SharedPreferences sp = getNeedPreferences(context);
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}
	
	/**
	 * 根据指定的key进行读取配置信息
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 * @return 如果不存在,返回默认值false
	 */
	public static String getString(Context context, String key) {
		SharedPreferences sp = getNeedPreferences(context);
		return sp.getString(key, null);
	}
	
	/**
	 * 根据指定的key进行读取配置文件信息
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 *            指定键
	 * @param defaultValue
	 *            如果不存在的默认值
	 * @return 如果不存在,返回默认值
	 */
	public static String getString(Context context, String key,
			String defaultValue) {
		SharedPreferences sp = getNeedPreferences(context);
		return sp.getString(key, defaultValue);
	}
	/**
	 * 进行配置信息的存储,更新或者不更新标记的存储
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putInt(Context context, String key, int value) {
		SharedPreferences sp = getNeedPreferences(context);
		Editor edit = sp.edit();
		edit.putInt(key, value);
		edit.commit();
	}
	
	/**
	 * 根据指定的key进行读取配置信息
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 * @return 如果不存在,返回默认值-1
	 */
	public static int getInt(Context context, String key) {
		SharedPreferences sp = getNeedPreferences(context);
		return sp.getInt(key, -1);
	}
	
	/**
	 * 根据指定的key进行读取配置文件信息
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 *            指定键
	 * @param defaultValue
	 *            如果不存在的默认值
	 * @return 如果不存在,返回默认值
	 */
	public static int getInt(Context context, String key,
			int defaultValue) {
		SharedPreferences sp = getNeedPreferences(context);
		return sp.getInt(key, defaultValue);
	}
}

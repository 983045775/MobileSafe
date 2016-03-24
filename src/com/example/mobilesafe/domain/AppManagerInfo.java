package com.example.mobilesafe.domain;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

public class AppManagerInfo implements Serializable {

	private Drawable icon;
	private String packageName;	
	private boolean isSdcard;
	private boolean isAndroidApp;
	private String appName;
	private String size;
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public boolean isSdcard() {
		return isSdcard;
	}
	public void setSdcard(boolean isSdcard) {
		this.isSdcard = isSdcard;
	}
	public boolean isAndroidApp() {
		return isAndroidApp;
	}
	public void setAndroidApp(boolean isAndroidApp) {
		this.isAndroidApp = isAndroidApp;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
}

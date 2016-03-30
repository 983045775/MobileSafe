package com.aliyouyouzi.mobilesafe.domain;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

public class CacheInfo implements Serializable {

	private String packageName;
	private String name;
	private Drawable icon;
	private long cacheSize;

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public long getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(long cacheSize) {
		this.cacheSize = cacheSize;
	}

}

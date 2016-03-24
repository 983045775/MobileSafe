package com.example.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class ProgressManagerInfo {

	private Drawable icon;
	private String AppName;
	private String PackagerName;
	private long UserMemory;
	private boolean isSystem;
	private boolean isCheck;

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getAppName() {
		return AppName;
	}

	public void setAppName(String appName) {
		AppName = appName;
	}

	public String getPackagerName() {
		return PackagerName;
	}

	public void setPackagerName(String packagerName) {
		PackagerName = packagerName;
	}

	public long getUserMemory() {
		return UserMemory;
	}

	public void setUserMemory(long userMemory) {
		UserMemory = userMemory;
	}

	public boolean isSystem() {
		return isSystem;
	}

	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
}

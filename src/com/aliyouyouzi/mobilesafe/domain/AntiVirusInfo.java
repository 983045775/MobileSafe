package com.aliyouyouzi.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class AntiVirusInfo {

	private String name;
	private String packageName;
	private boolean isAntiVirus;
	private Drawable icon;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public boolean isAntiVirus() {
		return isAntiVirus;
	}
	public void setAntiVirus(boolean isAntiVirus) {
		this.isAntiVirus = isAntiVirus;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	
	
}

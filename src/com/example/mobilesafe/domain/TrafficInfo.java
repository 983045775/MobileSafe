package com.example.mobilesafe.domain;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

public class TrafficInfo implements Serializable {

	private long rcv;
	private long send;
	private Drawable icon;
	private String name;
	private String packageName;

	public long getRcv() {
		return rcv;
	}

	public void setRcv(long rcv) {
		this.rcv = rcv;
	}

	public long getSend() {
		return send;
	}

	public void setSend(long send) {
		this.send = send;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

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
}

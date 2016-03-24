package com.example.mobilesafe.domain;

import java.io.Serializable;

public class BlackInfo implements Serializable {

	private String number;
	private String type;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}

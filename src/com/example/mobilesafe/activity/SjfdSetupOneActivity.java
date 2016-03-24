package com.example.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.mobilesafe.R;

public class SjfdSetupOneActivity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sjfd_setup_one);
	}

	protected boolean setupNext() {
		Intent intent = new Intent(this, SjfdSetupTwoActivity.class);
		startActivity(intent);	
		return false;
	}

	protected boolean setuppre() {
		return true;
	}
}

package com.example.mobilesafe.view;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.mobilesafe.R;
import com.example.mobilesafe.utils.Constants;
import com.example.mobilesafe.utils.PreferencesUtils;

public class AddresssDialog extends Dialog implements OnItemClickListener {

	private static final String TAG = "AddresssDialog";
	private ListView mLvAddress;

	public AddresssDialog(Context context) {
		super(context, R.style.AddressStyle);
		setContentView(R.layout.dialog_setting_addresss);

		mLvAddress = (ListView) findViewById(R.id.address_dialog_lv_item);
		mLvAddress.setOnItemClickListener(this);
		LayoutParams params = getWindow().getAttributes();
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(params);
	}

	public void setAdapter(ListAdapter adapter) {
		mLvAddress.setAdapter(adapter);
	}

	private int[] icons = new int[] { R.drawable.toast_address_shape,
			R.drawable.toast_address_orange, R.drawable.toast_address_blue,
			R.drawable.toast_address_gray, R.drawable.toast_address_green };

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		dismiss();
		PreferencesUtils.putInt(getContext(), Constants.ADDRESS_STYLE,
				icons[position]);
		Log.d(TAG, position + "");
	}
}
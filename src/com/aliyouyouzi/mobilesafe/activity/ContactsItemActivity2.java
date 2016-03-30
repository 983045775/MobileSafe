package com.aliyouyouzi.mobilesafe.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aliyouyouzi.mobilesafe.domain.ContactsItem;
import com.aliyouyouzi.mobilesafe.utils.Constants;
import com.aliyouyouzi.mobilesafe.utils.ContactsUtils;
import com.aliyouyouzi.mobilesafe.R;

/**
 * 这次用curuorAdapter来进行操作
 * 
 * @author liu
 * 
 */
public class ContactsItemActivity2 extends Activity implements
		OnItemClickListener {

	private ListView contacts_listview;
	private ProgressBar mProbar;
	private Cursor cursor;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_listview);
		contacts_listview = (ListView) findViewById(R.id.contacts_lv);
		mProbar = (ProgressBar) findViewById(R.id.contacts_probar);
		new Thread() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				cursor = ContactsUtils
						.getAllContactsCursor(ContactsItemActivity2.this);
				// 进行内容提供者,查询电话号,联系人id,联系人姓名
				runOnUiThread(new Runnable() {
					public void run() {

						// 操作ui了
						mProbar.setVisibility(ProgressBar.GONE);
						contacts_listview.setAdapter(new ContactsAdapter(
								ContactsItemActivity2.this, cursor));
					}
				});
			};
		}.start();
		// 给每个item都添加点击事件
		contacts_listview.setOnItemClickListener(this);
	}

	private class ContactsAdapter extends CursorAdapter {

		public ContactsAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return View.inflate(ContactsItemActivity2.this,
					R.layout.item_contacts_listview, null);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ImageView iv_person = (ImageView) view
					.findViewById(R.id.item_contacts_iv_person);
			TextView tv_name = (TextView) view
					.findViewById(R.id.item_contacts_tv_name);
			TextView tv_number = (TextView) view
					.findViewById(R.id.item_contacts_tv_number);
			ContactsItem contactsItem = ContactsUtils.getCursorToItem(cursor);
			tv_name.setText(contactsItem.getName());
			tv_number.setText(contactsItem.getNumber());
			// 查找图片
			Bitmap bitmap = ContactsUtils.getCantactsPhoto(
					ContactsItemActivity2.this, contactsItem.getNameId() + "");
			if (bitmap != null) {
				iv_person.setImageBitmap(bitmap);
			} else {
				iv_person.setImageResource(R.drawable.ic_contact);
			}
		}
	}

	/**
	 * 点击每个item触发事件的方法
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// 关闭当前页面
		Intent data = new Intent();
		cursor.moveToPosition(position);
		data.putExtra(Constants.NUMBER, ContactsUtils.getCursorToItem(cursor)
				.getNumber());
		setResult(Activity.RESULT_OK, data);
		ContactsItemActivity2.this.finish();
	}
}

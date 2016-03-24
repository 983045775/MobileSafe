package com.example.mobilesafe.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mobilesafe.R;
import com.example.mobilesafe.domain.ContactsItem;
import com.example.mobilesafe.utils.Constants;
import com.example.mobilesafe.utils.ContactsUtils;
/**
 * 选择安全号码页面
 * @author liu
 *
 */
public class ContactsItemActivity extends Activity implements
		OnItemClickListener {

	private ListView contacts_listview;
	private ProgressBar mProbar;
	public List<ContactsItem> list;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_listview);
		contacts_listview = (ListView) findViewById(R.id.contacts_lv);
		mProbar = (ProgressBar) findViewById(R.id.contacts_probar);
		new Thread() {
			public void run() {
				// 进行内容提供者,查询电话号,联系人id,联系人姓名
				list = ContactsUtils.getAllContacts(ContactsItemActivity.this);
				runOnUiThread(new Runnable() {
					public void run() {
						// 操作ui了
						mProbar.setVisibility(ProgressBar.GONE);
						contacts_listview.setAdapter(new ContactsAdapter());
					}
				});
			};
		}.start();
		// 给每个item都添加点击事件
		contacts_listview.setOnItemClickListener(this);
	}

	private class ContactsAdapter extends BaseAdapter {

		public int getCount() {
			return list.size();
		}

		public Object getItem(int position) {
			return list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(ContactsItemActivity.this,
						R.layout.item_contacts_listview, null);
				holder = new ViewHolder();
				// 获取控件先
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.item_contacts_tv_name);
				holder.tv_number = (TextView) convertView
						.findViewById(R.id.item_contacts_tv_number);
				holder.iv_item_photo = (ImageView) convertView
						.findViewById(R.id.item_contacts_iv_person);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// 添加照片
			Bitmap bitmap = ContactsUtils.getCantactsPhoto(
					ContactsItemActivity.this, list.get(position).getNameId()
							+ "");
			if (bitmap != null) {
				holder.iv_item_photo.setImageBitmap(bitmap);
			} else {
				holder.iv_item_photo.setImageResource(R.drawable.ic_contact);
			}
			// 添加姓名
			holder.tv_name.setText(list.get(position).getName());
			// 添加号码
			holder.tv_number.setText(list.get(position).getNumber());
			return convertView;
		}

	}

	/**
	 * 定义一个静态内部类
	 */
	private static class ViewHolder {
		TextView tv_name;
		TextView tv_number;
		ImageView iv_item_photo;
	}

	/**
	 * 点击每个item触发事件的方法
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// 关闭当前页面
		Intent data = new Intent();
		data.putExtra(Constants.NUMBER, list.get(position).getNumber());
		setResult(Activity.RESULT_OK, data);
		ContactsItemActivity.this.finish();
	}
}

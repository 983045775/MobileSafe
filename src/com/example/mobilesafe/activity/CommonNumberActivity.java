package com.example.mobilesafe.activity;

import com.example.mobilesafe.R;
import com.example.mobilesafe.db.CommonNumberDao;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

/**
 * 常用号码页面
 * 
 * @author liu
 * 
 */
public class CommonNumberActivity extends Activity implements
		OnGroupClickListener, OnChildClickListener {

	private ExpandableListView mElnumber;
	private int clickGroup = -1;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number);
		mElnumber = (ExpandableListView) findViewById(R.id.common_El_number);
		// 添加适配器
		mElnumber.setAdapter(new CommonNumberAdapter());
		// 添加Group的点击事件
		mElnumber.setOnGroupClickListener(this);
		// 添加每个child的点击事件
		mElnumber.setOnChildClickListener(this);
	}

	private class CommonNumberAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			return CommonNumberDao.SelectCount(CommonNumberActivity.this);
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return CommonNumberDao.SelectChildCount(getApplicationContext(),
					groupPosition);
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = new TextView(CommonNumberActivity.this);
				((TextView) convertView).setPadding(10, 10, 10, 10);
				((TextView) convertView).setTextSize(25);
				((TextView) convertView).setBackgroundColor(Color
						.parseColor("#06000000"));
			}
			String name = CommonNumberDao.SelectItmeContent(
					getApplicationContext(), groupPosition);
			((TextView) convertView).setText(name);
			return convertView;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			String[] results = CommonNumberDao.SelectChildItemContent(
					getApplicationContext(), groupPosition, childPosition);
			StringBuilder sb = new StringBuilder();
			for (int x = 0; x < results.length; x++) {
				sb.append(results[x]);
				if (x != results.length - 1) {
					sb.append("\n");
				}
			}
			TextView view = null;
			if (convertView == null) {
				convertView = new TextView(getApplicationContext());
				view = (TextView) convertView;
				view.setTextSize(20);
				view.setPadding(25, 5, 5, 5);
				view.setTextColor(Color.parseColor("#000000"));
			} else {
				view = (TextView) convertView;
			}
			view.setText(sb.toString());
			return convertView;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	// child的点击事件
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		return false;
	}

	// group的点击事件
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		if (clickGroup == -1) {
			// 说明没有点呢,打开他
			mElnumber.expandGroup(groupPosition);
			clickGroup = groupPosition;
		} else {
			// 点过了
			if (groupPosition == clickGroup) {
				// 说明点的还是原来的那个,关掉他
				mElnumber.collapseGroup(groupPosition);
				clickGroup = -1;
			} else {
				// 说明点了一个新的
				mElnumber.collapseGroup(clickGroup);
				mElnumber.expandGroup(groupPosition);
				clickGroup = groupPosition;
			}
		}
		return true;
	}
}

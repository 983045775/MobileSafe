package com.example.mobilesafe.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.mobilesafe.domain.ContactsItem;

public class ContactsUtils {

	private static final String TAG = "ContactsUtils";

	/**
	 * 根据所给的上下文进行查询联系人的姓名,id,手机号
	 * 
	 * @param context
	 *            上下文
	 * @return List集合封装了每个联系人信息的bean
	 */
	public static List<ContactsItem> getAllContacts(Context context) {
		List<ContactsItem> list = new ArrayList<ContactsItem>();
		ContentResolver resolver = context.getContentResolver();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		Log.d(TAG, uri.toString());
		// 写上要查询的字段
		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };
		// 获取查询的游标
		Cursor cursor = resolver.query(uri, projection, null, null, null);
		while (cursor.moveToNext()) {
			// 向下移动
			ContactsItem contactsItem = new ContactsItem();
			contactsItem.setNumber(cursor.getString(0));
			contactsItem.setNameId(cursor.getLong(1));
			contactsItem.setName(cursor.getString(2));
			list.add(contactsItem);
		}
		return list;
	}

	/**
	 * 根据上下文,联系人的id号找出对应的bitmap
	 * 
	 * @param context
	 *            上下文
	 * @param contact
	 *            联系人id号
	 * @return 返回对应的bitmap图片
	 */
	public static Bitmap getCantactsPhoto(Context context, String contact) {
		Bitmap bitmap;
		InputStream stream = null;
		try {
			// 获取解析器
			ContentResolver resolver = context.getContentResolver();
			// 获取联系人的uri
			Uri contactUri = ContactsContract.Contacts.CONTENT_URI;
			Log.d(TAG, contactUri.toString());
			// 设置每个id
			contactUri = Uri.withAppendedPath(contactUri, contact);

			// 获取图片的流
			stream = ContactsContract.Contacts.openContactPhotoInputStream(
					resolver, contactUri);
			bitmap = BitmapFactory.decodeStream(stream);
			return bitmap;
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			stream = null;
		}
	}

	/**
	 * 根据上下文,查询联系人id,姓名,电话号的游标
	 * 
	 * @param context
	 *            上下文
	 * @return 游标
	 */
	public static Cursor getAllContactsCursor(Context context) {
		ContentResolver resolver = context.getContentResolver();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		Log.d(TAG, uri.toString());
		// 写上要查询的字段
		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone._ID };
		// 获取查询的游标
		return resolver.query(uri, projection, null, null, null);
	}

	/**
	 * 根据每条游标返回对应的item信息
	 * 
	 * @param cursor
	 *            游标
	 * @return 每条信息
	 */
	public static ContactsItem getCursorToItem(Cursor cursor) {
		ContactsItem contactsItem = new ContactsItem();
		contactsItem.setName(cursor.getString(2));
		contactsItem.setNameId(cursor.getLong(1));
		contactsItem.setNumber(cursor.getString(0));
		return contactsItem;

	}
}

package com.example.mobilesafe.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.mobilesafe.utils.Constants;
import com.example.mobilesafe.utils.PreferencesUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

public class GPSService extends Service {

	private static final String TAG = "GPSService";

	private double longitude;
	private double latitude;

	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressWarnings("static-access")
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "开启GPS服务报警服务");
		// 获取系统服务
		LocationManager GPSService = (LocationManager) getSystemService(this.LOCATION_SERVICE);
		long minTime = 5 * 1000;// 位置更新以毫秒为单位的最小时间间隔
		float minDistance = 10;// 位置更新，以米为单位的最小距离
		GPSService.requestLocationUpdates(GPSService.GPS_PROVIDER, minTime,
				minDistance, listener);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "经纬度查询结束销毁");
	}

	private LocationListener listener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// 状态更改
		}

		@Override
		public void onProviderEnabled(String provider) {
			// 能够提供
		}

		@Override
		public void onProviderDisabled(String provider) {
			// 提供失败

		}

		@Override
		public void onLocationChanged(Location location) {
			longitude = location.getLongitude();
			latitude = location.getLatitude();
			Log.d(TAG, "经度" + longitude);
			Log.d(TAG, "纬度" + latitude);
			// 进行网络api的查询
			String url = "http://lbs.juhe.cn/api/getaddressbylngb";
			HttpUtils httpUtils = new HttpUtils();
			// 网络访问超时
			httpUtils.configSoTimeout(5 * 1000);
			httpUtils.configTimeout(5 * 1000);
			RequestParams params = new RequestParams();
			params.addQueryStringParameter("lngy", longitude + "");
			params.addQueryStringParameter("lngx", latitude + "");
			params.addQueryStringParameter("dtype", "json");
			// 第一个是请求方式,第二个是url地址,第三个是参数,第四个
			httpUtils.send(HttpMethod.GET, url, params, requestCall);
		}
	};
	private RequestCallBack<String> requestCall = new RequestCallBack<String>() {

		public void onSuccess(ResponseInfo<String> responseInfo) {
			// 成功了调用
			String json = responseInfo.result;
			Log.d(TAG, "json : = " + json);
			try {
				JSONObject jsonObject = new JSONObject(json);
				JSONObject rowJson = jsonObject.getJSONObject("row");
				JSONObject resultJson = rowJson.getJSONObject("result");
				// 解析到了定位的地址
				String locationName = resultJson.getString("formatted_address");
				Log.d(TAG, "locationName : = " + locationName);
				// 进行短信发送
				sendSms("success");
				try {
					sendSms(locationName);
				} catch (Exception e) {

				}
				// 应该停止服务
				stopSelf();
			} catch (JSONException e) {
				e.printStackTrace();
				Log.d(TAG, "JSON解析失败");
				sendSms("jingdu: " + longitude + "  weidu : " + latitude);
				// 应该停止服务
				stopSelf();
			}
		}

		public void onFailure(HttpException e, String string) {
			// 访问失败了调用
			e.printStackTrace();
			Log.d(TAG, "网络访问不成功");
			sendSms("jingdu: " + longitude + "  weidu: " + latitude);
			// 应该停止服务
			stopSelf();
		}
	};

	/**
	 * 给安全号码发送短信
	 * 
	 * @param message
	 *            发送的内容
	 */
	public void sendSms(String message) {
		String number = PreferencesUtils.getString(this, Constants.SJFD_NUMBER);
		SmsManager manager = SmsManager.getDefault();
		manager.sendTextMessage(number, null, message, null, null);
	}
}

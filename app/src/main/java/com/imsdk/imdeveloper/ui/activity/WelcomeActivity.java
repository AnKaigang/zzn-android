package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Window;

import com.baidu.mapapi.SDKInitializer;
import com.google.gson.Gson;
import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.app.IMConfiguration;
import com.imsdk.imdeveloper.db.userDao;
import com.imsdk.imdeveloper.http.httpLoginNoActivity;
import com.imsdk.imdeveloper.ui.a1common.UICommon;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;

import imsdk.data.IMMyself;

public class WelcomeActivity extends Activity {
	private long mSplashDelay = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 使得音量键控制媒体声音
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);
		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		registerReceiver(mReceiver, iFilter);

		userDao uD=new userDao();
		uD.createUser();
		JSONObject jsonObject=uD.query();
		uD.destroy();
		String user="";
		String passwd="";

		try {
			if(jsonObject!=null) {
				user = jsonObject.getString("user");
				passwd = jsonObject.getString("passwd");
				AsyncHttpClient client = new AsyncHttpClient();
				RequestParams requestParams=new RequestParams();
				requestParams.add("username",user);
				requestParams.add("password", passwd);

				client.post(IMConfiguration.login, requestParams, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int i, Header[] headers,byte[] bytes) {
						try {
							JSONObject jsonObject=new JSONObject(new String(bytes));
							//IMMyself.setCustomUserID(jsonObject.getString("username"));
							//IMMyself.setPassword(jsonObject.getString("password"));
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							LoginActivity.loginNo(WelcomeActivity.this, jsonObject.getString("username"), jsonObject.getString("password"), 0,0);

							finish();
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if(bytes!=null&&"null".equals(new String(bytes))) {
							UICommon.showTips(WelcomeActivity.this, R.drawable.tips_error, "该用户不存在");
						}else{
							UICommon.showTips(WelcomeActivity.this, R.drawable.tips_error, "网络异常");
							Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
							startActivity(intent);
							finish();
						}
					}
				});
			}
			else{
				Intent loginIntent = new Intent().setClass(WelcomeActivity.this,
						LoginActivity.class);

				startActivity(loginIntent);

				finish();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

//		TimerTask task = new TimerTask() {
//			@Override
//			public void run() {
//				Intent mainIntent = new Intent().setClass(WelcomeActivity.this,
//						LoginActivity.class);
//
//				startActivity(mainIntent);
//
//				finish();
//
//				overridePendingTransition(android.R.anim.fade_in,
//						android.R.anim.fade_out);
//			}
//		};
//
//		Timer timer = new Timer();
//
//		timer.schedule(task, mSplashDelay);
//
	}

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			//Log.d(LTAG, "action: " + s);
			//text.setTextColor(Color.RED);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				//text.setText("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
			} else if (s
					.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
				//text.setText("key 验证成功! 功能可以正常使用");
				//text.setTextColor(Color.YELLOW);
			}
			else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
	//			text.setText("网络出错");
			}
		}
	}

	private SDKReceiver mReceiver;





	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 取消监听 SDK 广播
		unregisterReceiver(mReceiver);
	}



}

package com.mtu.foundation;

import org.apache.http.HttpStatus;

import com.mtu.foundation.db.DBHelper;
import com.mtu.foundation.net.ThreadPoolUtils;
import com.mtu.foundation.net.httpjersey.Callback;
import com.mtu.foundation.net.httpjersey.NetworkHandler;
import com.mtu.foundation.net.httpjersey.TransResp;
import com.mtu.foundation.util.CommonUtil;
import com.mtu.foundation.util.Constants;
import com.mtu.foundation.util.FileOperRunnable;
import com.mtu.foundation.util.FileUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class StartActivity extends BaseActivity {
	private NetworkHandler networkHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		networkHandler = NetworkHandler.getInstance();
		if (CommonUtil.isNetWorkConnected(this)) {
			getDonateData();
			getNewsData();
			getThanksData();
		}
		DBHelper db = DBHelper.getInstance(this);
		setContentView(R.layout.activity_start);
		mMainHandler.sendEmptyMessageDelayed(0, 1500);
	}

	private Handler mMainHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switchTo(MainActivity.class);
			overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
		}
	};

	@Override
	public void onClick(View arg0) {

	}

	private void getThanksData() {
		networkHandler.get(Constants.URI_THANKS, null, 15,
				new Callback<TransResp>() {
					@Override
					public void callback(TransResp tr) {
						if (tr.getRetcode() == HttpStatus.SC_OK) {
							ThreadPoolUtils.execute(new FileOperRunnable(
									FileUtil.getCacheFile(Constants.CACHE_THANKS),
									false, true, tr.getRetjson(), null));
						}
					}
				});
	}

	private void getNewsData() {
		networkHandler.get(Constants.URI_NEWS, null, 15,
				new Callback<TransResp>() {
					@Override
					public void callback(TransResp tr) {
						if (tr.getRetcode() == HttpStatus.SC_OK) {
							ThreadPoolUtils.execute(new FileOperRunnable(
									FileUtil.getCacheFile(Constants.CACHE_NEWS),
									false, true, tr.getRetjson(), null));
						}
					}
				});
	}

	private void getDonateData() {
		networkHandler.get(Constants.URI_DONATE, null, 15,
				new Callback<TransResp>() {
					@Override
					public void callback(TransResp tr) {
						if (tr.getRetcode() == HttpStatus.SC_OK) {
							ThreadPoolUtils.execute(new FileOperRunnable(
									FileUtil.getCacheFile(Constants.CACHE_DONATE),
									false, true, tr.getRetjson(), null));
						}
					}
				});
	}

}

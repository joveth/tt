package com.mtu.foundation;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.http.HttpStatus;

import com.mtu.foundation.bean.HistoryItemBean;
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
		setContentView(R.layout.activity_start);
		networkHandler = NetworkHandler.getInstance();
		if (CommonUtil.isNetWorkConnected(this)) {
			getDonateData();
			getNewsData();
			getThanksData();
		}
		DBHelper dbHelper = DBHelper.getInstance(this);
		if (dbHelper.getHistorysCnt() == 0) {
			HistoryItemBean bean = new HistoryItemBean();
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss ");
			try {
				String date = format.format(calendar.getTime());
				bean.setDate(date);
				bean.setDay(date.substring(8, 10));
				bean.setImgId(String.valueOf(R.drawable.blue_cricle));
				bean.setContent("第一次使用海事基金会安卓应用，开始了感恩母校，回馈母校，爱心捐赠之旅，捐赠不在于多少，不在于地位，而是一颗报答的心。");
				dbHelper.saveHistory(bean);
			} catch (Exception e) {
			}
		}
		mMainHandler.sendEmptyMessageDelayed(0, 1000);
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

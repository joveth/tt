package com.mtu.foundation;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class StartActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		mMainHandler.sendEmptyMessageDelayed(0, 3000);
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
}

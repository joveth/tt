package com.mtu.foundation;

import org.apache.http.HttpStatus;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.mtu.foundation.net.HTMLParser;
import com.mtu.foundation.net.ThreadPoolUtils;
import com.mtu.foundation.net.httpjersey.Callback;
import com.mtu.foundation.net.httpjersey.NetworkHandler;
import com.mtu.foundation.net.httpjersey.TransResp;
import com.mtu.foundation.util.CommonUtil;
import com.mtu.foundation.util.Constants;
import com.mtu.foundation.util.FileOperRunnable;
import com.mtu.foundation.util.FileUtil;
import com.mtu.foundation.view.CustomProgressDialog;

public class DonateDescActivity extends BaseActivity {
	private Handler cacheHandler;
	private TextView vDesTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_donatedesc);
		initView();
		initOther();
		ThreadPoolUtils.execute(new FileOperRunnable(FileUtil
				.getCacheFile(Constants.CACHE_DESC), true, false, null,
				cacheHandler));
	}

	private void initView() {
		leftBtn = findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.top_title);
		title.setText("捐赠说明");
		vDesTxt = (TextView) findViewById(R.id.donate_desc);

	}

	private void initOther() {
		cacheHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case Constants.READ_RESULT_OK:
					String cacheData = (String) msg.obj;
					if (!CommonUtil.isEmpty(cacheData)) {
						vDesTxt.setText(Html.fromHtml(cacheData));
						getData(false);
					} else {
						getData(true);
					}
					break;
				}
			}
		};
	}

	private HTMLParser parser;

	private void getData(final boolean flag) {
		if (!CommonUtil.isNetWorkConnected(this)) {
			if (flag) {
				showSimpleMessageDialog("网络无法连接！");
			}
			return;
		}
		if (flag) {
			if (progressDialog == null) {
				progressDialog = new CustomProgressDialog(this, "正在加载...",
						false);
			}
		}

		if (networkHandler == null) {
			networkHandler = NetworkHandler.getInstance();
		}
		networkHandler.get(Constants.URI_DESC, null, 15,
				new Callback<TransResp>() {

					@Override
					public void callback(TransResp t) {
						if (progressDialog != null) {
							progressDialog.dismiss();
						}
						if (t.getRetcode() == HttpStatus.SC_OK) {
							if (!CommonUtil.isEmpty(t.getRetjson())) {
								if (parser == null) {
									parser = new HTMLParser(t.getRetjson());
								} else {
									parser.setHTMLStr(t.getRetjson());
								}
								String content = parser.getHtmlContent();
								if (flag) {
									vDesTxt.setText(Html.fromHtml(content));
								}
								ThreadPoolUtils.execute(new FileOperRunnable(
										FileUtil.getCacheFile(Constants.CACHE_DESC),
										false, true, content, null));
							}
						}
					}
				});
	}

	@Override
	public void onClick(View arg0) {
		if (arg0 == leftBtn) {
			finish();
		}
	}

}

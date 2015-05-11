package com.mtu.foundation;

import org.apache.http.HttpStatus;

import android.os.Bundle;
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

public class AboutFoundationActivity extends BaseActivity {
	private View vAbout1, vAbout2, vAbout3, vAbout4, vAbout5, vAbout6, vAbout7;
	private HTMLParser parser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getData();
		setContentView(R.layout.activity_foundation);
		initView();
	}

	private void initView() {
		leftBtn = findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.top_title);
		title.setText("关于基金会");
		vAbout1 = findViewById(R.id.about_1);
		vAbout1.setOnClickListener(this);
		vAbout2 = findViewById(R.id.about_2);
		vAbout2.setOnClickListener(this);
		vAbout3 = findViewById(R.id.about_3);
		vAbout3.setOnClickListener(this);
		vAbout4 = findViewById(R.id.about_4);
		vAbout4.setOnClickListener(this);
		vAbout5 = findViewById(R.id.about_5);
		vAbout5.setOnClickListener(this);
		vAbout6 = findViewById(R.id.about_6);
		vAbout6.setOnClickListener(this);
		vAbout7 = findViewById(R.id.about_7);
		vAbout7.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		if (arg0 == leftBtn) {
			finish();
		}
		if (arg0 == vAbout1) {
			Bundle bundle = new Bundle();
			bundle.putString("htmlfile", Constants.CACHE_ABOUT);
			bundle.putString("title", "基金会简介");
			bundle.putString("url", Constants.URI_ABOUT);

			switchTo(AboutWebViewActivity.class, bundle);
			return;
		}
		if (arg0 == vAbout2) {
			Bundle bundle = new Bundle();
			bundle.putString("htmlfile", Constants.CACHE_CONSITUTION);
			bundle.putString("title", "基金会章程");
			bundle.putString("url", Constants.URI_CONSITUTION);

			switchTo(AboutWebViewActivity.class, bundle);
			return;
		}
		if (arg0 == vAbout3) {
			Bundle bundle = new Bundle();
			/*bundle.putString("htmlfile", Constants.CACHE_ABOUT_52);
			bundle.putString("title", "基金会登记决定书");
			bundle.putString("url", Constants.URI_ABOUT_52);*/
			bundle.putInt("imgResid", R.drawable.jijin);
			switchTo(ImageShowerActivity.class, bundle);
			return;
		}
		if (arg0 == vAbout4) {
			Bundle bundle = new Bundle();
			bundle.putString("htmlfile", Constants.CACHE_ABOUT_53);
			bundle.putString("title", "组织架构");
			bundle.putString("url", Constants.URI_ABOUT_53);

			switchTo(AboutWebViewActivity.class, bundle);
			return;
		}
		if (arg0 == vAbout5) {
			Bundle bundle = new Bundle();
			bundle.putString("htmlfile", Constants.CACHE_ABOUT_58);
			bundle.putString("title", "接受捐赠管理办法");
			bundle.putString("url", Constants.URI_ABOUT_58);

			switchTo(AboutWebViewActivity.class, bundle);
			return;
		}
		if (arg0 == vAbout6) {
			Bundle bundle = new Bundle();
			bundle.putString("htmlfile", Constants.CACHE_ABOUT_54);
			bundle.putString("title", "捐赠项目管理办法");
			bundle.putString("url", Constants.URI_ABOUT_54);

			switchTo(AboutWebViewActivity.class, bundle);
			return;
		}
		if (arg0 == vAbout7) {
			Bundle bundle = new Bundle();
			bundle.putString("htmlfile", Constants.CACHE_ABOUT_55);
			bundle.putString("title", "捐赠资金管理办法");
			bundle.putString("url", Constants.URI_ABOUT_55);

			switchTo(AboutWebViewActivity.class, bundle);
			return;
		}

	}

	private void getData() {
		if (networkHandler == null) {
			networkHandler = NetworkHandler.getInstance();
		}
		networkHandler.get(Constants.URI_ABOUT, null, 15,
				new Callback<TransResp>() {

					@Override
					public void callback(TransResp t) {
						if (t.getRetcode() == HttpStatus.SC_OK) {
							if (!CommonUtil.isEmpty(t.getRetjson())) {
								if (parser == null) {
									parser = new HTMLParser(t.getRetjson());
								} else {
									parser.setHTMLStr(t.getRetjson());
								}
								ThreadPoolUtils.execute(new FileOperRunnable(
										FileUtil.getCacheFile(Constants.CACHE_ABOUT),
										false, true, parser.getHtmlContent(),
										null));
							}
						}
					}
				});
		networkHandler.get(Constants.URI_CONSITUTION, null, 15,
				new Callback<TransResp>() {

					@Override
					public void callback(TransResp t) {
						if (t.getRetcode() == HttpStatus.SC_OK) {
							if (!CommonUtil.isEmpty(t.getRetjson())) {
								if (parser == null) {
									parser = new HTMLParser(t.getRetjson());
								} else {
									parser.setHTMLStr(t.getRetjson());
								}
								ThreadPoolUtils.execute(new FileOperRunnable(
										FileUtil.getCacheFile(Constants.CACHE_CONSITUTION),
										false, true, parser.getHtmlContent(),
										null));
							}
						}
					}
				});
		networkHandler.get(Constants.URI_ABOUT_52, null, 15,
				new Callback<TransResp>() {

					@Override
					public void callback(TransResp t) {
						if (t.getRetcode() == HttpStatus.SC_OK) {
							if (!CommonUtil.isEmpty(t.getRetjson())) {
								if (parser == null) {
									parser = new HTMLParser(t.getRetjson());
								} else {
									parser.setHTMLStr(t.getRetjson());
								}
								ThreadPoolUtils.execute(new FileOperRunnable(
										FileUtil.getCacheFile(Constants.CACHE_ABOUT_52),
										false, true, parser.getHtmlContent(),
										null));
							}
						}
					}
				});
		networkHandler.get(Constants.URI_ABOUT_53, null, 15,
				new Callback<TransResp>() {

					@Override
					public void callback(TransResp t) {
						if (t.getRetcode() == HttpStatus.SC_OK) {
							if (!CommonUtil.isEmpty(t.getRetjson())) {
								if (parser == null) {
									parser = new HTMLParser(t.getRetjson());
								} else {
									parser.setHTMLStr(t.getRetjson());
								}
								ThreadPoolUtils.execute(new FileOperRunnable(
										FileUtil.getCacheFile(Constants.CACHE_ABOUT_53),
										false, true, parser.getHtmlContent(),
										null));
							}
						}
					}
				});
		networkHandler.get(Constants.URI_ABOUT_54, null, 15,
				new Callback<TransResp>() {

					@Override
					public void callback(TransResp t) {
						if (t.getRetcode() == HttpStatus.SC_OK) {
							if (!CommonUtil.isEmpty(t.getRetjson())) {
								if (parser == null) {
									parser = new HTMLParser(t.getRetjson());
								} else {
									parser.setHTMLStr(t.getRetjson());
								}
								ThreadPoolUtils.execute(new FileOperRunnable(
										FileUtil.getCacheFile(Constants.CACHE_ABOUT_54),
										false, true, parser.getHtmlContent(),
										null));
							}
						}
					}
				});
		networkHandler.get(Constants.URI_ABOUT_55, null, 15,
				new Callback<TransResp>() {

					@Override
					public void callback(TransResp t) {
						if (t.getRetcode() == HttpStatus.SC_OK) {
							if (!CommonUtil.isEmpty(t.getRetjson())) {
								if (parser == null) {
									parser = new HTMLParser(t.getRetjson());
								} else {
									parser.setHTMLStr(t.getRetjson());
								}
								ThreadPoolUtils.execute(new FileOperRunnable(
										FileUtil.getCacheFile(Constants.CACHE_ABOUT_55),
										false, true, parser.getHtmlContent(),
										null));
							}
						}
					}
				});
		networkHandler.get(Constants.URI_ABOUT_58, null, 15,
				new Callback<TransResp>() {

					@Override
					public void callback(TransResp t) {
						if (t.getRetcode() == HttpStatus.SC_OK) {
							if (!CommonUtil.isEmpty(t.getRetjson())) {
								if (parser == null) {
									parser = new HTMLParser(t.getRetjson());
								} else {
									parser.setHTMLStr(t.getRetjson());
								}
								ThreadPoolUtils.execute(new FileOperRunnable(
										FileUtil.getCacheFile(Constants.CACHE_ABOUT_58),
										false, true, parser.getHtmlContent(),
										null));
							}
						}
					}
				});
	}
}

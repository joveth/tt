package com.mtu.foundation;

import org.apache.http.HttpStatus;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

public class AboutWebViewActivity extends BaseActivity {
	private WebView webView;
	private String contentfile, aboutTitle, url;
	private Handler cacheHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_foundation);
		inidData();
		initView();
		initOther();
		ThreadPoolUtils.execute(new FileOperRunnable(FileUtil
				.getCacheFile(contentfile), true, false, null, cacheHandler));
	}

	private void inidData() {
		Intent intent = getIntent();
		contentfile = intent.getStringExtra("htmlfile");
		aboutTitle = intent.getStringExtra("title");
		url = intent.getStringExtra("url");
		progressDialog = new CustomProgressDialog(this, "正在加载...", true);
		progressDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
				arg0.dismiss();
			}
		});
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
						htmlContent(cacheData);
					} else {
						getData();
					}
					break;
				}
			}
		};
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {

		leftBtn = findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.top_title);
		title.setText("关于基金会");
		if (!CommonUtil.isEmpty(aboutTitle)) {
			title.setText(aboutTitle);
		}
		webView = (WebView) findViewById(R.id.webview);
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int scale = dm.densityDpi;
		if (scale == 240) { //
			webView.getSettings().setDefaultZoom(ZoomDensity.FAR);
		} else if (scale == 160) {
			webView.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
		} else {
			webView.getSettings().setDefaultZoom(ZoomDensity.CLOSE);
		}
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				return super.onJsAlert(view, url, message, result);
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}
		});
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return super.shouldOverrideUrlLoading(view, url);// 停止在当前界面
			}

			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (progressDialog != null) {
					progressDialog.dismiss();
				}
			}

			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}
		});
		ThreadPoolUtils.execute(new FileOperRunnable(FileUtil
				.getCacheFile(contentfile), true, false, null, cacheHandler));
	}

	@Override
	public void onClick(View view) {
		if (view == leftBtn) {
			finish();
		}
	}

	private void htmlContent(String html) {
		try {
			String htmlHeader = "<style type=\"text/css\"> img{width:100%!important}; h1{font-size:20px}</style>";
			webView.loadDataWithBaseURL(null, htmlHeader + html, "text/html",
					"utf-8", null);
		} catch (Exception e) {
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		}
	}

	private HTMLParser parser;

	private void getData() {
		if(!CommonUtil.isNetWorkConnected(this)){
			showSimpleMessageDialog("网络无法连接！");
			return;
		}
		
		if (networkHandler == null) {
			networkHandler = NetworkHandler.getInstance();
		}
		networkHandler.get(url, null, 15, new Callback<TransResp>() {

			@Override
			public void callback(TransResp t) {
				if (t.getRetcode() == HttpStatus.SC_OK) {
					if (!CommonUtil.isEmpty(t.getRetjson())) {
						if (parser == null) {
							parser = new HTMLParser(t.getRetjson());
						} else {
							parser.setHTMLStr(t.getRetjson());
						}
						htmlContent(parser.getHtmlContent());
					}
				} else {
					if (progressDialog != null) {
						progressDialog.dismiss();
					}
				}
			}
		});
	}
}

package com.mtu.foundation;

import com.mtu.foundation.util.Constants;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class WebViewActivity extends BaseActivity {
	private WebView webView;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		inidData();
		initView();
	}

	private void inidData() {
		Intent intent = getIntent();
		url = intent.getStringExtra("newsurl");
	}

	private void initView() {
		leftBtn = (TextView) findViewById(R.id.left_btn);
		leftBtn.setText("返回");
		leftBtn.setOnClickListener(this);
		title = (TextView) findViewById(R.id.top_title);
		title.setText("新闻公告");
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
		webView.getSettings().setJavaScriptEnabled(true);// 允许webkit执行js代码；
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
				Log.d("url", url);
				return super.shouldOverrideUrlLoading(view, url);// 停止在当前界面
			}

			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				Log.d("url", url);
			}

			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.d("url", url);
				super.onPageStarted(view, url, favicon);
			}
		});
		webView.loadUrl(Constants.URI_DOMAIN + url);
	}

	@Override
	public void onClick(View view) {
		if (view == leftBtn) {
			finish();
		}
	}

}

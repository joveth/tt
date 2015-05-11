package com.mtu.foundation;

import org.apache.http.HttpStatus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.mtu.foundation.net.HttpGetThread;
import com.mtu.foundation.net.ThreadPoolUtils;
import com.mtu.foundation.net.httpjersey.Callback;
import com.mtu.foundation.net.httpjersey.NetworkHandler;
import com.mtu.foundation.net.httpjersey.TransResp;
import com.mtu.foundation.util.CommonUtil;
import com.mtu.foundation.util.Constants;
import com.mtu.foundation.view.CustomProgressDialog;

public class WebViewActivity extends BaseActivity {
	private WebView webView;
	private String url;
	private String newsTitle;
	private TextView newstitleTxt;
	private View newstitleLay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!CommonUtil.isNetWorkConnected(this)) {
			showMsgDialogWithCallback("网络无法连接，请检查网络！");
			return;
		}
		setContentView(R.layout.activity_webview);
		inidData();
		initView();
	}

	private void inidData() {
		Intent intent = getIntent();
		url = intent.getStringExtra("newsurl");
		progressDialog = new CustomProgressDialog(this, "正在加载...", true);
		progressDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
				arg0.dismiss();
			}
		});
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		leftBtn = findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.top_title);
		title.setText("新闻公告");
		newstitleTxt = (TextView) findViewById(R.id.newstitle_txt);
		webView = (WebView) findViewById(R.id.webview);
		newstitleLay = findViewById(R.id.newstitle_lay);
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
		webView.addJavascriptInterface(new JavascriptInterface(this),
				"imagelistner");
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
				view.getSettings().setJavaScriptEnabled(true);
				super.onPageFinished(view, url);
				newstitleTxt.setText(newsTitle);
				newstitleLay.setVisibility(View.VISIBLE);
				if (progressDialog != null) {
					progressDialog.dismiss();
				}
				addImageClickListner();
			}

			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				view.getSettings().setJavaScriptEnabled(true);
				super.onPageStarted(view, url, favicon);
			}
		});
		getData();
	}

	// 注入js函数监听
	private void addImageClickListner() {
		// 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
		webView.loadUrl("javascript:(function(){"
				+ "var objs = document.getElementsByTagName(\"img\"); "
				+ "for(var i=0;i<objs.length;i++)  " + "{"
				+ "    objs[i].onclick=function()  " + "    {  "
				+ "        window.imagelistner.openImage(this.src);  "
				+ "    }  " + "}" + "})()");
	}

	// js通信接口
	public class JavascriptInterface {

		private Context context;

		public JavascriptInterface(Context context) {
			this.context = context;
		}

		@android.webkit.JavascriptInterface
		public void openImage(String img) {
			if (!CommonUtil.isNetWorkConnected(context)) {
				return;
			}
			int ind = img.indexOf("?");
			if (ind > -1) {
				img = img.substring(0, ind);
			}
			ThreadPoolUtils.execute(new HttpGetThread(img));
			Intent intent = new Intent();
			intent.putExtra("image", img);
			intent.setClass(context, ImageShowerActivity.class);
			context.startActivity(intent);
		}
	}

	@Override
	public void onClick(View view) {
		if (view == leftBtn) {
			finish();
		}
	}

	private void htmlContent(String html) {
		try {
			Document doc = Jsoup.parse(html);
			Element rootEle = doc.getElementById("block-system-main");
			Elements conEle = rootEle.getElementsByClass("content");
			Element content = conEle.get(conEle.size() - 1);
			Element titleEle = doc.getElementById("page-title");
			newsTitle = titleEle.text();
			String htmlHeader = "<style type=\"text/css\"> img{width:100%!important;height:40%!important}; h1{font-size:100%}</style>";
			webView.loadDataWithBaseURL(null, htmlHeader + content.html(),
					"text/html", "utf-8", null);

		} catch (Exception e) {
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		}
	}

	private void getData() {
		NetworkHandler networkHandler = NetworkHandler.getInstance();
		networkHandler.get(Constants.URI_DOMAIN + url, null, 30,
				new Callback<TransResp>() {
					@Override
					public void callback(TransResp tr) {
						if (tr.getRetcode() == HttpStatus.SC_OK) {
							htmlContent(tr.getRetjson());
						} else {
							if (progressDialog != null) {
								progressDialog.dismiss();
							}
						}
					}
				});
	}

}

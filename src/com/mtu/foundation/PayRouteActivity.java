package com.mtu.foundation;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EncodingUtils;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
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

import com.mtu.foundation.net.httpjersey.NetworkHandler;
import com.mtu.foundation.util.Constants;
import com.mtu.foundation.view.CustomProgressDialog;

public class PayRouteActivity extends BaseActivity {
	private NetworkHandler networkHandler;
	private String item, amount, comment, username, gender, is_alumni, email,
			tel, cellphone, address, postcode, company, is_anonymous, paytype;
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paytoute);
		leftBtn = findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.top_title);
		title.setText("付款");
		progressDialog = new CustomProgressDialog(this, "正在加载...", true);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				arg0.dismiss();
			}
		});
		initData();
		initView();
	}

	private void initData() {
		networkHandler = NetworkHandler.getInstance();
		Intent intent = getIntent();
		if (intent == null) {
			finish();
			return;
		}
		item = intent.getStringExtra("item");
		amount = intent.getStringExtra("amount");
		comment = intent.getStringExtra("comment");
		username = intent.getStringExtra("username");
		gender = intent.getStringExtra("gender");
		is_alumni = intent.getStringExtra("is_alumni");
		email = intent.getStringExtra("email");
		tel = intent.getStringExtra("tel");
		cellphone = intent.getStringExtra("cellphone");
		address = intent.getStringExtra("address");
		postcode = intent.getStringExtra("postcode");
		company = intent.getStringExtra("company");
		is_anonymous = intent.getStringExtra("is_anonymous");

		
	}

	private void initView() {
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
				Log.d("url", url);
				return super.shouldOverrideUrlLoading(view, url);// 停止在当前界面
			}

			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				Log.d("url", url);
				if (progressDialog != null) {
					progressDialog.dismiss();
				}
			}

			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.d("url", url);
				super.onPageStarted(view, url, favicon);
			}
		});
		doPost();
	}

	@Override
	public void onClick(View arg0) {
		if (arg0 == leftBtn) {
			finish();
			return;
		}
	}

	private void doPost() {
		List<NameValuePair> paramspost = new ArrayList<NameValuePair>();
		paramspost.add(new BasicNameValuePair("item", item));
		paramspost.add(new BasicNameValuePair("amount", amount));
		paramspost.add(new BasicNameValuePair("username", username));
		paramspost.add(new BasicNameValuePair("gender", gender));
		paramspost.add(new BasicNameValuePair("is_alumni", is_alumni));
		paramspost.add(new BasicNameValuePair("email", email));
		paramspost.add(new BasicNameValuePair("tel", tel));
		paramspost.add(new BasicNameValuePair("cellphone", cellphone));
		paramspost.add(new BasicNameValuePair("is_anonymous", is_anonymous));
		paramspost.add(new BasicNameValuePair("paytype", "alipay"));
		paramspost.add(new BasicNameValuePair("form_id", "donate_form"));
		String postData = "item=" + item + "&amount=" + amount + "&username="
				+ username + "&gender=" + gender + "&is_alumni=" + is_alumni
				+ "&email=" + email + "&tel=" + tel + "&cellphone=" + cellphone
				+ "&is_anonymous=" + is_anonymous
				+ "&paytype=alipay&form_id=donate_form";

		webView.postUrl(Constants.URI_DONATE,
				EncodingUtils.getBytes(postData, "BASE64"));
		/*
		 * networkHandler.post(Constants.URI_DONATE, paramspost, 30, new
		 * Callback<TransResp>() {
		 * 
		 * @Override public void callback(TransResp tr) { if (tr.getRetcode() ==
		 * HttpStatus.SC_OK) { Log.d("ret", tr.getRetjson() + "");
		 * webView.loadDataWithBaseURL(null, tr.getRetjson(), "text/html",
		 * "utf-8", null);
		 * 
		 * } } });
		 */
	}
}

package com.mtu.foundation;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.mtu.foundation.net.httpjersey.Callback;
import com.mtu.foundation.net.httpjersey.NetworkHandler;
import com.mtu.foundation.net.httpjersey.TransResp;
import com.mtu.foundation.util.CommonUtil;
import com.mtu.foundation.util.Constants;
import com.mtu.foundation.util.alipay.AliPayUtil;
import com.mtu.foundation.util.alipay.AlipayCore;
import com.mtu.foundation.util.alipay.RSA;
import com.mtu.foundation.view.CustomProgressDialog;

/**
 * Created by jov on 2015/2/14.
 */
public class AliPayWapActivity extends BaseActivity {
	private WebView webView;
	private static final String SUBJECT = "捐赠";
	private String refNo = "201502280956";
	private String price = "0.01";
	private static final String SUCCESS_URL = "http://source.supwisdom.com:9090";
	private static final String NOTIFY_URL = "http://source.supwisdom.com:9090";
	private String userid = "2013100015";
	private CustomProgressDialog progressDialog;
	private int okFlag = Constants.RESULT_FAILED;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_foundation);
		initData();
		initView();
	}

	private void initData() {

	}

	private void initView() {
		leftBtn = findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.top_title);
		title.setText("支付宝网页版");

		webView = (WebView) this.findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);// 允许webkit执行js代码
		webView.getSettings().setDefaultTextEncodingName("UTF -8");
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
				if (!CommonUtil.isEmpty(url) && url.contains("asyn_payment_result")) {
					okFlag = Constants.RESULT_OK;
				}
			}

			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}
		});
		doCallRemote();
	}

	@Override
	public void onClick(View view) {
		if (view == leftBtn) {
			sendResult(okFlag);
			return;
		}
	}

	private AlertDialog.Builder anonymousDialog;

	private void showAnonymousDialog() {
		if (anonymousDialog == null) {
			anonymousDialog = new AlertDialog.Builder(this)
					.setTitle(null)
					.setMessage("是否已经完成付款？")
					.setNegativeButton("付款遇到问题",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.dismiss();
									sendResult(Constants.RESULT_FAILED);
								}
							})
					.setPositiveButton("已完成付款",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.dismiss();
									sendResult(Constants.RESULT_OK);
								}
							}).setCancelable(false);
		}
		anonymousDialog.show();
	}

	private void sendResult(int ret) {
		Intent intent = new Intent(this, PayRouteActivity.class);
		setResult(ret, intent);
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			sendResult(okFlag);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean cancelFlag = false;

	private void doCallRemote() {
		cancelFlag = false;
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.show(this, "正在加载...", true);
			progressDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialogInterface) {
							cancelFlag = true;
							Toast.makeText(AliPayWapActivity.this, "交易被取消!",
									Toast.LENGTH_SHORT).show();
							finish();
						}
					});
		}
		progressDialog.setMsg("正在加载...");
		progressDialog.show();
		if (networkHandler == null) {
			networkHandler = NetworkHandler.getInstance();
		}
		Map<String, String> sParaTemp = AliPayUtil.getAccessTokenParamsMap();
		String req = AliPayUtil.getAliPayWAPOrderBodyInfor("test", price,
				"1282889689801", SUCCESS_URL, NOTIFY_URL, userid);
		Log.d("req_data", req);
		sParaTemp.put("req_data", req);
		// 生成签名结果
		String mysign = AliPayUtil.buildRequestMysign(sParaTemp);
		// 签名结果与签名方式加入请求提交参数组中
		sParaTemp.put("sign", mysign);
		Log.d("sign", mysign);
		List<NameValuePair> paramspost = generatNameValuePair(sParaTemp);
		networkHandler.postForZfb(AliPayUtil.ALIPAY_WAP_URL, sParaTemp, 15,
				new Callback<TransResp>() {
					@Override
					public void callback(TransResp transResp) {
						if (cancelFlag) {
							return;
						}
						if (transResp.getRetcode() == HttpStatus.SC_OK) {
							if (CommonUtil.isEmpty(transResp.getRetjson())) {
								progressDialog.dismiss();
								Toast.makeText(AliPayWapActivity.this, "请求失败!",
										Toast.LENGTH_SHORT).show();
								finish();
								return;
							}
							String dec = "";
							/*
							 * try { // dec =
							 * URLDecoder.decode(transResp.getRetjson(),
							 * "utf-8"); } catch (UnsupportedEncodingException
							 * e) { e.printStackTrace();
							 * progressDialog.dismiss();
							 * Toast.makeText(AliPayWapActivity.this,
							 * "请求内容解析失败，请稍后再试!", Toast.LENGTH_SHORT).show();
							 * return; }
							 */
							String ret;
							try {
								// ret = RSA.getRequestToken(dec);
								// Log.d("ret", ret);
								if (!CommonUtil.isEmpty(transResp.getRetjson())) {
									// wapPay(transResp.getRetjson());
									System.out.println("return---url="
											+ transResp.getRetjson());
									webView.loadUrl(transResp.getRetjson());
								} else {
									progressDialog.dismiss();
									Toast.makeText(AliPayWapActivity.this,
											"请求内容解析失败，请稍后再试!",
											Toast.LENGTH_SHORT).show();
									return;
								}
							} catch (Exception e) {
								e.printStackTrace();
								progressDialog.dismiss();
								Toast.makeText(AliPayWapActivity.this,
										"请求内容解析失败，请稍后再试!", Toast.LENGTH_SHORT)
										.show();
								return;
							}
						} else {
							progressDialog.dismiss();
							Toast.makeText(AliPayWapActivity.this, "请求未响应!",
									Toast.LENGTH_SHORT).show();
							return;
						}
					}
				});
	}

	private void wapPay(String token) throws UnsupportedEncodingException {
		Map<String, String> sParaTemp = AliPayUtil.getBillParamsMap();
		String req_data = "<auth_and_execute_req><request_token>" + token
				+ "</request_token></auth_and_execute_req>";
		Log.d("return data", req_data);
		sParaTemp.put("req_data", req_data);
		String mysign = AliPayUtil.buildRequestMysign(sParaTemp);
		// sParaTemp.put("sign", mysign);
		List<NameValuePair> paramspost = generatNameValuePair(sParaTemp);
		String par = "req_data="
				+ req_data
				+ "&service=alipay.wap.auth.authAndExecute&sec_id=0001&partner="
				+ AliPayUtil.PARTNER + "&sign="
				+ URLEncoder.encode(mysign, "utf-8") + "&format=xml&v=2.0";
		Log.d("par", par);
		String url = AliPayUtil.ALIPAY_WAP_URL + "?"
				+ AlipayCore.createLinkString(sParaTemp) + "&sign="
				+ URLEncoder.encode(mysign, "utf-8");
		Log.d("url", url);
		// webView.loadUrl(url);
		networkHandler.get(url, null, 15, new Callback<TransResp>() {

			@Override
			public void callback(TransResp t) {
				Log.d("code", t.getRetcode() + "");
				Log.d("jsom", t.getRetjson() + "");
				Log.d("mes", t.getRetmsg() + "");
			}

		});
		webView.loadUrl(url);
	}

	private List<NameValuePair> generatNameValuePair(
			Map<String, String> properties) {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(
				properties.size());
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			Log.d("key,value", entry.getKey() + "," + entry.getValue());
			nameValuePair.add(new BasicNameValuePair(entry.getKey(), entry
					.getValue()));
		}
		return nameValuePair;
	}
}

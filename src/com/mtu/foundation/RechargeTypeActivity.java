package com.mtu.foundation;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mtu.foundation.adapter.RechargeTypeAdapter;
import com.mtu.foundation.bean.RechargeType;
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

/**
 * Created by jov on 2015/2/11.
 */
public class RechargeTypeActivity extends BaseActivity {
	private List<RechargeType> list;
	private RechargeTypeAdapter adapter;
	private ListView listView;
	private String checkedCard;
	private Handler cacheHandler;
	private HTMLParser parser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge_type);
		initData();
		initView();
		initOther();
		ThreadPoolUtils.execute(new FileOperRunnable(FileUtil
				.getCacheFile(Constants.CACHE_DONATE), true, false, null,
				cacheHandler));
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.show(this, "正在加载...", true);
			progressDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialogInterface) {
							cancelFlag = true;
							finish();
						}
					});
		}
	}

	private void initData() {
		Intent intent = getIntent();
		checkedCard = intent.getStringExtra("checkedcard");
		list = new ArrayList<RechargeType>();
	}

	private void initView() {
		leftBtn = findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.top_title);
		title.setText("选择银行");
		adapter = new RechargeTypeAdapter(this, list);
		listView = (ListView) findViewById(R.id.listview);
		listView.setAdapter(adapter);
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
						tranceData(cacheData);
					} else {
						doGetCards();
					}
					break;
				}
			}
		};
	}

	@Override
	public void onClick(View view) {
		Intent intent = new Intent(this, PayRouteActivity.class);
		if (view == leftBtn) {
			this.setResult(Constants.RESULT_NOCHANGE, intent);
			this.finish();
			return;
		}
	}

	private boolean cancelFlag;

	private void doGetCards() {
		if (!CommonUtil.isNetWorkConnected(this)) {
			Toast.makeText(this, "网络无法连接", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		cancelFlag = false;
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.show(this, "正在加载...", true);
			progressDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialogInterface) {
							cancelFlag = true;
							finish();
						}
					});
		}
		progressDialog.setMsg("正在加载...");
		progressDialog.show();
		if (networkHandler == null) {
			networkHandler = NetworkHandler.getInstance();
		}
		networkHandler.get(Constants.URI_DONATE, null, 15,
				new Callback<TransResp>() {

					@Override
					public void callback(TransResp transResp) {
						if (cancelFlag) {
							return;
						}
						if (transResp.getRetcode() == HttpStatus.SC_OK) {
							tranceData(transResp.getRetjson());
						} else {
							progressDialog.dismiss();
							Toast.makeText(
									RechargeTypeActivity.this,
									"请求失败，返回"
											+ String.valueOf(transResp
													.getRetcode()),
									Toast.LENGTH_SHORT).show();
							return;
						}
					}
				});
	}

	private void tranceData(String html) {
		try {
			if (parser == null) {
				parser = new HTMLParser(html);
			} else {
				parser.setHTMLStr(html);
			}
			List<RechargeType> temp = parser.getInitDonateBankBean();
			if (temp != null && temp.size() > 0) {
				if (temp != null && temp.size() > 0) {
					list.clear();
					list.addAll(temp);

				}
			}
			if (list.size() == 0) {
				Toast.makeText(RechargeTypeActivity.this, "查询失败了!",
						Toast.LENGTH_SHORT).show();
			} else {
				if (!CommonUtil.isEmpty(checkedCard)) {
					for (RechargeType t : list) {
						if (checkedCard.equals(t.getBankkey())) {
							t.setChecked(true);
							break;
						}
					}
				}
			}
			adapter.notifyDataSetChanged();
		} finally {
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		}
	}
}

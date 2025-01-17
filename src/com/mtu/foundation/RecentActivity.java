package com.mtu.foundation;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import com.mtu.foundation.adapter.ThanksItemAdapter;
import com.mtu.foundation.bean.ThankBean;
import com.mtu.foundation.net.HTMLParser;
import com.mtu.foundation.net.ThreadPoolUtils;
import com.mtu.foundation.net.httpjersey.Callback;
import com.mtu.foundation.net.httpjersey.NetworkHandler;
import com.mtu.foundation.net.httpjersey.TransResp;
import com.mtu.foundation.util.CommonUtil;
import com.mtu.foundation.util.Constants;
import com.mtu.foundation.util.FileOperRunnable;
import com.mtu.foundation.util.FileUtil;
import com.mtu.foundation.view.PullDownView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RecentActivity extends BaseActivity implements
		PullDownView.OnPullDownListener {
	private NetworkHandler networkHandler;
	private ListView listView;
	private List<ThankBean> list;
	private ThanksItemAdapter adapter;
	private PullDownView pullDownView;
	private Handler cacheHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recent);
		initView();
		initOther();
		networkHandler = NetworkHandler.getInstance();
		ThreadPoolUtils.execute(new FileOperRunnable(FileUtil
				.getCacheFile(Constants.CACHE_THANKS), true, false, null,
				cacheHandler));
	}

	private void initView() {
		leftBtn = findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.top_title);
		title.setText("最新捐赠");

		pullDownView = (PullDownView) findViewById(R.id.thanks_listview);
		listView = pullDownView.getListView();
		list = new ArrayList<ThankBean>();
		adapter = new ThanksItemAdapter(this, list);
		listView.setAdapter(adapter);
		pullDownView.setOnPullDownListener(this);
		pullDownView.enableAutoFetchMore(true, 3);
		pullDownView.setShowFooter();
		pullDownView.setShowHeader();
		pullDownView.notifyDidMore();
	}

	private void initOther() {
		cacheHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case Constants.READ_RESULT_OK:
					try {
						String cacheData = (String) msg.obj;
						if (!CommonUtil.isEmpty(cacheData)) {
							tranceData(cacheData);
						} else {
							getData();
						}
					} finally {
						pullDownView.refreshComplete();
						pullDownView.notifyDidMore();
					}
					break;
				}
			}
		};
	}

	private HTMLParser parser;
	private int PULL_STATE = 1;// 1 下拉，2上拉
	private int page = 0, totalPage = 0;

	private void getData() {
		if(!CommonUtil.isNetWorkConnected(this)){
			Toast.makeText(this,"网络无法连接！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		networkHandler.get(Constants.URI_THANKS + "?page=" + page, null, 30,
				new Callback<TransResp>() {
					@Override
					public void callback(TransResp tr) {
						try {
							if (tr.getRetcode() == HttpStatus.SC_OK) {
								tranceData(tr.getRetjson());
							}
						} finally {
							pullDownView.refreshComplete();
							pullDownView.notifyDidMore();
						}
					}
				});
	}

	private void tranceData(String html) {
		if (parser == null) {
			parser = new HTMLParser(html);
		} else {
			parser.setHTMLStr(html);
		}
		List<ThankBean> tempList = parser.getThanks();
		if (PULL_STATE == 1) {
			list.clear();
			String last = parser.getLastPager();
			Log.d("last_pager", last + "");
			if (!CommonUtil.isEmpty(last)) {
				try {
					totalPage = Integer.parseInt(last);
					Log.d("totalPage", totalPage + "");
				} catch (Exception e) {

				}
			}
		}
		list.addAll(tempList);
		if (totalPage == 0) {
			String last = parser.getLastPager();
			if (!CommonUtil.isEmpty(last)) {
				try {
					totalPage = Integer.parseInt(last);
				} catch (Exception e) {
				}
			}
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onRefresh() {
		if (!CommonUtil.isNetWorkConnected(this)) {
			pullDownView.refreshComplete();
			return;
		}
		PULL_STATE = 1;
		page = 0;
		getData();
	}

	@Override
	public void onMore() {
		if (!CommonUtil.isNetWorkConnected(this)) {
			pullDownView.notifyDidMore();
			return;
		}
		PULL_STATE = 2;
		Log.d("total", totalPage + "");
		if (page > totalPage) {
			pullDownView.notifyDidMore();
			return;
		}
		page++;
		getData();
	}

	@Override
	public void onClick(View arg0) {
		if (arg0 == leftBtn) {
			finish();
		}
	}

}
